package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the DiseaseExtentGenerator class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorTest {
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private DiseaseService diseaseService;
    private ExpertService expertService;

    private DiseaseExtentClass presenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.PRESENCE, 100);
    private DiseaseExtentClass possiblePresenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE, 50);
    private DiseaseExtentClass uncertainDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.UNCERTAIN, 0);
    private DiseaseExtentClass possibleAbsenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.POSSIBLE_ABSENCE, -50);
    private DiseaseExtentClass absenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.ABSENCE, -100);

    private int diseaseGroupId = 87;
    private DiseaseGroup diseaseGroup;
    private List<? extends AdminUnitGlobalOrTropical> adminUnits;
    private DateTime minimumOccurrenceDate;
    private int maximumMonthsAgo = 24;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        expertService = mock(ExpertService.class);
        adminUnits = getAdminUnits();

        diseaseExtentGenerator = new DiseaseExtentGenerator(diseaseService, expertService);
        mockGetDiseaseExtentClass(presenceDiseaseExtentClass);
        mockGetDiseaseExtentClass(possiblePresenceDiseaseExtentClass);
        mockGetDiseaseExtentClass(uncertainDiseaseExtentClass);
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        diseaseGroup = createDiseaseGroup();
        minimumOccurrenceDate = getFixedMonthsAgo(maximumMonthsAgo);
    }

    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup group = new DiseaseGroup(diseaseGroupId, null, "Dengue", DiseaseGroupType.SINGLE);
        group.setGlobal(false);
        group.setAutomaticModelRunsStartDate(DateTime.now());

        DiseaseExtent parameters = createParameters();
        group.setDiseaseExtentParameters(parameters);

        return group;
    }

    @Test
    public void generateDiseaseExtentSetsAllToUncertainIfNoRelevantOccurrencesExist() {
        // Arrange
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getInitialDiseaseExtentAllUncertain(DateTime.now());
        standardMocks();
        mockGetDiseaseOccurrencesForUpdatedDiseaseExtent(diseaseGroup.getDiseaseExtentParameters(),
                                                         new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        mockGetExistingDiseaseExtent(new ArrayList<AdminUnitDiseaseExtentClass>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(0);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateInitialDiseaseExtentForTypicalCase() {
        // Arrange
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getInitialDiseaseExtent(DateTime.now());
        standardMocks();
        mockGetDiseaseOccurrencesForInitialDiseaseExtent(getOccurrences());
        mockGetExistingDiseaseExtent(new ArrayList<AdminUnitDiseaseExtentClass>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(0);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentSetsAllToUncertainIfNoRelevantOccurrencesOrReviewsExist() {
        // Arrange - variables
        DateTime createdDate = DateTime.now().minusDays(1);
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent(createdDate);

        // Arrange - set the expected disease extent to be the initial disease extent, with all changed to uncertain
        // and classChangedDate set appropriately
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getInitialDiseaseExtent(createdDate);
        for (AdminUnitDiseaseExtentClass extentClass : expectedDiseaseExtent) {
            extentClass.setOccurrenceCount(0);
            if (!extentClass.getDiseaseExtentClass().equals(uncertainDiseaseExtentClass)) {
                extentClass.setDiseaseExtentClass(uncertainDiseaseExtentClass);
                extentClass.setClassChangedDate(DateTime.now());
            }
        }

        // Arrange - mocks
        standardMocks();
        mockGetDiseaseOccurrencesForUpdatedDiseaseExtent(diseaseGroup.getDiseaseExtentParameters(),
                                                         new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(new ArrayList<AdminUnitReview>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentOccurrencesOnly() {
        // Arrange
        DateTime createdDate = DateTime.now().minusDays(1);
        DateTime updatedDate = DateTime.now();
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent(createdDate);
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getUpdatedDiseaseExtentOccurrencesOnly(createdDate, updatedDate);
        standardMocks();
        mockGetDiseaseOccurrencesForUpdatedDiseaseExtent(diseaseGroup.getDiseaseExtentParameters(), getOccurrences());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(new ArrayList<AdminUnitReview>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentOccurrencesAndReviews() {
        // Arrange
        DateTime createdDate = DateTime.now().minusDays(1);
        DateTime updatedDate = DateTime.now();
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent(createdDate);
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getUpdatedDiseaseExtentOccurrencesAndReviews(createdDate, updatedDate);
        standardMocks();
        mockGetDiseaseOccurrencesForUpdatedDiseaseExtent(diseaseGroup.getDiseaseExtentParameters(), getOccurrences());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(getReviews());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentOccurrencesOnlyWithAutomaticModelRunsDisabled() {
        // Arrange
        DiseaseExtent parameters = diseaseGroup.getDiseaseExtentParameters();
        DateTime createdDate = DateTime.now().minusDays(1);
        DateTime updatedDate = DateTime.now();
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent(createdDate);
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getUpdatedDiseaseExtentOccurrencesOnly(createdDate, updatedDate);
        standardMocks();
        when(diseaseService.getDiseaseOccurrencesForDiseaseExtent(eq(diseaseGroupId),
                eq(parameters.getMinValidationWeighting()), eq((DateTime) null))).thenReturn(getOccurrences());

        mockGetDiseaseOccurrencesForUpdatedDiseaseExtent(parameters, getOccurrences());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(new ArrayList<AdminUnitReview>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    private DiseaseExtent createParameters() {
        double minimumValidationWeighting = 0.2;
        int minimumOccurrencesForPresence = 5;
        int minimumOccurrencesForPossiblePresence = 1;
        int minimumMonthsAgoForHigherOccurrenceScore = 12;
        int lowerOccurrenceScore = 1;
        int higherOccurrenceScore = 2;

        return new DiseaseExtent(diseaseGroup,
                minimumValidationWeighting, minimumOccurrencesForPresence, minimumOccurrencesForPossiblePresence,
                minimumMonthsAgoForHigherOccurrenceScore, lowerOccurrenceScore, higherOccurrenceScore);
    }

    private void standardMocks() {
        mockGetAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId();
        mockGetDiseaseGroupById();
        mockGetAllDiseaseExtentClasses();
    }

    private void mockGetExistingDiseaseExtent(List<AdminUnitDiseaseExtentClass> diseaseExtent) {
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId)).thenReturn(diseaseExtent);
    }

    private void mockGetAllDiseaseExtentClasses() {
        when(diseaseService.getAllDiseaseExtentClasses()).thenReturn(createList(
                presenceDiseaseExtentClass, possiblePresenceDiseaseExtentClass, uncertainDiseaseExtentClass,
                possibleAbsenceDiseaseExtentClass, absenceDiseaseExtentClass
        ));
    }

    private void mockGetDiseaseOccurrencesForInitialDiseaseExtent(List<DiseaseOccurrenceForDiseaseExtent> occurrences) {
        when(diseaseService.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId, null, null)).thenReturn(occurrences);
    }

    private void mockGetDiseaseOccurrencesForUpdatedDiseaseExtent(DiseaseExtent parameters,
                                                                  List<DiseaseOccurrenceForDiseaseExtent> occurrences) {
        double minimumValidationWeighting = parameters.getMinValidationWeighting();

        when(diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                eq(diseaseGroupId), eq(minimumValidationWeighting), eq(getFixedMonthsAgo(maximumMonthsAgo)))).thenReturn(occurrences);
    }

    private void mockGetAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId() {
        when(diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId))
                .thenAnswer(convertToAnswer(adminUnits));
    }

    private void mockGetDiseaseGroupById() {
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
    }

    private void mockGetDiseaseExtentClass(DiseaseExtentClass diseaseExtentClass) {
        when(diseaseService.getDiseaseExtentClass(diseaseExtentClass.getName())).thenReturn(diseaseExtentClass);
    }

    private void mockGetRelevantReviews(List<AdminUnitReview> reviews) {
        when(expertService.getAllAdminUnitReviewsForDiseaseGroup(diseaseGroupId)).thenReturn(reviews);
    }

    private void expectGetDiseaseOccurrencesForDiseaseExtent(int times) {
        verify(diseaseService, times(times)).getDiseaseOccurrencesForDiseaseExtent(anyInt(), anyDouble(),
                any(DateTime.class));
    }

    private void expectGetRelevantReviews(int times) {
        verify(expertService, times(times)).getAllAdminUnitReviewsForDiseaseGroup(anyInt());
    }

    private void expectSaveAdminUnitDiseaseExtentClass(List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent) {
        verify(diseaseService, times(expectedDiseaseExtent.size())).saveAdminUnitDiseaseExtentClass(
                any(AdminUnitDiseaseExtentClass.class));

        for (AdminUnitDiseaseExtentClass extentClass : expectedDiseaseExtent) {
            verify(diseaseService, times(1)).saveAdminUnitDiseaseExtentClass(eq(extentClass));
        }
    }

    private void expectUpdateAggregatedDiseaseExtent(int times) {
        verify(diseaseService, times(times)).updateAggregatedDiseaseExtent(eq(diseaseGroupId),
                eq(diseaseGroup.isGlobal()));
    }

    private List<DiseaseOccurrenceForDiseaseExtent> getOccurrences() {
        // 0 occurrences of global GAUL code 100, and 0 occurrences in its parent country (GAUL code 10) too
        // 0 occurrences of tropical GAUL code 125, and 4 occurrences (possibly present) in its parent country (GAUL code 20)
        // 0 occurrences of tropical GAUL code 130, but 10 occurrences (present) in its parent country (GAUL code 30)
        // 1 occurrence of global GAUL code 150 (all over 2 years old)
        // 4 occurrences of tropical GAUL code 200 (one of which is exactly 1 year old)
        // 5 occurrences of tropical GAUL code 250 (all over 2 years old)
        // 10 occurrences of global GAUL code 300 (all under 2 years old)
        return randomise(concatenate(
                createOccurrences(150, 48, 1),
                createOccurrences(200, 36, 3),
                createOccurrences(200, 12, 1),
                createOccurrences(250, 60, 5),
                createOccurrences(300, 12, 5),
                createOccurrences(300, 36, 5)
        ));
    }

    private List<AdminUnitReview> getReviews() {
        // The disease extent class in the comments below is what is assigned when these reviews are combined with
        // the occurrences returned by getOccurrences().
        //
        // GAUL code 100: No reviews -> uncertain
        // GAUL code 125: No reviews -> possible presence
        // GAUL code 130: Reviews average just over -1 -> absence
        // GAUL code 150: Reviews average exactly -1 -> uncertain (i.e. exactly cancel out the occurrences)
        // GAUL code 200: Reviews average exactly -4 -> possible presence (i.e. score is 1 when combined with the occurrences)
        // GAUL code 250: Reviews average just over 1 when combined with the occurrences -> presence
        // GAUL code 300: Reviews average just under 1 when combined with the occurrences -> possible presence
        Expert expert1 = createExpert(0);
        Expert expert2 = createExpert(0.25);
        Expert expert3 = createExpert(0.5);
        Expert expert4 = createExpert(0.75);
        Expert expert5 = createExpert(1);
        Expert expert6 = new Expert();

        return randomise(createList(
                new AdminUnitReview(expert5, null, 130, diseaseGroup, absenceDiseaseExtentClass),
                new AdminUnitReview(expert3, null, 130, diseaseGroup, possibleAbsenceDiseaseExtentClass),
                new AdminUnitReview(expert6, null, 130, diseaseGroup, absenceDiseaseExtentClass),
                new AdminUnitReview(expert1, null, 150, diseaseGroup, presenceDiseaseExtentClass),
                new AdminUnitReview(expert2, null, 150, diseaseGroup, possibleAbsenceDiseaseExtentClass),
                new AdminUnitReview(expert4, null, 150, diseaseGroup, possibleAbsenceDiseaseExtentClass),
                new AdminUnitReview(expert3, null, 200, diseaseGroup, presenceDiseaseExtentClass),
                new AdminUnitReview(expert2, null, 200, diseaseGroup, absenceDiseaseExtentClass),
                new AdminUnitReview(expert5, null, 200, diseaseGroup, possiblePresenceDiseaseExtentClass),
                new AdminUnitReview(expert4, null, 200, diseaseGroup, presenceDiseaseExtentClass),
                new AdminUnitReview(expert4, null, 250, diseaseGroup, presenceDiseaseExtentClass),
                new AdminUnitReview(expert3, null, 250, diseaseGroup, presenceDiseaseExtentClass),
                new AdminUnitReview(expert5, null, 250, diseaseGroup, presenceDiseaseExtentClass),
                new AdminUnitReview(expert2, null, 250, diseaseGroup, uncertainDiseaseExtentClass),
                new AdminUnitReview(expert5, null, 300, diseaseGroup, absenceDiseaseExtentClass),
                new AdminUnitReview(expert4, null, 300, diseaseGroup, possiblePresenceDiseaseExtentClass),
                new AdminUnitReview(expert3, null, 300, diseaseGroup, uncertainDiseaseExtentClass),
                new AdminUnitReview(expert2, null, 300, diseaseGroup, possibleAbsenceDiseaseExtentClass)
        ));
    }

    private List<? extends AdminUnitGlobalOrTropical> getAdminUnits() {
        return createList(
                new AdminUnitGlobal(100, 10, '1'),
                new AdminUnitTropical(125, 20, '1'),
                new AdminUnitTropical(130, 30, '1'),
                new AdminUnitGlobal(150, null, '0'),
                new AdminUnitTropical(200, 20, '1'),
                new AdminUnitTropical(250, 20, '1'),
                new AdminUnitGlobal(300, 30, '1')
        );
    }

    private List<AdminUnitDiseaseExtentClass> getInitialDiseaseExtent(DateTime createdDate) {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, possiblePresenceDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, presenceDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, possiblePresenceDiseaseExtentClass, 1, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, possiblePresenceDiseaseExtentClass, 4, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, presenceDiseaseExtentClass, 5, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, presenceDiseaseExtentClass, 10, createdDate)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getInitialDiseaseExtentAllUncertain(DateTime createdDate) {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getUpdatedDiseaseExtentOccurrencesOnly(DateTime createdDate, DateTime updatedDate) {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, possiblePresenceDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, presenceDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, possiblePresenceDiseaseExtentClass, 1, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, presenceDiseaseExtentClass, 4, updatedDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, possiblePresenceDiseaseExtentClass, 5, updatedDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, presenceDiseaseExtentClass, 10, createdDate)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getUpdatedDiseaseExtentOccurrencesAndReviews(DateTime createdDate, DateTime updatedDate) {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, possiblePresenceDiseaseExtentClass, 0, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, possibleAbsenceDiseaseExtentClass, 0, updatedDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, uncertainDiseaseExtentClass, 1, updatedDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, possiblePresenceDiseaseExtentClass, 4, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, presenceDiseaseExtentClass, 5, createdDate),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, possiblePresenceDiseaseExtentClass, 10, updatedDate)
        );
    }

    private <A extends AdminUnitGlobalOrTropical> Answer<List<A>> convertToAnswer(final List<A> values) {
        // This is necessary to support types with bounded wildcards
        return new Answer<List<A>>() {
            public List<A> answer(InvocationOnMock invocation) throws Throwable {
                return values;
            }
        };
    }


    private DateTime getFixedMonthsAgo(int monthsAgo) {
        return DateTime.now().minusMonths(monthsAgo);
    }

    private List<DiseaseOccurrenceForDiseaseExtent> createOccurrences(int adminUnitGaulCode,
                                                                      int numberOfMonthsAgo,
                                                                      int numberOfTimes) {
        DateTime occurrenceDate = DateTime.now().minusMonths(numberOfMonthsAgo);
        List<DiseaseOccurrenceForDiseaseExtent> occurrences = new ArrayList<>();
        for (int i = 0; i < numberOfTimes; i++) {
            occurrences.add(new DiseaseOccurrenceForDiseaseExtent(occurrenceDate, LocationPrecision.ADMIN1,
                    adminUnitGaulCode));
        }
        return occurrences;
    }

    private AdminUnitGlobal getAdminUnitGlobal(int gaulCode) {
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            if (adminUnit instanceof AdminUnitGlobal && adminUnit.getGaulCode() == gaulCode) {
                return (AdminUnitGlobal) adminUnit;
            }
        }
        return null;
    }

    private AdminUnitTropical getAdminUnitTropical(int gaulCode) {
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            if (adminUnit instanceof AdminUnitTropical && adminUnit.getGaulCode() == gaulCode) {
                return (AdminUnitTropical) adminUnit;
            }
        }
        return null;
    }

    private <T> List<T> concatenate(List<T>... inputLists) {
        List<T> outputList = new ArrayList<T>();
        for (List<T> inputList : inputLists) {
            outputList.addAll(inputList);
        }
        return outputList;
    }

    private <T> List<T> randomise(List<T> list) {
        Collections.shuffle(list, new Random(System.nanoTime()));
        return list;
    }

    private <T> List<T> createList(T... items) {
        List<T> list = new ArrayList<T>();
        Collections.addAll(list, items);
        return list;
    }

    private Expert createExpert(double weighting) {
        Expert expert = new Expert();
        expert.setWeighting(weighting);
        return expert;
    }
}
