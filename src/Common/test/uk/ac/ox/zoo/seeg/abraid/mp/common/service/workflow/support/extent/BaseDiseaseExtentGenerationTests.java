package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test utils for extent generation test.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class BaseDiseaseExtentGenerationTests {
    protected DiseaseExtentClass presenceDiseaseExtentClass;
    protected DiseaseExtentClass possiblePresenceDiseaseExtentClass;
    protected DiseaseExtentClass possibleAbsenceDiseaseExtentClass;
    protected DiseaseExtentClass absenceDiseaseExtentClass;
    protected DiseaseExtentClass uncertainDiseaseExtentClass;

    protected List<DiseaseExtentClass> diseaseExtentClasses;
    protected List<? extends AdminUnitGlobalOrTropical> adminUnits;
    protected Integer diseaseGroupId;
    protected DiseaseGroup diseaseGroup;

    protected DiseaseExtent parameters;

    protected void baseSetup() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());

        presenceDiseaseExtentClass = createDiseaseExtentClass(DiseaseExtentClass.PRESENCE, 100);
        possiblePresenceDiseaseExtentClass = createDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE, 50);
        possibleAbsenceDiseaseExtentClass = createDiseaseExtentClass(DiseaseExtentClass.POSSIBLE_ABSENCE, -50);
        absenceDiseaseExtentClass = createDiseaseExtentClass(DiseaseExtentClass.ABSENCE, -100);
        uncertainDiseaseExtentClass = createDiseaseExtentClass(DiseaseExtentClass.UNCERTAIN, 0);
        diseaseExtentClasses = createDiseaseExtentClasses();
        parameters = createParameters();
        diseaseGroupId = 87;
        diseaseGroup = createDiseaseGroup();
        adminUnits = createAdminUnits();
    }

    protected DiseaseExtentGenerationInputData createInputData(Collection<AdminUnitReview> reviews, Collection<DiseaseOccurrence> occurrences) {
        return new DiseaseExtentGenerationInputData(diseaseExtentClasses, adminUnits, reviews, occurrences);
    }

    protected DiseaseGroup createDiseaseGroup() {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.isGlobal()).thenReturn(false);
        when(mock.getId()).thenReturn(diseaseGroupId);
        when(mock.getDiseaseExtentParameters()).thenReturn(parameters);
        return mock;
    }

    protected List<DiseaseExtentClass> createDiseaseExtentClasses() {
        return createList(
                presenceDiseaseExtentClass,
                possiblePresenceDiseaseExtentClass,
                possibleAbsenceDiseaseExtentClass,
                absenceDiseaseExtentClass,
                uncertainDiseaseExtentClass
        );
    }

    protected DiseaseExtent createParameters() {
        DiseaseExtent diseaseExtent = mock(DiseaseExtent.class);
        when(diseaseExtent.getMinValidationWeighting()).thenReturn(0.2);
        when(diseaseExtent.getMaxMonthsAgoForHigherOccurrenceScore()).thenReturn(12);
        when(diseaseExtent.getLowerOccurrenceScore()).thenReturn(1);
        when(diseaseExtent.getHigherOccurrenceScore()).thenReturn(2);
        return diseaseExtent;
    }

    protected DiseaseExtentClass createDiseaseExtentClass(String name, Integer weighting) {
        DiseaseExtentClass extentClass = mock(DiseaseExtentClass.class);
        when(extentClass.getName()).thenReturn(name);
        when(extentClass.getWeighting()).thenReturn(weighting);
        return extentClass;
    }

    protected List<AdminUnitTropical> createAdminUnits() {
        return createList(
                createAdminUnit(100, 10, '1'),
                createAdminUnit(125, 20, '1'),
                createAdminUnit(130, 30, '1'),
                createAdminUnit(150, null, '0'),
                createAdminUnit(200, 20, '1'),
                createAdminUnit(250, 20, '1'),
                createAdminUnit(300, 30, '1'));
    }

    protected AdminUnitTropical createAdminUnit(Integer gaul, Integer countryGaul, char level) {
        AdminUnitTropical unit = mock(AdminUnitTropical.class);
        when(unit.getGaulCode()).thenReturn(gaul);
        when(unit.getCountryGaulCode()).thenReturn(countryGaul);
        when(unit.getLevel()).thenReturn(level);
        return unit;
    }

    protected List<DiseaseOccurrence> createOccurrences() {
        // 0 occurrences of global GAUL code 100, and 0 occurrences in its parent country (GAUL code 10) too
        // 0 occurrences of tropical GAUL code 125, and 4 occurrences (possibly present) in its parent country (GAUL code 20)
        // 0 occurrences of tropical GAUL code 130, but 10 occurrences (present) in its parent country (GAUL code 30)
        // 1 occurrence of global GAUL code 150 (all over 2 years old)
        // 4 occurrences of tropical GAUL code 200 (one of which is exactly 1 year old)
        // 5 occurrences of tropical GAUL code 250 (all over 2 years old)
        // 10 occurrences of global GAUL code 300 (all under 2 years old)
        return randomise(concatenate(
                createOccurrenceSet(150, 48, 1),
                createOccurrenceSet(200, 36, 3),
                createOccurrenceSet(200, 12, 1),
                createOccurrenceSet(250, 60, 5),
                createOccurrenceSet(300, 12, 5),
                createOccurrenceSet(300, 36, 5)
        ));
    }

    protected List<DiseaseOccurrence> createOccurrenceSet(int adminUnitGaulCode,
                                                        int numberOfMonthsAgo,
                                                        int numberOfTimes) {
        DateTime occurrenceDate = DateTime.now().minusMonths(numberOfMonthsAgo);
        Location location = createLocation(adminUnitGaulCode);
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        for (int i = 0; i < numberOfTimes; i++) {
            occurrences.add(createOccurrence(occurrenceDate, location));
        }
        return occurrences;
    }

    protected DiseaseOccurrence createOccurrence(DateTime occurrenceDate, Location location) {
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(occurrence.getLocation()).thenReturn(location);
        when(occurrence.getOccurrenceDate()).thenReturn(occurrenceDate);
        return occurrence;
    }

    protected Location createLocation(Integer adminUnitGaulCode) {
        Location location = mock(Location.class);
        when(location.getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(location.getAdminUnitTropicalGaulCode()).thenReturn(adminUnitGaulCode);
        return location;
    }

    protected List<AdminUnitReview> createReviews() {
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
        Expert expert1 = createExpert(1, 0);
        Expert expert2 = createExpert(2, 0.25);
        Expert expert3 = createExpert(3, 0.5);
        Expert expert4 = createExpert(4, 0.75);
        Expert expert5 = createExpert(5, 1);
        Expert expert6 = createExpert(6, 0);

        return randomise(createList(
                createReview(expert5, 130, absenceDiseaseExtentClass),
                createReview(expert3, 130, absenceDiseaseExtentClass),
                createReview(expert6, 130, absenceDiseaseExtentClass),
                createReview(expert1, 150, presenceDiseaseExtentClass),
                createReview(expert2, 150, possibleAbsenceDiseaseExtentClass),
                createReview(expert4, 150, possibleAbsenceDiseaseExtentClass),
                createReview(expert3, 200, presenceDiseaseExtentClass),
                createReview(expert2, 200, absenceDiseaseExtentClass),
                createReview(expert5, 200, possiblePresenceDiseaseExtentClass),
                createReview(expert4, 200, presenceDiseaseExtentClass),
                createReview(expert4, 250, presenceDiseaseExtentClass),
                createReview(expert3, 250, presenceDiseaseExtentClass),
                createReview(expert5, 250, presenceDiseaseExtentClass),
                createReview(expert2, 250, uncertainDiseaseExtentClass),
                createReview(expert5, 300, absenceDiseaseExtentClass),
                createReview(expert4, 300, possiblePresenceDiseaseExtentClass),
                createReview(expert3, 300, uncertainDiseaseExtentClass),
                createReview(expert2, 300, possibleAbsenceDiseaseExtentClass)
        ));
    }

    protected AdminUnitReview createReview(Expert expert, int gaulCode, DiseaseExtentClass response) {
        AdminUnitReview review = mock(AdminUnitReview.class);
        when(review.getExpert()).thenReturn(expert);
        when(review.getAdminUnitGlobalOrTropicalGaulCode()).thenReturn(gaulCode);
        when(review.getResponse()).thenReturn(response);
        return review;
    }

    protected Expert createExpert(int id, double weighting) {
        Expert expert = mock(Expert.class);
        when(expert.getId()).thenReturn(id);
        when(expert.getWeighting()).thenReturn(weighting);
        return expert;
    }

    private <T> List<T> concatenate(List<T>... inputLists) {
        List<T> outputList = new ArrayList<>();
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
        List<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }

    protected List<AdminUnitDiseaseExtentClass> createRandomExistingDiseaseExtent() {
        List<AdminUnitDiseaseExtentClass> list = new ArrayList<>();
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            int count = ThreadLocalRandom.current().nextInt(8);
            list.add(createAdminUnitDiseaseExtentClass(adminUnit,
                    diseaseExtentClasses.get(ThreadLocalRandom.current().nextInt(5)),
                    diseaseExtentClasses.get(ThreadLocalRandom.current().nextInt(5)),
                    count,
                    createOccurrenceSet(adminUnit.getGaulCode(), 3, Math.min(count, 5)),
                    DateTime.now().minusHours(3)));
        }
        return list;
    }

    private AdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(AdminUnitGlobalOrTropical adminUnit, DiseaseExtentClass validatorExtentClass, DiseaseExtentClass modellingExtentClass, int count, Collection<DiseaseOccurrence> latestOccurrences, DateTime classChangedDate) {
        AdminUnitDiseaseExtentClass result = new AdminUnitDiseaseExtentClass();
        result.setDiseaseGroup(diseaseGroup);
        result.setAdminUnitGlobalOrTropical(adminUnit);
        result.setValidatorDiseaseExtentClass(validatorExtentClass);
        result.setDiseaseExtentClass(modellingExtentClass);
        result.setClassChangedDate(classChangedDate);
        result.setValidatorOccurrenceCount(count);
        result.setLatestValidatorOccurrences(latestOccurrences);

        return result;
    }

    protected DiseaseExtentGenerationOutputData createInitialDiseaseExtentResults() {
        DiseaseExtentGenerationOutputData data = createEmptyResult();
        addAdminUnitResults(data, 100, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 125, possiblePresenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 130, presenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 150, possiblePresenceDiseaseExtentClass, 1);
        addAdminUnitResults(data, 200, possiblePresenceDiseaseExtentClass, 4);
        addAdminUnitResults(data, 250, presenceDiseaseExtentClass, 5);
        addAdminUnitResults(data, 300, presenceDiseaseExtentClass, 10);
        return data;
    }

    protected DiseaseExtentGenerationOutputData createAllUncertainExtentResults() {
        DiseaseExtentGenerationOutputData data = createEmptyResult();
        addAdminUnitResults(data, 100, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 125, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 130, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 150, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 200, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 250, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 300, uncertainDiseaseExtentClass, 0);
        return data;
    }

    protected DiseaseExtentGenerationOutputData createUpdatedDiseaseExtentOccurrencesOnlyResults() {
        DiseaseExtentGenerationOutputData data = createEmptyResult();
        addAdminUnitResults(data, 100, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 125, possiblePresenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 130, possiblePresenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 150, possiblePresenceDiseaseExtentClass, 1);
        addAdminUnitResults(data, 200, presenceDiseaseExtentClass, 4);
        addAdminUnitResults(data, 250, possiblePresenceDiseaseExtentClass, 5);
        addAdminUnitResults(data, 300, presenceDiseaseExtentClass, 10);
        return data;
    }

    protected DiseaseExtentGenerationOutputData createUpdatedDiseaseExtentReviewOnlyResults() {
        DiseaseExtentGenerationOutputData data = createEmptyResult();
        addAdminUnitResults(data, 100, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 125, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 130, possibleAbsenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 150, possibleAbsenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 200, possiblePresenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 250, presenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 300, possibleAbsenceDiseaseExtentClass, 0);
        return data;
    }

    protected DiseaseExtentGenerationOutputData createUpdatedDiseaseExtentOccurrencesAndReviewsResults() {
        DiseaseExtentGenerationOutputData data = createEmptyResult();
        addAdminUnitResults(data, 100, uncertainDiseaseExtentClass, 0);
        addAdminUnitResults(data, 125, possiblePresenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 130, possibleAbsenceDiseaseExtentClass, 0);
        addAdminUnitResults(data, 150, uncertainDiseaseExtentClass, 1);
        addAdminUnitResults(data, 200, possiblePresenceDiseaseExtentClass, 4);
        addAdminUnitResults(data, 250, presenceDiseaseExtentClass, 5);
        addAdminUnitResults(data, 300, possiblePresenceDiseaseExtentClass, 10);
        return data;
    }

    protected DiseaseExtentGenerationOutputData createRandomDiseaseExtentResults() {
        DiseaseExtentGenerationOutputData data = createEmptyResult();
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            int count = ThreadLocalRandom.current().nextInt(10);
            addAdminUnitResults(data, adminUnit.getGaulCode(), diseaseExtentClasses.get(ThreadLocalRandom.current().nextInt(5)),
                    count,
                    createOccurrenceSet(adminUnit.getGaulCode(), 3, Math.min(count, 5)));
        }
        return data;
    }

    protected DiseaseExtentGenerationOutputData createEmptyResult() {
        Map<Integer, DiseaseExtentClass> extent = new HashMap<>();
        Map<Integer, Integer> count = new HashMap<>();
        Map<Integer, Collection<DiseaseOccurrence>> latest = new HashMap<>();
        return new DiseaseExtentGenerationOutputData(extent, count, latest);
    }

    protected void addAdminUnitResults(DiseaseExtentGenerationOutputData data, Integer gaulCode, DiseaseExtentClass extentClass, int count, List<DiseaseOccurrence> occurrenceSet) {
        addAdminUnitResults(data, gaulCode, extentClass, count);
        data.getLatestOccurrencesByGaulCode().get(gaulCode).addAll(occurrenceSet);
    }

    protected void addAdminUnitResults(DiseaseExtentGenerationOutputData data, int gaul, DiseaseExtentClass extentClass, int count) {
        data.getDiseaseExtentClassByGaulCode().put(gaul, extentClass);
        data.getOccurrenceCounts().put(gaul, count);
        data.getLatestOccurrencesByGaulCode().put(gaul, new ArrayList<DiseaseOccurrence>());
    }

    protected DiseaseExtentClass extractDiseaseExtentClass(DiseaseExtentGenerationOutputData data, AdminUnitGlobalOrTropical adminUnit) {
        return data.getDiseaseExtentClassByGaulCode().get(adminUnit.getGaulCode());
    }

    protected Integer extractOccurrenceCount(DiseaseExtentGenerationOutputData data, AdminUnitGlobalOrTropical adminUnit) {
        return data.getOccurrenceCounts().get(adminUnit.getGaulCode());
    }

    protected Collection<DiseaseOccurrence> extractLatestOccurrences(DiseaseExtentGenerationOutputData data, AdminUnitGlobalOrTropical adminUnit) {
        return data.getLatestOccurrencesByGaulCode().get(adminUnit.getGaulCode());
    }
}
