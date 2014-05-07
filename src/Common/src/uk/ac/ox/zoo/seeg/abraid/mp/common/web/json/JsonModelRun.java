package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;

/**
 * The JSON DTO used to trigger model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRun {
    private JsonModelDisease disease;
    private GeoJsonDiseaseOccurrenceFeatureCollection occurrences;
    private Collection<Integer> extents;

    public JsonModelRun() {
    }

    public JsonModelRun(JsonModelDisease disease,
                        GeoJsonDiseaseOccurrenceFeatureCollection occurrences,
                        Collection<Integer> extents) {
        setDisease(disease);
        setOccurrences(occurrences);
        setExtents(extents);
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

    public Collection<Integer> getExtents() {
        return extents;
    }

    public void setExtents(Collection<Integer> extents) {
        this.extents = extents;
    }

    @JsonIgnore
    public boolean isValid() {
        return disease != null && occurrences != null && extents != null;
    }
}
