package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util;

import org.apache.commons.exec.OS;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for OSCheckerImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class OSCheckerTest {
    @Test
    public void isWindowsReturnsCorrectResult() throws Exception {
        assertThat((new OSCheckerImpl().isWindows())).isEqualTo(OS.isFamilyWindows());
    }
}
