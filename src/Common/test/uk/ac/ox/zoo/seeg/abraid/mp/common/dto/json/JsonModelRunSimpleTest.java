package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonModelRunSimpleTest.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonModelRunSimpleTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        ModelRun run = mock(ModelRun.class);
        when(run.getName()).thenReturn("NAME");
        DateTime now = DateTime.now();
        when(run.getRequestDate()).thenReturn(now);
        when(run.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(run.getDiseaseGroup().getShortNameForDisplay()).thenReturn("DISEASE");

        // Act
        JsonModelRunSimple result = new JsonModelRunSimple(run);

        // Assert
        assertThat(result.getName()).isEqualTo("NAME");
        assertThat(result.getDate()).isEqualTo(now.toLocalDate());
        assertThat(result.getDisease()).isEqualTo("DISEASE");
    }
}
