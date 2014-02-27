package com.sequenceiq.samples;

import com.sequenceiq.samples.core.BaseMorphlineTest;
import org.junit.Test;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class ReverseTest extends BaseMorphlineTest {

    @Test
    public void testReverseFunctionReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/reverse/simpleReverse",
                "input/reverse/simpleReverse",
                "expected/reverse/simpleReverse");
    }

    @Test
    public void testReverseFunctionWithMoreLineReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/reverse/simpleReverse",
                "input/reverse/simpleReverseWithMoreLine",
                "expected/reverse/simpleReverseWithMoreLine");
    }
}
