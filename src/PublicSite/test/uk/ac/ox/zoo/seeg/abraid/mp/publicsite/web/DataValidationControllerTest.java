package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseExtentFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

/**
 * Tests for the DataValidationController.
 * Copyright (c) 2014 University of Oxford
 */
public class DataValidationControllerTest {
    private ValidatorDiseaseGroup validatorDiseaseGroup = null;
    private DiseaseOccurrence diseaseOccurrence = null;
    private DiseaseGroup diseaseGroup = null;
    private DiseaseService diseaseService = null;
    private ExpertService expertService = null;
    private CurrentUserService currentUserService = null;
    private Expert expert = null;
    private GeometryService geometryService = null;
    private DiseaseExtentClass diseaseExtentClass = null;

    @After
    public void cleanUp() {
        validatorDiseaseGroup = null;
        diseaseOccurrence = null;
        diseaseGroup = null;
        diseaseService = null;
        expertService = null;
        currentUserService = null;
        expert = null;
        geometryService = null;
        diseaseExtentClass = null;
    }

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
    public void showHelpReturnsDataValidationHelpPage() {
        // Arrange
        DataValidationController target = createTarget();

        // Act
        String result = target.showHelp();

        // Assert
        assertThat(result).isEqualTo("datavalidation/help");
    }

    @Test
    public void showPageReturnsDataValidationContentPageWithModelData() {
        // Arrange
        wireUpAnonmousUser();
        wireUpValidatorDiseaseGroups();
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        String result = target.showPage(model);

        // Assert
        assertThat(result).isEqualTo("datavalidation/content");
    }

    @Test
    public void showPageReturnsDataModelForAnonymousUser() {
        // Arrange
        wireUpAnonmousUser();
        diseaseService = mock(DiseaseService.class);

        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("userLoggedIn", false);
        verify(model).addAttribute("userSeeg", false);
        verify(model).addAttribute("showHelpText", false);
        verify(model).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model).addAttribute("adminUnitReviewCount", 0);
        verify(model).addAttribute("diseasesRequiringExtentInput", new ArrayList<Integer>());
        verify(model).addAttribute("diseasesRequiringOccurrenceInput", new ArrayList<Integer>());

        verify(diseaseService, never()).getDiseaseGroupsNeedingExtentReviewByExpert(expert);
        verify(diseaseService, never()).getDiseaseGroupsNeedingOccurrenceReviewByExpert(expert);

        verify(model, never()).addAttribute("diseaseInterests", new ArrayList<>());
        verify(model, never()).addAttribute("allOtherDiseases", new ArrayList<>());
        verify(model, never()).addAttribute("validatorDiseaseGroupMap", new HashMap<>());

        verify(model).addAttribute("defaultValidatorDiseaseGroupName", "dengue");
        verify(model).addAttribute("defaultDiseaseGroupShortName", "dengue");
    }

    @Test
    public void showPageReturnsDataModelForLoggedInNonSeegUser() {
        // Arrange
        boolean isSeeg = false;
        wireUpExpert(1, isSeeg, true);
        wireUpValidatorDiseaseGroups();
        wireUpDiseasesNeedingExtentReview(expert, diseaseService, 1, 3, 4, 5);
        wireUpDiseasesNeedingOccurrenceReview(expert, diseaseService, 1, 3, 2, 5);
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("userLoggedIn", true);
        verify(model).addAttribute("userSeeg", isSeeg);
        verify(model).addAttribute("showHelpText", false);
        verify(model).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model).addAttribute("adminUnitReviewCount", 0);
        verify(model).addAttribute("diseasesRequiringExtentInput", Arrays.asList(1, 3, 4, 5));
        verify(model).addAttribute("diseasesRequiringOccurrenceInput", Arrays.asList(1, 3, 2, 5));

        verify(model).addAttribute("diseaseInterests", new ArrayList<>());
        verify(model).addAttribute("allOtherDiseases", new ArrayList<>());
        verify(model).addAttribute("validatorDiseaseGroupMap", new HashMap<>());

        verify(model, never()).addAttribute("defaultValidatorDiseaseGroupName", "dengue");
        verify(model, never()).addAttribute("defaultDiseaseGroupShortName", "dengue");
    }

    @Test
    public void showPageReturnsDataModelForLoggedInSeegUser() {
        // Arrange
        boolean isSeeg = true;
        wireUpExpert(1, isSeeg, true);
        wireUpValidatorDiseaseGroups();
        wireUpDiseasesNeedingExtentReview(expert, diseaseService, 1, 3, 4, 5);
        wireUpDiseasesNeedingOccurrenceReview(expert, diseaseService, 1, 3, 2, 5);
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("userLoggedIn", true);
        verify(model).addAttribute("userSeeg", isSeeg);
        verify(model).addAttribute("showHelpText", false);
        verify(model).addAttribute("diseaseOccurrenceReviewCount", 0);
        verify(model).addAttribute("adminUnitReviewCount", 0);
        verify(model).addAttribute("diseasesRequiringExtentInput", Arrays.asList(1, 3, 4, 5));
        verify(model).addAttribute("diseasesRequiringOccurrenceInput", Arrays.asList(1, 3, 2, 5));

        verify(model).addAttribute("diseaseInterests", new ArrayList<>());
        verify(model).addAttribute("allOtherDiseases", new ArrayList<>());
        verify(model).addAttribute("validatorDiseaseGroupMap", new HashMap<>());

        verify(model, never()).addAttribute("defaultValidatorDiseaseGroupName", "dengue");
        verify(model, never()).addAttribute("defaultDiseaseGroupShortName", "dengue");
    }

    @Test
    public void showPageIndicatesToShowHelpTextToLoggedInUserOnce() {
        // Arrange
        boolean seenHelp = false;
        wireUpExpert(1, false, seenHelp);
        wireUpValidatorDiseaseGroups();
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("showHelpText", true);
        verify(expert).setHasSeenHelpText(true);
        verify(expertService).saveExpert(expert);
    }

    @Test
    public void showPageIndicatesNotToShowHelpTextToLoggedInUserWhenUserHasAlreadySeenHelpText() {
        // Arrange
        boolean seenHelp = true;
        wireUpExpert(1, false, seenHelp);
        wireUpValidatorDiseaseGroups();
        Model model = mock(Model.class);
        DataValidationController target = createTarget();

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("showHelpText", false);
    }

    @Test
    public void diseaseInterestsAndAllOtherDiseasesAddedToDataModelInCaseInsensitiveAlphabeticalOrder() {
        // Arrange
        Model model = mock(Model.class);

        ValidatorDiseaseGroup a = new ValidatorDiseaseGroup("a");
        ValidatorDiseaseGroup b = new ValidatorDiseaseGroup("b");
        ValidatorDiseaseGroup c = new ValidatorDiseaseGroup("C");
        ValidatorDiseaseGroup d = new ValidatorDiseaseGroup("D");
        ValidatorDiseaseGroup e = new ValidatorDiseaseGroup("E");

        List<ValidatorDiseaseGroup> diseaseInterests = Arrays.asList(d, b);
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = new ArrayList<>();
        validatorDiseaseGroups.addAll(Arrays.asList(c, b, a, e, d));

        wireUpExpert(1, false, true);
        when(expertService.getDiseaseInterests(1)).thenReturn(diseaseInterests);
        diseaseService = mockIfNull(diseaseService, DiseaseService.class);
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(validatorDiseaseGroups);

        // Act
        createTarget().showPage(model);

        // Assert
        verify(model).addAttribute("diseaseInterests", Arrays.asList(b, d));
        verify(model).addAttribute("allOtherDiseases", Arrays.asList(a, c, e));
    }

    @Test
    public void showPageReturnsValidatorDiseaseGroups() {
        // Arrange
        wireUpExpert(1, false, true);
        wireUpValidatorDiseaseGroups();
        Model model = mock(Model.class);

        HashMap<String, List<DiseaseGroup>> expectation = new HashMap<>();
        expectation.put("foo", Arrays.asList(mock(DiseaseGroup.class)));
        when(diseaseService.getValidatorDiseaseGroupMap()).thenReturn(expectation);

        DataValidationController target = createTarget();

        // Act
        target.showPage(model);

        // Assert
        verify(model).addAttribute("validatorDiseaseGroupMap", expectation);
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserReturnsCorrectOccurrences() throws Exception {
        // Arrange
        int expertId = 1;
        boolean userIsSeeg = false;
        int validatorDiseaseGroupId = 2;
        wireUpExpert(expertId, userIsSeeg, true);

        DiseaseOccurrence o1 = AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence();
        when(o1.getId()).thenReturn(1);

        DiseaseOccurrence o2 = AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence();
        when(o2.getId()).thenReturn(2);

        List<DiseaseOccurrence> occurrences = Arrays.asList(o1, o2);
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, userIsSeeg, validatorDiseaseGroupId)).thenReturn(occurrences);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(validatorDiseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSameSizeAs(occurrences);
        assertThat(result.getBody().getFeatures().get(0).getId()).isEqualTo(1);
        assertThat(result.getBody().getFeatures().get(1).getId()).isEqualTo(2);
    }

    @Test
    public void getDiseaseOccurrencesForReviewByCurrentUserFailsForInvalidDisease() throws Exception {
        // Arrange
        Integer expertId = 1;
        boolean userIsSeeg = true;
        Integer validatorDiseaseGroupId = 2;
        wireUpExpert(expertId, userIsSeeg, false);
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, userIsSeeg, validatorDiseaseGroupId)).thenThrow(new IllegalArgumentException());

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> result =
                target.getDiseaseOccurrencesForReviewByCurrentUser(validatorDiseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpForbiddenForNonSeegUserBeforeFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = false;
        String review = "YES";

        Integer expertId = 1;
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpNoContentForSeegUserBeforeFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = true;
        boolean hasModelRunPrepOccurredForDisease = false;
        String review = "YES";

        Integer expertId = 1;
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveDiseaseOccurrenceReview(expertId, diseaseOccurrenceId, DiseaseOccurrenceReviewResponse.YES);
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpNoContentForNonSeegUserAfterFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "YES";

        Integer expertId = 1;
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveDiseaseOccurrenceReview(expertId, diseaseOccurrenceId, DiseaseOccurrenceReviewResponse.YES);
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpNoContentForSeegUserAfterFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = true;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "YES";

        Integer expertId = 1;
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveDiseaseOccurrenceReview(expertId, diseaseOccurrenceId, DiseaseOccurrenceReviewResponse.YES);
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForMissingValidatorDiseaseGroupId() {
        // Arrange
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(null, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForMissingDiseaseOccurrenceId() {
        // Arrange
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, null, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpNoContentForIDontKnowReview() {
        // Arrange - "I don't know"
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, null);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveDiseaseOccurrenceReview(1, diseaseOccurrenceId, null);
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForInvalidValidatorDiseaseGroupId() {
        // Arrange
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId + 1, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForInvalidValidatorDiseaseOccurrenceId() {
        // Arrange
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId + 1, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForInvalidReview() {
        // Arrange
        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review + "1");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForUnmatchedValidatorDiseaseGroupAndDiseaseOccurrence() {
        // Arrange
        boolean doesOccurrenceBelongToValidatorGroup = false;

        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(1, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, doesOccurrenceBelongToValidatorGroup, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void submitDiseaseOccurrenceReviewReturnsHttpBadRequestForAlreadyReviewOccurrence() {
        // Arrange
        Integer expertId = 1;

        Integer validatorDiseaseGroupId = 2;
        Integer diseaseOccurrenceId = 3;
        String review = "YES";

        wireUpExpert(expertId, false, false);
        wireUpForOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, true, true);
        wireUpExistingOccurrenceReview(expertId, diseaseOccurrenceId);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitDiseaseOccurrenceReview(validatorDiseaseGroupId, diseaseOccurrenceId, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveDiseaseOccurrenceReview(anyInt(), anyInt(), any(DiseaseOccurrenceReviewResponse.class));
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupFailsForInvalidDiseaseGroup() throws Exception {
        // Arrange
        wireUpExpert(1, false, true);
        int diseaseGroupId = 3;
        diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(null);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsCorrectEmptyForNonSeegUserAndPreModelRunDisease() {
        // Arrange
        boolean isSeeg = false;
        boolean diseaseGroupHasModelRun = false;
        int diseaseGroupId = 3;
        int expertId = 1;
        wireUpExpert(expertId, isSeeg, true);
        wireUpDiseaseExtent(diseaseGroupId, expertId, diseaseGroupHasModelRun);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSize(0);
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsCorrectContentForSeegUserAndPreModelRunDisease() {
        // Arrange
        boolean isSeeg = true;
        boolean diseaseGroupHasModelRun = false;
        int diseaseGroupId = 3;
        int expertId = 1;
        wireUpExpert(expertId, isSeeg, true);
        wireUpDiseaseExtent(diseaseGroupId, expertId, diseaseGroupHasModelRun);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSize(2);
        assertThat(result.getBody().getFeatures().get(0).getId()).isEqualTo(1);
        assertThat(result.getBody().getFeatures().get(0).getProperties().needsReview()).isFalse();
        assertThat(result.getBody().getFeatures().get(1).getId()).isEqualTo(2);
        assertThat(result.getBody().getFeatures().get(1).getProperties().needsReview()).isTrue();
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsCorrectContentForNonSeegUserAndPostModelRunDisease() {
        // Arrange
        boolean isSeeg = false;
        boolean diseaseGroupHasModelRun = true;
        int diseaseGroupId = 3;
        int expertId = 1;
        wireUpExpert(expertId, isSeeg, true);
        wireUpDiseaseExtent(diseaseGroupId, expertId, diseaseGroupHasModelRun);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSize(2);
        assertThat(result.getBody().getFeatures().get(0).getId()).isEqualTo(1);
        assertThat(result.getBody().getFeatures().get(0).getProperties().needsReview()).isFalse();
        assertThat(result.getBody().getFeatures().get(1).getId()).isEqualTo(2);
        assertThat(result.getBody().getFeatures().get(1).getProperties().needsReview()).isTrue();
    }

    @Test
    public void getDiseaseExtentForDiseaseGroupReturnsCorrectContentForSeegUserAndPostModelRunDisease() {
        // Arrange
        boolean isSeeg = true;
        boolean diseaseGroupHasModelRun = true;
        int diseaseGroupId = 3;
        int expertId = 1;
        wireUpExpert(expertId, isSeeg, true);
        wireUpDiseaseExtent(diseaseGroupId, expertId, diseaseGroupHasModelRun);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDiseaseExtentForDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSize(2);
        assertThat(result.getBody().getFeatures().get(0).getId()).isEqualTo(1);
        assertThat(result.getBody().getFeatures().get(0).getProperties().needsReview()).isFalse();
        assertThat(result.getBody().getFeatures().get(1).getId()).isEqualTo(2);
        assertThat(result.getBody().getFeatures().get(1).getProperties().needsReview()).isTrue();
    }

    @Test
    public void getDefaultDiseaseExtentReturnsExpectedResponseEntity() {
        // Arrange
        wireUpAnonmousUser();
        wireUpDiseaseExtent(87, 1, true);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity<GeoJsonDiseaseExtentFeatureCollection> result = target.getDefaultDiseaseExtent();

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getFeatures()).hasSize(2);
        assertThat(result.getBody().getFeatures().get(0).getId()).isEqualTo(1);
        assertThat(result.getBody().getFeatures().get(0).getProperties().needsReview()).isTrue();
        assertThat(result.getBody().getFeatures().get(1).getId()).isEqualTo(2);
        assertThat(result.getBody().getFeatures().get(1).getProperties().needsReview()).isTrue();
    }

    @Test
    public void getLatestDiseaseOccurrencesForAdminUnitDiseaseExtentClassForDefaultDiseaseGroupCallsMethodWithDengue() {
        // Arrange
        wireUpExpert(1, false, true);
        diseaseService = mock(DiseaseService.class);
        int defaultDiseaseGroupId = 87;
        int gaulCode = 1;
        DataValidationController spy = spy(createTarget());
        // Act
        spy.getLatestDiseaseOccurrencesForAdminUnitDiseaseExtentClassForDefaultDiseaseGroup(gaulCode);
        // Assert
        verify(spy).getLatestDiseaseOccurrencesForAdminUnitDiseaseExtentClass(defaultDiseaseGroupId, gaulCode);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpForbiddenForNonSeegUserBeforeFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = false;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpNoContentForSeegUserBeforeFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = true;
        boolean hasModelRunPrepOccurredForDisease = false;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveAdminUnitReview(expertId, diseaseId, gaulCode, diseaseExtentClass);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpNoContentForNonSeegUserAfterFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveAdminUnitReview(expertId, diseaseId, gaulCode, diseaseExtentClass);
    }

    @Test
    public void submitAdminUnitOccurrenceReviewReturnsHttpNoContentForSeegUserAfterFirstModelRunPrep() {
        // Arrange
        boolean userIsSeeg = true;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveAdminUnitReview(expertId, diseaseId, gaulCode, diseaseExtentClass);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForMissingDiseaseGroupId() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(null, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForMissingGaulCode() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, null, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpNoContentForIDontKnowReview() {
        // Arrange - I don't know
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, "ABSENCE", hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, null);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(expertService).saveAdminUnitReview(expertId, diseaseId, gaulCode, null);
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForInvalidDiseaseGroupId() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId + 1, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForInvalidGaulCode() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode + 1, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForInvalidReview() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, review + "1");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    @Test
    public void submitAdminUnitReviewReturnsHttpBadRequestForAlreadyReviewAdminUnit() {
        // Arrange
        boolean userIsSeeg = false;
        boolean hasModelRunPrepOccurredForDisease = true;
        String review = "ABSENCE";

        Integer expertId = 1;
        Integer diseaseId = 2;
        Integer gaulCode = 3;

        wireUpExpert(expertId, userIsSeeg, false);
        wireUpForExtentReview(diseaseId, gaulCode, review, hasModelRunPrepOccurredForDisease);
        wireUpExistingExtentReview(expertId, gaulCode);

        DataValidationController target = createTarget();

        // Act
        ResponseEntity result = target.submitAdminUnitReview(diseaseId, gaulCode, review);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(expertService, never()).saveAdminUnitReview(anyInt(), anyInt(), anyInt(), any(DiseaseExtentClass.class));
    }

    private DataValidationController createTarget() {
        return new DataValidationController(currentUserService, diseaseService, expertService, geometryService);
    }

    private List<AdminUnitDiseaseExtentClass> createDiseaseExtent() {
        diseaseGroup = mockIfNull(diseaseGroup, DiseaseGroup.class);

        return Arrays.asList(
                new AdminUnitDiseaseExtentClass(createAdminUnitGlobal(1), diseaseGroup, new DiseaseExtentClass(DiseaseExtentClass.PRESENCE), new DiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE), 0),
                new AdminUnitDiseaseExtentClass(createAdminUnitGlobal(2), diseaseGroup, new DiseaseExtentClass(DiseaseExtentClass.PRESENCE), new DiseaseExtentClass(DiseaseExtentClass.PRESENCE), 0)
        );
    }

    private List<AdminUnitReview> createReviews() {
        AdminUnitReview adminUnitReview = mock(AdminUnitReview.class);
        when(adminUnitReview.getAdminUnitGlobalOrTropicalGaulCode()).thenReturn(1);
        when(adminUnitReview.getCreatedDate()).thenReturn(DateTime.now());
        return Arrays.asList(adminUnitReview);
    }

    private AdminUnitGlobal createAdminUnitGlobal(int gaul) {
        AdminUnitGlobal adminUnitGlobal = new AdminUnitGlobal();
        adminUnitGlobal.setGaulCode(gaul);
        adminUnitGlobal.setPublicName("AUG");
        adminUnitGlobal.setLevel('1');
        adminUnitGlobal.setSimplifiedGeom(createMultiPolygon());
        return adminUnitGlobal;
    }

    private MultiPolygon createMultiPolygon() {
        Polygon polygon = GeometryUtils.createPolygon(1, 1, 2, 2, 3, 3, 1, 1);
        return GeometryUtils.createMultiPolygon(polygon);
    }

    private <T> T mockIfNull(T mock, Class<T> classToMock) {
        return mock == null ? mock(classToMock) : mock;
    }

    private void wireUpAnonmousUser() {
        currentUserService = mockIfNull(currentUserService, CurrentUserService.class);

        when(currentUserService.getCurrentUserId()).thenReturn(null);
    }

    private void wireUpExpert(Integer expertId, boolean isSeeg, boolean seenHelp) {
        expertService = mockIfNull(expertService, ExpertService.class);
        currentUserService = mockIfNull(currentUserService, CurrentUserService.class);
        expert = mockIfNull(expert, Expert.class);

        when(currentUserService.getCurrentUserId()).thenReturn(expertId);
        when(expertService.getExpertById(expertId)).thenReturn(expert);
        when(expert.isSeegMember()).thenReturn(isSeeg);
        when(expert.hasSeenHelpText()).thenReturn(seenHelp);
    }

    private void wireUpValidatorDiseaseGroups(ValidatorDiseaseGroup... groups) {
        diseaseService = mockIfNull(diseaseService, DiseaseService.class);

        List<ValidatorDiseaseGroup> list = new ArrayList<>();
        list.addAll(Arrays.asList(groups));

        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(list);
    }

    private void wireUpDiseasesNeedingExtentReview(Expert expert, DiseaseService diseaseService, Integer... diseaseIds) {
        diseaseService = mockIfNull(diseaseService, DiseaseService.class);

        List<DiseaseGroup> list = new ArrayList<>();
        for (Integer id : diseaseIds) {
            DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
            when(diseaseGroup.getId()).thenReturn(id);
            list.add(diseaseGroup);
        }

        when(diseaseService.getDiseaseGroupsNeedingExtentReviewByExpert(eq(expert))).thenReturn(list);
    }

    private void wireUpDiseasesNeedingOccurrenceReview(Expert expert, DiseaseService diseaseService, Integer... diseaseIds) {
        diseaseService = mockIfNull(diseaseService, DiseaseService.class);

        List<DiseaseGroup> list = new ArrayList<>();
        for (Integer id : diseaseIds) {
            DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
            when(diseaseGroup.getId()).thenReturn(id);
            list.add(diseaseGroup);
        }

        when(diseaseService.getDiseaseGroupsNeedingOccurrenceReviewByExpert(eq(expert))).thenReturn(list);
    }

    private void wireUpExistingOccurrenceReview(Integer expertId, Integer occurrenceId) {
        expertService = mockIfNull(expertService, ExpertService.class);

        when(expertService.doesDiseaseOccurrenceReviewExist(expertId, occurrenceId)).thenReturn(true);
    }

    private void wireUpExistingExtentReview(Integer expertId, Integer gaulCode) {
        expertService = mockIfNull(expertService, ExpertService.class);

        when(expertService.doesAdminUnitReviewExistForLatestDiseaseExtent(expertId, diseaseGroup, createAdminUnitGlobal(gaulCode))).thenReturn(true);
    }

    private void wireUpDiseaseGroup(Integer id, boolean hasModelRunPrepOccurredForDiseaseGroup) {
        diseaseService = mockIfNull(diseaseService, DiseaseService.class);
        diseaseGroup = mockIfNull(diseaseGroup, DiseaseGroup.class);

        when(diseaseService.getDiseaseGroupById(id)).thenReturn(diseaseGroup);
        when(diseaseGroup.getLastModelRunPrepDate()).thenReturn(hasModelRunPrepOccurredForDiseaseGroup ? DateTime.now().minusHours(1) : null);
        when(diseaseGroup.getLastExtentGenerationDate()).thenReturn(hasModelRunPrepOccurredForDiseaseGroup ? DateTime.now().minusHours(1) : null);
    }

    private void wireUpDiseaseExtent(Integer diseaseGroupId, Integer expertId, boolean hasModelRunPrepOccurredForDiseaseGroup) {
        wireUpDiseaseGroup(diseaseGroupId, hasModelRunPrepOccurredForDiseaseGroup);

        expertService = mockIfNull(expertService, ExpertService.class);

        List<AdminUnitDiseaseExtentClass> diseaseExtent = createDiseaseExtent();
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(eq(diseaseGroupId))).thenReturn(diseaseExtent);
        List<AdminUnitReview> reviews = createReviews();
        when(expertService.getAllAdminUnitReviewsForDiseaseGroup(eq(expertId), eq(diseaseGroupId))).thenReturn(reviews);
    }

    private void wireUpForOccurrenceReview(Integer validatorDiseaseGroupId, Integer diseaseOccurrenceId, boolean isValidatorDiseaseGroupCorrect, boolean hasModelRunPrepOccurredForDiseaseGroup) {
        wireUpDiseaseGroup(99, hasModelRunPrepOccurredForDiseaseGroup);

        validatorDiseaseGroup = mockIfNull(validatorDiseaseGroup, ValidatorDiseaseGroup.class);
        diseaseOccurrence = mockIfNull(diseaseOccurrence, DiseaseOccurrence.class);

        when(diseaseService.getValidatorDiseaseGroupById(validatorDiseaseGroupId)).thenReturn(validatorDiseaseGroup);
        when(diseaseService.getDiseaseOccurrenceById(diseaseOccurrenceId)).thenReturn(diseaseOccurrence);
        when(diseaseOccurrence.getId()).thenReturn(diseaseOccurrenceId);
        when(diseaseOccurrence.getValidatorDiseaseGroup()).thenReturn(isValidatorDiseaseGroupCorrect ? validatorDiseaseGroup : mock(ValidatorDiseaseGroup.class));
        when(diseaseOccurrence.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(validatorDiseaseGroup.getId()).thenReturn(validatorDiseaseGroupId);
    }

    private void wireUpForExtentReview(Integer diseaseId, Integer gaulCode, String review, boolean hasModelRunPrepOccurredForDiseaseGroup) {
        wireUpDiseaseGroup(diseaseId, hasModelRunPrepOccurredForDiseaseGroup);

        geometryService = mockIfNull(geometryService, GeometryService.class);
        diseaseExtentClass = mockIfNull(diseaseExtentClass, DiseaseExtentClass.class);

        when(geometryService.getAdminUnitGlobalOrTropicalByGaulCode(diseaseGroup, gaulCode)).thenReturn(createAdminUnitGlobal(gaulCode));
        when(diseaseService.getDiseaseExtentClass(review)).thenReturn(diseaseExtentClass);
    }
}
