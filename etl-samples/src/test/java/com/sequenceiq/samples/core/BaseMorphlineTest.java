package com.sequenceiq.samples.core;

import org.kitesdk.morphline.api.AbstractMorphlineTest;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class BaseMorphlineTest extends AbstractMorphlineTest {

    private final String SEPARATOR = "\t";

    protected void assertResult(String morphlineSpecification, String inputFile, String expectedFile) throws IOException {
        morphline = createMorphline(morphlineSpecification);

        List<String> inputFileRows = TestBasedFileReader.getFileContentAsList(inputFile);
        List<String> expectedFileRows = TestBasedFileReader.getFileContentAsList(expectedFile);
        String[] inputHeaders = inputFileRows.get(0).split(SEPARATOR);
        String[] expectedHeaders = expectedFileRows.get(0).split(SEPARATOR);
        for (int i = 1; i < inputFileRows.size(); i++) {
            String[] split = inputFileRows.get(i).split(SEPARATOR);
            Record inputRecord = new Record();
            for (int j = 0; j < split.length; j++) {
                inputRecord.put(inputHeaders[j], split[j]);
            }

            String[] expectedSplit = expectedFileRows.get(i).split(SEPARATOR);
            Record expectedRecord = new Record();
            for (int j = 0; j < expectedSplit.length; j++) {
                expectedRecord.put(expectedHeaders[j], expectedSplit[j]);
            }

            assertTrue(morphline.process(inputRecord));
            assertThat(collector.getRecords().get(i-1), equalTo(expectedRecord));
        }
    }

}
