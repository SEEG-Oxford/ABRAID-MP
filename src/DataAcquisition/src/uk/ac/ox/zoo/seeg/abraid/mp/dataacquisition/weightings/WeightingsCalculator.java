package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import ch.lambdaj.function.convert.Converter;
import org.apache.log4j.Logger;
import org.hamcrest.core.IsEqual;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config.ConfigurationService;

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
            "Starting process to update occurrences' expert weightings for disease %d";
    private static final String NOT_UPDATING_WEIGHTINGS =
            "Occurrences' expert weightings will not be updated for disease %d" +
                    " - a week has not elapsed since last retrieval on %s";
    private static final String NO_NEW_REVIEWS = "No new reviews have been submitted - weightings will not be updated";
    private static final String UPDATING_WEIGHTINGS =
            "Updating expert weightings for %d disease occurrences given %d new reviews";

    private ConfigurationService configurationService;
    private DiseaseService diseaseService;

    public WeightingsCalculator(ConfigurationService configurationService, DiseaseService diseaseService) {
        this.configurationService = configurationService;
        this.diseaseService = diseaseService;
    }

    /**
     * For every disease occurrence point that has had new reviews submitted since the last recalculation a week ago,
     * calculate its new weighting, by taking the weighted average of every expert review in the database (not just
     * the new reviews).
     * @param diseaseGroupId The id of the disease group.
     */
    @Transactional
    public void updateDiseaseOccurrenceWeightings(int diseaseGroupId) {
        LocalDateTime lastRetrievalDate = configurationService.getLastRetrievalDate();
        if (shouldContinue(lastRetrievalDate)) {
            LOGGER.info(String.format(STARTING_UPDATE_WEIGHTINGS, diseaseGroupId));
            configurationService.setLastRetrievalDate(LocalDateTime.now());
            List<DiseaseOccurrenceReview> allReviews = getAllReviewsForDiseaseGroup(lastRetrievalDate, diseaseGroupId);
            if (allReviews.isEmpty()) {
                LOGGER.info(NO_NEW_REVIEWS);
            } else {
                calculateNewDiseaseOccurrenceWeightings(allReviews);
            }
        } else {
            LOGGER.info(String.format(NOT_UPDATING_WEIGHTINGS, diseaseGroupId, lastRetrievalDate.toString()));
        }
    }

    // Weightings should be updated if there is no lastRetrievalDate in properties file, or more than a week has elapsed
    private boolean shouldContinue(LocalDateTime lastRetrievalDate) {
        if (lastRetrievalDate == null) {
            return true;
        } else {
            LocalDate today = LocalDate.now();
            LocalDate comparisonDate = lastRetrievalDate.toLocalDate().plusWeeks(1);
            return (comparisonDate.isEqual(today) || comparisonDate.isBefore(today));
        }
    }

    private List<DiseaseOccurrenceReview> getAllReviewsForDiseaseGroup(LocalDateTime lastRetrievalDate,
                                                                       int diseaseGroupId) {
        if (lastRetrievalDate == null) {
            return diseaseService.getAllDiseaseOccurrenceReviewsByDiseaseGroupId(diseaseGroupId);
        } else {
            return diseaseService.getAllReviewsForDiseaseGroupOccurrencesWithNewReviewsSinceLastRetrieval(
                    lastRetrievalDate, diseaseGroupId);
        }
    }

    private void calculateNewDiseaseOccurrenceWeightings(List<DiseaseOccurrenceReview> allReviews) {
        Set<DiseaseOccurrence> distinctOccurrences = extractDistinctDiseaseOccurrences(allReviews);
        LOGGER.info(String.format(UPDATING_WEIGHTINGS, distinctOccurrences.size(), allReviews.size()));
        for (DiseaseOccurrence occurrence : distinctOccurrences) {
            List<DiseaseOccurrenceReview> reviews = select(allReviews,
                    having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), IsEqual.equalTo(occurrence)));
            Double weighting = calculateWeightedAverageResponse(reviews);
            occurrence.setValidationWeighting(weighting);
        }
    }

    private Set<DiseaseOccurrence> extractDistinctDiseaseOccurrences(List<DiseaseOccurrenceReview> allReviews) {
        return new HashSet<>(extract(allReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    private Double calculateWeightedAverageResponse(List<DiseaseOccurrenceReview> reviews) {
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
}
