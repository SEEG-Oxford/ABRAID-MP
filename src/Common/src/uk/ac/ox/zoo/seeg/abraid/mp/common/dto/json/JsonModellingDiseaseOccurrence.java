package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

/**
 * A DTO to represent a DiseaseOccurrence in an R compatible form (NA instead of null).
 * Used for CSV serialization of occurrences for modelling.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({ "longitude", "latitude", "weight", "admin", "gaul", "disease" })
public class JsonModellingDiseaseOccurrence extends JsonSupplementaryModellingDiseaseOccurrence{

    @JsonProperty("Weight")
    private double weight;

    public JsonModellingDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                          double longitude, double latitude, double weight,
                                          int admin, String gaul, int disease) {
        super(precisionAdjuster, longitude, latitude, admin, gaul, disease);
        setWeight(weight);
    }

    public JsonModellingDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                          DiseaseOccurrence occurrence) {
        this(precisionAdjuster,
            occurrence.getLocation().getGeom().getX(),
            occurrence.getLocation().getGeom().getY(),
            occurrence.getFinalWeighting(),
            occurrence.getLocation().getPrecision().getModelValue(),
            extractGaulString(occurrence.getLocation()),
            occurrence.getDiseaseGroup().getId());
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
