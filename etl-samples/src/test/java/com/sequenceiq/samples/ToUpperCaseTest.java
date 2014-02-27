package com.sequenceiq.samples;

import com.sequenceiq.samples.core.BaseMorphlineTest;
import org.junit.Test;

/**
 * Created by Ricsi on 2014.02.13..
 */
public class ToUpperCaseTest extends BaseMorphlineTest {

    @Test
    public void testUpperCaseFunctionReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/toupper/simpleToUpper",
                "input/toupper/simpleToUpper",
                "expected/toupper/simpleToUpper");
    }

    @Test
    public void testLowerCaseFunctionWithMoreLineReturnLowerCasedString() throws Exception {
        assertResult("test-morphlines/toupper/simpleToUpper",
                "input/toupper/simpleToUpperWithMoreLine",
                "expected/toupper/simpleToUpperWithMoreLine");
    }
}
