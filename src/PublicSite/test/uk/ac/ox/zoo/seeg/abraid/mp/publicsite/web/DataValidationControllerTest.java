package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.joda.time.DateTime;
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

import java.util.*;

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
        verify(model, times(1)).addAttribute("userSeeg", false);
        verify(model, times(1)).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model, times(1)).addAttribute("adminUnitReviewCount", 0);
    }

    @Test
    public void showPageReturnsDataModelForLoggedInSeegUser() {
        // Arrange
        Model model = mock(Model.class);
        ExpertService expertService = createExpertService();
        when(expertService.getExpertById(1).isSeegMember()).thenReturn(true);
        DataValidationController target = createTarget(null, null, expertService);

        // Act
        String result = target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("userSeeg", true);
    }

    @Test
    public void diseaseInterestsAndAllOtherDiseasesAddedToDataModelInAlphabeticalOrder() {
        // Arrange
        Model model = mock(Model.class);

        ValidatorDiseaseGroup a = new ValidatorDiseaseGroup("a");
        ValidatorDiseaseGroup b = new ValidatorDiseaseGroup("b");
        ValidatorDiseaseGroup c = new ValidatorDiseaseGroup("c");
        ValidatorDiseaseGroup d = new ValidatorDiseaseGroup("d");
        ValidatorDiseaseGroup e = new ValidatorDiseaseGroup("e");

        List<ValidatorDiseaseGroup> diseaseInterests = createList(d, b);
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = createList(c, b, a, e, d);

        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(validatorDiseaseGroups);

        ExpertService expertService = mock(ExpertService.class);
        when(expertService.getExpertById(anyInt())).thenReturn(new Expert());
        when(expertService.getDiseaseInterests(anyInt())).thenReturn(diseaseInterests);

        // Act
        createTarget(null, diseaseService, expertService).showPage(model);

        // Assert
        verify(model).addAttribute("diseaseInterests", Arrays.asList(b, d));
        verify(model).addAttribute("allOtherDiseases", Arrays.asList(a, c, e));
    }

    private List<ValidatorDiseaseGroup> createList(ValidatorDiseaseGroup... groups) {
        List<ValidatorDiseaseGroup> list = new ArrayList<>();
        Collections.addAll(list, groups);
        return list;
    }

    @Test
    public void showPageReturnsValidatorDiseaseGroups() {
        // Arrange
        Model model = mock(Model.class);
        ExpertService expertService = createExpertService();
        when(expertService.getExpertById(1).isSeegMember()).thenReturn(false);
        DiseaseService diseaseService = createDiseaseService();
        HashMap<String, List<DiseaseGroup>> result = new HashMap<>();
        when(diseaseService.getValidatorDiseaseGroupMap()).thenReturn(result);
        DataValidationController target = createTarget(null, diseaseService, expertService);

        result.put("foo", Arrays.asList(mock(DiseaseGroup.class)));

        // Act
        target.showPage(model);

        // Assert
        verify(model, times(1)).addAttribute("validatorDiseaseGroupMap", result);
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
        verify(model, times(1)).addAttribute("userSeeg", false);
        verify(model, times(1)).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model, times(1)).addAttribute("adminUnitReviewCount", 0);
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserReturnsAllOccurrencesForSeegUser() throws Exception {
        // Arrange
        ExpertService expertService = createExpertService(true);
        List<DiseaseOccurrence> occurrences = mockOccurrences();
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, true, 1)).thenReturn(occurrences);

        DataValidationController target = createTarget(null, null, expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(1);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSameSizeAs(occurrences);
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserReturnsCorrectOccurrenceForExternalUser() throws Exception {
        // Arrange
        ExpertService expertService = createExpertService(false);
        DiseaseOccurrence occurrence = mockOccurrenceOfDiseaseGroupWithAutomaticModelRunsEnabled();
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, false, 1)).thenReturn(Arrays.asList(occurrence));

        DataValidationController target = createTarget(null, null, expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(1);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSize(1);
    }

    private List<DiseaseOccurrence> mockOccurrences() {
        // Occurrence of disease group where automatic model runs are not enabled.
        DiseaseOccurrence o1 = AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence();
        when(o1.getId()).thenReturn(1);

        DiseaseOccurrence o2 = mockOccurrenceOfDiseaseGroupWithAutomaticModelRunsEnabled();
        when(o2.getId()).thenReturn(2);

        return Arrays.asList(o1, o2);
    }

    private DiseaseOccurrence mockOccurrenceOfDiseaseGroupWithAutomaticModelRunsEnabled() {
        DiseaseOccurrence occurrence = AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence();

        DiseaseGroup diseaseGroup = AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseGroup();
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);
        when(occurrence.getDiseaseGroup()).thenReturn(diseaseGroup);

        return occurrence;
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserFailsForInvalidDisease() throws Exception {
        // Arrange
        ExpertService expertService = createExpertService();
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, false, 1)).thenThrow(new IllegalArgumentException());

        DataValidationController target = createTarget(null, null, expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(1);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsExtentToNonSeegUserIfAutomaticModelRunsEnabled() {
        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();
        testGetDiseaseExtent(false, true, diseaseExtent, diseaseExtent);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsExtentToSeegUserIfAutomaticModelRunsEnabled() {
        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();
        testGetDiseaseExtent(true, true, diseaseExtent, diseaseExtent);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsEmptyExtentToNonSeegUserIfDuringDiseaseSetUp() {
        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();
        testGetDiseaseExtent(false, false, diseaseExtent, null);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsExtentToSeegUserIfDuringDiseaseSetUp() {
        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();
        testGetDiseaseExtent(true, false, diseaseExtent, diseaseExtent);
    }

    private void testGetDiseaseExtent(boolean userIsSeeg, boolean automaticModelRunsEnabled,
                                      List<AdminUnitDiseaseExtentClass> diseaseExtent, List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent) {
        Integer diseaseGroupId = 22;
        ExpertService expertService = createExpertService(userIsSeeg);

        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(diseaseGroupId).isAutomaticModelRunsEnabled()).thenReturn(automaticModelRunsEnabled);
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(diseaseExtent);

        DataValidationController target = createTarget(null, diseaseService, expertService);

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        if (expectedDiseaseExtent != null) {
            assertThat(result.getBody().getFeatures()).hasSameSizeAs(expectedDiseaseExtent);
        } else {
            assertThat(result.getBody().getFeatures()).isNull();
        }
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
    public void submitDiseaseOccurrenceReviewReturnsForbiddenForNonSeegUserIfDuringDiseaseSetUp() {
        // Arrange
        DiseaseService diseaseService = createDiseaseServiceWithOccurrence();

        ExpertService expertService = createExpertService();
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        DataValidationController target = createTarget(null, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpNoContentForValidInputsForSeegUser() {
        // Arrange
        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(true);

        ExpertService expertService = createExpertService(true);
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
        DiseaseService diseaseService = createDiseaseServiceWithOccurrence();
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(false);

        ExpertService expertService = createExpertService(true);
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
        DiseaseService diseaseService = createDiseaseServiceWithOccurrence();
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(false);

        ExpertService expertService = createExpertService(true);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(true);

        DataValidationController target = createTarget(null, diseaseService, expertService);
        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(1, 1, "YES");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitAdminUnitReviewAcceptsNullReview() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());

        DiseaseService diseaseService = createDiseaseService();
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        DataValidationController target = createTarget(null, diseaseService, null);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseGroupId, 2, null);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
        when(currentUserService.getCurrentUser().getId()).thenReturn(1);

        ExpertService expertService = createExpertService();

        DataValidationController target = createTarget(currentUserService, diseaseService, expertService);

        // Act
        ResponseEntity result = target.submitAdminUnitReview(1, 2, "PRESENCE");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService, times(1)).saveAdminUnitReview(1, 1, 2, diseaseExtentClass);
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

    private DiseaseService createDiseaseServiceWithOccurrence() {
        DiseaseService diseaseService = mock(DiseaseService.class);
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);

        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(false);
        when(occurrence.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseOccurrenceById(anyInt())).thenReturn(occurrence);
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(true);
        return diseaseService;
    }

    private ExpertService createExpertService() {
        return createExpertService(false);
    }

    private ExpertService createExpertService(boolean userIsSeeg) {
        ExpertService returnedExpertService = mock(ExpertService.class);
        Expert returnedExpert = mock(Expert.class);
        when(returnedExpert.isSeegMember()).thenReturn(userIsSeeg);
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
