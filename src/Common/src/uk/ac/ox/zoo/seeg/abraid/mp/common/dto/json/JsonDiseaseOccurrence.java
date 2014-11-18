package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.ObjectUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

/**
 * A DTO to represent a DiseaseOccurrence in an R compatible form (NA instead of null).
 * Used for CSV serialization of occurrences.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({ "longitude", "latitude", "weight", "admin", "gaul" })
public class JsonDiseaseOccurrence {
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

    public JsonDiseaseOccurrence(double longitude, double latitude, double weight, int admin, String gaul) {
        setLongitude(longitude);
        setLatitude(latitude);
        setWeight(weight);
        setAdmin(admin);
        setGaul(gaul);
    }

    public JsonDiseaseOccurrence(DiseaseOccurrence inputDiseaseOccurrence) {
        this(inputDiseaseOccurrence.getLocation().getGeom().getX(),
            inputDiseaseOccurrence.getLocation().getGeom().getY(),
            inputDiseaseOccurrence.getFinalWeighting(),
            inputDiseaseOccurrence.getLocation().getPrecision().getModelValue(),
            extractGaulCode(inputDiseaseOccurrence.getLocation()));
    }

    public JsonDiseaseOccurrence(GeoJsonDiseaseOccurrenceFeature occurrence) {
        this(occurrence.getGeometry().getCoordinates().getLongitude(),
            occurrence.getGeometry().getCoordinates().getLatitude(),
            occurrence.getProperties().getWeighting(),
            occurrence.getProperties().getLocationPrecision().getModelValue(),
            extractGaulCode(occurrence));
    }

    private static String extractGaulCode(Location location) {
        if (location.getPrecision() == LocationPrecision.PRECISE) {
            return null;
        } else {
            return ObjectUtils.toString(location.getAdminUnitQCGaulCode());
        }
    }

    private static String extractGaulCode(GeoJsonDiseaseOccurrenceFeature occurrence) {
        if (occurrence.getProperties().getLocationPrecision() == LocationPrecision.PRECISE) {
            return null;
        } else {
            return ObjectUtils.toString(occurrence.getProperties().getGaulCode());
        }
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
