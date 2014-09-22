package com.sequenceiq.tez.topk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.tez.client.TezClient;
import org.apache.tez.dag.api.DAG;
import org.apache.tez.dag.api.ProcessorDescriptor;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.dag.api.TezException;
import org.apache.tez.dag.api.UserPayload;
import org.apache.tez.dag.api.Vertex;
import org.apache.tez.dag.api.client.DAGClient;
import org.apache.tez.dag.api.client.DAGStatus;
import org.apache.tez.mapreduce.output.MROutput;
import org.apache.tez.mapreduce.processor.SimpleMRProcessor;
import org.apache.tez.runtime.api.ProcessorContext;
import org.apache.tez.runtime.library.api.KeyValueWriter;

public class TopKDataGen extends Configured implements Tool {

    private static final Log LOG = LogFactory.getLog(TopKDataGen.class);

    private static final String OUTPUT = "output";

    public static void main(String[] args) throws Exception {
        TopKDataGen dataGen = new TopKDataGen();
        int status = ToolRunner.run(new Configuration(), dataGen, args);
        System.exit(status);
    }

    private static void printUsage() {
        System.err.println("Usage: topkgen <outPath> <size> <numExtraColumns> <numTasks>");
        ToolRunner.printGenericCommandUsage(System.err);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        int result = validateArgs(otherArgs);
        if (result != 0) {
            return result;
        }
        return execute(otherArgs);
    }

    public int run(Configuration conf, String[] args, TezClient tezClient) throws Exception {
        setConf(conf);
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        int result = validateArgs(otherArgs);
        if (result != 0) {
            return result;
        }
        return execute(otherArgs, tezClient);
    }

    private int validateArgs(String[] otherArgs) {
        if (otherArgs.length < 2) {
            printUsage();
            return 2;
        }
        return 0;
    }

    private int execute(String[] args) throws TezException, IOException, InterruptedException {
        TezConfiguration tezConf = new TezConfiguration(getConf());
        TezClient tezClient = null;
        try {
            tezClient = createTezClient(tezConf);
            return execute(args, tezConf, tezClient);
        } finally {
            if (tezClient != null) {
                tezClient.stop();
            }
        }
    }

    private int execute(String[] args, TezClient tezClient) throws IOException, TezException, InterruptedException {
        TezConfiguration tezConf = new TezConfiguration(getConf());
        return execute(args, tezConf, tezClient);
    }

    private TezClient createTezClient(TezConfiguration tezConf) throws TezException, IOException {
        TezClient tezClient = TezClient.create("TopK datagen", tezConf);
        tezClient.start();
        return tezClient;
    }

    private int execute(String[] args, TezConfiguration tezConf, TezClient tezClient)
            throws IOException, TezException, InterruptedException {
        LOG.info("Running TopK DataGen");

        UserGroupInformation.setConfiguration(tezConf);

        String outDir = args[0];
        long outDirSize = Long.parseLong(args[1]);

        int numExtraColumns = 0;
        if (args.length > 2) {
            numExtraColumns = Integer.parseInt(args[2]);
        }
        int numTasks = 5;
        if (args.length > 3) {
            numTasks = Integer.parseInt(args[3]);
        }

        Path outPath = new Path(outDir);

        // Verify output path existence
        FileSystem fs = FileSystem.get(tezConf);
        int res = 0;
        res = checkOutputDirectory(fs, outPath);
        if (res != 0) {
            return 3;
        }

        if (numTasks <= 0) {
            System.err.println("NumTasks must be > 0");
            return 4;
        }

        DAG dag = createDag(tezConf, outPath, outDirSize, numExtraColumns, numTasks);

        tezClient.waitTillReady();
        DAGClient dagClient = tezClient.submitDAG(dag);
        DAGStatus dagStatus = dagClient.waitForCompletionWithStatusUpdates(null);
        if (dagStatus.getState() != DAGStatus.State.SUCCEEDED) {
            LOG.info("DAG diagnostics: " + dagStatus.getDiagnostics());
            return -1;
        }
        return 0;

    }

    private DAG createDag(TezConfiguration tezConf, Path outPath, long outSize, int extraColumns, int numTasks)
            throws IOException {

        long largeOutSizePerTask = outSize / numTasks;

        DAG dag = DAG.create("TopK DataGen");

        Vertex genDataVertex = Vertex.create("datagen", ProcessorDescriptor.create(
                        GenDataProcessor.class.getName()).setUserPayload(
                        UserPayload.create(ByteBuffer.wrap(GenDataProcessor.createConfiguration(largeOutSizePerTask, extraColumns)))),
                numTasks);
        genDataVertex.addDataSink(OUTPUT,
                MROutput.createConfigBuilder(new Configuration(tezConf),
                        TextOutputFormat.class, outPath.toUri().toString()).build());
        dag.addVertex(genDataVertex);

        return dag;
    }

    public static class GenDataProcessor extends SimpleMRProcessor {

        private Random random = new Random();
        private long streamOutputFileSize;
        private int extraColumns;

        public GenDataProcessor(ProcessorContext context) {
            super(context);
        }

        public static byte[] createConfiguration(long streamOutputFileSize, int extraColumns) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeLong(streamOutputFileSize);
            dos.writeInt(extraColumns);
            dos.close();
            bos.close();
            return bos.toByteArray();
        }

        @Override
        public void initialize() throws Exception {
            byte[] payload = getContext().getUserPayload().deepCopyAsArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(payload);
            DataInputStream dis = new DataInputStream(bis);
            streamOutputFileSize = dis.readLong();
            extraColumns = dis.readInt();
            dis.close();
            bis.close();
        }

        @Override
        public void run() throws Exception {
            KeyValueWriter streamOutputWriter = (KeyValueWriter) getOutputs().get(OUTPUT).getWriter();
            long sizeLarge = 0;
            while (sizeLarge < streamOutputFileSize) {
                String str = createRowString();
                Text text = new Text(str);
                int size = text.getLength();
                streamOutputWriter.write(text, NullWritable.get());
                sizeLarge += size;
            }
        }

        private String createRowString() {
            StringBuilder sb = new StringBuilder();
            sb
                    .append("user").append(random.nextInt(999) + 1).append(',')
                    .append(System.currentTimeMillis()).append(',')
                    .append(RandomStringUtils.randomNumeric(8)).append(',')
                    .append(RandomStringUtils.random(10, new char[]{'a', 'b', 'c', 'd', 'e', 'f'}));
            for (int i = 0; i < Math.abs(extraColumns); i++) {
                sb
                        .append(',').append(RandomStringUtils.random(10, new char[]{'a', 'b', 'c', 'd', 'e', 'f'}))
                        .append(',').append(RandomStringUtils.random(15, new char[]{'g', 'h', 'i', 'j', 'k', 'l'}));
            }
            return sb.toString();
        }

    }

    private int checkOutputDirectory(FileSystem fs, Path path) throws IOException {
        if (fs.exists(path)) {
            System.err.println("Output directory: " + path + " already exists");
            return 2;
        }
        return 0;
    }

}
