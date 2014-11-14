package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.experts;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertValidationRulesChecker;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for AdminExpertsControllerValidator.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminExpertsControllerValidatorTest {
    @Test
    public void validateChecksId() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AdminExpertsControllerValidator target = new AdminExpertsControllerValidator(checker);
        JsonExpertFull expert = mockExpert();
        when(expert.getId()).thenReturn(123);

        // Act
        target.validate(expert);

        // Assert
        verify(checker).checkId(eq(123), anyListOf(String.class));
    }

    @Test
    public void validateChecksVisibilityApproved() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AdminExpertsControllerValidator target = new AdminExpertsControllerValidator(checker);
        JsonExpertFull expert = mockExpert();
        when(expert.getVisibilityApproved()).thenReturn(false);

        // Act
        target.validate(expert);

        // Assert
        verify(checker).checkVisibilityApproved(eq(false), anyListOf(String.class));
    }

    @Test
    public void validateChecksWeighting() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AdminExpertsControllerValidator target = new AdminExpertsControllerValidator(checker);
        JsonExpertFull expert = mockExpert();
        when(expert.getWeighting()).thenReturn(123.321);

        // Act
        target.validate(expert);

        // Assert
        verify(checker).checkWeighting(eq(123.321), anyListOf(String.class));
    }

    @Test
    public void validateChecksIsAdministrator() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AdminExpertsControllerValidator target = new AdminExpertsControllerValidator(checker);
        JsonExpertFull expert = mockExpert();
        when(expert.isAdministrator()).thenReturn(false);

        // Act
        target.validate(expert);

        // Assert
        verify(checker).checkIsAdministrator(eq(false), anyListOf(String.class));
    }

    @Test
    public void validateChecksIsSeegMember() throws Exception {
        // Arrange
        ExpertValidationRulesChecker checker = mock(ExpertValidationRulesChecker.class);
        AdminExpertsControllerValidator target = new AdminExpertsControllerValidator(checker);
        JsonExpertFull expert = mockExpert();
        when(expert.isSEEGMember()).thenReturn(false);

        // Act
        target.validate(expert);

        // Assert
        verify(checker).checkIsSeegMember(eq(false), anyListOf(String.class));
    }

    private static JsonExpertFull mockExpert() {
        JsonExpertFull result = mock(JsonExpertFull.class);
        when(result.getId()).thenReturn(7);
        when(result.getVisibilityApproved()).thenReturn(true);
        when(result.getWeighting()).thenReturn(9.1);
        when(result.isAdministrator()).thenReturn(true);
        when(result.isSEEGMember()).thenReturn(true);
        return result;
    }
}
