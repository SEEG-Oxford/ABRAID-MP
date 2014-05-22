package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;

/**
 * Updates the weightings.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculator {
    private static final Logger LOGGER = Logger.getLogger(WeightingsCalculator.class);
    private static final String STARTING_UPDATE_WEIGHTINGS =
            "Starting process to update occurrences' weightings for disease %d";
    private static final String NOT_UPDATING_WEIGHTINGS =
            "Occurrences' weightings will not be updated for disease %d" +
                    " - a week has not elapsed since last model run preparation on %s";
    private static final String NO_NEW_REVIEWS =
            "No new reviews have been submitted - expert weightings will not be updated";
    private static final String RECALCULATING_EXPERT_WEIGHTINGS =
            "Recalculating expert weightings for %d disease occurrences given %d new reviews";
    private static final String NO_OCCURRENCES_FOR_MODEL_RUN =
            "No new occurrences - validation and final weightings will not be updated";
    private static final String RECALCULATING_WEIGHTINGS =
            "Recalculating validation and final weightings for %d disease occurrences in preparation for model run";
    private static final String UPDATING_WEIGHTINGS =
            "Updating weightings for %d occurrences";

    private DiseaseService diseaseService;

    public WeightingsCalculator(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    /**
     * If a week has elapsed since last update of weighting values, update the expert weightings of disease occurrences
     * for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     */
    @Transactional
    public void updateDiseaseOccurrenceExpertWeightings(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime lastModelRunPrepDate = diseaseGroup.getLastModelRunPrepDate();
        if (shouldContinue(lastModelRunPrepDate)) {
            LOGGER.info(String.format(STARTING_UPDATE_WEIGHTINGS, diseaseGroupId));
            saveModelRunPrepDate(diseaseGroup, DateTime.now());
            updateDiseaseOccurrenceExpertWeightings(lastModelRunPrepDate, diseaseGroupId);
        } else {
            LOGGER.info(String.format(NOT_UPDATING_WEIGHTINGS, diseaseGroupId, lastModelRunPrepDate.toString()));
        }
    }

    /**
     * Weightings should be updated if there is no lastModelRunPrepDate for disease, or more than a week has elapsed.
     */
    private boolean shouldContinue(DateTime lastModelRunPrepDate) {
        if (lastModelRunPrepDate == null) {
            return true;
        } else {
            LocalDate today = LocalDate.now();
            LocalDate comparisonDate = lastModelRunPrepDate.toLocalDate().plusWeeks(1);
            return (comparisonDate.isEqual(today) || comparisonDate.isBefore(today));
        }
    }

    private void saveModelRunPrepDate(DiseaseGroup diseaseGroup, DateTime lastModelRunPrepDate) {
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    /**
     * For every disease occurrence point that has had new reviews submitted since the last recalculation a week ago,
     * calculate its new weighting, by taking the weighted average of every expert review in the database (not just
     * the new reviews).
     */
    private void updateDiseaseOccurrenceExpertWeightings(DateTime lastModelRunPrepDate, int diseaseGroupId) {
        List<DiseaseOccurrenceReview> allReviews = getAllReviewsForDiseaseGroup(lastModelRunPrepDate, diseaseGroupId);
        if (allReviews.isEmpty()) {
            LOGGER.info(NO_NEW_REVIEWS);
        } else {
            calculateNewDiseaseOccurrenceExpertWeightings(allReviews);
        }
    }

    private List<DiseaseOccurrenceReview> getAllReviewsForDiseaseGroup(DateTime lastModelRunPrepDate,
                                                                       int diseaseGroupId) {
        if (lastModelRunPrepDate == null) {
            return diseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(diseaseGroupId);
        } else {
            return diseaseService.getAllReviewsForDiseaseGroupOccurrencesWithNewReviewsSinceLastModelRunPrep(
                    lastModelRunPrepDate, diseaseGroupId);
        }
    }

    private void calculateNewDiseaseOccurrenceExpertWeightings(List<DiseaseOccurrenceReview> allReviews) {
        Set<DiseaseOccurrence> distinctOccurrences = extractDistinctDiseaseOccurrences(allReviews);
        LOGGER.info(String.format(RECALCULATING_EXPERT_WEIGHTINGS, distinctOccurrences.size(), allReviews.size()));
        for (DiseaseOccurrence occurrence : distinctOccurrences) {
            List<DiseaseOccurrenceReview> reviews = select(allReviews,
                    having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), IsEqual.equalTo(occurrence)));
            double expertWeighting = calculateWeightedAverageResponse(reviews);
            occurrence.setExpertWeighting(expertWeighting);
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }

    private Set<DiseaseOccurrence> extractDistinctDiseaseOccurrences(List<DiseaseOccurrenceReview> allReviews) {
        return new HashSet<>(extract(allReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    private double calculateWeightedAverageResponse(List<DiseaseOccurrenceReview> reviews) {
        List<Double> weightings = calculateWeightingForEachReview(reviews);
        return average(weightings);
    }

    private List<Double> calculateWeightingForEachReview(List<DiseaseOccurrenceReview> reviews) {
        return convert(reviews, new Converter<DiseaseOccurrenceReview, Double>() {
            public Double convert(DiseaseOccurrenceReview review) {
                return review.getResponse().getValue() * review.getExpert().getWeighting();
            }
        });
    }

    private double average(List<Double> weightings) {
        return ((double) sum(weightings)) / weightings.size();
    }

    /**
     * For every occurrence of the specified disease group for which is_validated is true, update its validation
     * weighting and final weighting.
     * @param diseaseGroupId The id of the disease group.
     */
    @Transactional
    public void updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(int diseaseGroupId) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId);
        if (occurrences.size() == 0) {
            LOGGER.info(NO_OCCURRENCES_FOR_MODEL_RUN);
        } else {
            LOGGER.info(String.format(RECALCULATING_WEIGHTINGS, occurrences.size()));
            updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(occurrences);
        }
    }

    private void updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(List<DiseaseOccurrence> occurrences) {
        Set<DiseaseOccurrence> pendingSave = new HashSet<>();
        for (DiseaseOccurrence occurrence : occurrences) {
            updateDiseaseOccurrenceValidationWeighting(occurrence, pendingSave);
            updateDiseaseOccurrenceFinalWeighting(occurrence, pendingSave);
        }
        saveChanges(pendingSave);
    }

    /**
     * For every disease occurrence point that has the is_validated flag set to true, set its validation weighting as
     * the expert weighting if it exists, otherwise the system weighting.
     */
    private void updateDiseaseOccurrenceValidationWeighting(DiseaseOccurrence occurrence,
                                                             Set<DiseaseOccurrence> pendingSave) {
        Double expertWeighting = occurrence.getExpertWeighting();
        double systemWeighting = occurrence.getSystemWeighting();
        double weighting = (expertWeighting != null) ? expertWeighting : systemWeighting;
        if (occurrence.getValidationWeighting() == null || occurrence.getValidationWeighting() != weighting) {
            pendingSave.add(occurrence);
            occurrence.setValidationWeighting(weighting);
        }
    }

    /**
     * Recalculate the final weighting as the average across each of the 4 properties. If the value of any of the
     * weightings is 0, then the occurrence should be discounted by the model by setting the final weighting to 0.
     */
    private void updateDiseaseOccurrenceFinalWeighting(DiseaseOccurrence occurrence,
                                                        Set<DiseaseOccurrence> pendingSave) {
        double locationResolutionWeighting = occurrence.getLocation().getResolutionWeighting();
        double feedWeighting = occurrence.getAlert().getFeed().getWeighting();
        double diseaseGroupTypeWeighting = occurrence.getDiseaseGroup().getWeighting();
        double weighting;
        if (locationResolutionWeighting == 0.0 || feedWeighting == 0.0 || diseaseGroupTypeWeighting == 0.0) {
            weighting = 0.0;
        } else {
            weighting = average(Arrays.asList(locationResolutionWeighting, feedWeighting, diseaseGroupTypeWeighting,
                    occurrence.getValidationWeighting()));
        }
        if (occurrence.getFinalWeighting() == null || occurrence.getFinalWeighting() != weighting) {
            pendingSave.add(occurrence);
            occurrence.setFinalWeighting(weighting);
        }
    }

    private void saveChanges(Set<DiseaseOccurrence> pendingSave) {
        LOGGER.info(String.format(UPDATING_WEIGHTINGS, pendingSave.size()));
        for (DiseaseOccurrence occurrence : pendingSave) {
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }
}
