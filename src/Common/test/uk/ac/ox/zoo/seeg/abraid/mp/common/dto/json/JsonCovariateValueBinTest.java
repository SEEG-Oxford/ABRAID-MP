package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateValueBin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonCovariateValueBin.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonCovariateValueBinTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        CovariateValueBin expected = mock(CovariateValueBin.class);
        when(expected.getCount()).thenReturn(1);
        when(expected.getMax()).thenReturn(2d);
        when(expected.getMin()).thenReturn(3d);

        // Act
        JsonCovariateValueBin actual = new JsonCovariateValueBin(expected);

        // Assert
        assertThat(actual.getCount()).isEqualTo(expected.getCount());
        assertThat(actual.getMin()).isEqualTo(expected.getMin());
        assertThat(actual.getMax()).isEqualTo(expected.getMax());
    }
}
