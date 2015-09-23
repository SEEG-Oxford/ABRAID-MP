package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.ObjectUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

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

    public JsonModellingDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                          double longitude, double latitude, double weight, int admin, String gaul) {
        setLongitude(longitude);
        setLatitude(latitude);
        setWeight(weight);
        setAdmin(precisionAdjuster.adjust(admin, gaul));
        setGaul(gaul);
    }

    public JsonModellingDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                          DiseaseOccurrence occurrence) {
        this(precisionAdjuster,
            occurrence.getLocation().getGeom().getX(),
            occurrence.getLocation().getGeom().getY(),
            occurrence.getFinalWeighting(),
            occurrence.getLocation().getPrecision().getModelValue(),
            extractGaulString(occurrence.getLocation()));
    }


    /**
     * Gets the string representing the GAUL code to use in the model data.
     * @param location The occurrence location.
     * @return The GAUL code string.
     */
    protected static String extractGaulString(Location location) {
        if (location.getPrecision().equals(LocationPrecision.PRECISE)) {
            return replaceNullGaul(null);
        } else if (location.getPrecision().equals(LocationPrecision.COUNTRY)) {
            return replaceNullGaul(location.getCountryGaulCode());
        } else {
            return replaceNullGaul(location.getAdminUnitQCGaulCode());
        }
    }

    private static String replaceNullGaul(Integer gaul) {
        return (gaul == null) ? R_CODE_NULL_IDENTIFIER : ObjectUtils.toString(gaul);
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
