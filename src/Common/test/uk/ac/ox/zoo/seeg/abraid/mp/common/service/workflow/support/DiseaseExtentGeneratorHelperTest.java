package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Tests the DiseaseExtentGeneratorHelper class.
 * This mostly tests non-typical cases, as DiseaseExtentGeneratorTest tests typical cases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorHelperTest {
    private DiseaseGroup defaultDiseaseGroup = new DiseaseGroup(87, null, "Dengue", DiseaseGroupType.SINGLE);
    private DiseaseExtentParameters defaultParameters = new DiseaseExtentParameters(60, 0.6, 5, 1, 24, 1, 2);
    private List<? extends AdminUnitGlobalOrTropical> defaultAdminUnits = createDefaultAdminUnits();

    private List<AdminUnitDiseaseExtentClass> emptyDiseaseExtent = new ArrayList<>();
    private List<DiseaseOccurrenceForDiseaseExtent> emptyOccurrences = new ArrayList<>();
    private List<AdminUnitReview> emptyReviews = new ArrayList<>();

    // Disease extent classes
    private DiseaseExtentClass presenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.PRESENCE, 100);
    private DiseaseExtentClass possiblePresenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.POSSIBLE_PRESENCE, 50);
    private DiseaseExtentClass uncertainDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.UNCERTAIN, 0);
    private DiseaseExtentClass possibleAbsenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.POSSIBLE_ABSENCE, -50);
    private DiseaseExtentClass absenceDiseaseExtentClass = new DiseaseExtentClass(DiseaseExtentClass.ABSENCE, -100);
    private List<DiseaseExtentClass> defaultDiseaseExtentClasses = createList(presenceDiseaseExtentClass,
            possiblePresenceDiseaseExtentClass, uncertainDiseaseExtentClass, possibleAbsenceDiseaseExtentClass,
            absenceDiseaseExtentClass);

    @Before
    public void setUp() {
        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    @Test
    public void groupOccurrencesByAdminUnitWithNoOccurrencesCreatesEmptyMappings() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();

        // Act
        helper.groupOccurrencesByAdminUnit();

        // Assert
        assertThat(helper.getOccurrencesByAdminUnit()).hasSize(defaultAdminUnits.size());
        for (List<DiseaseOccurrenceForDiseaseExtent> occurrences : helper.getOccurrencesByAdminUnit().values()) {
            assertThat(occurrences).isEmpty();
        }
    }

    @Test
    public void groupOccurrencesByCountryWithNoOccurrencesCreatesNoMappings() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();

        // Act
        helper.groupOccurrencesByCountry();

        // Assert
        assertThat(helper.getNumberOfOccurrencesByCountry()).isEmpty();
    }

    @Test
    public void groupReviewsByAdminUnitWithNoReviewsCreatesNoMappings() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();
        helper.setReviews(emptyReviews);

        // Act
        helper.groupReviewsByAdminUnit();

        // Assert
        assertThat(helper.getReviewsByAdminUnit()).isEmpty();
    }

    @Test
    public void computeDiseaseExtentClassUsingOccurrenceCountWithDefaultParameters() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();

        // Act and assert
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(0, 1)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(1, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(2, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(3, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(4, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(5, 1)).isEqualTo(presenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(6, 1)).isEqualTo(presenceDiseaseExtentClass);

        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(0, 2)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(1, 2)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(2, 2)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(9, 2)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(10, 2)).isEqualTo(presenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(11, 2)).isEqualTo(presenceDiseaseExtentClass);
    }

    @Test
    public void computeDiseaseExtentClassUsingOccurrenceCountWithNonDefaultParameters() {
        // Arrange
        // Minimum occurrences for presence = 8, for possible presence = 4
        DiseaseExtentParameters parameters = new DiseaseExtentParameters(60, 0.6, 8, 4, 24, 1, 2);
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper(parameters);

        // Act and assert
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(0, 1)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(1, 1)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(2, 1)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(3, 1)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(4, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(5, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(6, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(7, 1)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(8, 1)).isEqualTo(presenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(9, 1)).isEqualTo(presenceDiseaseExtentClass);

        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(0, 2)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(7, 2)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(8, 2)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(15, 2)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(16, 2)).isEqualTo(presenceDiseaseExtentClass);
        assertThat(helper.computeDiseaseExtentClassUsingOccurrenceCount(17, 2)).isEqualTo(presenceDiseaseExtentClass);
    }

    @Test
    public void computeScoreForOccurrencesAndReviewsWithNoOccurrencesOrReviews() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();
        double expectedScore = 0;

        // Act
        double actualScore = helper.computeScoreForOccurrencesAndReviews(emptyOccurrences, emptyReviews);

        // Assert
        assertThat(actualScore).isEqualTo(expectedScore);
    }

    @Test
    public void computeScoreForOccurrencesOnlyAndNonDefaultParameters() {
        // Arrange
        // Maximum months = 84, maximum months for higher score = 36, lower score = 10, higher score = 20
        DiseaseExtentParameters parameters = new DiseaseExtentParameters(84, 0.6, 5, 1, 36, 10, 20);
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper(parameters);

        List<DiseaseOccurrenceForDiseaseExtent> occurrences = createList(
                createOccurrence(0), createOccurrence(12), createOccurrence(24), createOccurrence(36),
                createOccurrence(48), createOccurrence(60), createOccurrence(72));
        // Expected score is (20 * 4 + 10 * 3) / 7  (which we calculate ourselves in case of int-to-double issues)
        double expectedScore = 15.71429;

        // Act
        double actualScore = helper.computeScoreForOccurrencesAndReviews(occurrences, emptyReviews);

        // Assert
        assertThat(actualScore).isEqualTo(expectedScore, offset(0.000005));
    }

    @Test
    public void computeScoreForReviewsOnlyAndNonDefaultParameters() {
        // Arrange
        // Maximum months = 84, maximum months for higher score = 36, lower score = 10, higher score = 20
        DiseaseExtentParameters parameters = new DiseaseExtentParameters(84, 0.6, 5, 1, 36, 10, 20);
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper(parameters);

        List<AdminUnitReview> reviews = createList(
                createReview(absenceDiseaseExtentClass, 0),
                createReview(possibleAbsenceDiseaseExtentClass, 0.25),
                createReview(uncertainDiseaseExtentClass, 0.5),
                createReview(possiblePresenceDiseaseExtentClass, 0.75),
                createReview(presenceDiseaseExtentClass, 1));

        // Expected score is (-2*0 + -1*0.25 + 0*0.5 + 1*0.75 + 2*1) / 5
        double expectedScore = 0.5;

        // Act
        double actualScore = helper.computeScoreForOccurrencesAndReviews(emptyOccurrences, reviews);

        // Assert
        assertThat(actualScore).isEqualTo(expectedScore);
    }


    @Test
    public void computeScoreForOccurrencesAndReviewsAndNonDefaultParameters() {
        // Arrange
        // Maximum months = 7 x 12 = 84, maximum months for higher score = 3 x 12 = 36, lower score = 2, higher score = 3
        DiseaseExtentParameters parameters = new DiseaseExtentParameters(84, 0.6, 5, 1, 36, 2, 3);
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper(parameters);

        List<DiseaseOccurrenceForDiseaseExtent> occurrences = createList(
                createOccurrence(0), createOccurrence(12), createOccurrence(24), createOccurrence(36),
                createOccurrence(48), createOccurrence(60), createOccurrence(72));

        List<AdminUnitReview> reviews = createList(
                createReview(absenceDiseaseExtentClass, 0),
                createReview(possibleAbsenceDiseaseExtentClass, 0.25),
                createReview(uncertainDiseaseExtentClass, 0.5),
                createReview(possiblePresenceDiseaseExtentClass, 0.75),
                createReview(presenceDiseaseExtentClass, 1));

        // Expected score is ((3 * 4 + 2 * 3) + (-2*0 + -1*0.25 + 0*0.5 + 1*0.75 + 2*1)) / (7 + 5)
        double expectedScore = 1.70833;

        // Act
        double actualScore = helper.computeScoreForOccurrencesAndReviews(occurrences, reviews);

        // Assert
        assertThat(actualScore).isEqualTo(expectedScore, offset(0.000005));
    }

    @Test
    public void computeDiseaseExtentClassForCountryWhenGaulCodeIsNull() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();

        // Act
        DiseaseExtentClass actualExtentClass = helper.computeDiseaseExtentClassForCountry(null);

        // Assert
        assertThat(actualExtentClass).isEqualTo(uncertainDiseaseExtentClass);
    }

    @Test
    public void computeDiseaseExtentClassForCountryWhenGaulCodeIsNotInMapping() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();
        helper.groupOccurrencesByCountry();
        int countryGaulCode = 123456789;

        // Act
        DiseaseExtentClass actualExtentClass = helper.computeDiseaseExtentClassForCountry(countryGaulCode);

        // Assert
        assertThat(actualExtentClass).isEqualTo(uncertainDiseaseExtentClass);
    }

    @Test
    public void computeDiseaseExtentClassForCountryWhenGaulCodeIsInMapping() {
        // Arrange
        DiseaseExtentGeneratorHelper helper = createDefaultDiseaseExtentGeneratorHelper();
        helper.groupOccurrencesByCountry();
        int countryGaulCode = 123456789;

        // Act and assert
        assertThat(computeDiseaseExtentClassForCountry(helper, countryGaulCode, 0)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(computeDiseaseExtentClassForCountry(helper, countryGaulCode, 1)).isEqualTo(uncertainDiseaseExtentClass);
        assertThat(computeDiseaseExtentClassForCountry(helper, countryGaulCode, 2)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(computeDiseaseExtentClassForCountry(helper, countryGaulCode, 9)).isEqualTo(possiblePresenceDiseaseExtentClass);
        assertThat(computeDiseaseExtentClassForCountry(helper, countryGaulCode, 10)).isEqualTo(presenceDiseaseExtentClass);
        assertThat(computeDiseaseExtentClassForCountry(helper, countryGaulCode, 11)).isEqualTo(presenceDiseaseExtentClass);
    }

    private DiseaseExtentClass computeDiseaseExtentClassForCountry(DiseaseExtentGeneratorHelper helper, int gaulCode,
                                                                   int numberOfOccurrences) {
        helper.getNumberOfOccurrencesByCountry().put(gaulCode, numberOfOccurrences);
        return helper.computeDiseaseExtentClassForCountry(gaulCode);
    }

    private DiseaseExtentGeneratorHelper createDefaultDiseaseExtentGeneratorHelper() {
        DiseaseExtentGeneratorHelper helper = new DiseaseExtentGeneratorHelper(
                defaultDiseaseGroup, defaultParameters, emptyDiseaseExtent, defaultAdminUnits,
                defaultDiseaseExtentClasses);
        helper.setOccurrences(emptyOccurrences);
        return helper;
    }

    private DiseaseExtentGeneratorHelper createDefaultDiseaseExtentGeneratorHelper(DiseaseExtentParameters parameters) {
        DiseaseExtentGeneratorHelper helper = new DiseaseExtentGeneratorHelper(
                defaultDiseaseGroup, parameters, emptyDiseaseExtent, defaultAdminUnits,
                defaultDiseaseExtentClasses);
        helper.setOccurrences(emptyOccurrences);
        return helper;
    }

    private List<? extends AdminUnitGlobalOrTropical> createDefaultAdminUnits() {
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

    private DiseaseOccurrenceForDiseaseExtent createOccurrence(int numberOfMonthssAgo) {
        DateTime occurrenceDate = DateTime.now().minusMonths(numberOfMonthssAgo);
        return new DiseaseOccurrenceForDiseaseExtent(occurrenceDate, 0);
    }

    private AdminUnitReview createReview(DiseaseExtentClass extentClass, double expertWeighting) {
        Expert expert = new Expert();
        expert.setWeighting(expertWeighting);
        return new AdminUnitReview(expert, 0, null, new DiseaseGroup(), extentClass);
    }

    private <T> List<T> createList(T... items) {
        List<T> list = new ArrayList<T>();
        Collections.addAll(list, items);
        return list;
    }
}
