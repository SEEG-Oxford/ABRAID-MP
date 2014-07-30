package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

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
    public void getByIds() {
        // Arrange
        List<Integer> ids = Arrays.asList(274016, 274430, 274432);

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getByIds(ids);

        // Assert
        assertThat(occurrences).hasSize(3);
    }

    @Test
    public void getByDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(occurrences).hasSize(48);
    }

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
        location.setResolutionWeighting(1.0);
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
    public void getDiseaseOccurrencesForDiseaseExtentWithNullParameters() {
        getDiseaseOccurrencesForDiseaseExtent(87, null, null, false, 42);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithAllParameters() {
        getDiseaseOccurrencesForDiseaseExtent(87, 0.6, new DateTime("2014-02-25"), false, 22);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithGlobalDisease() {
        getDiseaseOccurrencesForDiseaseExtent(277, 0.6, new DateTime("2014-02-27"), true, 4);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithSomeWeightingsNull() {
        getDiseaseOccurrencesForDiseaseExtent(87, 0.6, new DateTime("2014-02-25"), false, 22);
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
        setValidatedFlagForAllOccurrencesOfDiseaseGroup(diseaseGroupId, true);

        // Act
        List<DiseaseOccurrence> result = diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);

        // Assert
        assertThat(result).isEmpty();
    }

    private int setValidatedFlagForAllOccurrencesOfDiseaseGroup(int diseaseGroupId, boolean validated) {
        List<DiseaseOccurrence> occurrences = select(diseaseOccurrenceDao.getAll(),
                having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(), equalTo(diseaseGroupId)));
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setValidated(validated);
            diseaseOccurrenceDao.save(occurrence);
        }
        return occurrences.size();
    }

    @Test
    public void getDiseaseOccurrencesYetToHaveFinalWeightingAssignedWithAnyEnvironmentalSuitability() {
        getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(false, 25);
    }

    @Test
    public void getDiseaseOccurrencesYetToHaveFinalWeightingAssignedWithNonNullEnvironmentalSuitability() {
        getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(true, 10);
    }

    private void getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(boolean mustHaveEnvironmentalSuitability,
                                                                      int expectedSize) {
        // Arrange
        int diseaseGroupId = 87;
        int numberOfOccurrencesWithFinalWeightingNull = 25;
        int numberOfOccurrencesWithEnvironmentalSuitabilityNotNull = 10;

        // Arrange - get a random list of all dengue occurrences with isValidated = true
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getAll();
        occurrences = select(occurrences, having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(),
                equalTo(diseaseGroupId)));
        occurrences = select(occurrences, having(on(DiseaseOccurrence.class).isValidated(), equalTo(true)));
        Collections.shuffle(occurrences);

        // Arrange - set the final weightings to null
        occurrences = occurrences.subList(0, numberOfOccurrencesWithFinalWeightingNull);
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setFinalWeighting(null);
            diseaseOccurrenceDao.save(occurrence);
        }

        // Arrange - of these, set the environmental suitability weightings to non-null
        occurrences = occurrences.subList(0, numberOfOccurrencesWithEnvironmentalSuitabilityNotNull);
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setEnvironmentalSuitability(Math.random());
            diseaseOccurrenceDao.save(occurrence);
        }

        // Act
        List<DiseaseOccurrence> actualOccurrences =
                diseaseOccurrenceDao.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId,
                        mustHaveEnvironmentalSuitability);

        // Assert
        assertThat(actualOccurrences).hasSize(expectedSize);
    }

    @Test
    public void getDiseaseOccurrencesForModelRunRequest() {
        // Arrange
        int diseaseGroupId = 87; // Dengue

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);

        // Assert
        assertThat(occurrences).hasSize(27);
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getDiseaseGroup().getId()).isEqualTo(diseaseGroupId);
            assertThat(occurrence.isValidated()).isTrue();
            assertThat(occurrence.getFinalWeighting()).isNotNull();
            assertThat(occurrence.getFinalWeighting()).isGreaterThan(0);
        }
    }

    @Test
    public void getDiseaseOccurrencesForModelRunRequestOrdersByOccurrenceDateDescending() {
        // Arrange
        int diseaseGroupId = 87; // Dengue

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);

        // Assert
        for (int i = 0; i < occurrences.size() - 1; i++) {
            assertThat(isDescendingChronologically(occurrences.get(i), occurrences.get(i + 1))).isTrue();
        }
    }

    private boolean isDescendingChronologically(DiseaseOccurrence o1, DiseaseOccurrence o2) {
        return !(o1.getOccurrenceDate().isBefore(o2.getOccurrenceDate()));
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

    @Test
    public void getDiseaseOccurrenceStatisticsWithSomeOccurrences() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        DiseaseOccurrenceStatistics statistics = diseaseOccurrenceDao.getDiseaseOccurrenceStatistics(diseaseGroupId);

        // Assert
        assertThat(statistics.getOccurrenceCount()).isEqualTo(45);
        assertThat(statistics.getMinimumOccurrenceDate()).isEqualTo(new DateTime("2014-02-24T17:35:29"));
        assertThat(statistics.getMaximumOccurrenceDate()).isEqualTo(new DateTime("2014-02-27T08:06:46"));
    }

    @Test
    public void getDiseaseOccurrenceStatisticsWithNoOccurrences() {
        // Arrange
        int diseaseGroupId = 1;

        // Act
        DiseaseOccurrenceStatistics statistics = diseaseOccurrenceDao.getDiseaseOccurrenceStatistics(diseaseGroupId);

        // Assert
        assertThat(statistics.getOccurrenceCount()).isEqualTo(0);
        assertThat(statistics.getMinimumOccurrenceDate()).isNull();
        assertThat(statistics.getMaximumOccurrenceDate()).isNull();
    }

    @Test
    public void getIDsForBatching() {
        // Arrange - the first 4 of these occurrences are before or on the batch end date, the others are after
        // or do not have isValidated = true
        setFinalWeightingOfSelectedOccurrencesToNull(274656, 273401, 275758, 275714, 275107, 274779);
        int diseaseGroupId = 87;
        DateTime batchEndDate = new DateTime("2014-02-25T02:45:35");

        // Act
        List<Integer> occurrences = diseaseOccurrenceDao.getIDsForBatching(diseaseGroupId, batchEndDate);

        // Assert
        assertThat(occurrences).hasSize(3);
    }

    private void getDiseaseOccurrencesForDiseaseExtent(int diseaseGroupId, Double minimumValidationWeight,
                                                       DateTime minimumOccurrenceDate, boolean isGlobal,
                                                       int expectedOccurrenceCount) {
        // Act
        List<DiseaseOccurrenceForDiseaseExtent> occurrences =
                diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeight,
                        minimumOccurrenceDate, isGlobal);

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

    private void setFinalWeightingOfAllOccurrencesToNull() {
        for (DiseaseOccurrence occurrence : diseaseOccurrenceDao.getAll()) {
            occurrence.setFinalWeighting(null);
            diseaseOccurrenceDao.save(occurrence);
        }
    }
    private void setFinalWeightingOfSelectedOccurrencesToNull(Integer... ids) {
        for (DiseaseOccurrence occurrence : diseaseOccurrenceDao.getByIds(Arrays.asList(ids))) {
            occurrence.setFinalWeighting(null);
            diseaseOccurrenceDao.save(occurrence);
        }
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
