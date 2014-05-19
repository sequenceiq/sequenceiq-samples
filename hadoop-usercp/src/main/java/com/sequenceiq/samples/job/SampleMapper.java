package com.sequenceiq.samples.job;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampleMapper extends Mapper<LongWritable, Text, LongWritable, Text> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleMapper.class);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        try {
            String jar = JsonNode.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            Text text = new Text();
            text.set(jar);
            context.write(key, text);
        } catch (Exception e) {
            LOGGER.error("Exception during line processing", e);
            context.getCounter("Map", "LinesWithErrors").increment(1);
        }
    }

}
