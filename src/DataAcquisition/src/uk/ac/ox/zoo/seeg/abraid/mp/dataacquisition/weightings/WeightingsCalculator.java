package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import ch.lambdaj.function.convert.Converter;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
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
@Transactional
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
    public void calculateDiseaseOccurrenceWeightings() {
        DateTime lastRetrievalDate = configurationService.getLastRetrievalDate();
        if (lastRetrievalDate.hourOfDay().roundCeilingCopy().plusWeeks(1).isAfterNow()) {
            List<DiseaseOccurrenceReview> allReviews =
                diseaseService.getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(lastRetrievalDate);
            for (DiseaseOccurrence occurrence : extractDistinctDiseaseOccurrences(allReviews)) {
                List<DiseaseOccurrenceReview> reviews = select(allReviews,
                    having(on(DiseaseOccurrenceReview.class).getDiseaseOccurrence(), IsEqual.equalTo(occurrence)));
                Double weighting = calculateWeightedAverageResponse(reviews);
                occurrence.setValidationWeighting(weighting);
            }
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
        return total/weightings.size();
    }
}
