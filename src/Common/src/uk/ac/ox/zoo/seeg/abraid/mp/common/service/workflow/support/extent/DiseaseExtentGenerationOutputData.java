package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.Collection;
import java.util.Map;

/**
 * A wrapper for all of the results of a disease extent generation.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGenerationOutputData {
    private final Map<Integer, DiseaseExtentClass> diseaseExtentClassByGaulCode;
    private final Map<Integer, Integer> occurrenceCounts;
    private final Map<Integer, Collection<DiseaseOccurrence>> latestOccurrencesByGaulCode;

    public DiseaseExtentGenerationOutputData(Map<Integer, DiseaseExtentClass> diseaseExtentClassByGaulCode,
                                             Map<Integer, Integer> occurrenceCounts,
                                             Map<Integer, Collection<DiseaseOccurrence>> latestOccurrencesByGaulCode) {
        this.diseaseExtentClassByGaulCode = diseaseExtentClassByGaulCode;
        this.occurrenceCounts = occurrenceCounts;
        this.latestOccurrencesByGaulCode = latestOccurrencesByGaulCode;
    }

    public Map<Integer, DiseaseExtentClass> getDiseaseExtentClassByGaulCode() {
        return diseaseExtentClassByGaulCode;
    }

    public Map<Integer, Integer> getOccurrenceCounts() {
        return occurrenceCounts;
    }

    public Map<Integer, Collection<DiseaseOccurrence>> getLatestOccurrencesByGaulCode() {
        return latestOccurrencesByGaulCode;
    }
}
