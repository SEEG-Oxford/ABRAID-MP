package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import ch.lambdaj.function.convert.Converter;
import org.hamcrest.core.IsEqual;
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
     */
    @Transactional
    public void updateDiseaseOccurrenceWeightings() {
        LocalDateTime lastRetrievalDate = configurationService.getLastRetrievalDate();
        if (shouldContinue(lastRetrievalDate)) {
            configurationService.setLastRetrievalDate(LocalDateTime.now());
            List<DiseaseOccurrenceReview> allReviews = getAllReviews(lastRetrievalDate);
            for (DiseaseOccurrence occurrence : extractDistinctDiseaseOccurrences(allReviews)) {
                List<DiseaseOccurrenceReview> reviews = select(allReviews,
                    having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), IsEqual.equalTo(occurrence)));
                Double weighting = calculateWeightedAverageResponse(reviews);
                occurrence.setValidationWeighting(weighting);
            }
        }
    }

    // Weightings should be updated if there is no lastRetrievalDate in properties file, or more than a week has elapsed
    private boolean shouldContinue(LocalDateTime lastRetrievalDate) {
        return (lastRetrievalDate == null ||
                lastRetrievalDate.hourOfDay().roundFloorCopy().plusWeeks(1).isBefore(LocalDateTime.now()));
    }

    private List<DiseaseOccurrenceReview> getAllReviews(LocalDateTime lastRetrievalDate) {
        if (lastRetrievalDate == null) {
            return diseaseService.getAllDiseaseOccurrenceReviews();
        } else {
            return diseaseService.getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(lastRetrievalDate);
        }
    }

    private Set<DiseaseOccurrence> extractDistinctDiseaseOccurrences(List<DiseaseOccurrenceReview> allReviews) {
        return new HashSet<>(extract(allReviews, on(DiseaseOccurrenceReview.class).getDiseaseOccurrence()));
    }

    private Double calculateWeightedAverageResponse(List<DiseaseOccurrenceReview> reviews) {
        List<Double> weightings = calculateWeightingForEachReview(reviews);
        return calculateAverage(weightings);
    }

    private List<Double> calculateWeightingForEachReview(List<DiseaseOccurrenceReview> reviews) {
        return convert(reviews, new Converter<DiseaseOccurrenceReview, Double>() {
            public Double convert(DiseaseOccurrenceReview review) {
                return review.getResponse().getValue() * review.getExpert().getWeighting();
            }
        });
    }

    private Double calculateAverage(List<Double> weightings) {
        Double total = 0.0;
        for (Double weighting : weightings) {
            total += weighting;
        }
        return total / weightings.size();
    }
}
