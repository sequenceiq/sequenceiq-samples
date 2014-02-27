package com.sequenceiq.samples;

import com.sequenceiq.samples.core.BaseMorphlineTest;
import org.junit.Test;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class ToLowerCaseTest extends BaseMorphlineTest {

    @Test
    public void testLowerCaseFunctionReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/tolower/simpleToLower",
                "input/tolower/simpleToLower",
                "expected/tolower/simpleToLower");
    }

    @Test
    public void testLowerCaseFunctionWithMoreLineReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/tolower/simpleToLower",
                "input/tolower/simpleToLowerWithMoreLine",
                "expected/tolower/simpleToLowerWithMoreLine");
    }
}
