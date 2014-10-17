package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseService class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseServiceTest extends AbstractCommonSpringUnitTests {
    @Autowired
    private DiseaseService diseaseService;

    @Test
    public void saveDiseaseOccurrence() {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        diseaseService.saveDiseaseOccurrence(occurrence);
        verify(diseaseOccurrenceDao).save(eq(occurrence));
    }

    @Test
    public void saveHealthMapDisease() {
        HealthMapDisease disease = new HealthMapDisease();
        diseaseService.saveHealthMapDisease(disease);
        verify(healthMapDiseaseDao).save(eq(disease));
    }

    @Test
    public void saveAdminUnitDiseaseExtentClass() {
        AdminUnitDiseaseExtentClass disease = new AdminUnitDiseaseExtentClass();
        diseaseService.saveAdminUnitDiseaseExtentClass(disease);
        verify(adminUnitDiseaseExtentClassDao).save(eq(disease));
    }

    @Test
    public void getAllDiseaseExtentClasses() {
        // Arrange
        List<DiseaseExtentClass> diseaseExtentClasses = Arrays.asList(new DiseaseExtentClass());
        when(diseaseExtentClassDao.getAll()).thenReturn(diseaseExtentClasses);

        // Act
        List<DiseaseExtentClass> testDiseaseExtentClasses = diseaseService.getAllDiseaseExtentClasses();

        // Assert
        assertThat(testDiseaseExtentClasses).isSameAs(diseaseExtentClasses);
    }

    @Test
    public void getAllHealthMapDiseases() {
        // Arrange
        List<HealthMapDisease> diseases = Arrays.asList(new HealthMapDisease());
        when(healthMapDiseaseDao.getAll()).thenReturn(diseases);

        // Act
        List<HealthMapDisease> testDiseases = diseaseService.getAllHealthMapDiseases();

        // Assert
        assertThat(testDiseases).isSameAs(diseases);
    }

    @Test
    public void getAllDiseaseGroups() {
        // Arrange
        List<DiseaseGroup> diseaseGroups = Arrays.asList(new DiseaseGroup());
        when(diseaseGroupDao.getAll()).thenReturn(diseaseGroups);

        // Act
        List<DiseaseGroup> testDiseaseGroups = diseaseService.getAllDiseaseGroups();

        // Assert
        assertThat(testDiseaseGroups).isSameAs(diseaseGroups);
    }

    @Test
    public void getAllValidatorDiseaseGroups() {
        // Arrange
        List<ValidatorDiseaseGroup> expectedValidatorDiseaseGroups = Arrays.asList(new ValidatorDiseaseGroup());
        when(validatorDiseaseGroupDao.getAll()).thenReturn(expectedValidatorDiseaseGroups);

        // Act
        List<ValidatorDiseaseGroup> actualValidatorDiseaseGroups = diseaseService.getAllValidatorDiseaseGroups();

        // Assert
        assertThat(actualValidatorDiseaseGroups).isSameAs(expectedValidatorDiseaseGroups);
    }

    @Test
    public void getValidatorDiseaseGroupMap() {
        // Arrange
        ValidatorDiseaseGroup validatorDiseaseGroup1 = new ValidatorDiseaseGroup("ascariasis");
        ValidatorDiseaseGroup validatorDiseaseGroup2 = new ValidatorDiseaseGroup("trypanosomiases");
        DiseaseGroup diseaseGroup1 = new DiseaseGroup("Ascariasis", validatorDiseaseGroup1);
        DiseaseGroup diseaseGroup2 = new DiseaseGroup("Trypanosomiasis - American", validatorDiseaseGroup2);
        DiseaseGroup diseaseGroup3 = new DiseaseGroup("Poliomyelitis");
        DiseaseGroup diseaseGroup4 = new DiseaseGroup("Trypanosomiases", validatorDiseaseGroup2);
        diseaseGroup4.setAutomaticModelRunsStartDate(DateTime.now().minusDays(1));
        DiseaseGroup diseaseGroup5 = new DiseaseGroup("Trypanosomiasis - African", validatorDiseaseGroup2);
        diseaseGroup5.setAutomaticModelRunsStartDate(DateTime.now().minusDays(1));

        List<DiseaseGroup> diseaseGroups = Arrays.asList(diseaseGroup1, diseaseGroup2, diseaseGroup3, diseaseGroup4,
                diseaseGroup5);

        Map<String, List<DiseaseGroup>> expectedMap = new HashMap<>();
        expectedMap.put("ascariasis", Arrays.asList(diseaseGroup1));
        expectedMap.put("trypanosomiases", Arrays.asList(diseaseGroup4, diseaseGroup5, diseaseGroup2));

        when(diseaseGroupDao.getAll()).thenReturn(diseaseGroups);

        // Act
        Map<String, List<DiseaseGroup>> actualMap = diseaseService.getValidatorDiseaseGroupMap();

        // Assert
        assertThat(actualMap).isEqualTo(expectedMap);
    }

    @Test
    public void getDiseaseOccurrencesById() {
        // Arrange
        List<Integer> ids = new ArrayList<>();
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getByIds(ids)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesById(ids);

        // Assert
        assertThat(actualOccurrences).isSameAs(expectedOccurrences);
    }

    @Test
    public void getDiseaseOccurrencesByDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = 1;
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getByDiseaseGroupId(diseaseGroupId)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(actualOccurrences).isSameAs(expectedOccurrences);
    }

    @Test
    public void diseaseOccurrenceExists() {
        // Arrange
        Alert alert = new Alert(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Location location = new Location(1);
        DateTime occurrenceDate = DateTime.now();

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(alert);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setOccurrenceDate(occurrenceDate);

        DiseaseOccurrence returnedOccurrence = new DiseaseOccurrence();
        List<DiseaseOccurrence> occurrences = Arrays.asList(returnedOccurrence);
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(diseaseGroup, location, alert,
                occurrenceDate)).thenReturn(occurrences);

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isTrue();
    }

    @Test
    public void diseaseOccurrenceDoesNotExist() {
        // Arrange
        Alert alert = new Alert(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Location location = new Location(1);
        DateTime occurrenceDate = DateTime.now();

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(alert);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setOccurrenceDate(occurrenceDate);

        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(diseaseGroup, location, alert,
                occurrenceDate)).thenReturn(occurrences);

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseAlertIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(null);
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseDiseaseGroupIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(null);
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseLocationIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(null);
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseAlertIdIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert());
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseDiseaseGroupIdIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(new DiseaseGroup());
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseLocationIdIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(new Location());
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void doesDiseaseOccurrenceMatchDiseaseGroupReturnsExpectedResult() {
        // Arrange
        int validatorDiseaseGroupId = 1;
        ValidatorDiseaseGroup validatorDiseaseGroup = new ValidatorDiseaseGroup(validatorDiseaseGroupId);

        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);

        int occurrenceId = 2;
        DiseaseOccurrence occurrence = new DiseaseOccurrence(occurrenceId);
        occurrence.setDiseaseGroup(diseaseGroup);
        when(diseaseOccurrenceDao.getById(occurrenceId)).thenReturn(occurrence);

        // Act
        boolean diseaseOccurrenceMatches =
                diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(occurrenceId,
                        validatorDiseaseGroupId);
        boolean diseaseOccurrenceDoesNotMatch =
                diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(occurrenceId, 3);

        // Assert
        assertThat(diseaseOccurrenceMatches).isTrue();
        assertThat(diseaseOccurrenceDoesNotMatch).isFalse();
    }

    @Test
    public void getDiseaseExtentByDiseaseGroupIdReturnsGlobalExtentForGlobalDisease() {
        // Arrange
        int diseaseGroupId = 10;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(true);
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = new ArrayList<>();

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(adminUnitDiseaseExtentClassDao.getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId))
                .thenReturn(expectedDiseaseExtent);

        // Act
        List<AdminUnitDiseaseExtentClass> actualDiseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(actualDiseaseExtent).isSameAs(expectedDiseaseExtent);
    }

    @Test
    public void getDiseaseExtentByDiseaseGroupIdReturnsTropicalExtentForTropicalDisease() {
        // Arrange
        int diseaseGroupId = 10;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(false);
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = new ArrayList<>();

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId))
                .thenReturn(expectedDiseaseExtent);

        // Act
        List<AdminUnitDiseaseExtentClass> actualDiseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(actualDiseaseExtent).isSameAs(expectedDiseaseExtent);
    }

    @Test
    public void getDiseaseExtentByDiseaseGroupIdReturnsTropicalExtentForUnspecifiedDisease() {
        // Arrange
        int diseaseGroupId = 10;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = new ArrayList<>();

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId))
                .thenReturn(expectedDiseaseExtent);

        // Act
        List<AdminUnitDiseaseExtentClass> actualDiseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(actualDiseaseExtent).isSameAs(expectedDiseaseExtent);
    }

    @Test
    public void getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupIdReturnsGlobalsForGlobalDisease() {
        // Arrange
        int diseaseGroupId = 10;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(true);
        List<AdminUnitGlobal> expectedAdminUnits = new ArrayList<>();

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(adminUnitGlobalDao.getAll()).thenReturn(expectedAdminUnits);

        // Act
        List actualAdminUnits =
                diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId);

        // Assert
        //noinspection unchecked
        assertThat(actualAdminUnits).isSameAs(expectedAdminUnits);
    }

    @Test
    public void getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupIdReturnsTropicalsForTropicalDisease() {
        // Arrange
        int diseaseGroupId = 10;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(false);
        List<AdminUnitTropical> expectedAdminUnits = new ArrayList<>();

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(adminUnitTropicalDao.getAll()).thenReturn(expectedAdminUnits);

        // Act
        List actualAdminUnits =
                diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId);

        // Assert
        //noinspection unchecked
        assertThat(actualAdminUnits).isSameAs(expectedAdminUnits);
    }

    @Test
    public void getDiseaseExtentClass() {
        // Arrange
        DiseaseExtentClass expectedExtentClass = new DiseaseExtentClass();
        String name = DiseaseExtentClass.ABSENCE;
        when(diseaseExtentClassDao.getByName(name)).thenReturn(expectedExtentClass);

        // Act
        DiseaseExtentClass actualExtentClass = diseaseService.getDiseaseExtentClass(name);

        // Assert
        assertThat(actualExtentClass).isSameAs(expectedExtentClass);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentForGlobalDisease() {
        getDiseaseOccurrencesForDiseaseExtent(true);
    }

    @Test
    public void getDiseaseOccurrencesForDiseaseExtentForTropicalDisease() {
        getDiseaseOccurrencesForDiseaseExtent(false);
    }

    @Test
    public void updateAggregatedDiseaseExtent() {
        // Arrange
        int diseaseGroupId = 87;
        boolean isGlobal = true;

        // Act
        diseaseService.updateAggregatedDiseaseExtent(diseaseGroupId, isGlobal);

        // Assert
        verify(nativeSQL, times(1)).updateAggregatedDiseaseExtent(eq(diseaseGroupId), eq(isGlobal));
    }

    @Test
    public void getDiseaseOccurrenceStatistics() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseOccurrenceStatistics expectedStatistics =
                new DiseaseOccurrenceStatistics(1, DateTime.now(), DateTime.now());
        when(diseaseOccurrenceDao.getDiseaseOccurrenceStatistics(diseaseGroupId)).thenReturn(expectedStatistics);

        // Act
        DiseaseOccurrenceStatistics actualStatistics = diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId);

        // Assert
        assertThat(actualStatistics).isSameAs(expectedStatistics);
    }

    @Test
    public void getDiseaseGroupIdsForAutomaticModelRuns() {
        // Arrange
        List<Integer> expectedIDs = new ArrayList<>();
        when(diseaseGroupDao.getIdsForAutomaticModelRuns()).thenReturn(expectedIDs);

        // Act
        List<Integer> actualIDs = diseaseService.getDiseaseGroupIdsForAutomaticModelRuns();

        // Assert
        assertThat(actualIDs).isSameAs(expectedIDs);
    }

    @Test
    public void getDiseaseOccurrenceIDsForBatching() {
        // Arrange
        int diseaseGroupId = 1;
        DateTime batchEndDate = DateTime.now();
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForBatching(diseaseGroupId, batchEndDate)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesForBatching(diseaseGroupId, batchEndDate);

        // Assert
        assertThat(actualOccurrences).isSameAs(expectedOccurrences);
    }


    @Test
    public void getDiseaseOccurrencesForModelRunRequest() {
        // Arrange
        int diseaseGroupId = 87;
        boolean useGoldStandardOccurrences = true;
        List<DiseaseOccurrence> occurrences = Arrays.asList(new DiseaseOccurrence());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, useGoldStandardOccurrences)).thenReturn(occurrences);

        // Act
        List<DiseaseOccurrence> testOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, useGoldStandardOccurrences);

        // Assert
        assertThat(testOccurrences).isSameAs(occurrences);
    }

    private void getDiseaseOccurrencesForDiseaseExtent(boolean isGlobal) {
        // Arrange
        int diseaseGroupId = 10;
        double minimumValidationWeighting = 0.7;
        boolean useGoldStandardOccurrences = true;
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(isGlobal);

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeighting,
                minimumOccurrenceDate, isGlobal, useGoldStandardOccurrences)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                diseaseGroupId, minimumValidationWeighting, minimumOccurrenceDate, useGoldStandardOccurrences);

        // Assert
        assertThat(expectedOccurrences).isSameAs(actualOccurrences);
    }
}
