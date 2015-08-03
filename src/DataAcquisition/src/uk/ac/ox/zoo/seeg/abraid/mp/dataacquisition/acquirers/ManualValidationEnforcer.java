package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers;

import ch.lambdaj.group.Group;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.collection.LambdaCollections.with;
import static org.hamcrest.Matchers.equalTo;

/**
 * Marks some occurrences that receive a machine weighting as needing manual review. This is to ensure that the data
 * rates on the validator remain sensible, and to ensure the machine learning training data set remains representative
 * of all of the acquired data.
 * Copyright (c) 2015 University of Oxford
 */
public class ManualValidationEnforcer {
    private static final Logger LOGGER = Logger.getLogger(ManualValidationEnforcer.class);
    private static final String MESSAGE =
            "Disease group %s: %s new occurrences, %s with machine weighting added for manual review.";

    private final int minToValidatorPerDiseasePerAcquisition;
    private final double targetFractionToValidatorPerDiseaseAcquisition;
    private final int maxToValidatorPerDiseasePerAcquisition;

    private DiseaseService diseaseService;

    public ManualValidationEnforcer(int minToValidatorPerDiseasePerAcquisition,
                                    double targetFractionToValidatorPerDiseaseAcquisition,
                                    int maxToValidatorPerDiseasePerAcquisition,
                                    DiseaseService diseaseService) {
        this.minToValidatorPerDiseasePerAcquisition = minToValidatorPerDiseasePerAcquisition;
        this.targetFractionToValidatorPerDiseaseAcquisition = targetFractionToValidatorPerDiseaseAcquisition;
        this.maxToValidatorPerDiseasePerAcquisition = maxToValidatorPerDiseasePerAcquisition;
        this.diseaseService = diseaseService;
    }

    /**
     * Adds a random subset of specified occurrences to the IN_REVIEW status if they are: part of any automatic disease,
     * are in the state READY (got a machine weighting) and are model eligible. The number of occurrences put into the
     * IN_REVIEW state is strictly controlled.
     * @param occurrences The newly acquired occurrences to adjust.
     */
    public void addRandomSubsetToManualValidation(Set<DiseaseOccurrence> occurrences) {
        // Work with a cloned list of occurrences of interest
        Set<DiseaseOccurrence> occurrencesOfInterest = with(new HashSet<>(occurrences))
            // Don't put occurrences for not setup diseases on the validator
            .retain(having(on(DiseaseOccurrence.class).getDiseaseGroup().isAutomaticModelRunsEnabled(), equalTo(true)))
            // Occurrences for automatic diseases, with status ready after acquisition, must have received an MW
            .retain(having(on(DiseaseOccurrence.class).getStatus(), equalTo(DiseaseOccurrenceStatus.READY)))
            // Don't put large countries on the validator
            .retain(having(on(DiseaseOccurrence.class).getLocation().isModelEligible(), equalTo(true)));

        // Group the model eligible occurrences by disease group and status
        Group<DiseaseOccurrence> groups = with(occurrencesOfInterest).group(
                by(on(DiseaseOccurrence.class).getDiseaseGroup()));

        for (Group<DiseaseOccurrence> group : groups.subgroups()) {
            List<DiseaseOccurrence> occurrencesOfInterestForSingleDisease = group.findAll();
            Collections.shuffle(occurrencesOfInterestForSingleDisease);

            int numberToAdd = calculateNumberOfOccurrenceToAdjust(occurrencesOfInterestForSingleDisease.size());

            for (int i = 0; i < numberToAdd; i++) {
                DiseaseOccurrence occurrence = occurrencesOfInterestForSingleDisease.get(i);
                occurrence.setStatus(DiseaseOccurrenceStatus.IN_REVIEW);
                diseaseService.saveDiseaseOccurrence(occurrence);
            }
            LOGGER.info(String.format(MESSAGE, ((DiseaseGroup) group.key()).getId(), group.getSize(), numberToAdd));
        }
    }

    private int calculateNumberOfOccurrenceToAdjust(int numberOfOccurrences) {
        int targetReviewCount = (int) Math.round(numberOfOccurrences * targetFractionToValidatorPerDiseaseAcquisition);
        targetReviewCount = Math.max(targetReviewCount, minToValidatorPerDiseasePerAcquisition);
        targetReviewCount = Math.min(targetReviewCount, maxToValidatorPerDiseasePerAcquisition);
        targetReviewCount = Math.min(targetReviewCount, numberOfOccurrences);
        return targetReviewCount;
    }

}
