package com.sequenceiq.lastfm.etl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.kitesdk.morphline.api.AbstractMorphlineTest;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

public class LatestSongCommandTest extends AbstractMorphlineTest {


    protected Record assertResult(String morphlineSpecification, String inputFile) throws IOException {
        morphline = createMorphline(morphlineSpecification);
        InputStream in = new FileInputStream(new File(RESOURCES_DIR + inputFile));
        Record record = new Record();
        record.put(Fields.ATTACHMENT_BODY, in);
        return record;
    }

    @Test
    public void testOk2011() throws IOException {
        Record record = assertResult("test-morphlines/date", "/test-documents/sample2011.json");
        assertFalse(morphline.process(record));
    }

    @Test
    public void testOk2012() throws IOException {
        Record record = assertResult("test-morphlines/date", "/test-documents/sample2012.json");
        assertTrue(morphline.process(record));
    }

    @Test
    public void testOk2013() throws IOException {
        Record record = assertResult("test-morphlines/date", "/test-documents/sample2013.json");
        assertTrue(morphline.process(record));
    }
}
