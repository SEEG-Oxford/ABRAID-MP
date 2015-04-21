package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseService class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseServiceTest {
    private DiseaseService diseaseService;
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;
    private DiseaseGroupDao diseaseGroupDao;
    private HealthMapDiseaseDao healthMapDiseaseDao;
    private HealthMapSubDiseaseDao healthMapSubDiseaseDao;
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;
    private DiseaseExtentClassDao diseaseExtentClassDao;
    private NativeSQL nativeSQL;

    @Before
    public void setUp() {
        diseaseOccurrenceDao = mock(DiseaseOccurrenceDao.class);
        diseaseOccurrenceReviewDao = mock(DiseaseOccurrenceReviewDao.class);
        diseaseGroupDao = mock(DiseaseGroupDao.class);
        healthMapDiseaseDao = mock(HealthMapDiseaseDao.class);
        healthMapSubDiseaseDao = mock(HealthMapSubDiseaseDao.class);
        validatorDiseaseGroupDao = mock(ValidatorDiseaseGroupDao.class);
        adminUnitDiseaseExtentClassDao = mock(AdminUnitDiseaseExtentClassDao.class);
        diseaseExtentClassDao = mock(DiseaseExtentClassDao.class);
        nativeSQL = mock(NativeSQL.class);
        diseaseService = new DiseaseServiceImpl(diseaseOccurrenceDao, diseaseOccurrenceReviewDao, diseaseGroupDao,
                healthMapDiseaseDao, healthMapSubDiseaseDao, validatorDiseaseGroupDao, adminUnitDiseaseExtentClassDao,
                diseaseExtentClassDao, 5, 6, nativeSQL);
    }

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
    public void getAllHealthMapSubDiseases() {
        // Arrange
        List<HealthMapSubDisease> subDiseases = Arrays.asList(new HealthMapSubDisease());
        when(healthMapSubDiseaseDao.getAll()).thenReturn(subDiseases);

        // Act
        List<HealthMapSubDisease> testSubDiseases = diseaseService.getAllHealthMapSubDiseases();

        // Assert
        assertThat(testSubDiseases).isSameAs(subDiseases);
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
    public void getDiseaseOccurrencesByDiseaseGroupIdAndStatus() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseOccurrenceStatus status = DiseaseOccurrenceStatus.IN_REVIEW;
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getByDiseaseGroupIdAndStatuses(diseaseGroupId, status)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences =
                diseaseService.getDiseaseOccurrencesByDiseaseGroupIdAndStatuses(diseaseGroupId, status);

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
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(diseaseGroupId);
        when(diseaseGroup.isGlobal()).thenReturn(isGlobal);

        // Act
        diseaseService.updateAggregatedDiseaseExtent(diseaseGroup);

        // Assert
        verify(nativeSQL).updateAggregatedDiseaseExtent(eq(diseaseGroupId), eq(isGlobal));
    }

    @Test
    public void getDiseaseOccurrenceStatistics() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseOccurrenceStatistics expectedStatistics =
                new DiseaseOccurrenceStatistics(1, 0, DateTime.now(), DateTime.now());
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
    public void getDiseaseOccurrencesForBatchingInitialisation() {
        // Arrange
        int diseaseGroupId = 1;
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesForBatchingInitialisation(diseaseGroupId);

        // Assert
        assertThat(actualOccurrences).isSameAs(expectedOccurrences);
    }

    @Test
    public void getDiseaseOccurrencesForBatching() {
        // Arrange
        int diseaseGroupId = 1;
        DateTime batchStartDate = DateTime.now();
        DateTime batchEndDate = DateTime.now().plusDays(1);
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDate, batchEndDate)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesForBatching(diseaseGroupId, batchStartDate, batchEndDate);

        // Assert
        assertThat(actualOccurrences).isSameAs(expectedOccurrences);
    }

    @Test
    public void getDiseaseOccurrencesForModelRunRequest() {
        // Arrange
        int diseaseGroupId = 87;
        boolean onlyUseGoldStandardOccurrences = true;
        List<DiseaseOccurrence> occurrences = Arrays.asList(new DiseaseOccurrence());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, onlyUseGoldStandardOccurrences)).thenReturn(occurrences);

        // Act
        List<DiseaseOccurrence> testOccurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(
                diseaseGroupId, onlyUseGoldStandardOccurrences);

        // Assert
        assertThat(testOccurrences).isSameAs(occurrences);
    }

    private void getDiseaseOccurrencesForDiseaseExtent(boolean isGlobal) {
        // Arrange
        int diseaseGroupId = 10;
        double minimumValidationWeighting = 0.7;
        boolean onlyUseGoldStandardOccurrences = true;
        DateTime minimumOccurrenceDate = DateTime.now();
        List<DiseaseOccurrence> expectedOccurrences = new ArrayList<>();
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setGlobal(isGlobal);

        when(diseaseGroupDao.getById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, minimumValidationWeighting,
                minimumOccurrenceDate, onlyUseGoldStandardOccurrences)).thenReturn(expectedOccurrences);

        // Act
        List<DiseaseOccurrence> actualOccurrences = diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                diseaseGroupId, minimumValidationWeighting, minimumOccurrenceDate, onlyUseGoldStandardOccurrences);

        // Assert
        assertThat(expectedOccurrences).isSameAs(actualOccurrences);
    }

    @Test
    public void subtractMaxDaysOnValidator() {
        // Arrange
        DateTime inputDateTime = new DateTime("2014-10-09T12:13:14");
        LocalDate expectedResult = new LocalDate("2014-10-04"); // minus 5

        // Act
        LocalDate actualResult = diseaseService.subtractMaxDaysOnValidator(inputDateTime);

        // Assert
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void subtractDaysBetweenModelRuns() {
        // Arrange
        DateTime inputDateTime = new DateTime("2014-10-09T12:13:14");
        LocalDate expectedResult = new LocalDate("2014-10-03"); // minus 6

        // Act
        LocalDate actualResult = diseaseService.subtractDaysBetweenModelRuns(inputDateTime);

        // Assert
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
