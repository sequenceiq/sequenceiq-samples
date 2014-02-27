package com.sequenceiq.samples;

import com.sequenceiq.samples.core.BaseMorphlineTest;
import org.junit.Test;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class CapitalizeTest extends BaseMorphlineTest {

    @Test
    public void testCapitalizeFunctionReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/capitalize/simpleCapitalize",
                "input/capitalize/simpleCapitalize",
                "expected/capitalize/simpleCapitalize");
    }

    @Test
    public void testCapitalizeFunctionWithMoreLineReturnLowerCasedString() throws Exception {
        assertResult(  "test-morphlines/capitalize/simpleCapitalize",
                "input/capitalize/simpleCapitalizeWithMoreLine",
                "expected/capitalize/simpleCapitalizeWithMoreLine");
    }
}
