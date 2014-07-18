package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertDetailsTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Expert mockExpert = mock(Expert.class);
        when(mockExpert.getName()).thenReturn("expected name");
        when(mockExpert.getJobTitle()).thenReturn("expected job");
        when(mockExpert.getInstitution()).thenReturn("expected institution");
        when(mockExpert.isPubliclyVisible()).thenReturn(true);
        when(mockExpert.getValidatorDiseaseGroups()).thenReturn(Arrays.asList(
            new ValidatorDiseaseGroup(1, "foo"),
            new ValidatorDiseaseGroup(2, "foo2")
        ));

        // Act
        JsonExpertDetails result = new JsonExpertDetails(mockExpert);

        // Assert
        assertThat(result.getName()).isEqualTo(mockExpert.getName());
        assertThat(result.getJobTitle()).isEqualTo(mockExpert.getJobTitle());
        assertThat(result.getInstitution()).isEqualTo(mockExpert.getInstitution());
        assertThat(result.isPubliclyVisible()).isEqualTo(mockExpert.isPubliclyVisible());
        assertThat(result.getDiseaseInterests()).containsOnly(1, 2);
    }
}
