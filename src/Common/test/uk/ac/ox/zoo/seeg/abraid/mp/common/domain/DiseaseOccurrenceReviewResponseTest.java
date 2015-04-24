package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DiseaseOccurrenceReviewResponse.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseOccurrenceReviewResponseTest {
    @Test
    public void hasTheCorrectValues() {
        assertThat(DiseaseOccurrenceReviewResponse.YES.getValue()).isEqualTo(1);
        assertThat(DiseaseOccurrenceReviewResponse.UNSURE.getValue()).isEqualTo(0.5);
        assertThat(DiseaseOccurrenceReviewResponse.NO.getValue()).isEqualTo(0);
    }

    @Test
    public void parsesCorrectly() {
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString("YES")).isEqualTo(DiseaseOccurrenceReviewResponse.YES);
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString("UNSURE")).isEqualTo(DiseaseOccurrenceReviewResponse.UNSURE);
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString("NO")).isEqualTo(DiseaseOccurrenceReviewResponse.NO);
    }

    @Test
    public void rejectsInvalidStrings() {
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString(null)).isNull();
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString("asdfae")).isNull();
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString("")).isNull();
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString(" ")).isNull();
        assertThat(DiseaseOccurrenceReviewResponse.parseFromString("YES ")).isNull();
    }
}
