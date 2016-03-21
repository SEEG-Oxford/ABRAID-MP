package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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

    @Autowired
    private ProvenanceDao provenanceDao;

    @Autowired
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;

    private static Feed testFeed;
    private static Feed goldStandardFeed;
    private static String insertOccurrenceQuery = "INSERT INTO disease_occurrence (disease_group_id, alert_id, location_id, status, expert_weighting, distance_from_extent, env_suitability, created_date, occurrence_date) VALUES (1, 212855, %s, '%s', %s, %s, %s, '%s', '" + LocalDateTime.now().toString() + "')";
    private int modelIneligibleLocation1 = 6;
    private int modelIneligibleLocation2 = 40;
    private int modelEligibleCountryLocation = 12;
    private int modelEligibleLocation1 = 133;
    private int modelEligibleLocation2 = 5447;
    private int modelEligibleLocation3 = 8648;
    private int modelEligibleLocation4 = 14993;
    private int modelEligibleLocation5 = 20635;
    private int excludedButEligibleLocation1 = 2872;
    private int excludedButEligibleLocation2 = 9394;

    @Before
    public void setUp() {
        testFeed = new Feed("Test feed", provenanceDao.getByName(ProvenanceNames.MANUAL));
        feedDao.save(testFeed);

        goldStandardFeed = new Feed("Gold standard feed", provenanceDao.getByName(ProvenanceNames.MANUAL_GOLD_STANDARD));
        feedDao.save(goldStandardFeed);
    }

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
    public void getByDiseaseGroupIdAndStatusReady() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getByDiseaseGroupIdAndStatuses(diseaseGroupId,
                DiseaseOccurrenceStatus.READY);

        // Assert
        assertThat(occurrences).hasSize(45);
    }

    @Test
    public void getByDiseaseGroupIdAndStatusDiscardedFailedQc() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getByDiseaseGroupIdAndStatuses(diseaseGroupId,
                DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);

        // Assert
        assertThat(occurrences).hasSize(3);
    }

    @Test
    public void getByDiseaseGroupIdAndStatusAwaitingBatching() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getByDiseaseGroupIdAndStatuses(diseaseGroupId,
                DiseaseOccurrenceStatus.AWAITING_BATCHING);

        // Assert
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getByDiseaseGroupIdAndStatusReadyAndFailedQc() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getByDiseaseGroupIdAndStatuses(diseaseGroupId,
                DiseaseOccurrenceStatus.READY, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);

        // Assert
        assertThat(occurrences).hasSize(48);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustNotReturnACountryPoint() {
        // Arrange
        Expert expert = expertDao.getByEmail("helena.patching@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);    // Occurrence has country location (Mexico)

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, true,
                diseaseGroupId);

        // Assert
        assertThat(list).doesNotContain(occurrence);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustNotReturnAReviewedPoint() {
        // Arrange
        Expert expert = expertDao.getByEmail("helena.patching@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(274016);    // Occurrence has precise location (in Sao Paulo, Brazil)
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, true,
                diseaseGroupId);

        // Assert
        assertThat(list).doesNotContain(occurrence);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustOnlyReturnSpecifiedDiseaseGroup() {
        // Arrange
        Expert expert = expertDao.getByEmail("helena.patching@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(274016);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, true,
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
        Expert expert0 = expertDao.getByEmail("helena.patching@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence0 = diseaseOccurrenceDao.getById(272829);
        occurrence0.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        DiseaseOccurrenceReviewResponse response0 = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert0, occurrence0, response0);

        Expert expert1 = expertDao.getByEmail("edward.wiles@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence1 = diseaseOccurrenceDao.getById(272830);
        occurrence1.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        DiseaseOccurrenceReviewResponse response1 = DiseaseOccurrenceReviewResponse.NO;
        createAndSaveDiseaseOccurrenceReview(expert1, occurrence1, response1);

        // Act
        Integer expertId = expert0.getId();
        Integer validatorDiseaseGroupId = occurrence0.getValidatorDiseaseGroup().getId();
        List<DiseaseOccurrence> occurrencesYetToBeReviewedByExpert =
                diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expertId, true, validatorDiseaseGroupId);

        // Assert
        assertThat(occurrencesYetToBeReviewedByExpert).contains(occurrence1);
        assertThat(occurrencesYetToBeReviewedByExpert).doesNotContain(occurrence0);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnCorrectOccurrencesForSeegUser() {
        // Arrange
        int validatorDiseaseGroupId = 2;
        ValidatorDiseaseGroup validatorDiseaseGroup = validatorDiseaseGroupDao.getById(validatorDiseaseGroupId);

        Expert expert = expertDao.getByEmail("helena.patching@zoo.ox.ac.uk");

        DiseaseOccurrence occurrenceWithModelRunPrepDate = saveOccurrenceInDiseaseGroupWithModelRunPrepDate(validatorDiseaseGroup);
        DiseaseOccurrence occurrenceWithoutModelRunPrepDate = saveOccurrenceInDiseaseGroupWithoutModelRunPrepDate(validatorDiseaseGroup);

        // Act
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expert.getId(), expert.isSeegMember(), validatorDiseaseGroupId);

        // Assert
        assertThat(list).hasSize(2);
        assertThat(list).containsOnly(occurrenceWithModelRunPrepDate, occurrenceWithoutModelRunPrepDate);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnCorrectOccurrencesForNonSeegUser() {
        // Arrange
        int validatorDiseaseGroupId = 2;
        ValidatorDiseaseGroup validatorDiseaseGroup = validatorDiseaseGroupDao.getById(validatorDiseaseGroupId);

        Expert expert = expertDao.getByEmail("edward.wiles@zoo.ox.ac.uk");

        DiseaseOccurrence occurrenceWithModelRunPrepDate = saveOccurrenceInDiseaseGroupWithModelRunPrepDate(validatorDiseaseGroup);
        DiseaseOccurrence occurrenceWithoutModelRunPrepDate = saveOccurrenceInDiseaseGroupWithoutModelRunPrepDate(validatorDiseaseGroup);

        // Act
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(expert.getId(), expert.isSeegMember(), validatorDiseaseGroupId);

        // Assert
        assertThat(list).hasSize(1);
        assertThat(list).contains(occurrenceWithModelRunPrepDate);
        assertThat(list).doesNotContain(occurrenceWithoutModelRunPrepDate);
    }

    private DiseaseOccurrence saveOccurrenceInDiseaseGroupWithoutModelRunPrepDate(ValidatorDiseaseGroup validatorDiseaseGroup) {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        diseaseGroup.setLastModelRunPrepDate(null);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        diseaseGroupDao.save(diseaseGroup);

        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        occurrence.setDiseaseGroup(diseaseGroup);
        diseaseOccurrenceDao.save(occurrence);

        return occurrence;
    }

    private DiseaseOccurrence saveOccurrenceInDiseaseGroupWithModelRunPrepDate(ValidatorDiseaseGroup validatorDiseaseGroup) {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(2);
        diseaseGroup.setLastModelRunPrepDate(DateTime.now().minusHours(1));
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        diseaseGroupDao.save(diseaseGroup);

        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(273401);
        occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
        occurrence.setDiseaseGroup(diseaseGroup);
        diseaseOccurrenceDao.save(occurrence);

        return occurrence;
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustNotReturnReadyOrDiscardedOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        int validatorDiseaseGroupId = 4;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseOccurrence occ1 = createDiseaseOccurrence(1, diseaseGroup, DiseaseOccurrenceStatus.IN_REVIEW);
        DiseaseOccurrence occ2 = createDiseaseOccurrence(2, diseaseGroup, DiseaseOccurrenceStatus.READY);
        DiseaseOccurrence occ3 = createDiseaseOccurrence(3, diseaseGroup, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);

        // Act
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(1, true, validatorDiseaseGroupId);

        // Assert
        assertThat(list).contains(occ1);
        assertThat(list).doesNotContain(occ2);
        assertThat(list).doesNotContain(occ3);
    }

    private DiseaseOccurrence createDiseaseOccurrence(int id, DiseaseGroup diseaseGroup, DiseaseOccurrenceStatus status) {
        return createDiseaseOccurrence(id, diseaseGroup, status, 80, new DateTime());
    }

    private DiseaseOccurrence createDiseaseOccurrence(int id, DiseaseGroup diseaseGroup, DiseaseOccurrenceStatus status, int locationId, DateTime occurrenceDate) {
        Location location = locationDao.getById(locationId);
        Alert alert = alertDao.getById(212855);
        DiseaseOccurrence occurrence = new DiseaseOccurrence(id, diseaseGroup, location, alert, status, 0.7, occurrenceDate);
        diseaseOccurrenceDao.save(occurrence);
        return occurrence;
    }

    private DiseaseOccurrence createBiasDiseaseOccurrence(int id, DiseaseGroup diseaseGroup, DiseaseOccurrenceStatus status, int locationId, DateTime occurrenceDate, DiseaseGroup biasDisease) {
        DiseaseOccurrence occurrence = createDiseaseOccurrence(id, diseaseGroup, DiseaseOccurrenceStatus.READY, locationId, occurrenceDate);
        occurrence.setStatus(status);
        occurrence.setBiasDisease(biasDisease);
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
        DiseaseOccurrenceStatus status = DiseaseOccurrenceStatus.READY;
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
        occurrence.setStatus(status);
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
        assertThat(occurrence.getStatus()).isEqualTo(status);
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
    public void saveThenReloadBiasDiseaseOccurrence() {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        DiseaseGroup biasDiseaseGroup = diseaseGroupDao.getById(64);
        Location location = locationDao.getById(80);
        Alert alert = new Alert();
        alert.setFeed(testFeed);

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setAlert(alert);
        occurrence.setStatus(DiseaseOccurrenceStatus.BIAS);
        occurrence.setOccurrenceDate(DateTime.now());
        occurrence.setBiasDisease(biasDiseaseGroup);

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

        assertThat(occurrence.getAlert().getId()).isNotNull();
        assertThat(occurrence.getCreatedDate()).isNotNull();
        assertThat(occurrence.getLocation().getId()).isEqualTo(80);
        assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.BIAS);
        assertThat(occurrence.getEnvironmentalSuitability()).isNull();
        assertThat(occurrence.getDistanceFromDiseaseExtent()).isNull();
        assertThat(occurrence.getExpertWeighting()).isNull();
        assertThat(occurrence.getMachineWeighting()).isNull();
        assertThat(occurrence.getValidationWeighting()).isNull();
        assertThat(occurrence.getFinalWeighting()).isNull();
        assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        assertThat(occurrence.getDiseaseGroup().getId()).isEqualTo(87);
        assertThat(occurrence.getOccurrenceDate()).isNotNull();
        assertThat(occurrence.getBiasDisease().getId()).isEqualTo(64);
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
    public void getDiseaseOccurrencesForExistenceCheckIgnoresExistingBiasPoints() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        occurrence.setBiasDisease(diseaseGroupDao.getById(87));
        occurrence.setStatus(DiseaseOccurrenceStatus.BIAS);
        diseaseOccurrenceDao.save(occurrence);

        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), occurrence.getAlert(),
                occurrence.getOccurrenceDate());
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesForExistenceCheckIgnoresExistingFailedBiasPoints() {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(272407);
        occurrence.setBiasDisease(diseaseGroupDao.getById(87));
        occurrence.setStatus(DiseaseOccurrenceStatus.DISCARDED_FAILED_QC);
        diseaseOccurrenceDao.save(occurrence);

        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), occurrence.getAlert(),
                occurrence.getOccurrenceDate());
        assertThat(occurrences).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithNullParameters() {
        getDiseaseOccurrencesForDiseaseExtent(87, null, null, false, 49);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithAllParameters() {
        getDiseaseOccurrencesForDiseaseExtent(87, 0.6, new DateTime("2014-02-25"), false, 28);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithGlobalDisease() {
        getDiseaseOccurrencesForDiseaseExtent(277, 0.6, new DateTime("2014-02-27"), false, 4);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentWithSomeWeightingsNull() {
        getDiseaseOccurrencesForDiseaseExtent(87, 0.6, new DateTime("2014-02-25"), false, 28);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentUsingGoldStandardOccurrences() {
        getDiseaseOccurrencesForDiseaseExtent(87, null, null, true, 2);
    }

    @Test
    public void getDiseaseOccurrencesInValidationReturnsCorrectOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        int n = setStatusForAllOccurrencesOfDiseaseGroup(diseaseGroupId, DiseaseOccurrenceStatus.IN_REVIEW);

        // Act
        List<DiseaseOccurrence> result = diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);

        // Assert
        assertThat(result.size()).isEqualTo(n);
        for (DiseaseOccurrence occurrence : result) {
            assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.IN_REVIEW);
            assertThat(occurrence.getDiseaseGroup().getId()).isEqualTo(diseaseGroupId);
        }
    }

    @Test
    public void getDiseaseOccurrencesInValidationDoesNotReturnAnyReadyOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        setStatusForAllOccurrencesOfDiseaseGroup(diseaseGroupId, DiseaseOccurrenceStatus.READY);

        // Act
        List<DiseaseOccurrence> result = diseaseOccurrenceDao.getDiseaseOccurrencesInValidation(diseaseGroupId);

        // Assert
        assertThat(result).isEmpty();
    }

    private int setStatusForAllOccurrencesOfDiseaseGroup(int diseaseGroupId, DiseaseOccurrenceStatus status) {
        List<DiseaseOccurrence> occurrences = select(diseaseOccurrenceDao.getAll(),
                having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(), equalTo(diseaseGroupId)));
        for (DiseaseOccurrence occurrence : occurrences) {
            occurrence.setStatus(status);
            diseaseOccurrenceDao.save(occurrence);
        }
        return occurrences.size();
    }

    @Test
    public void getDiseaseOccurrencesYetToHaveFinalWeightingAssigned() {
        getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(25);
    }

    private void getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(int expectedSize) {
        // Arrange
        int diseaseGroupId = 87;
        int numberOfOccurrencesWithFinalWeightingNull = 25;
        int numberOfOccurrencesWithEnvironmentalSuitabilityNotNull = 10;

        // Arrange - get a random list of all dengue occurrences with status READY
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getAll();
        occurrences = select(occurrences, having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(),
                equalTo(diseaseGroupId)));
        occurrences = select(occurrences, having(on(DiseaseOccurrence.class).getStatus(),
                equalTo(DiseaseOccurrenceStatus.READY)));
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
                diseaseOccurrenceDao.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId, DiseaseOccurrenceStatus.READY);

        // Assert
        assertThat(actualOccurrences).hasSize(expectedSize);
    }

    @Test
    public void getDiseaseOccurrencesForModelRunRequest() {
        // Arrange
        int diseaseGroupId = 87; // Dengue
        addManuallyUploadedOccurrences();

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, false);

        // Assert
        assertThat(occurrences).hasSize(30);
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getDiseaseGroup().getId()).isEqualTo(diseaseGroupId);
            assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
            assertThat(occurrence.getFinalWeighting()).isGreaterThan(0);
            assertThat(occurrence.getLocation().getPrecision()).isNotEqualTo(LocationPrecision.COUNTRY);
        }

        for (int i = 0; i < occurrences.size() - 1; i++) {
            assertThat(isDescendingChronologically(occurrences.get(i), occurrences.get(i + 1))).isTrue();
        }
    }

    @Test
    public void getDiseaseOccurrencesForModelRunRequestUsingGoldStandardOccurrences() {
        // Arrange
        int diseaseGroupId = 87; // Dengue
        addManuallyUploadedOccurrences();

        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, true);

        // Assert
        assertThat(occurrences).hasSize(2);
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getDiseaseGroup().getId()).isEqualTo(diseaseGroupId);
            assertThat(occurrence.getStatus()).isEqualTo(DiseaseOccurrenceStatus.READY);
            assertThat(occurrence.getFinalWeighting()).isEqualTo(1);
            assertThat(occurrence.getAlert().getFeed().getProvenance().getName()).isEqualTo(ProvenanceNames.MANUAL_GOLD_STANDARD);
            assertThat(occurrence.getLocation().getPrecision()).isNotEqualTo(LocationPrecision.COUNTRY);
        }
    }

    private boolean isDescendingChronologically(DiseaseOccurrence o1, DiseaseOccurrence o2) {
        return !(o1.getOccurrenceDate().isBefore(o2.getOccurrenceDate()));
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRun() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(modelEligibleLocation2, "IN_REVIEW", null, 51.0, 0.4, now.minusHours(1).toString()); // no
        insertOccurrence(modelEligibleLocation2, "IN_REVIEW", null, 51.0, 0.4, now.minusHours(1).toString()); // no
        insertOccurrence(modelEligibleLocation3, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(modelEligibleLocation4, "READY", null, 49.0, 0.6, now.minusHours(1).toString()); // no
        insertOccurrence(modelEligibleLocation5, "READY", null, 51.0, 0.4, now.minusDays(2).toString()); // no
        insertOccurrence(modelEligibleCountryLocation, "READY", null, 51.0, 0.6, now.minusHours(1).toString()); // yes
        insertOccurrence(modelIneligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // no
        insertOccurrence(excludedButEligibleLocation2, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // no/yes

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 3, 4);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesNonUnique() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesBasedOnStatus() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(modelEligibleLocation2, "IN_REVIEW", null, 51.0, 0.4, now.minusHours(1).toString()); // no
        insertOccurrence(modelEligibleLocation3, "IN_REVIEW", null, 51.0, 0.4, now.minusHours(1).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesBasedOnLocationEligibility() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(modelIneligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // no
        insertOccurrence(modelIneligibleLocation2, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
         public void getDistinctLocationsCountForTriggeringModelRunExcludesBasedCreationDate() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusDays(1).plusMillis(1).toString()); // yes
        insertOccurrence(modelEligibleLocation2, "READY", null, 51.0, 0.4, now.minusDays(1).toString()); // no
        insertOccurrence(modelEligibleLocation3, "READY", null, 51.0, 0.4, now.minusDays(1).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesBasedCreationDateAccountingForValidatorTime() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", 0.5, 51.0, 0.4, now.minusDays(8).plusMillis(1).toString()); // yes
        insertOccurrence(modelEligibleLocation2, "READY", 0.5, 51.0, 0.4, now.minusDays(8).toString()); // no
        insertOccurrence(modelEligibleLocation3, "READY", 0.5, 51.0, 0.4, now.minusDays(8).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesBasedOnDistanceFromExtent() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.6, now.minusHours(1).toString()); // yes
        insertOccurrence(modelEligibleLocation2, "READY", null, 49.0, 0.6, now.minusHours(1).toString()); // no
        insertOccurrence(modelEligibleLocation3, "READY", null, 49.0, 0.6, now.minusHours(1).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesBasedEnvironmentalSuitability() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 49.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(modelEligibleLocation2, "READY", null, 49.0, 0.6, now.minusHours(1).toString()); // no
        insertOccurrence(modelEligibleLocation3, "READY", null, 49.0, 0.6, now.minusHours(1).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 1);
    }

    @Test
    public void getDistinctLocationsCountForTriggeringModelRunExcludesIdsUsedInLastRun() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        insertOccurrence(modelEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // yes
        insertOccurrence(excludedButEligibleLocation1, "READY", null, 51.0, 0.4, now.minusHours(1).toString()); // no

        // Act/Assert
        getDistinctLocationsCountForTriggeringModelRunActAssert(now, 1, 2);
    }

    private void getDistinctLocationsCountForTriggeringModelRunActAssert(LocalDateTime now, int expectedCountWithLastModelRunIds, int expectedCountWithoutLastModelRunIds) {
        HashSet<Integer> locationsFromLastModelRun = new HashSet<>(Arrays.asList(excludedButEligibleLocation1, excludedButEligibleLocation2));

        // Act
        long countWithLastModelRunIds = diseaseOccurrenceDao.getDistinctLocationsCountForTriggeringModelRun(
                1, locationsFromLastModelRun, now.minusDays(1).toDateTime(), now.minusDays(8).toDateTime(), 0.5, 50.0);
        long countWithoutLastModelRunIds = diseaseOccurrenceDao.getDistinctLocationsCountForTriggeringModelRun(
                1, new HashSet<Integer>(),  now.minusDays(1).toDateTime(), now.minusDays(8).toDateTime(), 0.5, 50.0);

        // Assert
        assertThat(countWithLastModelRunIds).isEqualTo(expectedCountWithLastModelRunIds);
        assertThat(countWithoutLastModelRunIds).isEqualTo(expectedCountWithoutLastModelRunIds);
    }

    private void insertOccurrence(int locationId, String status, Double expertWeighting, Double distanceFromDiseaseExtent, Double environmentalSuitability, String createdDate) {
        executeSQLUpdate(String.format(insertOccurrenceQuery, locationId, status, expertWeighting, distanceFromDiseaseExtent, environmentalSuitability, createdDate));
        flushAndClear();
    }

    @Test
    public void getDiseaseOccurrenceStatisticsWithSomeOccurrences() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        DiseaseOccurrenceStatistics statistics = diseaseOccurrenceDao.getDiseaseOccurrenceStatistics(diseaseGroupId);

        // Assert
        // Equivalent to:
        // select count(*) from disease_occurrence join location on location.id=location_id
        // where disease_group_id=87
        // and status in ('READY', 'IN_REVIEW', 'AWAITING_BATCHING')
        assertThat(statistics.getOccurrenceCount()).isEqualTo(45);
        // Equivalent to:
        // select count(*) from disease_occurrence join location on location.id=location_id
        // where disease_group_id=87
        // and status in ('READY', 'IN_REVIEW', 'AWAITING_BATCHING')
        // and model_eligible=TRUE
        assertThat(statistics.getModelEligibleOccurrenceCount()).isEqualTo(38);
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
        assertThat(statistics.getModelEligibleOccurrenceCount()).isEqualTo(0);
        assertThat(statistics.getMinimumOccurrenceDate()).isNull();
        assertThat(statistics.getMaximumOccurrenceDate()).isNull();
    }

    @Test
    public void getOccurrencesForBatchingInitialisation() {
        // There are 45 occurrences, but 5 are gold standard
        setOccurrencesToGoldStandard(274656, 273401, 275758, 275107, 274779);

        int diseaseGroupId = 87;

        // Act
        List<DiseaseOccurrence> occurrences =
                diseaseOccurrenceDao.getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId);

        // Assert
        assertThat(occurrences).hasSize(40);
    }

    @Test
    public void getOccurrencesForBatching() {
        // Arrange - the first 3 of these occurrences are before or on the batch end date, the others are after.
        // There is also an occurrence (275714) with status DISCARDED_FAILED_QC which is before the batch end date.
        // One of the 3 occurrences is gold standard.
        setOccurrencesToStatus(DiseaseOccurrenceStatus.AWAITING_BATCHING, 274656, 273401, 275758, 275107, 274779);
        setOccurrencesToGoldStandard(274656);

        int diseaseGroupId = 87;
        DateTime batchStartDate = new DateTime("2014-02-24T02:45:35");
        DateTime batchEndDate = new DateTime("2014-02-25T02:45:35");

        // Act
        List<DiseaseOccurrence> occurrences =
                diseaseOccurrenceDao.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDate, batchEndDate);

        // Assert
        assertThat(occurrences).hasSize(2);
    }

    @Test
    public void getDiseaseOccurrencesForTrainingPredictor() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseOccurrence recentOccurrence = occurrenceForTrainingPredictor(diseaseGroupId, DateTime.now().minusWeeks(51));
        DiseaseOccurrence oldOccurrence = occurrenceForTrainingPredictor(diseaseGroupId, DateTime.now().minusWeeks(52));
        // Act
        List<DiseaseOccurrence> occurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForTrainingPredictor(diseaseGroupId);
        // Assert
        assertThat(occurrences).contains(recentOccurrence);
        assertThat(occurrences).doesNotContain(oldOccurrence);
    }

    private DiseaseOccurrence occurrenceForTrainingPredictor(int diseaseGroupId, DateTime occurrenceDate) {
        DiseaseOccurrence o = new DiseaseOccurrence();
        o.setOccurrenceDate(occurrenceDate);

        // Set properties required by named query
        o.setDiseaseGroup(diseaseGroupDao.getById(diseaseGroupId));
        o.setStatus(DiseaseOccurrenceStatus.READY);
        o.setLocation(locationDao.getById(80));
        o.setDistanceFromDiseaseExtent(100.0);
        o.setEnvironmentalSuitability(0.2);
        o.setExpertWeighting(0.15);
        // Also set required not-null properties in order to save occurrence
        o.setAlert(alertDao.getById(212855));

        diseaseOccurrenceDao.save(o);
        return o;
    }

    @Test
    public void getNumberOfOccurrencesEligibleForModelRun() {
        // Arrange
        // Set some points in our date range to IN_REVIEW and AWAITING_BATCHING (including occurrences with COUNTRY
        // precision, which should not be selected)
        setOccurrencesToStatus(DiseaseOccurrenceStatus.IN_REVIEW, 275761, 275219);
        setOccurrencesToStatus(DiseaseOccurrenceStatus.AWAITING_BATCHING, 275758, 274790);

        int diseaseGroupId = 87;
        DateTime startDate = new DateTime("2014-02-25");
        DateTime endDate = new DateTime("2014-02-26T15:35:21Z");

        long count = diseaseOccurrenceDao.getNumberOfOccurrencesEligibleForModelRun(diseaseGroupId, startDate,
                endDate);

        // Equivalent to:
        // select count(*) from disease_occurrence join location on location.id=location_id
        // where disease_group_id=87
        // and status in ('READY', 'IN_REVIEW', 'AWAITING_BATCHING')
        // and model_eligible=TRUE
        // and occurrence_date >= '2014-02-25T00:00:00Z' and occurrence_date <= '2014-02-26T15:35:21Z'
        assertThat(count).isEqualTo(28);
    }

    @Test
    public void getDefaultBiasOccurrencesForModelRun() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        DiseaseGroup asc = diseaseGroupDao.getById(22);
        DateTime startDate = new DateTime("2014-02-25T00:00:00");
        DateTime endDate = new DateTime("2014-02-27T03:00:00");
        int initialAscCount = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(asc, startDate, endDate).size();
        int initialDengCount = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(deng, startDate, endDate).size();
        assertThat(initialAscCount).isEqualTo(0);
        assertThat(initialDengCount).isEqualTo(1);

        // Act
        createDiseaseOccurrence(1, deng, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(2, deng, DiseaseOccurrenceStatus.DISCARDED_UNUSED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(3, deng, DiseaseOccurrenceStatus.AWAITING_BATCHING, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(4, deng, DiseaseOccurrenceStatus.IN_REVIEW, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(5, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // model ineligible
        createDiseaseOccurrence(6, deng, DiseaseOccurrenceStatus.READY, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(7, deng, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 80, new DateTime("2014-02-26T00:00:00"));
        createBiasDiseaseOccurrence(8, deng, DiseaseOccurrenceStatus.BIAS, 80, new DateTime("2014-02-26T00:00:00"), deng);
        createDiseaseOccurrence(9, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // outside extent

        List<DiseaseOccurrence> resultAsc = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(asc, startDate, endDate);
        List<DiseaseOccurrence> resultDeng = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(deng, startDate, endDate);

        // Assert
        assertThat(resultAsc).hasSize(initialAscCount + 5);
        assertThat(resultDeng).hasSize(initialDengCount);
    }

    @Test
    public void getDefaultBiasOccurrencesForModelRunWithAgentFilter() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        deng.setFilterBiasDataByAgentType(true);
        deng.setAgentType(DiseaseGroupAgentType.VIRUS);
        diseaseGroupDao.save(deng);
        DiseaseGroup asc = diseaseGroupDao.getById(22);
        asc.setAgentType(DiseaseGroupAgentType.BACTERIA);
        asc.setFilterBiasDataByAgentType(true);
        diseaseGroupDao.save(asc);
        DateTime startDate = new DateTime("2014-02-25T00:00:00");
        DateTime endDate = new DateTime("2014-02-27T03:00:00");
        int initialAscCount = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(asc, startDate, endDate).size();
        int initialDengCount = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(deng, startDate, endDate).size();
        assertThat(initialAscCount).isEqualTo(0);
        assertThat(initialDengCount).isEqualTo(0); // The 1 from the filter-less variant is no longer added

        // Act
        createDiseaseOccurrence(1, deng, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(2, deng, DiseaseOccurrenceStatus.DISCARDED_UNUSED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(3, deng, DiseaseOccurrenceStatus.AWAITING_BATCHING, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(4, deng, DiseaseOccurrenceStatus.IN_REVIEW, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(5, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // model ineligible
        createDiseaseOccurrence(6, deng, DiseaseOccurrenceStatus.READY, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(7, deng, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 80, new DateTime("2014-02-26T00:00:00"));
        createBiasDiseaseOccurrence(8, deng, DiseaseOccurrenceStatus.BIAS, 80, new DateTime("2014-02-26T00:00:00"), deng);
        createDiseaseOccurrence(9, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // outside extent

        List<DiseaseOccurrence> resultAsc = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(asc, startDate, endDate);
        List<DiseaseOccurrence> resultDeng = diseaseOccurrenceDao.getDefaultBiasOccurrencesForModelRun(deng, startDate, endDate);

        // Assert
        assertThat(resultAsc).hasSize(initialAscCount); // No of the act deng points get added as deng has different agent
        assertThat(resultDeng).hasSize(initialDengCount);
    }

    @Test
    public void getBespokeBiasOccurrencesForModelRun() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        DiseaseGroup malaria = diseaseGroupDao.getById(202);
        createBiasDiseaseOccurrence(1, malaria, DiseaseOccurrenceStatus.BIAS, 12, new DateTime("2014-02-26T00:00:00"), deng);
        createBiasDiseaseOccurrence(2, malaria, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 12, new DateTime("2014-02-26T00:00:00"), deng); // failed qc
        createBiasDiseaseOccurrence(3, malaria, DiseaseOccurrenceStatus.BIAS, 6, new DateTime("2014-02-26T00:00:00"), deng); // outside extent
        createBiasDiseaseOccurrence(4, malaria, DiseaseOccurrenceStatus.BIAS, 12, new DateTime("2014-02-28T00:00:00"), deng); // out of date range
        createBiasDiseaseOccurrence(5, malaria, DiseaseOccurrenceStatus.BIAS, 40, new DateTime("2014-02-26T00:00:00"), deng); // model ineligible
        DateTime startDate = new DateTime("2014-02-25T00:00:00");
        DateTime endDate = new DateTime("2014-02-27T03:00:00");

        // Act
        List<DiseaseOccurrence> result = diseaseOccurrenceDao.getBespokeBiasOccurrencesForModelRun(deng, startDate, endDate);

        // Assert
        assertThat(result).hasSize(1);
    }

    @Test
    public void getCountOfUnfilteredBespokeBiasOccurrences() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        DiseaseGroup malaria = diseaseGroupDao.getById(202);
        createBiasDiseaseOccurrence(1, malaria, DiseaseOccurrenceStatus.BIAS, 12, new DateTime("2014-02-26T00:00:00"), deng);
        createBiasDiseaseOccurrence(2, malaria, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 12, new DateTime("2014-02-26T00:00:00"), deng); // failed qc
        createBiasDiseaseOccurrence(3, malaria, DiseaseOccurrenceStatus.BIAS, 6, new DateTime("2014-02-26T00:00:00"), deng); // outside extent
        createBiasDiseaseOccurrence(4, malaria, DiseaseOccurrenceStatus.BIAS, 12, new DateTime("2014-02-28T00:00:00"), deng); // out of date range
        createBiasDiseaseOccurrence(5, malaria, DiseaseOccurrenceStatus.BIAS, 40, new DateTime("2014-02-26T00:00:00"), deng); // model ineligible
        DateTime startDate = new DateTime("2014-02-25T00:00:00");
        DateTime endDate = new DateTime("2014-02-27T03:00:00");

        // Act
        long result = diseaseOccurrenceDao.getCountOfUnfilteredBespokeBiasOccurrences(deng);

        // Assert
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void getEstimateCountOfFilteredBespokeBiasOccurrences() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        DiseaseGroup malaria = diseaseGroupDao.getById(202);
        createBiasDiseaseOccurrence(1, malaria, DiseaseOccurrenceStatus.BIAS, 12, new DateTime("2014-02-26T00:00:00"), deng);
        createBiasDiseaseOccurrence(2, malaria, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 12, new DateTime("2014-02-26T00:00:00"), deng); // failed qc
        createBiasDiseaseOccurrence(3, malaria, DiseaseOccurrenceStatus.BIAS, 6, new DateTime("2014-02-26T00:00:00"), deng); // outside extent
        createBiasDiseaseOccurrence(4, malaria, DiseaseOccurrenceStatus.BIAS, 12, new DateTime("2014-02-28T00:00:00"), deng); // out of date range
        createBiasDiseaseOccurrence(5, malaria, DiseaseOccurrenceStatus.BIAS, 40, new DateTime("2014-02-26T00:00:00"), deng); // model ineligible

        // Act
        long result = diseaseOccurrenceDao.getEstimateCountOfFilteredBespokeBiasOccurrences(deng);

        // Assert
        assertThat(result).isEqualTo(2);
    }

    @Test
     public void getEstimateCountOfFilteredDefaultBiasOccurrences() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        createDiseaseOccurrence(1, deng, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(2, deng, DiseaseOccurrenceStatus.DISCARDED_UNUSED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(3, deng, DiseaseOccurrenceStatus.AWAITING_BATCHING, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(4, deng, DiseaseOccurrenceStatus.IN_REVIEW, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(5, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // model ineligible
        createDiseaseOccurrence(6, deng, DiseaseOccurrenceStatus.READY, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(7, deng, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 80, new DateTime("2014-02-26T00:00:00"));
        createBiasDiseaseOccurrence(8, deng, DiseaseOccurrenceStatus.BIAS, 80, new DateTime("2014-02-26T00:00:00"), deng);
        createDiseaseOccurrence(9, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // outside extent

        // Act
        long result = diseaseOccurrenceDao.getEstimateCountOfFilteredDefaultBiasOccurrences(deng);

        // Assert
        assertThat(result).isEqualTo(5);
    }

    @Test
    public void getEstimateCountOfFilteredDefaultBiasOccurrencesWithAgentFilter() {
        // Arrange
        DiseaseGroup deng = diseaseGroupDao.getById(87);
        deng.setFilterBiasDataByAgentType(true);
        diseaseGroupDao.save(deng);
        createDiseaseOccurrence(1, deng, DiseaseOccurrenceStatus.DISCARDED_UNREVIEWED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(2, deng, DiseaseOccurrenceStatus.DISCARDED_UNUSED, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(3, deng, DiseaseOccurrenceStatus.AWAITING_BATCHING, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(4, deng, DiseaseOccurrenceStatus.IN_REVIEW, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(5, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // model ineligible
        createDiseaseOccurrence(6, deng, DiseaseOccurrenceStatus.READY, 80, new DateTime("2014-02-26T00:00:00"));
        createDiseaseOccurrence(7, deng, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 80, new DateTime("2014-02-26T00:00:00"));
        createBiasDiseaseOccurrence(8, deng, DiseaseOccurrenceStatus.BIAS, 80, new DateTime("2014-02-26T00:00:00"), deng);
        createDiseaseOccurrence(9, deng, DiseaseOccurrenceStatus.READY, 6, new DateTime("2014-02-26T00:00:00")); // outside extent

        // Act
        long result = diseaseOccurrenceDao.getEstimateCountOfFilteredDefaultBiasOccurrences(deng);

        // Assert
        assertThat(result).isEqualTo(4);
    }

    private void getDiseaseOccurrencesForDiseaseExtent(int diseaseGroupId, Double minimumValidationWeight,
                                                       DateTime minimumOccurrenceDate,
                                                       boolean onlyUseGoldStandardOccurrences,
                                                       int expectedOccurrenceCount) {
        // Arrange
        addManuallyUploadedOccurrences();

        // Act
        List<DiseaseOccurrence> occurrences =
                diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeight,
                        minimumOccurrenceDate, onlyUseGoldStandardOccurrences);

        // Assert
        assertThat(occurrences).hasSize(expectedOccurrenceCount);
    }

    private Alert createAlert() {
        Feed feed = feedDao.getByProvenanceName(ProvenanceNames.HEALTHMAP).get(0);
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

    private void setOccurrencesToStatus(DiseaseOccurrenceStatus status, Integer... ids) {
        for (DiseaseOccurrence occurrence : diseaseOccurrenceDao.getByIds(Arrays.asList(ids))) {
            occurrence.setStatus(status);
            diseaseOccurrenceDao.save(occurrence);
        }
    }

    private void setOccurrencesToGoldStandard(Integer... ids) {
        Provenance provenance = provenanceDao.getByName(ProvenanceNames.MANUAL_GOLD_STANDARD);
        Feed feed = new Feed();
        feed.setProvenance(provenance);
        feed.setName("foo");
        feedDao.save(feed);
        Alert alert = new Alert();
        alert.setFeed(feed);
        alertDao.save(alert);
        for (DiseaseOccurrence occurrence : diseaseOccurrenceDao.getByIds(Arrays.asList(ids))) {
            occurrence.setAlert(alert);
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

    private void addManuallyUploadedOccurrences() {
        createManuallyUploadedDiseaseOccurrenceForDengue(null, false);
        createManuallyUploadedDiseaseOccurrenceForDengue(0.9, false);
        createManuallyUploadedDiseaseOccurrenceForDengue(1.0, true);
        createManuallyUploadedDiseaseOccurrenceForDengue(1.0, true);
    }

    private void createManuallyUploadedDiseaseOccurrenceForDengue(Double finalWeighting, boolean isGoldStandard) {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        Location location = locationDao.getById(80);
        Alert alert = new Alert();
        if (isGoldStandard) {
            alert.setFeed(goldStandardFeed);
        } else {
            alert.setFeed(testFeed);
        }

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setAlert(alert);
        occurrence.setFinalWeighting(finalWeighting);
        occurrence.setFinalWeightingExcludingSpatial(finalWeighting);
        occurrence.setStatus(DiseaseOccurrenceStatus.READY);
        occurrence.setOccurrenceDate(DateTime.now());
        diseaseOccurrenceDao.save(occurrence);
    }
}
