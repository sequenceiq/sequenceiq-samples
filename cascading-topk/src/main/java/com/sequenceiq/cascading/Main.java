package com.sequenceiq.cascading;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.FlowDef;
import cascading.flow.FlowRuntimeProps;
import cascading.flow.tez.Hadoop2TezFlowConnector;
import cascading.operation.aggregator.Count;
import cascading.operation.buffer.FirstNBuffer;
import cascading.pipe.Every;
import cascading.pipe.GroupBy;
import cascading.pipe.Pipe;
import cascading.property.AppProps;
import cascading.scheme.Scheme;
import cascading.scheme.hadoop.TextDelimited;
import cascading.tap.Tap;
import cascading.tap.hadoop.Hfs;
import cascading.tuple.Fields;

import java.util.Properties;

/**
 * A TopK implementation of cascading
 */
public class Main {
    public static void main( String[] args ) {
        Properties properties = AppProps.appProps()
                .setJarClass(Main.class)
                .buildProperties();

        properties = FlowRuntimeProps.flowRuntimeProps()
                .setGatherPartitions(1)
                .buildProperties(properties);

        FlowConnector flowConnector = new Hadoop2TezFlowConnector(properties);

        final String inputPath = args[0];
        final String outputPath = args[1];
        final int top = Integer.parseInt(args[2]);

        final Fields fields = new Fields("userId", "data1", "data2", "data3");
        final Scheme scheme = new TextDelimited(fields, false, true, ",");

        final Pipe inPipe = new Pipe("inPipe");
        final Tap inTap = new Hfs(scheme, inputPath);
        final Fields groupFields = new Fields("userId");

        Pipe topUsersPipe = new GroupBy("topUsers", inPipe, groupFields);
        topUsersPipe = new Every(topUsersPipe, groupFields, new Count(), Fields.ALL);
        topUsersPipe = new GroupBy(topUsersPipe, Fields.NONE, new Fields("count", "userId"), true);
        topUsersPipe = new Every(topUsersPipe, Fields.ALL, new FirstNBuffer(top), Fields.ARGS);

        final Fields resultFields = new Fields("userId", "count");
        final Scheme outputScheme = new TextDelimited(resultFields, false, true, ",");
        Tap sinkTap = new Hfs(outputScheme, outputPath);

        FlowDef flowDef = FlowDef.flowDef()
                .setName("TopK-TEZ")
                .addSource(inPipe, inTap)
                .addTailSink(topUsersPipe, sinkTap);

        Flow flow = flowConnector.connect(flowDef);
        flow.complete();

        System.exit(0);

    }
}
