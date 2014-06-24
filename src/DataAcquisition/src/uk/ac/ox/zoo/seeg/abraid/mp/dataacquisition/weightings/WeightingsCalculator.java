package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * Updates the weightings of experts and of disease occurrences, given new reviews.
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculator {
    private static Logger logger = Logger.getLogger(WeightingsCalculator.class);
    private static final String NOT_UPDATING_OCCURRENCE_EXPERT_WEIGHTINGS =
            "No new reviews have been submitted - expert weightings of disease occurrences will not be updated";
    private static final String NOT_UPDATING_WEIGHTINGS_OF_EXPERTS =
            "No reviews have been submitted - weightings of experts will not be updated";
    private static final String RECALCULATING_OCCURRENCE_EXPERT_WEIGHTINGS =
            "Recalculating expert weightings for %d disease occurrence(s) given %d new review(s)";
    private static final String RECALCULATING_WEIGHTINGS_OF_EXPERTS =
            "Recalculating weightings of experts given %d review(s)";
    private static final String NO_OCCURRENCES_FOR_MODEL_RUN =
            "No occurrences for model run - validation and final weightings will not be updated";
    private static final String UPDATING_WEIGHTINGS =
            "Updating validation and final weightings for %d disease occurrence(s) in preparation for model run";
    private static final String SAVING_WEIGHTINGS_OF_EXPERTS =
            "Weightings changed for %d expert(s) - saving to database";
    private static final String NOT_SAVING_WEIGHTINGS_OF_EXPERTS =
            "Weightings of experts have not changed - nothing to save";

    private DiseaseService diseaseService;
    private ExpertService expertService;

    public WeightingsCalculator(DiseaseService diseaseService, ExpertService expertService) {
        this.diseaseService = diseaseService;
        this.expertService = expertService;
    }

    /**
     * Get every disease occurrence point that has had new reviews submitted since the last recalculation.
     * Calculate its new weighting, by taking the weighted average of every expert review in the database (not just the
     * new reviews) and shifting it to be between 0 and 1.
     * @param lastModelRunPrepDate The date on which the model was last run for the disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    public void updateDiseaseOccurrenceExpertWeightings(DateTime lastModelRunPrepDate, int diseaseGroupId) {
        List<DiseaseOccurrenceReview> allReviews = getAllReviewsForDiseaseGroup(lastModelRunPrepDate, diseaseGroupId);
        if (allReviews.isEmpty()) {
            logger.info(NOT_UPDATING_OCCURRENCE_EXPERT_WEIGHTINGS);
        } else {
            calculateNewDiseaseOccurrenceExpertWeightings(allReviews);
        }
    }

    private List<DiseaseOccurrenceReview> getAllReviewsForDiseaseGroup(DateTime lastModelRunPrepDate,
                                                                       int diseaseGroupId) {
        if (lastModelRunPrepDate == null) {
            return diseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(diseaseGroupId);
        } else {
            return diseaseService.getDiseaseOccurrenceReviewsForModelRunPrep(lastModelRunPrepDate, diseaseGroupId);
        }
    }

    private void calculateNewDiseaseOccurrenceExpertWeightings(List<DiseaseOccurrenceReview> allReviews) {
        Set<DiseaseOccurrence> distinctOccurrences = extractDistinctDiseaseOccurrences(allReviews);
        logger.info(String.format(RECALCULATING_OCCURRENCE_EXPERT_WEIGHTINGS, distinctOccurrences.size(),
                allReviews.size()));
        for (DiseaseOccurrence occurrence : distinctOccurrences) {
            List<DiseaseOccurrenceReview> reviews = extractReviewsForOccurrence(allReviews, occurrence);
            double expertWeighting = calculateWeightedAverageResponse(reviews);
            occurrence.setExpertWeighting(expertWeighting);
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }

    private Set<DiseaseOccurrence> extractDistinctDiseaseOccurrences(List<DiseaseOccurrenceReview> allReviews) {
        return new HashSet<>(extract(allReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    private List<DiseaseOccurrenceReview> extractReviewsForOccurrence(List<DiseaseOccurrenceReview> allReviews,
                                                                      DiseaseOccurrence occurrence) {
        return select(allReviews,
               having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), IsEqual.equalTo(occurrence)));
    }

    private double calculateWeightedAverageResponse(List<DiseaseOccurrenceReview> reviews) {
        List<Double> weightings = calculateWeightingForEachReview(reviews);
        return shift(average(weightings));
    }

    private List<Double> calculateWeightingForEachReview(List<DiseaseOccurrenceReview> reviews) {
        return convert(reviews, new Converter<DiseaseOccurrenceReview, Double>() {
            public Double convert(DiseaseOccurrenceReview review) {
                return review.getResponse().getValue() * review.getExpert().getWeighting();
            }
        });
    }

    // Shift weighting from range [-1, 1] to desired range of [0, 1]
    private double shift(double weighting) {
        return (weighting + 1) / 2;
    }

    /**
     * For every occurrence of the specified disease group for which is_validated is true, update its validation
     * weighting and final weighting.
     * @param diseaseGroupId The id of the disease group.
     * @return The list of disease occurrences for requesting a model run.
     */
    public List<DiseaseOccurrence> updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
        if (occurrences.size() == 0) {
            logger.info(NO_OCCURRENCES_FOR_MODEL_RUN);
        } else {
            logger.info(String.format(UPDATING_WEIGHTINGS, occurrences.size()));
            updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(occurrences);
        }
        return occurrences;
    }

    private void updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(List<DiseaseOccurrence> occurrences) {
        for (DiseaseOccurrence occurrence : occurrences) {
            double newValidation = calculateNewValidationWeighting(occurrence);
            double newFinal = calculateNewFinalWeighting(occurrence, newValidation);
            double newFinalExcludingSpatial = calculateNewFinalWeightingExcludingSpatial(occurrence, newValidation);
            if (hasAnyWeightingChanged(occurrence, newValidation, newFinal, newFinalExcludingSpatial)) {
                occurrence.setValidationWeighting(newValidation);
                occurrence.setFinalWeighting(newFinal);
                occurrence.setFinalWeightingExcludingSpatial(newFinalExcludingSpatial);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
        }
    }

    /**
     * For every disease occurrence point that has the is_validated flag set to true, set its validation weighting as
     * the expert weighting if it exists, otherwise the machine weighting.
     */
    private double calculateNewValidationWeighting(DiseaseOccurrence occurrence) {
        Double expertWeighting = occurrence.getExpertWeighting();
        double machineWeighting = occurrence.getMachineWeighting();
        return (expertWeighting != null) ? expertWeighting : machineWeighting;
    }

    /**
     * Recalculate the final weighting as the average across each of the 4 properties. If the value of any of the
     * weightings is 0, then the occurrence should be discounted by the model by setting the final weighting to 0.
     */
    private double calculateNewFinalWeighting(DiseaseOccurrence occurrence, double validationWeighting) {
        double locationResolutionWeighting = occurrence.getLocation().getResolutionWeighting();
        double feedWeighting = occurrence.getAlert().getFeed().getWeighting();
        double diseaseGroupTypeWeighting = occurrence.getDiseaseGroup().getWeighting();
        double weighting;
        if (locationResolutionWeighting == 0.0 || diseaseGroupTypeWeighting == 0.0) {
            weighting = 0.0;
        } else {
            weighting = average(locationResolutionWeighting, feedWeighting, diseaseGroupTypeWeighting,
                    validationWeighting);
        }
        return weighting;
    }

    /**
     * As above, but excluding the location resolution weighting.
     */
    private double calculateNewFinalWeightingExcludingSpatial(DiseaseOccurrence occurrence,
                                                              double validationWeighting) {
        double feedWeighting = occurrence.getAlert().getFeed().getWeighting();
        double diseaseGroupTypeWeighting = occurrence.getDiseaseGroup().getWeighting();
        return (diseaseGroupTypeWeighting == 0.0) ? 0.0 :
                average(feedWeighting, diseaseGroupTypeWeighting, validationWeighting);
    }

    private boolean hasAnyWeightingChanged(DiseaseOccurrence occurrence,
                                           double newValidation, double newFinal, double newFinalExcludingSpatial) {
        return (hasWeightingChanged(occurrence.getValidationWeighting(), newValidation) ||
                hasWeightingChanged(occurrence.getFinalWeighting(), newFinal) ||
                hasWeightingChanged(occurrence.getFinalWeightingExcludingSpatial(), newFinalExcludingSpatial));
    }

    private boolean hasWeightingChanged(Double currentWeighting, double newWeighting) {
        return (currentWeighting == null || currentWeighting != newWeighting);
    }

    /**
     * For each expert, calculate their new weighting as the absolute difference between their response and the average
     * response from all other experts, averaged over all the occurrences that they have reviewed.
     * Record the values in a map, to be saved after updating the occurrences' weightings.
     * @return A map from expert ID to new weighting value.
     */
    public Map<Integer, Double> calculateNewExpertsWeightings() {
        Map<Integer, Double> newExpertsWeightings = new HashMap<>();
        List<DiseaseOccurrenceReview> allReviews = diseaseService.getAllDiseaseOccurrenceReviews();
        if (allReviews.size() == 0) {
            logger.info(NOT_UPDATING_WEIGHTINGS_OF_EXPERTS);
        } else {
            logger.info(String.format(RECALCULATING_WEIGHTINGS_OF_EXPERTS, allReviews.size()));
            for (Expert expert : extractDistinctExperts(allReviews)) {
                List<Double> differencesInResponses = new ArrayList<>();
                for (DiseaseOccurrence occurrence : selectExpertsReviewedOccurrences(allReviews, expert)) {
                    differencesInResponses.add(calculateDifference(allReviews, occurrence, expert));
                }
                double newWeighting = 1 - average(differencesInResponses);
                if (hasWeightingChanged(expert.getWeighting(), newWeighting)) {
                    newExpertsWeightings.put(expert.getId(), newWeighting);
                }
            }
        }
        return newExpertsWeightings;
    }

    private Set<Expert> extractDistinctExperts(List<DiseaseOccurrenceReview> allReviews) {
        return new HashSet<>(extract(allReviews, on(DiseaseOccurrenceReview.class).getExpert()));
    }

    private Set<DiseaseOccurrence> selectExpertsReviewedOccurrences(List<DiseaseOccurrenceReview> reviews,
                                                                    Expert expert) {
        List<DiseaseOccurrenceReview> expertsReviews = select(reviews,
                having(on(DiseaseOccurrenceReview.class).getExpert(), IsEqual.equalTo(expert)));
        return new HashSet<>(extract(expertsReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    private Double calculateDifference(List<DiseaseOccurrenceReview> reviews, DiseaseOccurrence occurrence,
                                       Expert expert) {
        List<DiseaseOccurrenceReview> reviewsOfOccurrence = select(reviews,
                having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), IsEqual.equalTo(occurrence)));
        DiseaseOccurrenceReview expertsReview = selectUnique(reviewsOfOccurrence,
                having(on(DiseaseOccurrenceReview.class).getExpert(), IsEqual.equalTo(expert)));
        reviewsOfOccurrence.remove(expertsReview);

        double expertsResponse = expertsReview.getResponse().getValue();
        double averageResponse = average(extractReviewResponseValues(reviewsOfOccurrence));
        return Math.abs(expertsResponse - averageResponse);
    }

    private List<Double> extractReviewResponseValues(List<DiseaseOccurrenceReview> reviewsOfOccurrence) {
        return convert(reviewsOfOccurrence, new Converter<DiseaseOccurrenceReview, Double>() {
            public Double convert(DiseaseOccurrenceReview review) {
                return review.getResponse().getValue();
            }
        });
    }

    /**
     * Saves each expert with a new weighting value.
     * @param newExpertsWeightings A map from expert ID to new weighting value.
     */
    public void saveExpertsWeightings(Map<Integer, Double> newExpertsWeightings) {
        if (newExpertsWeightings.size() > 0) {
            logger.info(String.format(SAVING_WEIGHTINGS_OF_EXPERTS, newExpertsWeightings.size()));
            for (Map.Entry<Integer, Double> entry : newExpertsWeightings.entrySet()) {
                // We need to reload each expert because you cannot reuse Hibernate objects across transactions
                Expert expert = expertService.getExpertById(entry.getKey());
                expert.setWeighting(entry.getValue());
                expertService.saveExpert(expert);
            }
        } else {
            logger.info(String.format(NOT_SAVING_WEIGHTINGS_OF_EXPERTS));
        }
    }

    private double average(Double... args) {
        return (double) avg(Arrays.asList(args));
    }

    private double average(List<Double> args) {
        return (double) avg(args);
    }
}
