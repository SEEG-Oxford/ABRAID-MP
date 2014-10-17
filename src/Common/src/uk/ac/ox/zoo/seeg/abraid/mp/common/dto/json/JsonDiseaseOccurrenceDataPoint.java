package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * DTO representing the features of a disease occurrence used by the machine learning component.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseOccurrenceDataPoint {

    private Double distanceFromExtent;
    private Double environmentalSuitability;
    private Integer feedId;
    private Double expertWeighting;

    public JsonDiseaseOccurrenceDataPoint(DiseaseOccurrence occurrence) {
        this.distanceFromExtent = occurrence.getDistanceFromDiseaseExtent();
        this.environmentalSuitability = occurrence.getEnvironmentalSuitability();
        this.feedId = occurrence.getAlert().getFeed().getId();
        this.expertWeighting = occurrence.getExpertWeighting();
    }

    public Double getDistanceFromExtent() {
        return distanceFromExtent;
    }

    public Double getEnvironmentalSuitability() {
        return environmentalSuitability;
    }

    public Integer getFeedId() {
        return feedId;
    }

    public Double getExpertWeighting() {
        return expertWeighting;
    }
}
