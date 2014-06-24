package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * The JSON DTO used to trigger model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRun {
    private JsonModelDisease disease;
    private GeoJsonDiseaseOccurrenceFeatureCollection occurrences;
    // Mapping from GAUL code to disease extent class weighting
    private Map<Integer, Integer> extentWeightings;

    public JsonModelRun() {
    }

    public JsonModelRun(JsonModelDisease disease,
                        GeoJsonDiseaseOccurrenceFeatureCollection occurrences,
                        Map<Integer, Integer> extentWeightings) {
        setDisease(disease);
        setOccurrences(occurrences);
        setExtentWeightings(extentWeightings);
    }

    public JsonModelDisease getDisease() {
        return disease;
    }

    public void setDisease(JsonModelDisease disease) {
        this.disease = disease;
    }

    public GeoJsonDiseaseOccurrenceFeatureCollection getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(GeoJsonDiseaseOccurrenceFeatureCollection occurrences) {
        this.occurrences = occurrences;
    }

    public Map<Integer, Integer> getExtentWeightings() {
        return extentWeightings;
    }

    public void setExtentWeightings(Map<Integer, Integer> extents) {
        this.extentWeightings = extents;
    }

    @JsonIgnore
    public boolean isValid() {
        return disease != null && occurrences != null && extentWeightings != null;
    }
}
