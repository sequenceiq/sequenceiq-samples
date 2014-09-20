package com.sequenceiq.tez.topk;

import static org.apache.commons.lang.StringUtils.join;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.tez.client.TezClient;
import org.apache.tez.dag.api.DAG;
import org.apache.tez.dag.api.DataSinkDescriptor;
import org.apache.tez.dag.api.DataSourceDescriptor;
import org.apache.tez.dag.api.Edge;
import org.apache.tez.dag.api.ProcessorDescriptor;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.dag.api.UserPayload;
import org.apache.tez.dag.api.Vertex;
import org.apache.tez.dag.api.client.DAGClient;
import org.apache.tez.dag.api.client.DAGStatus;
import org.apache.tez.mapreduce.input.MRInput;
import org.apache.tez.mapreduce.output.MROutput;
import org.apache.tez.mapreduce.processor.SimpleMRProcessor;
import org.apache.tez.runtime.api.ProcessorContext;
import org.apache.tez.runtime.library.api.KeyValueReader;
import org.apache.tez.runtime.library.api.KeyValueWriter;
import org.apache.tez.runtime.library.api.KeyValuesReader;
import org.apache.tez.runtime.library.conf.OrderedPartitionedKVEdgeConfig;
import org.apache.tez.runtime.library.partitioner.HashPartitioner;
import org.apache.tez.runtime.library.processor.SimpleProcessor;

public class TopK extends Configured implements Tool {

  private static final String INPUT = "input";
  private static final String WRITER = "writer";
  private static final String OUTPUT = "output";
  private static final String TOKENIZER = "tokenizer";
  private static final String SUM = "sum";

  public static void main(String[] args) throws Exception {
    int res = ToolRunner.run(new Configuration(), new TopK(), args);
    System.exit(res);
  }

  @Override
  public int run(String[] args) throws Exception {
    Configuration conf = getConf();
    TopK job = new TopK();
    if (job.run(args[0], args[1], args[2], args.length > 3 ? args[3] : "-1", args.length > 4 ? args[4] : "1", conf)) {
      return 0;
    }
    return 1;
  }

  private boolean run(String inputPath, String outputPath,
                      String columnIndex, String top, String partition, Configuration conf) throws Exception {
    TezConfiguration tezConf;
    if (conf != null) {
      tezConf = new TezConfiguration(conf);
    } else {
      tezConf = new TezConfiguration();
    }

    UserGroupInformation.setConfiguration(tezConf);

    // Create the TezClient to submit the DAG. Pass the tezConf that has all necessary global and
    // dag specific configurations
    TezClient tezClient = TezClient.create("topk", tezConf);
    // TezClient must be started before it can be used
    tezClient.start();

    try {
      DAG dag = createDAG(tezConf, inputPath, outputPath, columnIndex, top, partition);

      // check that the execution environment is ready
      tezClient.waitTillReady();
      // submit the dag and receive a dag client to monitor the progress
      DAGClient dagClient = tezClient.submitDAG(dag);

      // monitor the progress and wait for completion. This method blocks until the dag is done.
      DAGStatus dagStatus = dagClient.waitForCompletionWithStatusUpdates(null);
      // check success or failure and print diagnostics
      if (dagStatus.getState() != DAGStatus.State.SUCCEEDED) {
        System.out.println("topk failed with diagnostics: " + dagStatus.getDiagnostics());
        return false;
      }
      return true;
    } finally {
      // stop the client to perform cleanup
      tezClient.stop();
    }
  }

  private DAG createDAG(TezConfiguration tezConf,
                        String inputPath, String outputPath,
                        String columnIndex, String top, String partition) throws IOException {

    DataSourceDescriptor dataSource = MRInput.createConfigBuilder(new Configuration(tezConf),
      TextInputFormat.class, inputPath).build();

    DataSinkDescriptor dataSink = MROutput.createConfigBuilder(new Configuration(tezConf),
      TextOutputFormat.class, outputPath).build();

    Vertex tokenizerVertex = Vertex.create(TOKENIZER,
      ProcessorDescriptor.create(TokenProcessor.class.getName())
        .setUserPayload(createPayload(Integer.valueOf(columnIndex))))
      .addDataSource(INPUT, dataSource);

    Vertex sumVertex = Vertex.create(SUM,
      ProcessorDescriptor.create(SumProcessor.class.getName()), Integer.valueOf(partition));

    Vertex writerVertex = Vertex.create(WRITER,
      ProcessorDescriptor.create(Writer.class.getName())
        .setUserPayload(createPayload(Integer.valueOf(top))), 1)
      .addDataSink(OUTPUT, dataSink);

    OrderedPartitionedKVEdgeConfig edgeConf = OrderedPartitionedKVEdgeConfig
      .newBuilder(Text.class.getName(), IntWritable.class.getName(),
        HashPartitioner.class.getName()).build();

    OrderedPartitionedKVEdgeConfig sorterEdgeConf = OrderedPartitionedKVEdgeConfig
      .newBuilder(IntWritable.class.getName(), Text.class.getName(),
        HashPartitioner.class.getName())
      .setKeyComparatorClass(RevComparator.class.getName()).build();

    DAG dag = DAG.create("topk");
    return dag
      .addVertex(tokenizerVertex)
      .addVertex(sumVertex)
      .addVertex(writerVertex)
      .addEdge(Edge.create(tokenizerVertex, sumVertex, edgeConf.createDefaultEdgeProperty()))
      .addEdge(Edge.create(sumVertex, writerVertex, sorterEdgeConf.createDefaultEdgeProperty()));
  }

  private UserPayload createPayload(int num) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    dos.writeInt(num);
    dos.close();
    bos.close();
    ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
    return UserPayload.create(buffer);
  }

  public static class TokenProcessor extends SimpleProcessor {

    final IntWritable one = new IntWritable(1);
    int columnIndex;
    Text word = new Text();

    public TokenProcessor(ProcessorContext context) {
      super(context);
    }

    @Override
    public void initialize() throws Exception {
      byte[] payload = getContext().getUserPayload().deepCopyAsArray();
      ByteArrayInputStream bis = new ByteArrayInputStream(payload);
      DataInputStream dis = new DataInputStream(bis);
      columnIndex = dis.readInt();
      dis.close();
      bis.close();
    }

    @Override
    public void run() throws Exception {
      KeyValueReader kvReader = (KeyValueReader) getInputs().get(INPUT).getReader();
      KeyValueWriter kvWriter = (KeyValueWriter) getOutputs().get(SUM).getWriter();
      while (kvReader.next()) {
        String[] split = kvReader.getCurrentValue().toString().split(",");
        word.set(split[columnIndex]);
        kvWriter.write(word, one);
      }
    }
  }

  public static class SumProcessor extends SimpleProcessor {

    public SumProcessor(ProcessorContext context) {
      super(context);
    }

    @Override
    public void run() throws Exception {
      KeyValueWriter kvWriter = (KeyValueWriter) getOutputs().get(WRITER).getWriter();
      KeyValuesReader kvReader = (KeyValuesReader) getInputs().get(TOKENIZER).getReader();
      while (kvReader.next()) {
        Text word = (Text) kvReader.getCurrentKey();
        int sum = 0;
        for (Object value : kvReader.getCurrentValues()) {
          sum += ((IntWritable) value).get();
        }
        kvWriter.write(new IntWritable(sum), word);
      }
    }
  }

  public static class Writer extends SimpleMRProcessor {

    private int top;
    private int current;

    public Writer(ProcessorContext context) {
      super(context);
    }

    @Override
    public void initialize() throws Exception {
      byte[] payload = getContext().getUserPayload().deepCopyAsArray();
      ByteArrayInputStream bis = new ByteArrayInputStream(payload);
      DataInputStream dis = new DataInputStream(bis);
      top = dis.readInt();
      top = top == -1 ? Integer.MAX_VALUE : top;
      dis.close();
      bis.close();
    }

    @Override
    public void run() throws Exception {
      KeyValueWriter kvWriter = (KeyValueWriter) getOutputs().get(OUTPUT).getWriter();
      KeyValuesReader kvReader = (KeyValuesReader) getInputs().get(SUM).getReader();
      if (top > 0) {
        while (kvReader.next()) {
          if (current < top) {
            current++;
            List<String> words = new ArrayList<String>();
            for (Object word : kvReader.getCurrentValues()) {
              words.add(word.toString());
            }
            kvWriter.write(join(words, ','), kvReader.getCurrentKey());
          } else {
            break;
          }
        }
      }
    }
  }

  public static class RevComparator implements RawComparator<IntWritable> {

    @Override
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
      return WritableComparator.compareBytes(b2, s2, l2, b1, s1, l1);
    }

    @Override
    public int compare(IntWritable intWritable, IntWritable intWritable2) {
      return intWritable2.compareTo(intWritable);
    }
  }
}
