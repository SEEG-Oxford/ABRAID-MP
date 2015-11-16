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
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;
    private ModelRunDao modelRunDao;
    private DiseaseExtentClassDao diseaseExtentClassDao;
    private NativeSQL nativeSQL;
    private int maxDaysOnValidator;

    @Before
    public void setUp() {
        diseaseOccurrenceDao = mock(DiseaseOccurrenceDao.class);
        diseaseOccurrenceReviewDao = mock(DiseaseOccurrenceReviewDao.class);
        diseaseGroupDao = mock(DiseaseGroupDao.class);
        validatorDiseaseGroupDao = mock(ValidatorDiseaseGroupDao.class);
        adminUnitDiseaseExtentClassDao = mock(AdminUnitDiseaseExtentClassDao.class);
        modelRunDao = mock(ModelRunDao.class);
        diseaseExtentClassDao = mock(DiseaseExtentClassDao.class);
        nativeSQL = mock(NativeSQL.class);
        maxDaysOnValidator = 5;
        diseaseService = new DiseaseServiceImpl(diseaseOccurrenceDao, diseaseOccurrenceReviewDao, diseaseGroupDao,
                validatorDiseaseGroupDao, adminUnitDiseaseExtentClassDao,
                modelRunDao, diseaseExtentClassDao, maxDaysOnValidator, nativeSQL);
    }

    @Test
    public void saveDiseaseOccurrence() {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        diseaseService.saveDiseaseOccurrence(occurrence);
        verify(diseaseOccurrenceDao).save(eq(occurrence));
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
     public void getDiseaseGroupsNeedingOccurrenceReviewByExpert() {
        // Arrange
        Expert expert = mock(Expert.class);
        when(expert.getId()).thenReturn(123);
        List<DiseaseGroup> diseaseGroups = Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class), mock(DiseaseGroup.class));
        when(diseaseGroupDao.getDiseaseGroupsNeedingOccurrenceReviewByExpert(123)).thenReturn(diseaseGroups);

        // Act
        List<DiseaseGroup> result = diseaseService.getDiseaseGroupsNeedingOccurrenceReviewByExpert(expert);

        // Assert
        assertThat(result).isSameAs(diseaseGroups);
        verify(diseaseGroupDao).getDiseaseGroupsNeedingOccurrenceReviewByExpert(123);
    }

    @Test
    public void getDiseaseGroupsNeedingExtentReviewByExpert() {
        // Arrange
        Expert expert = mock(Expert.class);
        when(expert.getId()).thenReturn(123);
        List<DiseaseGroup> diseaseGroups = Arrays.asList(mock(DiseaseGroup.class), mock(DiseaseGroup.class), mock(DiseaseGroup.class));
        when(diseaseGroupDao.getDiseaseGroupsNeedingExtentReviewByExpert(123)).thenReturn(diseaseGroups);

        // Act
        List<DiseaseGroup> result = diseaseService.getDiseaseGroupsNeedingExtentReviewByExpert(expert);

        // Assert
        assertThat(result).isSameAs(diseaseGroups);
        verify(diseaseGroupDao).getDiseaseGroupsNeedingExtentReviewByExpert(123);
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
    public void getDistinctLocationsCountForTriggeringModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DateTime cutoff = DateTime.now();
        long expectedCount = 9;
        double minDistanceFromDiseaseExtent = 3;
        double maxEnvironmentalSuitability = 4;

        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(diseaseGroupId);
        when(diseaseGroup.getMinDistanceFromDiseaseExtentForTriggering()).thenReturn(minDistanceFromDiseaseExtent);
        when(diseaseGroup.getMaxEnvironmentalSuitabilityForTriggering()).thenReturn(maxEnvironmentalSuitability);
        ModelRun lastModelRun = mock(ModelRun.class);
        when(modelRunDao.getLastRequestedModelRun(diseaseGroup.getId())).thenReturn(lastModelRun);
        List<DiseaseOccurrence> occurrences = createOccurrences();
        when(lastModelRun.getInputDiseaseOccurrences()).thenReturn(occurrences);

        when(diseaseOccurrenceDao.getDistinctLocationsCountForTriggeringModelRun(
                anyInt(),
                anySetOf(Integer.class),
                any(DateTime.class),
                any(DateTime.class),
                anyDouble(),
                anyDouble()
        )).thenReturn(expectedCount);

        // Act
        long count = diseaseService.getDistinctLocationsCountForTriggeringModelRun(diseaseGroup, cutoff);

        // Assert
        verify(diseaseOccurrenceDao).getDistinctLocationsCountForTriggeringModelRun(
                        eq(diseaseGroupId),
                        eq(new HashSet<>(Arrays.asList(1, 2))),
                        eq(cutoff),
                        eq(cutoff.withTimeAtStartOfDay().minusDays(maxDaysOnValidator)),
                        eq(maxEnvironmentalSuitability),
                        eq(minDistanceFromDiseaseExtent));

        assertThat(count).isEqualTo(expectedCount);
    }

    private List<DiseaseOccurrence> createOccurrences() {
        DiseaseOccurrence o1 = createOccurrence(1);
        DiseaseOccurrence o2 = createOccurrence(2);
        DiseaseOccurrence o3 = createOccurrence(1);
        return Arrays.asList(o1, o2, o3);
    }

    private DiseaseOccurrence createOccurrence(int id) {
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        Location location = mock(Location.class);
        when(occurrence.getLocation()).thenReturn(location);
        when(location.getId()).thenReturn(id);
        return occurrence;
    }

    @Test
    public void getLatestChangeDateForDiseaseExtentClassByDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = 10;
        DateTime expectedTime = DateTime.now().minusDays(3);

        when(adminUnitDiseaseExtentClassDao.getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(diseaseGroupId))
                .thenReturn(expectedTime);

        // Act
        DateTime result =
                diseaseService.getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(diseaseGroupId);

        // Assert
        assertThat(result).isSameAs(expectedTime);
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
    public void getDiseaseGroupNamesForHealthMapReport() {
        // Arrange
        List<String> expected = Arrays.asList("A", "B", "C");
        when(diseaseGroupDao.getDiseaseGroupNamesForHealthMapReport()).thenReturn(expected);

        // Act
        List<String> actual = diseaseService.getDiseaseGroupNamesForHealthMapReport();

        // Assert
        assertThat(actual).isSameAs(expected);
    }

    @Test
    public void subtractMaxDaysOnValidator() {
        // Arrange
        DateTime inputDateTime = new DateTime("2014-10-09T12:13:14");
        LocalDate expectedResult = new LocalDate("2014-10-04"); // minus 5 days (maxDaysOnValidator field)

        // Act
        LocalDate actualResult = diseaseService.subtractMaxDaysOnValidator(inputDateTime);

        // Assert
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void getSupplementaryOccurrencesForModelRun() {
        // Arrange
        int diseaseGroupId = 123;
        DateTime startDate = DateTime.now().minusDays(1234);
        DateTime endDate = DateTime.now().minusDays(234);
        List<DiseaseOccurrence> expected = Arrays.asList(new DiseaseOccurrence(), new DiseaseOccurrence());
        when(diseaseOccurrenceDao.getSupplementaryOccurrencesForModelRun(diseaseGroupId, startDate, endDate)).thenReturn(expected);

        // Act
        List<DiseaseOccurrence> result = diseaseService.getSupplementaryOccurrencesForModelRun(diseaseGroupId, startDate, endDate);

        // Assert
        verify(diseaseOccurrenceDao).getSupplementaryOccurrencesForModelRun(diseaseGroupId, startDate, endDate);
        assertThat(result).isEqualTo(expected);
    }
}
