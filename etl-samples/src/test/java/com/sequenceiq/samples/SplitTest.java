package com.sequenceiq.samples;

import com.sequenceiq.samples.core.BaseMorphlineTest;
import org.junit.Test;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class SplitTest extends BaseMorphlineTest {

    @Test
    public void testSplitFunctionReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/split/simpleSplit",
                "input/split/simpleSplit",
                "expected/split/simpleSplit");
    }

    @Test
    public void testSplitWithMoreSegmentFunctionReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/split/simpleSplitWithMoreSegment",
                "input/split/simpleSplitWithMoreField",
                "expected/split/simpleSplitWithMoreField");
    }

}
