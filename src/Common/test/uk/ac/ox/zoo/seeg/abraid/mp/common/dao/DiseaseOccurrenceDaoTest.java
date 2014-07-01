package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseOccurrenceDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AlertDao alertDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    @Autowired
    private ExpertDao expertDao;

    @Autowired
    private FeedDao feedDao;

    @Autowired
    private LocationDao locationDao;

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustNotReturnAReviewedPoint() {
        // Arrange
        Expert expert = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId,
                diseaseGroupId);

        // Assert
        assertThat(list).doesNotContain(occurrence);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustOnlyReturnSpecifiedDiseaseGroup() {
        // Arrange
        Expert expert = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId,
                diseaseGroupId);

        // Assert
        for (DiseaseOccurrence item : list) {
            assertThat(item.getDiseaseGroup().getId()).isEqualTo(diseaseGroupId);
        }
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnOccurrencesForCorrectExpert() {
        // Arrange
        // Two experts save reviews for different disease occurrences of the same disease group
        Expert expert0 = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence0 = diseaseOccurrenceDao.getById(272829);
        occurrence0.setValidated(false);
        DiseaseOccurrenceReviewResponse response0 = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert0, occurrence0, response0);

        Expert expert1 = expertDao.getByEmail("zool1251@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence1 = diseaseOccurrenceDao.getById(272830);
        occurrence1.setValidated(false);
        DiseaseOccurrenceReviewResponse response1 = DiseaseOccurrenceReviewResponse.NO;
        createAndSaveDiseaseOccurrenceReview(expert1, occurrence1, response1);

        // Act
        Integer expertId = expert0.getId();
        Integer validatorDiseaseGroupId = occurrence0.getValidatorDiseaseGroup().getId();
        List<DiseaseOccurrence> occurrencesYetToBeReviewedByExpert =
                diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, validatorDiseaseGroupId);

        // Assert
        assertThat(occurrencesYetToBeReviewedByExpert).contains(occurrence1);
        assertThat(occurrencesYetToBeReviewedByExpert).doesNotContain(occurrence0);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustNotReturnValidatedOrNullOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        int validatorDiseaseGroupId = 4;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseOccurrence occ1 = createDiseaseOccurrence(1, diseaseGroup, false);
        DiseaseOccurrence occ2 = createDiseaseOccurrence(2, diseaseGroup, true);
        DiseaseOccurrence occ3 = createDiseaseOccurrence(3, diseaseGroup, null);

        // Act
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(1, validatorDiseaseGroupId);

        // Assert
        assertThat(list).contains(occ1);
        assertThat(list).doesNotContain(occ2);
        assertThat(list).doesNotContain(occ3);
    }

    private DiseaseOccurrence createDiseaseOccurrence(int id, DiseaseGroup diseaseGroup, Boolean isValidated) {
        Location location = locationDao.getById(6);
        Alert alert = alertDao.getById(212855);
        DiseaseOccurrence occurrence = new DiseaseOccurrence(id, diseaseGroup, location, alert, isValidated, 0.7, new DateTime());
        diseaseOccurrenceDao.save(occurrence);
        return occurrence;
    }

    @Test
    public void saveThenReloadDiseaseOccurrence() {
        // Arrange
        Alert alert = createAlert();
        Location location = new Location("Karachi", 25.0111455, 67.0647043, LocationPrecision.PRECISE);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        DateTime occurrenceDate = DateTime.now().minusDays(5);
        double expertWeighting = 0.1;
        double machineWeighting = 0.2;
        double validationWeighting = 0.3;
        double finalWeighting = 0.4;
        double finalWeightingExclSpatial = 0.5;
        double environmentalSuitability = 0.6;
        double distanceFromDiseaseExtent = 100;

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(alert);
        occurrence.setLocation(location);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setOccurrenceDate(occurrenceDate);
        occurrence.setEnvironmentalSuitability(environmentalSuitability);
        occurrence.setDistanceFromDiseaseExtent(distanceFromDiseaseExtent);
        occurrence.setExpertWeighting(expertWeighting);
        occurrence.setMachineWeighting(machineWeighting);
        occurrence.setValidationWeighting(validationWeighting);
        occurrence.setFinalWeighting(finalWeighting);
        occurrence.setFinalWeightingExcludingSpatial(finalWeightingExclSpatial);

        // Act
        diseaseOccurrenceDao.save(occurrence);

        // Assert
        assertThat(occurrence.getCreatedDate()).isNotNull();
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getId()).isNotNull();
        assertThat(occurrence.getAlert().getCreatedDate()).isNotNull();
        assertThat(occurrence.getLocation()).isNotNull();
        assertThat(occurrence.getLocation().getId()).isNotNull();
        assertThat(occurrence.getLocation().getCreatedDate()).isNotNull();

        Integer id = occurrence.getId();
        flushAndClear();
        occurrence = diseaseOccurrenceDao.getById(id);

        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getId()).isNotNull();
        assertThat(occurrence.getCreatedDate()).isNotNull();
        assertThat(occurrence.getLocation()).isNotNull();
        assertThat(occurrence.getLocation().getId()).isNotNull();
        assertThat(occurrence.getEnvironmentalSuitability()).isEqualTo(environmentalSuitability);
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isEqualTo(distanceFromDiseaseExtent);
        assertThat(occurrence.getExpertWeighting()).isEqualTo(expertWeighting);
        assertThat(occurrence.getMachineWeighting()).isEqualTo(machineWeighting);
        assertThat(occurrence.getValidationWeighting()).isEqualTo(validationWeighting);
        assertThat(occurrence.getFinalWeighting()).isEqualTo(finalWeighting);
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isEqualTo(finalWeightingExclSpatial);
        assertThat(occurrence.getDiseaseGroup()).isNotNull();
        assertThat(occurrence.getDiseaseGroup().getId()).isNotNull();
        assertThat(occurrence.getOccurrenceDate()).isEqualTo(occurrenceDate);
    }

    @Test
    public void getDiseaseOccurrencesForExistenceCheckExists() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), occurrence.getAlert(),
                occurrence.getOccurrenceDate());
        assertThat(occurrences).hasSize(1);
    }

    @Test
    public void getDiseaseOccurrencesForExistenceCheckDiseaseGroupDifferent() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                diseaseGroup, occurrence.getLocation(), occurrence.getAlert(),
                occurrence.getOccurrenceDate());
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesForExistenceCheckLocationDifferent() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        Location location = locationDao.getById(80);
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), location, occurrence.getAlert(),
                occurrence.getOccurrenceDate());
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesForExistenceCheckAlertDifferent() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        Alert alert = alertDao.getById(213235);
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), alert,
                occurrence.getOccurrenceDate());
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesForExistenceCheckOccurrenceDateDifferent() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        DateTime occurrenceDate = DateTime.now();
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), occurrence.getAlert(),
                occurrenceDate);
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithNullFeedIds() {
        getDiseaseOccurrencesForDiseaseExtent(null, 22, false);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithZeroFeedIds() {
        getDiseaseOccurrencesForDiseaseExtent(new ArrayList<Integer>(), 22, false);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithSomeFeedIds() {
        getDiseaseOccurrencesForDiseaseExtent(Arrays.asList(1, 4, 8), 17, false);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithGlobalDisease() {
        getDiseaseOccurrencesForDiseaseExtent(new ArrayList<Integer>(), 23, true);
    }

    @Test
    public void getDiseaseOccurrencesInValidationReturnsOnlyIsValidatedFalse() {
        // Arrange
        int diseaseGroupId = 87;
        int n = setValidatedFlagForAllOccurrencesOfDiseaseGroup(diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> result = diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);

        // Assert
        assertThat(result.size()).isEqualTo(n);
        for (DiseaseOccurrence occurrence : result) {
            assertThat(occurrence.isValidated()).isFalse();
            assertThat(occurrence.getDiseaseGroup().getId()).isEqualTo(diseaseGroupId);
        }
    }

    @Test
    public void getDiseaseOccurrencesInValidationReturnsNoIsValidated() {
        // Arrange
        int diseaseGroupId = 87;
        int n = setValidatedFlagForAllOccurrencesOfDiseaseGroup(diseaseGroupId, true);

        // Act
        List<DiseaseOccurrence> result = diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);

        // Assert
        assertThat(result).isEmpty();
    }

    private int setValidatedFlagForAllOccurrencesOfDiseaseGroup(int diseaseGroupId, boolean validated) {
        List<DiseaseOccurrence> occurrences = select(diseaseOccurrenceDao.getAll(),
                having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(), IsEqual.equalTo(diseaseGroupId)));
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setValidated(validated);
            diseaseOccurrenceDao.save(occurrence);
        }
        return occurrences.size();
    }

    @Test
    public void getDiseaseOccurrencesForModelRun() {
        // Arrange
        int diseaseGroupId = 87; // Dengue

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);

        // Assert
        assertThat(occurrences).hasSize(45);
    }

    @Test
    public void getNewOccurrencesCountByDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(diseaseGroup.getCreatedDate().minusDays(1));

        // Act
        long count = diseaseOccurrenceDao.getNewOccurrencesCountByDiseaseGroup(diseaseGroupId);

        // Assert
        assertThat(count).isEqualTo(45);
    }

    private void getDiseaseOccurrencesForDiseaseExtent(List<Integer> feedIds, int expectedOccurrenceCount,
                                                       boolean isGlobal) {
        // Arrange
        int diseaseGroupId = 87; // Dengue
        double minimumValidationWeight = 0.6;
        DateTime minimumOccurrenceDate = new DateTime("2014-02-25");

        // Act
        List<DiseaseOccurrenceForDiseaseExtent> occurrences =
                diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeight,
                        minimumOccurrenceDate, feedIds, isGlobal);

        // Assert
        assertThat(occurrences).hasSize(expectedOccurrenceCount);
    }

    private Alert createAlert() {
        Feed feed = feedDao.getById(1);
        DateTime publicationDate = DateTime.now().minusDays(5);
        int healthMapAlertId = 100;
        String title = "Dengue/DHF update (15): Asia, Indian Ocean, Pacific";
        String summary = "This is a summary of the alert";
        String url = "http://www.promedmail.org/direct.php?id=20140217.2283261";

        Alert alert = new Alert();
        alert.setFeed(feed);
        alert.setHealthMapAlertId(healthMapAlertId);
        alert.setPublicationDate(publicationDate);
        alert.setTitle(title);
        alert.setSummary(summary);
        alert.setUrl(url);
        return alert;
    }

    private DiseaseOccurrenceReview createAndSaveDiseaseOccurrenceReview(Expert expert, DiseaseOccurrence occurrence,
                                                                         DiseaseOccurrenceReviewResponse response) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview();
        review.setExpert(expert);
        review.setDiseaseOccurrence(occurrence);
        review.setResponse(response);
        diseaseOccurrenceReviewDao.save(review);
        return review;
    }
}
