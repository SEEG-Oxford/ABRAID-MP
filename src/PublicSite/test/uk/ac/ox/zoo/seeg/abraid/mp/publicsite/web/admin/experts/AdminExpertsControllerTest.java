package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.experts;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils.captorForClass;

/**
 * Tests for AdminExpertsController.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminExpertsControllerTest {
    @Test
    public void showPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        when(expertService.getAllExperts()).thenReturn(new ArrayList<Expert>());
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();

        AdminExpertsController target = new AdminExpertsController(expertService, objectMapper, null, null);

        // Act
        String result = target.showPage(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("admin/experts");
    }

    @Test
    public void showPageAddsAllExpertsToTheFreemarkerModelAsJson() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        Expert expert1 = mock(Expert.class);
        when(expert1.getId()).thenReturn(123);
        Expert expert2 = mock(Expert.class);
        when(expert2.getId()).thenReturn(543);
        List<Expert> experts = Arrays.asList(expert1, expert2);
        when(expertService.getAllExperts()).thenReturn(experts);
        when(expertService.getAdminUnitReviewCount(123)).thenReturn(987L);
        when(expertService.getDiseaseOccurrenceReviewCount(123)).thenReturn(666L);
        when(expertService.getLastReviewDate(123)).thenReturn(new DateTime("2015-03-11T16:44:25.426Z"));
        when(expertService.getAdminUnitReviewCount(543)).thenReturn(222L);
        when(expertService.getDiseaseOccurrenceReviewCount(543)).thenReturn(111L);
        when(expertService.getLastReviewDate(543)).thenReturn(new DateTime("2015-03-11T17:44:25.426Z"));
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();

        AdminExpertsController target = new AdminExpertsController(expertService, objectMapper, null, null);
        Model model = mock(Model.class);

        // Act
        target.showPage(model);

        // Assert
        ArgumentCaptor<String> captor = captorForClass(String.class);
        verify(model).addAttribute(eq("experts"), captor.capture());
        String json = captor.getValue();

        assertThat(json).startsWith("[{");
        assertThat(json).endsWith("}]");
        String[] objects = json.split("\\},\\{");

        assertThat(objects).hasSize(2);

        assertThat(objects[0]).contains("\"id\":123");
        assertThat(objects[0]).contains("\"weighting\":0.0");
        assertThat(objects[0]).contains("\"visibilityRequested\":false");
        assertThat(objects[0]).contains("\"visibilityApproved\":false");
        assertThat(objects[0]).contains("\"administrator\":false");
        assertThat(objects[0]).contains("\"seegmember\":false");
        assertThat(objects[0]).contains("\"diseaseInterestNames\":[]");
        assertThat(objects[0]).contains("\"diseaseOccurrenceReviewCount\":666");
        assertThat(objects[0]).contains("\"adminUnitReviewCount\":987");
        assertThat(objects[0]).contains("\"lastReviewDate\":\"2015-03-11T16:44:25.426Z\"");

        assertThat(objects[1]).contains("\"id\":543");
        assertThat(objects[1]).contains("\"weighting\":0.0");
        assertThat(objects[1]).contains("\"visibilityRequested\":false");
        assertThat(objects[1]).contains("\"visibilityApproved\":false");
        assertThat(objects[1]).contains("\"administrator\":false");
        assertThat(objects[1]).contains("\"seegmember\":false");
        assertThat(objects[1]).contains("\"diseaseInterestNames\":[]");
        assertThat(objects[1]).contains("\"diseaseOccurrenceReviewCount\":111");
        assertThat(objects[1]).contains("\"adminUnitReviewCount\":222");
        assertThat(objects[1]).contains("\"lastReviewDate\":\"2015-03-11T17:44:25.426Z\"");
    }

    @Test
    public void submitPageValidatesAllExpertsAndReturnsBadRequestForFailure() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsControllerHelper helper = mock(AdminExpertsControllerHelper.class);

        AdminExpertsControllerValidator validator = mock(AdminExpertsControllerValidator.class);
        AdminExpertsController target = new AdminExpertsController(expertService, null, validator, helper);

        JsonExpertFull validExpert = mock(JsonExpertFull.class);
        JsonExpertFull invalidExpert = mock(JsonExpertFull.class);
        List<JsonExpertFull> experts = Arrays.asList(validExpert, invalidExpert);
        when(validator.validate(validExpert)).thenReturn(new ArrayList<String>());
        when(validator.validate(validExpert)).thenReturn(Arrays.asList("Fail1", "Fail2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        verify(validator).validate(validExpert);
        verify(validator).validate(invalidExpert);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("Fail1", "Fail2");
    }

    @Test
    public void submitPagePassesValidExpertSetsToTransactionHelperMethodAndReturnsNoContent() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsControllerHelper helper = mock(AdminExpertsControllerHelper.class);

        AdminExpertsController target = new AdminExpertsController(expertService, null, mock(AdminExpertsControllerValidator.class), helper);

        JsonExpertFull validExpert = mock(JsonExpertFull.class);
        List<JsonExpertFull> experts = Arrays.asList(validExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        verify(helper).processExpertsAsTransaction(experts);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void submitPageReturnsBadRequestForValidationExceptionInHelper() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsControllerHelper helper = mock(AdminExpertsControllerHelper.class);
        List<String> expectedFailures = Arrays.asList("Expected");
        doThrow(new ValidationException(expectedFailures))
                .when(helper).processExpertsAsTransaction(anyCollectionOf(JsonExpertFull.class));

        AdminExpertsController target = new AdminExpertsController(expertService, null, mock(AdminExpertsControllerValidator.class), helper);

        JsonExpertFull helperIssueExpert = mock(JsonExpertFull.class);
        List<JsonExpertFull> experts = Arrays.asList(helperIssueExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(expectedFailures);
    }
}
