package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.group.Group;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReviewResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;

import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * Updates the weightings.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculator {
    private DiseaseService diseaseService;

    public WeightingsCalculator(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    public void calculateDiseaseOccurrenceWeightings() {
        DateTime lastRetrievalDate = DateTime.now(); // = get from commons conf properties file
        List<DiseaseOccurrenceReview> allReviews =
            diseaseService.getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval(lastRetrievalDate);
        for (DiseaseOccurrence occurrence : extractDistinctDiseaseOccurrences(allReviews)) {
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
