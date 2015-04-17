package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for EffectCurveCovariateInfluence.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseProcessTypeTest {
    @Test
    public void isAutomatic() throws Exception {
        assertThat(DiseaseProcessType.AUTOMATIC.isAutomatic()).isTrue();
        assertThat(DiseaseProcessType.MANUAL.isAutomatic()).isFalse();
        assertThat(DiseaseProcessType.MANUAL_GOLD_STANDARD.isAutomatic()).isFalse();
    }

    @Test
    public void isGoldStandard() throws Exception {
        assertThat(DiseaseProcessType.AUTOMATIC.isGoldStandard()).isFalse();
        assertThat(DiseaseProcessType.MANUAL.isGoldStandard()).isFalse();
        assertThat(DiseaseProcessType.MANUAL_GOLD_STANDARD.isGoldStandard()).isTrue();
    }
}
