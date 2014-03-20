package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.AbstractDiseaseOccurrenceGeoJsonTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
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
}
