package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseExtentFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

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
    public void showTabReturnsDataValidationPage() {
        // Arrange
        DataValidationController target = createTarget();

        // Act
        String result = target.showTab();

        // Assert
        assertThat(result).isEqualTo("datavalidation");
    }

    @Test
    public void showPageReturnsDataValidationContentPageWithModelData() {
        // Arrange
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        String result = target.showPage(model);

        // Assert
        assertThat(result).isEqualTo("datavalidationcontent");
    }

    @Test
    public void showPageReturnsDataModelForLoggedInUser() {
        // Arrange
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        String result = target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("diseaseInterests", new ArrayList<>());
        verify(model, times(1)).addAttribute("allOtherDiseases", new ArrayList<>());
        verify(model, times(1)).addAttribute("validatorDiseaseGroupMap", new HashMap<>());
        verify(model, times(1)).addAttribute("userLoggedIn", true);
        verify(model, times(1)).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model, times(1)).addAttribute("adminUnitReviewCount", 0);
    }

    @Test
    public void showPageReturnsDataModelForAnonymousUser() {
        // Arrange
        setupAnonymousUser();
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        String result = target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("userLoggedIn", false);
        verify(model, times(1)).addAttribute("reviewCount", 0);
        verify(model, times(1)).addAttribute("defaultValidatorDiseaseGroupName", "dengue");
        verify(model, times(1)).addAttribute("defaultDiseaseGroupShortName", "dengue");
    }

    private DataValidationController createTarget() {
        CurrentUserService currentUserService = new CurrentUserServiceImpl();
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        return new DataValidationController(currentUserService, diseaseService, expertService);
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
    public void getDiseaseExtentForDiseaseGroupFailsForInvalidDiseaseGroup() throws Exception {
        // Arrange
        CurrentUserService currentUserService = new CurrentUserServiceImpl();
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        DataValidationController target = new DataValidationController(currentUserService, diseaseService, expertService);
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(anyInt())).thenThrow(new IllegalArgumentException());

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(0);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsCorrectData() {
        // Arrange
        Integer diseaseGroupId = 22;
        CurrentUserService currentUserService = new CurrentUserServiceImpl();
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        DataValidationController target = new DataValidationController(currentUserService, diseaseService, expertService);

        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(diseaseExtent);

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSameSizeAs(diseaseExtent);
    }

    private List<AdminUnitDiseaseExtentClass> createDiseaseExtent() {
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                createAdminUnitGlobal(), new DiseaseGroup(), new DiseaseExtentClass(DiseaseExtentClass.PRESENCE), 0);
        return Arrays.asList(adminUnitDiseaseExtentClass);
    }

    private AdminUnitGlobal createAdminUnitGlobal() {
        AdminUnitGlobal adminUnitGlobal = new AdminUnitGlobal();
        adminUnitGlobal.setGaulCode(1);
        adminUnitGlobal.setPublicName("AUG");
        adminUnitGlobal.setLevel('1');
        adminUnitGlobal.setSimplifiedGeom(createMultiPolygon());
        return adminUnitGlobal;
    }

    private MultiPolygon createMultiPolygon() {
        Polygon polygon = GeometryUtils.createPolygon(1, 1, 2, 2, 3, 3, 1, 1);
        return GeometryUtils.createMultiPolygon(polygon);
    }

    @Test
    public void submitReviewReturnsHttpNoContentForValidInputs() {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ExpertService expertService = mock(ExpertService.class);
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(true);
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
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(false);
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
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(false);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(true);

        DataValidationController target = new DataValidationController(new CurrentUserServiceImpl(), diseaseService,
                expertService);

        // Act
        ResponseEntity result = target.submitReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
