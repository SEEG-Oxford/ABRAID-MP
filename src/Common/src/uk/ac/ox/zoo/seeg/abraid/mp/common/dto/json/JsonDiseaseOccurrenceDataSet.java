package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * DTO representing the collection of datapoints to be used when training the machine learning component.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseOccurrenceDataSet {

    private List<JsonDiseaseOccurrenceDataPoint> points;

    public JsonDiseaseOccurrenceDataSet(List<JsonDiseaseOccurrenceDataPoint> points) {
        this.points = points;
    }

    public List<JsonDiseaseOccurrenceDataPoint> getPoints() {
        return points;
    }
}
