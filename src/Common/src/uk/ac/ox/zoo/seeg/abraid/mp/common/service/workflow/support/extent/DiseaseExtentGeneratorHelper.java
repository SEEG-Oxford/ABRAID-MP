package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;

/**
 * A helper for the DiseaseExtentGenerator class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGeneratorHelper {
    private static final int SCALING_FACTOR = 50;
    private static final int LATEST_OCCURRENCES_MAX_LIST_SIZE = 5;

    // Input data
    private final DiseaseExtentGenerationInputData inputData;
    private final DiseaseExtent parameters;

    // Working fields
    private final Map<String, DiseaseExtentClass> classesByName;
    private final Map<Integer, AdminUnitGlobalOrTropical> adminUnitsByGaulCode;
    private final Map<Integer, List<DiseaseOccurrence>> occurrencesByAdminUnit;
    private final Map<Integer, List<DiseaseOccurrence>> occurrencesByParentCountry;
    private final Map<Integer, List<AdminUnitReview>> reviewsByAdminUnit;

    public DiseaseExtentGeneratorHelper(DiseaseExtentGenerationInputData inputData, DiseaseExtent parameters) {
        this.inputData = inputData;
        this.parameters = parameters;

        // Do initial processing
        this.classesByName = indexDiseaseExtentClassesOnName();
        this.adminUnitsByGaulCode = indexAdminUnitsByGaulCode();
        this.occurrencesByAdminUnit = groupOccurrencesByAdminUnit();
        this.occurrencesByParentCountry = groupOccurrencesByParentCountry();
        this.reviewsByAdminUnit = groupReviewsByAdminUnit();
    }

    /**
     * Index the disease extent classes by name.
     */
    private Map<String, DiseaseExtentClass> indexDiseaseExtentClassesOnName() {
        return index(inputData.getDiseaseExtentClasses(), on(DiseaseExtentClass.class).getName());
    }

    /**
     * Index the admin units by gaul code (global or tropical).
     */
    private Map<Integer, AdminUnitGlobalOrTropical> indexAdminUnitsByGaulCode() {
        return index(inputData.getAdminUnits(), on(AdminUnitGlobalOrTropical.class).getGaulCode());
    }

    /**
     * Groups the disease occurrences by admin unit (global or tropical).
     */
    private Map<Integer, List<DiseaseOccurrence>> groupOccurrencesByAdminUnit() {
        // Create empty groups of occurrences by admin unit
        Map<Integer, List<DiseaseOccurrence>> groups = new HashMap<>();
        for (AdminUnitGlobalOrTropical adminUnit : inputData.getAdminUnits()) {
            groups.put(adminUnit.getGaulCode(), new ArrayList<DiseaseOccurrence>());
        }

        // Add occurrences to the groups
        if (inputData.getOccurrences() != null) {
            for (DiseaseOccurrence occurrence : inputData.getOccurrences()) {
                Integer gaulCode = extractGaulCode(occurrence);
                // Exclude occurrences that have country precision if the admin unit is not a country. For example, the
                // centroid of the United States is in Kansas, but we should not count a United Status country-level
                // point as being a disease occurrence in Kansas itself.
                if (!isACountryPointInANonCountryAdminUnit(occurrence, gaulCode)) {
                    groups.get(gaulCode).add(occurrence);
                }
            }
        }

        return groups;
    }

    /**
     * Groups the occurrences by country (only for countries that are split into sub admin units).
     * The country GAUL code is taken from the admin unit global/tropical entity.
     */
    private Map<Integer, List<DiseaseOccurrence>> groupOccurrencesByParentCountry() {
        Map<Integer, List<DiseaseOccurrence>> groups = new HashMap<>();
        for (AdminUnitGlobalOrTropical adminUnit : inputData.getAdminUnits()) {
            Integer countryGaulCode = adminUnit.getCountryGaulCode();
            if (countryGaulCode != null && !groups.containsKey(countryGaulCode)) {
                groups.put(countryGaulCode, new ArrayList<DiseaseOccurrence>());
            }
        }

        if (inputData.getOccurrences() != null) {
            for (DiseaseOccurrence occurrence : inputData.getOccurrences()) {
                AdminUnitGlobalOrTropical adminUnit = adminUnitsByGaulCode.get(extractGaulCode(occurrence));
                Integer countryGaulCode = adminUnit.getCountryGaulCode();
                if (countryGaulCode != null) {
                    groups.get(countryGaulCode).add(occurrence);
                }
            }
        }

        return groups;
    }

    /**
     * Groups the expert reviews by admin unit (strictly, by admin unit GAUL code).
     */
    private Map<Integer, List<AdminUnitReview>> groupReviewsByAdminUnit() {
        // Create empty groups of reviews by admin unit
        Map<Integer, List<AdminUnitReview>> groups = new HashMap<>();
        for (AdminUnitGlobalOrTropical adminUnit : inputData.getAdminUnits()) {
            groups.put(adminUnit.getGaulCode(), new ArrayList<AdminUnitReview>());
        }

        if (inputData.getReviews() != null) {
            // Add reviews to the groups
            for (AdminUnitReview review : inputData.getReviews()) {
                Integer gaulCode = review.getAdminUnitGlobalOrTropicalGaulCode();
                groups.get(gaulCode).add(review);
            }
        }

        return groups;
    }

    /**
     * Computes the disease extent classes.
     * For each admin unit, convert its list of disease occurrences and reviews into a disease extent class.
     * Also collate the count of occurrences and most recent occurrences for each admin unit.
     * @return The extent generation results set.
     */
    public DiseaseExtentGenerationOutputData computeDiseaseExtent() {
        final Map<Integer, DiseaseExtentClass> classesByAdminUnit = new HashMap<>();
        final Map<Integer, Integer> occurrenceCountByAdminUnit = new HashMap<>();
        final Map<Integer, Collection<DiseaseOccurrence>> latestOccurrencesByAdminUnit = new HashMap<>();


        for (AdminUnitGlobalOrTropical adminUnit : inputData.getAdminUnits()) {
            final List<DiseaseOccurrence> occurrencesForAdminUnit = occurrencesByAdminUnit.get(adminUnit.getGaulCode());
            final List<AdminUnitReview> reviewsForAdminUnit = reviewsByAdminUnit.get(adminUnit.getGaulCode());
            final List<DiseaseOccurrence> occurrencesForParentCountry =
                    occurrencesByParentCountry.get(adminUnit.getCountryGaulCode());


            String extentClassNameForAdminUnit = computeAdminUnit(
                    occurrencesForAdminUnit, reviewsForAdminUnit, occurrencesForParentCountry);

            classesByAdminUnit.put(adminUnit.getGaulCode(), classesByName.get(extentClassNameForAdminUnit));
            occurrenceCountByAdminUnit.put(adminUnit.getGaulCode(), occurrencesForAdminUnit.size());
            latestOccurrencesByAdminUnit.put(adminUnit.getGaulCode(), pickLatestOccurrences(occurrencesForAdminUnit));
        }

        return new DiseaseExtentGenerationOutputData(
                classesByAdminUnit, occurrenceCountByAdminUnit, latestOccurrencesByAdminUnit);
    }

    private Collection<DiseaseOccurrence> pickLatestOccurrences(List<DiseaseOccurrence> occurrences) {
        Collections.sort(occurrences, new Comparator<DiseaseOccurrence>() {
            @Override
            public int compare(DiseaseOccurrence o1, DiseaseOccurrence o2) {
                return o2.getOccurrenceDate().compareTo(o1.getOccurrenceDate());    // descending
            }
        });
        int n = Math.min(occurrences.size(), LATEST_OCCURRENCES_MAX_LIST_SIZE);
        return occurrences.subList(0, n);
    }

    /**
     * Compute the disease extent class for an admin unit.
     * @param occurrencesForAdminUnit The occurrences in the admin unit.
     * @param reviewsForAdminUnit The occurrences of the admin unit.
     * @param occurrencesForParentCountry The number occurrences in parent country (null if there is no parent country).
     * @return The name of the disease extent class.
     */
    private String computeAdminUnit(List<DiseaseOccurrence> occurrencesForAdminUnit,
                          List<AdminUnitReview> reviewsForAdminUnit,
                          List<DiseaseOccurrence> occurrencesForParentCountry) {
        // Computes the updated disease extent class for one admin unit
        if (occurrencesForAdminUnit.size() != 0 || reviewsForAdminUnit.size() != 0) {
            // The are occurrences and/or reviews, use the score for the admin unit, using reviews, without a factor
            return computeDiseaseExtentClass(occurrencesForAdminUnit, reviewsForAdminUnit, 1);
        } else if (occurrencesForParentCountry != null) {
            // There are no occurrences or reviews, so use the score for the parent country, without reviews, halved
            return computeDiseaseExtentClass(occurrencesForParentCountry, new ArrayList<AdminUnitReview>(), 0.5); ///CHECKSTYLE:SUPPRESS LineLengthCheck|MagicNumberCheck
        } else {
            // There are no occurrences or reviews and the admin unit doesn't have a parent country (it is a country)
            return DiseaseExtentClass.UNCERTAIN;
        }
    }

    /**
     * Computes a disease extent class, based on a list of occurrences and a list of reviews.
     * @param occurrencesList The list of occurrences.
     * @param reviewsList The list of reviews.
     * @return The computed disease extent class name.
     */
    private String computeDiseaseExtentClass(
            List<DiseaseOccurrence> occurrencesList, List<AdminUnitReview> reviewsList, double factor) {
        double overallScore = computeScoreForOccurrencesAndReviews(occurrencesList, reviewsList);
        overallScore = overallScore * factor;

        if (overallScore > 1) {
            return DiseaseExtentClass.PRESENCE;
        } else if (overallScore > 0) {
            return DiseaseExtentClass.POSSIBLE_PRESENCE;
        } else if (overallScore == 0) {
            return DiseaseExtentClass.UNCERTAIN;
        } else if (overallScore >= -1) {
            return DiseaseExtentClass.POSSIBLE_ABSENCE;
        } else {
            return DiseaseExtentClass.ABSENCE;
        }
    }

    /**
     * Computes a disease extent score, based on a list of occurrences and a list of reviews.
     * @param occurrencesList The list of occurrences.
     * @param reviewsList The list of reviews.
     * @return The computed disease score.
     */
    private double computeScoreForOccurrencesAndReviews(List<DiseaseOccurrence> occurrencesList,
                                                       List<AdminUnitReview> reviewsList) {
        // Compute the score for each occurrence and each review, and take the average
        // Be extra careful with int -> double conversions...
        double occurrencesScore = computeOccurrencesScore(occurrencesList);
        double reviewsScore = computeReviewsScore(reviewsList);
        double totalScore = occurrencesScore + reviewsScore;
        double totalCount = occurrencesList.size() + reviewsList.size();
        return (totalCount == 0) ? 0 : (totalScore / totalCount);
    }

    private int computeOccurrencesScore(List<DiseaseOccurrence> occurrenceList) {
        DateTime oldestDateForHigherScore =
                DateTime.now().minusMonths(parameters.getMaxMonthsAgoForHigherOccurrenceScore());

        // Unlike computeReviewsScore(), the total is an integer so that we can maintain full accuracy over multiple
        // additions
        int total = 0;
        for (DiseaseOccurrence occurrence : occurrenceList) {
            // The score for each occurrence depends on the occurrence date. It scores the "higher score" unless it
            // is older than the oldest date allowed for the higher score, in which case it scores the "lower score".
            // These values are all defined by the disease extent parameters.
            boolean useLowerScore = occurrence.getOccurrenceDate().isBefore(oldestDateForHigherScore);
            total += useLowerScore ? parameters.getLowerOccurrenceScore() : parameters.getHigherOccurrenceScore();
        }
        return total;
    }

    private double computeReviewsScore(List<AdminUnitReview> reviewsList) {
        double total = 0;
        for (AdminUnitReview review : reviewsList) {
            // The response weighting is currently divided by 50 so that the weightings in the database (which
            // were chosen for use with the model) can be used for our purposes. Eventually this should be removed.
            int scaledResponseWeighting = review.getResponse().getWeighting() / SCALING_FACTOR;
            total += scaledResponseWeighting * review.getExpert().getWeighting();
        }
        return total;
    }

    private int extractGaulCode(DiseaseOccurrence occurrence) {
        return occurrence.getDiseaseGroup().isGlobal() ?
                occurrence.getLocation().getAdminUnitGlobalGaulCode() :
                occurrence.getLocation().getAdminUnitTropicalGaulCode();
    }

    private boolean isACountryPointInANonCountryAdminUnit(DiseaseOccurrence occurrence, Integer gaulCode) {
        return (occurrence.getLocation().getPrecision() == LocationPrecision.COUNTRY) &&
                (adminUnitsByGaulCode.get(gaulCode).getLevel() != '0');
    }
}
