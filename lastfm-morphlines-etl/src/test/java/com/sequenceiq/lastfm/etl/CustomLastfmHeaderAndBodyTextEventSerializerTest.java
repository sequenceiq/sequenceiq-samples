package com.sequenceiq.lastfm.etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.flume.Context;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.serialization.EventSerializer;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;

public class CustomLastfmHeaderAndBodyTextEventSerializerTest {
    File testFile = new File("src/test/resources/events.txt");
    File expectedFile = new File("src/test/resources/events.txt");

    @Test
    public void testWithNewline() throws FileNotFoundException, IOException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("message", "message1");
        OutputStream out = new FileOutputStream(testFile);
        CustomLastfmHeaderAndBodyTextEventSerializer.Builder builder = CustomLastfmHeaderAndBodyTextEventSerializer.builder();
        EventSerializer serializer = builder.build(new Context(), out);
        serializer.afterCreate();
        serializer.write(EventBuilder.withBody("messageBody", Charsets.UTF_8, headers));
        serializer.flush();
        serializer.beforeClose();
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        Assert.assertEquals("message1", reader.readLine());
        Assert.assertNull(reader.readLine());
        reader.close();

        FileUtils.forceDelete(testFile);
    }

    @Test
    public void testNoNewline() throws FileNotFoundException, IOException {

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("header1", "value1");

        OutputStream out = new FileOutputStream(testFile);
        Context context = new Context();
        context.put("appendNewline", "false");
        CustomLastfmHeaderAndBodyTextEventSerializer.Builder builder = CustomLastfmHeaderAndBodyTextEventSerializer.builder();
        EventSerializer serializer = builder.build(new Context(), out);
        serializer.afterCreate();
        serializer.write(EventBuilder.withBody("event 1", Charsets.UTF_8, headers));
        serializer.write(EventBuilder.withBody("event 2", Charsets.UTF_8, headers));
        serializer.write(EventBuilder.withBody("event 3", Charsets.UTF_8, headers));
        serializer.flush();
        serializer.beforeClose();
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(new FileReader(testFile));
        Assert.assertNull(reader.readLine());
        reader.close();

        FileUtils.forceDelete(testFile);
    }
}
