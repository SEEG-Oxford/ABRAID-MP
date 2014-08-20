package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseExtentFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for the DataValidationController.
 * Copyright (c) 2014 University of Oxford
 */
public class DataValidationControllerTest {

    @Test
    public void showTabReturnsDataValidationPage() {
        // Arrange
        DataValidationController target = createTarget();

        // Act
        String result = target.showTab();

        // Assert
        assertThat(result).isEqualTo("datavalidation/index");
    }

    @Test
    public void showPageReturnsDataValidationContentPageWithModelData() {
        // Arrange
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        String result = target.showPage(model);

        // Assert
        assertThat(result).isEqualTo("datavalidation/content");
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
    public void showPageReturnsReducedValidatorDiseaseGroupsForNonSeegMember() {
        // Arrange
        Model model = mock(Model.class);
        ExpertService expertService = createExpertService();
        when(expertService.getExpertById(1).isSeegMember()).thenReturn(false);
        DiseaseService diseaseService = createDiseaseService();
        HashMap<String, List<DiseaseGroup>> correctResult = new HashMap<>();
        when(diseaseService.getValidatorDiseaseGroupMap(false)).thenReturn(correctResult);
        HashMap<String, List<DiseaseGroup>> incorrectResult = new HashMap<>();
        when(diseaseService.getValidatorDiseaseGroupMap(true)).thenReturn(incorrectResult);
        DataValidationController target = createTarget(null, diseaseService, expertService);

        correctResult.put("foo", Arrays.asList(mock(DiseaseGroup.class)));
        incorrectResult.put("bar", Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class)));

        // Act
        target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("validatorDiseaseGroupMap", correctResult);
    }

    @Test
    public void showPageReturnsFullValidatorDiseaseGroupsForSeegMember() {
        // Arrange
        Model model = mock(Model.class);
        ExpertService expertService = createExpertService();
        when(expertService.getExpertById(1).isSeegMember()).thenReturn(true);
        DiseaseService diseaseService = createDiseaseService();
        HashMap<String, List<DiseaseGroup>> incorrectResult = new HashMap<>();
        when(diseaseService.getValidatorDiseaseGroupMap(false)).thenReturn(incorrectResult);
        HashMap<String, List<DiseaseGroup>> correctResult = new HashMap<>();
        when(diseaseService.getValidatorDiseaseGroupMap(true)).thenReturn(correctResult);
        DataValidationController target = createTarget(null, diseaseService, expertService);

        incorrectResult.put("foo", Arrays.asList(mock(DiseaseGroup.class)));
        correctResult.put("bar", Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class)));

        // Act
        target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("validatorDiseaseGroupMap", correctResult);
    }

    @Test
    public void showPageReturnsDataModelForAnonymousUser() {
        // Arrange
        CurrentUserService currentUserService = createCurrentUserService();
        when(currentUserService.getCurrentUser()).thenReturn(null);

        Model model = mock(Model.class);

        DataValidationController target = createTarget(currentUserService, null, null);

        // Act
        String result = target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("defaultValidatorDiseaseGroupName", "dengue");
        verify(model, times(1)).addAttribute("defaultDiseaseGroupShortName", "dengue");
        verify(model, times(1)).addAttribute("userLoggedIn", false);
        verify(model, times(1)).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model, times(1)).addAttribute("adminUnitReviewCount", 0);
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserReturnsCorrectData() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());

        ExpertService expertService = createExpertService();
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, 1)).thenReturn(occurrences);

        DataValidationController target = createTarget(null, null, expertService);

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
        ExpertService expertService = createExpertService();
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, 1)).thenThrow(new IllegalArgumentException());

        DataValidationController target = createTarget(null, null, expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(1);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupFailsForInvalidDiseaseGroup() throws Exception {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(anyInt())).thenThrow(new IllegalArgumentException());

        DataValidationController target = createTarget(null, diseaseService, null);

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(0);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsCorrectData() {
        // Arrange
        Integer diseaseGroupId = 22;
        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();

        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(diseaseGroupId).isAutomaticModelRunsEnabled()).thenReturn(true);
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(diseaseExtent);

        DataValidationController target = createTarget(null, diseaseService, null);

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSameSizeAs(diseaseExtent);
    }

    @Test
    public void submitReviewReturnsHttpNoContentForValidInputs() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(true);

        ExpertService expertService = createExpertService();
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        DataValidationController target = createTarget(null, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void submitReviewReturnsHttpBadRequestForInvalidInputOccurrenceDoesNotMatchDisease() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(false);

        ExpertService expertService = createExpertService();
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        DataValidationController target = createTarget(null, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitReviewReturnsHttpBadRequestForInvalidReviewAlreadyExists() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(false);

        ExpertService expertService = createExpertService();
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(true);

        DataValidationController target = createTarget(null, diseaseService, expertService);
        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void submitAdminUnitReviewReturnsHttpNoContentForValidInputs() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);
        DiseaseExtentClass diseaseExtentClass = mock(DiseaseExtentClass.class);
        when(diseaseService.getDiseaseExtentClass("PRESENCE")).thenReturn(diseaseExtentClass);

        CurrentUserService currentUserService = createCurrentUserService();
        when(currentUserService.getCurrentUser().getUsername()).thenReturn("foo");

        ExpertService expertService = createExpertService();

        DataValidationController target = createTarget(currentUserService, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 2, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService, times(1)).saveNewAdminUnitReview("foo", 1, 2, diseaseExtentClass);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForInvalidDisease() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(null);

        DataValidationController target = createTarget(null, diseaseService, null);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 1, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpForbiddenForNonSeegMemberReviewingNotAutomaticDisease() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(false);

        DataValidationController target = createTarget(null, diseaseService, null);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 1, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpNoContentForSeegMemberReviewingNotAutomaticDisease() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);
        when(diseaseService.getDiseaseExtentClass("PRESENCE")).thenReturn(mock(DiseaseExtentClass.class));

        ExpertService expertService = createExpertService();
        when(expertService.getExpertById(1).isSeegMember()).thenReturn(true);

        DataValidationController target = createTarget(null, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 1, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForInvalidReview() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);

        when(diseaseService.getDiseaseExtentClass("PRESENCE")).thenReturn(null);

        DataValidationController target = createTarget(null, diseaseService, null);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 1, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpNoContentForValidInputsWhenUpdatingExistingReview() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);
        DiseaseExtentClass diseaseExtentClass = mock(DiseaseExtentClass.class);
        when(diseaseService.getDiseaseExtentClass("PRESENCE")).thenReturn(diseaseExtentClass);

        ExpertService expertService = createExpertService();
        AdminUnitReview adminUnitReview = mock(AdminUnitReview.class);
        when(expertService.getAdminUnitReview(1, 1, 1)).thenReturn(adminUnitReview);

        DataValidationController target = createTarget(null, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 1, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(expertService, times(1)).updateExistingAdminUnitReview(adminUnitReview, diseaseExtentClass);
    }

    private DataValidationController createTarget(CurrentUserService currentUserService, DiseaseService diseaseService, ExpertService expertService) {
        return new DataValidationController(
                currentUserService == null ? createCurrentUserService() : currentUserService,
                diseaseService == null ? createDiseaseService() : diseaseService,
                expertService == null ? createExpertService() : expertService);
    }

    private DataValidationController createTarget() {
        return createTarget(null, null, null);
    }

    private CurrentUserService createCurrentUserService() {
        CurrentUserService currentUserService = mock(CurrentUserService.class);
        PublicSiteUser returnedUser = mock(PublicSiteUser.class);
        when(returnedUser.getId()).thenReturn(1);
        when(currentUserService.getCurrentUser()).thenReturn(returnedUser);
        return currentUserService;
    }

    private DiseaseService createDiseaseService() {
        return mock(DiseaseService.class);
    }

    private ExpertService createExpertService() {
        ExpertService returnedExpertService = mock(ExpertService.class);
        Expert returnedExpert = mock(Expert.class);
        when(returnedExpertService.getExpertById(1)).thenReturn(returnedExpert);
        return returnedExpertService;
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
}
