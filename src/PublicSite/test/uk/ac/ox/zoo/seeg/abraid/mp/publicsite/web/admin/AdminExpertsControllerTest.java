package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertFull;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();

        AdminExpertsController target = new AdminExpertsController(expertService, objectMapper, null);

        // Act
        String result = target.showPage(mock(Model.class));

        // Assert
        assertThat(result).isEqualTo("admin/experts");
    }

    @Test
    public void showPageAddsAllExpertsToTheFreemarkerModelAsJson() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        List<Expert> experts = Arrays.asList(mock(Expert.class), mock(Expert.class));
        when(expertService.getAllExperts()).thenReturn(experts);
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();

        AdminExpertsController target = new AdminExpertsController(expertService, objectMapper, null);
        Model model = mock(Model.class);

        // Act
        String result = target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute(eq("experts"),
                eq("[{\"visibilityRequested\":false,\"id\":0,\"weighting\":0.0,\"visibilityApproved\":false," +
                "\"administrator\":false,\"seegmember\":false},{\"visibilityRequested\":false,\"id\":0,\"weighting\":" +
                "0.0,\"visibilityApproved\":false,\"administrator\":false,\"seegmember\":false}]"));
    }

    @Test
    public void submitPageReturnsBadRequestForInvalidExpertsDueToNulls() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper helper = mock(AdminExpertsHelper.class);

        AdminExpertsController target = new AdminExpertsController(expertService, null, helper);

        JsonExpertFull validExpert = mock(JsonExpertFull.class);
        JsonExpertFull invalidExpert = mock(JsonExpertFull.class);
        when(invalidExpert.getId()).thenReturn(null);
        when(invalidExpert.getVisibilityApproved()).thenReturn(null);
        when(invalidExpert.getWeighting()).thenReturn(null);
        when(invalidExpert.isAdministrator()).thenReturn(null);
        when(invalidExpert.isSEEGMember()).thenReturn(null);
        List<JsonExpertFull> experts = Arrays.asList(validExpert, invalidExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).hasSize(5);
    }

    @Test
    public void submitPageReturnsBadRequestForInvalidExpertsDueToNaNWeighting() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper helper = mock(AdminExpertsHelper.class);

        AdminExpertsController target = new AdminExpertsController(expertService, null, helper);

        JsonExpertFull invalidExpert = mock(JsonExpertFull.class);
        when(invalidExpert.getWeighting()).thenReturn(Double.NaN);
        List<JsonExpertFull> experts = Arrays.asList(invalidExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    public void submitPageReturnsBadRequestForInvalidExpertsDueToInfWeighting() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper helper = mock(AdminExpertsHelper.class);

        AdminExpertsController target = new AdminExpertsController(expertService, null, helper);

        JsonExpertFull invalidExpert = mock(JsonExpertFull.class);
        when(invalidExpert.getWeighting()).thenReturn(Double.POSITIVE_INFINITY);
        List<JsonExpertFull> experts = Arrays.asList(invalidExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    public void submitPagePassesValidExpertSetsToTransactionHelperMethodAndReturnsNoContent() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper helper = mock(AdminExpertsHelper.class);

        AdminExpertsController target = new AdminExpertsController(expertService, null, helper);

        JsonExpertFull validExpert = mock(JsonExpertFull.class);
        List<JsonExpertFull> experts = Arrays.asList(validExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        verify(helper, times(1)).processExpertsAsTransaction(experts);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void submitPageReturnsBadRequestForValidationExceptionInHelper() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        AdminExpertsHelper helper = mock(AdminExpertsHelper.class);
        List<String> expectedFailures = Arrays.asList("Expected");
        doThrow(new ValidationException(expectedFailures))
                .when(helper).processExpertsAsTransaction(anyCollectionOf(JsonExpertFull.class));

        AdminExpertsController target = new AdminExpertsController(expertService, null, helper);

        JsonExpertFull helperIssueExpert = mock(JsonExpertFull.class);
        List<JsonExpertFull> experts = Arrays.asList(helperIssueExpert);

        // Act
        ResponseEntity<Collection<String>> result = target.submitPage(experts);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(expectedFailures);
    }
}
