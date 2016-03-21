package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ModellingLocationPrecisionAdjuster.
 * Copyright (c) 2015 University of Oxford
 */
public class ModellingLocationPrecisionAdjusterTest {
    @Test
    public void adjustChangesTargetGaulsToPrecise() throws Exception {
        // Arrange
        ModellingLocationPrecisionAdjuster adjuster = new ModellingLocationPrecisionAdjuster(new String[] {
                "1", "2", "7", "5111"
        });

        // Act
        int result1 = adjuster.adjust(LocationPrecision.ADMIN1.getModelValue(), "1");
        int result2 = adjuster.adjust(LocationPrecision.ADMIN2.getModelValue(), "2");
        int result3 = adjuster.adjust(LocationPrecision.COUNTRY.getModelValue(), "7");
        int result4 = adjuster.adjust(LocationPrecision.PRECISE.getModelValue(), "5111");

        // Assert
        assertThat(result1).isEqualTo(LocationPrecision.PRECISE.getModelValue());
        assertThat(result2).isEqualTo(LocationPrecision.PRECISE.getModelValue());
        assertThat(result3).isEqualTo(LocationPrecision.PRECISE.getModelValue());
        assertThat(result4).isEqualTo(LocationPrecision.PRECISE.getModelValue());
    }

    @Test
    public void adjustLeavesOtherGaulsUnchanged() throws Exception {
        // Arrange
        ModellingLocationPrecisionAdjuster adjuster = new ModellingLocationPrecisionAdjuster(new String[] {
                "1", "2", "7", "5111"
        });

        // Act
        int result1 = adjuster.adjust(LocationPrecision.ADMIN1.getModelValue(), "11");
        int result2 = adjuster.adjust(LocationPrecision.ADMIN2.getModelValue(), "22");
        int result3 = adjuster.adjust(LocationPrecision.COUNTRY.getModelValue(), "77");
        int result4 = adjuster.adjust(LocationPrecision.PRECISE.getModelValue(), "51111");

        // Assert
        assertThat(result1).isEqualTo(LocationPrecision.ADMIN1.getModelValue());
        assertThat(result2).isEqualTo(LocationPrecision.ADMIN2.getModelValue());
        assertThat(result3).isEqualTo(LocationPrecision.COUNTRY.getModelValue());
        assertThat(result4).isEqualTo(LocationPrecision.PRECISE.getModelValue());
    }
}
