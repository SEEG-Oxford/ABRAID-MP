package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

import java.util.*;

import static org.mockito.Matchers.*;
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

        diseaseGroup = new DiseaseGroup(diseaseGroupId, null, "Dengue", DiseaseGroupType.SINGLE);
        diseaseGroup.setGlobal(false);
    }

    @Test
    public void generateDiseaseExtentSetsAllToUncertainIfNoRelevantOccurrencesExist() {
        // Arrange
        DiseaseExtentParameters parameters = createParameters();
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getInitialDiseaseExtentAllUncertain();
        standardMocks();
        mockGetDiseaseOccurrencesForDiseaseExtent(parameters, new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        mockGetExistingDiseaseExtent(new ArrayList<AdminUnitDiseaseExtentClass>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, parameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(0);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateInitialDiseaseExtentForTypicalCase() {
        // Arrange
        DiseaseExtentParameters parameters = createParameters();
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getInitialDiseaseExtent();
        standardMocks();
        mockGetDiseaseOccurrencesForDiseaseExtent(parameters, getOccurrences());
        mockGetExistingDiseaseExtent(new ArrayList<AdminUnitDiseaseExtentClass>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, parameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(0);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentSetsAllToUncertainIfNoRelevantOccurrencesOrReviewsExist() {
        // Arrange - variables
        DiseaseExtentParameters parameters = createParameters();
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent();

        // Arrange - set the expected disease extent to be the initial disease extent, with all changed to uncertain
        // and hasClassChanged set appropriately
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getInitialDiseaseExtent();
        for (AdminUnitDiseaseExtentClass extentClass : expectedDiseaseExtent) {
            extentClass.setOccurrenceCount(0);
            if (extentClass.getDiseaseExtentClass().equals(uncertainDiseaseExtentClass)) {
                extentClass.setHasClassChanged(false);
            } else {
                extentClass.setDiseaseExtentClass(uncertainDiseaseExtentClass);
            }
        }

        // Arrange - mocks
        standardMocks();
        mockGetDiseaseOccurrencesForDiseaseExtent(parameters, new ArrayList<DiseaseOccurrenceForDiseaseExtent>());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(new ArrayList<AdminUnitReview>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, parameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentOccurrencesOnly() {
        // Arrange
        DiseaseExtentParameters parameters = createParameters();
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent();
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getUpdatedDiseaseExtentOccurrencesOnly();
        standardMocks();
        mockGetDiseaseOccurrencesForDiseaseExtent(parameters, getOccurrences());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(new ArrayList<AdminUnitReview>());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, parameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    @Test
    public void generateUpdatedDiseaseExtentOccurrencesAndReviews() {
        // Arrange
        DiseaseExtentParameters parameters = createParameters();
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = getInitialDiseaseExtent();
        List<AdminUnitDiseaseExtentClass> expectedDiseaseExtent = getUpdatedDiseaseExtentOccurrencesAndReviews();
        standardMocks();
        mockGetDiseaseOccurrencesForDiseaseExtent(parameters, getOccurrences());
        mockGetExistingDiseaseExtent(existingDiseaseExtent);
        mockGetRelevantReviews(getReviews());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, parameters);

        // Assert
        expectGetDiseaseOccurrencesForDiseaseExtent(1);
        expectGetRelevantReviews(1);
        expectSaveAdminUnitDiseaseExtentClass(expectedDiseaseExtent);
        expectUpdateAggregatedDiseaseExtent(1);
    }

    private DiseaseExtentParameters createParameters() {
        int maximumYearsAgo = 1;
        double minimumValidationWeighting = 0.2;
        int minimumOccurrencesForPresence = 5;
        int minimumOccurrencesForPossiblePresence = 1;
        int minimumYearsAgoForHigherOccurrenceScore = 2;
        int lowerOccurrenceScore = 1;
        int higherOccurrenceScore = 2;
        List<Integer> feedIds = new ArrayList<>();

        return new DiseaseExtentParameters(feedIds, maximumYearsAgo,
                minimumValidationWeighting, minimumOccurrencesForPresence, minimumOccurrencesForPossiblePresence,
                minimumYearsAgoForHigherOccurrenceScore, lowerOccurrenceScore, higherOccurrenceScore);
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

    private void mockGetDiseaseOccurrencesForDiseaseExtent(DiseaseExtentParameters parameters,
                                                           List<DiseaseOccurrenceForDiseaseExtent> occurrences) {
        double minimumValidationWeighting = parameters.getMinimumValidationWeighting();
        DateTime minimumOccurrenceDate = getFixedYearsAgo(parameters.getMaximumYearsAgo());
        List<Integer> feedIds = parameters.getFeedIds();

        when(diseaseService.getDiseaseOccurrencesForDiseaseExtent(eq(diseaseGroupId), eq(minimumValidationWeighting),
                eq(minimumOccurrenceDate), same(feedIds))).thenReturn(occurrences);
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
                any(DateTime.class), anyListOf(Integer.class));
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
        // 4 occurrences of tropical GAUL code 200 (one of which is exactly 2 years old)
        // 5 occurrences of tropical GAUL code 250 (all over 2 years old)
        // 10 occurrences of global GAUL code 300 (all under 2 years old)
        return randomise(concatenate(
                createOccurrences(150, 4, 1),
                createOccurrences(200, 3, 3),
                createOccurrences(200, 2, 1),
                createOccurrences(250, 5, 5),
                createOccurrences(300, 1, 5),
                createOccurrences(300, 3, 5)
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
                new AdminUnitGlobal(100, 10),
                new AdminUnitTropical(125, 20),
                new AdminUnitTropical(130, 30),
                new AdminUnitGlobal(150),
                new AdminUnitTropical(200, 20),
                new AdminUnitTropical(250, 20),
                new AdminUnitGlobal(300, 30)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getInitialDiseaseExtent() {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, possiblePresenceDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, presenceDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, possiblePresenceDiseaseExtentClass, 1),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, possiblePresenceDiseaseExtentClass, 4),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, presenceDiseaseExtentClass, 5),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, presenceDiseaseExtentClass, 10)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getInitialDiseaseExtentAllUncertain() {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, uncertainDiseaseExtentClass, 0),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, uncertainDiseaseExtentClass, 0)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getUpdatedDiseaseExtentOccurrencesOnly() {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, possiblePresenceDiseaseExtentClass, 0, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, presenceDiseaseExtentClass, 0, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, possiblePresenceDiseaseExtentClass, 1, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, presenceDiseaseExtentClass, 4, true),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, possiblePresenceDiseaseExtentClass, 5, true),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, presenceDiseaseExtentClass, 10, false)
        );
    }

    private List<AdminUnitDiseaseExtentClass> getUpdatedDiseaseExtentOccurrencesAndReviews() {
        return createList(
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(100), diseaseGroup, uncertainDiseaseExtentClass, 0, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(125), diseaseGroup, possiblePresenceDiseaseExtentClass, 0, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(130), diseaseGroup, absenceDiseaseExtentClass, 0, true),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(150), diseaseGroup, uncertainDiseaseExtentClass, 1, true),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(200), diseaseGroup, possiblePresenceDiseaseExtentClass, 4, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitTropical(250), diseaseGroup, presenceDiseaseExtentClass, 5, false),
                new AdminUnitDiseaseExtentClass(getAdminUnitGlobal(300), diseaseGroup, possiblePresenceDiseaseExtentClass, 10, true)
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


    private DateTime getFixedYearsAgo(int yearsAgo) {
        return DateTime.now().minusYears(yearsAgo);
    }

    private List<DiseaseOccurrenceForDiseaseExtent> createOccurrences(int adminUnitGaulCode,
                                                                      int numberOfYearsAgo,
                                                                      int numberOfTimes) {
        DateTime occurrenceDate = DateTime.now().minusYears(numberOfYearsAgo);
        List<DiseaseOccurrenceForDiseaseExtent> occurrences = new ArrayList<>();
        for (int i = 0; i < numberOfTimes; i++) {
            occurrences.add(new DiseaseOccurrenceForDiseaseExtent(occurrenceDate, adminUnitGaulCode));
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
