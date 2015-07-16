package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.ObjectUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelingLocationPrecisionAdjuster;

/**
 * A DTO to represent a DiseaseOccurrence in an R compatible form (NA instead of null).
 * Used for CSV serialization of occurrences for modelling.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({ "longitude", "latitude", "weight", "admin", "gaul" })
public class JsonModellingDiseaseOccurrence {
    private static final String R_CODE_NULL_IDENTIFIER = "NA";

    @JsonProperty("Longitude")
    private double longitude;

    @JsonProperty("Latitude")
    private double latitude;

    @JsonProperty("Weight")
    private double weight;

    @JsonProperty("Admin")
    private int admin;

    @JsonProperty("GAUL")
    private String gaul;

    public JsonModellingDiseaseOccurrence(ModelingLocationPrecisionAdjuster precisionAdjuster,
                                          double longitude, double latitude, double weight, int admin, String gaul) {
        setLongitude(longitude);
        setLatitude(latitude);
        setWeight(weight);
        setAdmin(precisionAdjuster.adjust(admin, gaul));
        setGaul(gaul);
    }

    public JsonModellingDiseaseOccurrence(ModelingLocationPrecisionAdjuster precisionAdjuster,
                                          GeoJsonDiseaseOccurrenceFeature occurrence) {
        this(precisionAdjuster,
            occurrence.getGeometry().getCoordinates().getLongitude(),
            occurrence.getGeometry().getCoordinates().getLatitude(),
            occurrence.getProperties().getWeighting(),
            occurrence.getProperties().getLocationPrecision().getModelValue(),
            extractGaulString(occurrence.getProperties().getGaulCode()));
    }

    /**
     * Handles null gaul codes appropriately. For use in constructors.
     * @param gaul The gaul code.
     * @return The gaul code string.
     */
    protected static String extractGaulString(Integer gaul) {
        return (gaul == null) ? null : ObjectUtils.toString(gaul);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public String getGaul() {
        return gaul;
    }

    public void setGaul(String gaul) {
        this.gaul = (gaul == null) ? R_CODE_NULL_IDENTIFIER : gaul;
    }
}
