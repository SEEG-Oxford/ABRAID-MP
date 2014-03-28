package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the DataValidationController.
 * Copyright (c) 2014 University of Oxford
 */
public class DataValidationControllerTest extends AbstractAuthenticatingTests {
    @Before
    public void setupUser() {
        PublicSiteUser user = mock(PublicSiteUser.class);
        setupSecurityContext();
        setupCurrentUser(user);
        when(user.getId()).thenReturn(1);
    }

    @Test
    public void showPageReturnsDataValidationPageAndDiseaseInterestsSet() {
        // Arrange
        CurrentUserService currentUserService = new CurrentUserServiceImpl();
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        Model model = mock(Model.class);

        DataValidationController target = new DataValidationController(currentUserService, diseaseService,
                expertService);

        // Act
        String result = target.showPage(model);

        // Assert
        assertThat(result).isEqualTo("datavalidation");
        assertThat(model.containsAttribute("diseaseInterestsSet"));
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserReturnsCorrectData() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());
        when(expertService.getDiseaseOccurrencesYetToBeReviewed(1, 1)).thenReturn(occurrences);

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(1);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSameSizeAs(occurrences);
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserFailsForInvalidDisease() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        when(expertService.getDiseaseOccurrencesYetToBeReviewed(1, 1)).thenThrow(new IllegalArgumentException());

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(1);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
     public void submitReviewReturnsHttpNoContentForValidInputs() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        when(diseaseService.doesDiseaseOccurrenceMatchDiseaseGroup(anyInt(), anyInt())).thenReturn(true);
        when(expertService.isDiseaseGroupInExpertsDiseaseInterests(anyInt(), anyInt())).thenReturn(true);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity result = target.submitReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void submitReviewReturnsHttpBadRequestForInvalidInputOccurrenceDoesNotMatchDisease() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        when(diseaseService.doesDiseaseOccurrenceMatchDiseaseGroup(anyInt(), anyInt())).thenReturn(false);
        when(expertService.isDiseaseGroupInExpertsDiseaseInterests(anyInt(), anyInt())).thenReturn(true);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity result = target.submitReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitReviewReturnsHttpBadRequestForInvalidInputDiseaseNotAnExpertsInterest() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        when(diseaseService.doesDiseaseOccurrenceMatchDiseaseGroup(anyInt(), anyInt())).thenReturn(true);
        when(expertService.isDiseaseGroupInExpertsDiseaseInterests(anyInt(), anyInt())).thenReturn(false);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity result = target.submitReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitReviewReturnsHttpBadRequestForInvalidReviewAlreadyExists() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        when(diseaseService.doesDiseaseOccurrenceMatchDiseaseGroup(anyInt(), anyInt())).thenReturn(false);
        when(expertService.isDiseaseGroupInExpertsDiseaseInterests(anyInt(), anyInt())).thenReturn(false);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(true);

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity result = target.submitReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
