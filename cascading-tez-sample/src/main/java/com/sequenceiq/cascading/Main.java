package com.sequenceiq.cascading;

import cascading.flow.Flow;
import cascading.flow.FlowConnector;
import cascading.flow.FlowDef;
import cascading.flow.FlowRuntimeProps;
import cascading.flow.tez.Hadoop2TezFlowConnector;
import cascading.operation.Filter;
import cascading.operation.aggregator.Count;
import cascading.operation.buffer.FirstNBuffer;
import cascading.operation.expression.ExpressionFilter;
import cascading.operation.regex.RegexFilter;
import cascading.pipe.Each;
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

        final Fields fields = new Fields("userId", "data1", "data2", "data3");
        final Scheme scheme = new TextDelimited(fields, false, true, ",");

        final Pipe inPipe = new Pipe("inPipe");
        final Tap inTap = new Hfs(scheme, inputPath);
        final Fields groupFields = new Fields("userId");

        Pipe usersPipe = new GroupBy("usersWithCount", inPipe, groupFields);
        usersPipe = new Every(usersPipe, groupFields, new Count(), Fields.ALL);
        usersPipe = new GroupBy(usersPipe, Fields.NONE, new Fields("count", "userId"), true);
        usersPipe = new Each(usersPipe, new Fields("count"), new RegexFilter( "^(?:[2-9]|(?:[1-9][0-9]+))" ));

        final Fields resultFields = new Fields("userId", "count");
        final Scheme outputScheme = new TextDelimited(resultFields, false, true, ",");
        Tap sinkTap = new Hfs(outputScheme, outputPath);

        FlowDef flowDef = FlowDef.flowDef()
                .setName("Cascading-TEZ")
                .addSource(inPipe, inTap)
                .addTailSink(usersPipe, sinkTap);

        Flow flow = flowConnector.connect(flowDef);
        flow.complete();

        System.exit(0);

    }
}
