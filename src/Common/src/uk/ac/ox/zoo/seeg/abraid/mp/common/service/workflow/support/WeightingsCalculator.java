package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Updates the weightings of experts and of disease occurrences, given new reviews.
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculator {

    private static Logger logger = Logger.getLogger(WeightingsCalculator.class);

    private static final String NOT_UPDATING_OCCURRENCE_EXPERT_WEIGHTINGS =
        "No new occurrence reviews have been submitted by experts with a weighting >= %.2f - " +
        "expert weightings of disease occurrences will not be updated";
    private static final String RECALCULATING_OCCURRENCE_EXPERT_WEIGHTINGS =
        "Recalculating expert weightings for %d disease occurrence(s) given %d new review(s)";

    private static final String NOT_UPDATING_WEIGHTINGS_OF_EXPERTS =
        "No occurrence reviews have been submitted - weightings of experts will not be updated";
    private static final String RECALCULATING_WEIGHTINGS_OF_EXPERTS =
        "Recalculating weightings of experts given %d review(s)";

    private static final String NO_OCCURRENCES_FOR_MODEL_RUN =
        "No occurrences found that need their validation and final weightings set";
    private static final String UPDATING_WEIGHTINGS =
        "Updating validation and final weightings for %d disease occurrence(s) in preparation for model run";

    private final double validationWeightingThreshold;
    private final double expertWeightingThreshold;

    private DiseaseService diseaseService;
    private ExpertService expertService;

    public WeightingsCalculator(DiseaseService diseaseService, ExpertService expertService,
                                double expertWeightingThreshold, double validationWeightingThreshold) {
        this.diseaseService = diseaseService;
        this.expertService = expertService;
        this.expertWeightingThreshold = expertWeightingThreshold;
        this.validationWeightingThreshold = validationWeightingThreshold;
    }

    /**
     * Calculate and save the new weighting of disease occurrence points in validation that have had reviews submitted:
     * Take the average response of the reviews from the "reliable" experts (those who have an expert weighting greater
     * than EXPERT_WEIGHTING_THRESHOLD).
     * @param diseaseGroupId The id of the disease group.
     */
    public void updateDiseaseOccurrenceExpertWeightings(int diseaseGroupId) {
        List<DiseaseOccurrenceReview> allReviews =
                diseaseService.getDiseaseOccurrenceReviewsForOccurrencesInValidationForUpdatingWeightings(
                    diseaseGroupId, expertWeightingThreshold);
        if (allReviews.isEmpty()) {
            logger.info(String.format(NOT_UPDATING_OCCURRENCE_EXPERT_WEIGHTINGS, expertWeightingThreshold));
        } else {
            updateDiseaseOccurrenceExpertWeightings(allReviews);
        }
    }

    private void updateDiseaseOccurrenceExpertWeightings(List<DiseaseOccurrenceReview> allReviews) {
        for (DiseaseOccurrence occurrence : extractDistinctDiseaseOccurrences(allReviews)) {
            List<DiseaseOccurrenceReview> reviews = selectReviewsForOccurrence(allReviews, occurrence);
            double averageResponseValue = average(extractReviewResponseValues(reviews));
            occurrence.setExpertWeighting(averageResponseValue);
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }

    private Set<DiseaseOccurrence> extractDistinctDiseaseOccurrences(List<DiseaseOccurrenceReview> allReviews) {
        Set<DiseaseOccurrence> occurrences = new HashSet<>(
            extract(allReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence())
        );
        logger.info(String.format(RECALCULATING_OCCURRENCE_EXPERT_WEIGHTINGS, occurrences.size(), allReviews.size()));
        return occurrences;
    }

    private List<DiseaseOccurrenceReview> selectReviewsForOccurrence(List<DiseaseOccurrenceReview> reviews,
                                                                     DiseaseOccurrence occurrence) {
        return select(reviews, having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), equalTo(occurrence)));
    }

    private List<Double> extractReviewResponseValues(List<DiseaseOccurrenceReview> reviewsOfOccurrence) {
        return convert(reviewsOfOccurrence, new Converter<DiseaseOccurrenceReview, Double>() {
            public Double convert(DiseaseOccurrenceReview review) {
                return review.getResponse().getValue();
            }
        });
    }

    /**
     * For every occurrence of the specified disease group for which the status is READY, and the final weighting is
     * not currently set, set its validation weighting and final weighting for the first and only time.
     * @param diseaseGroupId The id of the disease group.
     */
    public void updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences =
            diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(
                    diseaseGroupId, DiseaseOccurrenceStatus.READY);
        if (occurrences.size() == 0) {
            logger.info(NO_OCCURRENCES_FOR_MODEL_RUN);
        } else {
            logger.info(String.format(UPDATING_WEIGHTINGS, occurrences.size()));
            updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(occurrences);
        }
    }

    private void updateDiseaseOccurrenceValidationWeightingAndFinalWeightings(List<DiseaseOccurrence> occurrences) {
        for (DiseaseOccurrence occurrence : occurrences) {
            Double newValidation = calculateNewValidationWeighting(occurrence);
            double newFinal = calculateNewFinalWeighting(occurrence, newValidation);
            double newFinalExcludingSpatial = calculateNewFinalWeightingExcludingSpatial(newValidation);
            if (hasAnyWeightingChanged(occurrence, newValidation, newFinal, newFinalExcludingSpatial)) {
                occurrence.setValidationWeighting(newValidation);
                occurrence.setFinalWeighting(newFinal);
                occurrence.setFinalWeightingExcludingSpatial(newFinalExcludingSpatial);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    /**
     * Set the occurrence's validation weighting as the expert weighting if it exists, otherwise the machine weighting.
     */
    private Double calculateNewValidationWeighting(DiseaseOccurrence occurrence) {
        Double expertWeighting = occurrence.getExpertWeighting();
        Double machineWeighting = occurrence.getMachineWeighting();
        return (expertWeighting != null) ? expertWeighting : machineWeighting;
    }

    /**
     * If the validation weighting is null, there are no reviews on the disease occurrence and no machine weighting
     * either. So set the final weighting, nominally, to the location resolution weighting. (This happens during disease
     * group setup, when the model has not yet been run.)
     * Otherwise, recalculate the final weighting as the average of location resolution and validation weightings,
     * unless the location resolution weighting is 0.
     */
    private double calculateNewFinalWeighting(DiseaseOccurrence occurrence, Double validationWeighting) {
        double locationResolutionWeighting = occurrence.getLocation().getResolutionWeighting();
        if (validationWeighting == null) {
            return locationResolutionWeighting;
        } else if ((validationWeighting <= validationWeightingThreshold) || (locationResolutionWeighting == 0.0)) {
            return 0.0;
        } else {
            return average(locationResolutionWeighting, validationWeighting);
        }
    }

    /**
     * As above, but excluding the location resolution weighting.
     * In this case, if validation weighting is null, final weighting is 1.0.
     */
    private double calculateNewFinalWeightingExcludingSpatial(Double validationWeighting) {
        return (validationWeighting == null) ? 1.0 : validationWeighting;
    }

    private boolean hasAnyWeightingChanged(DiseaseOccurrence occurrence,
                                           Double newValidation, double newFinal, double newFinalExcludingSpatial) {
        return (hasWeightingChanged(occurrence.getValidationWeighting(), newValidation) ||
                hasWeightingChanged(occurrence.getFinalWeighting(), newFinal) ||
                hasWeightingChanged(occurrence.getFinalWeightingExcludingSpatial(), newFinalExcludingSpatial));
    }

    private boolean hasWeightingChanged(Double currentWeighting, Double newWeighting) {
        return (currentWeighting == null || !currentWeighting.equals(newWeighting));
    }

    /**
     * For each expert, calculate (and save) their new weighting as the absolute difference between their response and
     * the average response from all other experts, averaged over all the occurrences that they have reviewed.
     */
    public void updateExpertsWeightings() {
        List<DiseaseOccurrenceReview> allReviews = diseaseService.getAllDiseaseOccurrenceReviews();
        List<DiseaseOccurrenceReview> reviews = filter(
                having(on(DiseaseOccurrenceReview.class).getResponse(), notNullValue()), allReviews);
        if (reviews.size() == 0) {
            logger.info(NOT_UPDATING_WEIGHTINGS_OF_EXPERTS);
        } else {
            logger.info(String.format(RECALCULATING_WEIGHTINGS_OF_EXPERTS, reviews.size()));
            updateExpertsWeightings(reviews);
        }
    }

    private void updateExpertsWeightings(List<DiseaseOccurrenceReview> allReviews) {
        for (Expert expert : extractDistinctExperts(allReviews)) {
            List<Double> differencesInResponses = new ArrayList<>();
            for (DiseaseOccurrence occurrence : selectExpertsReviewedOccurrences(allReviews, expert)) {
                differencesInResponses.add(calculateDifference(allReviews, occurrence, expert));
            }
            double newWeighting = 1 - average(differencesInResponses);
            if (hasWeightingChanged(expert.getWeighting(), newWeighting)) {
                expert.setWeighting(newWeighting);
                expertService.saveExpert(expert);
            }
        }
    }

    private Set<Expert> extractDistinctExperts(List<DiseaseOccurrenceReview> allReviews) {
        return new HashSet<>(extract(allReviews, on(DiseaseOccurrenceReview.class).getExpert()));
    }

    private Set<DiseaseOccurrence> selectExpertsReviewedOccurrences(List<DiseaseOccurrenceReview> reviews,
                                                                    Expert expert) {
        List<DiseaseOccurrenceReview> expertsReviews = select(reviews,
                having(on(DiseaseOccurrenceReview.class).getExpert(), equalTo(expert)));
        return new HashSet<>(extract(expertsReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    private Double calculateDifference(List<DiseaseOccurrenceReview> reviews, DiseaseOccurrence occurrence,
                                       Expert expert) {
        List<DiseaseOccurrenceReview> reviewsOfOccurrence = select(reviews,
                having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), equalTo(occurrence)));
        DiseaseOccurrenceReview expertsReview = selectUnique(reviewsOfOccurrence,
                having(on(DiseaseOccurrenceReview.class).getExpert(), equalTo(expert)));
        reviewsOfOccurrence.remove(expertsReview);

        double expertsResponse = expertsReview.getResponse().getValue();
        if (reviewsOfOccurrence.size() > 0) {
            // For this occurrence, find the difference between this expert's review response and the average of
            // all other experts' review responses
            double averageResponse = average(extractReviewResponseValues(reviewsOfOccurrence));
            return Math.abs(expertsResponse - averageResponse);
        } else {
            // There are no other experts' review responses, so the difference is zero
            return 0.0;
        }
    }

    /**
     * Calculate the average of the provided values.
     * @param args The values.
     * @return The mean of the given values.
     */
    static double average(Double... args) {
        return average(Arrays.asList(args));
    }

    private static double average(List<Double> args) {
        List<Double> notNullValues = filter(notNullValue(), args);
        return (double) avg(notNullValues);
    }
}
