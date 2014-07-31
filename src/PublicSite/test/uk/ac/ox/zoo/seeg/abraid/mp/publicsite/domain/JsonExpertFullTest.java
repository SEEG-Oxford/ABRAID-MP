package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonExpertFull.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertFullTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Expert mockExpert = mock(Expert.class);
        when(mockExpert.getId()).thenReturn(321);
        when(mockExpert.getEmail()).thenReturn("expected email");
        when(mockExpert.getWeighting()).thenReturn(4.56);
        when(mockExpert.getVisibilityApproved()).thenReturn(true);
        when(mockExpert.isAdministrator()).thenReturn(true);
        when(mockExpert.isSeegMember()).thenReturn(true);
        when(mockExpert.getCreatedDate()).thenReturn(new DateTime(0));
        when(mockExpert.getUpdatedDate()).thenReturn(new DateTime(1));

        // Act
        JsonExpertFull result = new JsonExpertFull(mockExpert);

        // Assert
        assertThat(result.getId()).isEqualTo(mockExpert.getId());
        assertThat(result.getEmail()).isEqualTo(mockExpert.getEmail());
        assertThat(result.getWeighting()).isEqualTo(mockExpert.getWeighting());
        assertThat(result.getVisibilityApproved()).isEqualTo(mockExpert.getVisibilityApproved());
        assertThat(result.isAdministrator()).isEqualTo(mockExpert.isAdministrator());
        assertThat(result.isSEEGMember()).isEqualTo(mockExpert.isSeegMember());
        assertThat(result.getCreatedDate()).isEqualTo(mockExpert.getCreatedDate());
        assertThat(result.getUpdatedDate()).isEqualTo(mockExpert.getUpdatedDate());
    }

    @Test
    public void hasTheFieldsFromJsonExpertDetailsButNullDiseaseInterests() {
        // Arrange
        Expert mockExpert = mock(Expert.class);
        when(mockExpert.getName()).thenReturn("expected name");
        when(mockExpert.getJobTitle()).thenReturn("expected job");
        when(mockExpert.getInstitution()).thenReturn("expected institution");
        when(mockExpert.getVisibilityRequested()).thenReturn(true);
        when(mockExpert.getValidatorDiseaseGroups()).thenReturn(Arrays.asList(
                new ValidatorDiseaseGroup(1, "foo"),
                new ValidatorDiseaseGroup(2, "foo2")
        ));

        // Act
        JsonExpertFull result = new JsonExpertFull(mockExpert);

        // Assert
        assertThat(result.getName()).isEqualTo(mockExpert.getName());
        assertThat(result.getJobTitle()).isEqualTo(mockExpert.getJobTitle());
        assertThat(result.getInstitution()).isEqualTo(mockExpert.getInstitution());
        assertThat(result.getVisibilityRequested()).isEqualTo(mockExpert.getVisibilityRequested());
        assertThat(result.getDiseaseInterests()).isNull();
    }
}
