package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonApiDiseaseGroup.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiDiseaseGroupTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        int id = 123;
        String name = "qwe";
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(id);
        when(diseaseGroup.getPublicName()).thenReturn(name);

        // Act
        JsonApiDiseaseGroup dto = new JsonApiDiseaseGroup(diseaseGroup);

        // Assert
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getId()).isEqualTo(id);
    }
}
