package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

/**
 * A DTO to represent a DiseaseOccurrence in an R compatible form (NA instead of null), without a weighting.
 * Used for CSV serialization of bias occurrences for modelling.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({ "longitude", "latitude", "admin", "gaul", "disease", "date" })
public class JsonBiasModellingDiseaseOccurrence {
    private static final String R_CODE_NULL_IDENTIFIER = "NA";

    @JsonProperty("Longitude")
    private double longitude;

    @JsonProperty("Latitude")
    private double latitude;

    @JsonProperty("Admin")
    private int admin;

    @JsonProperty("GAUL")
    private String gaul;

    @JsonProperty("Disease")
    private int disease;

    @JsonProperty("Date")
    private String date;

    public JsonBiasModellingDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                              double longitude, double latitude,
                                              int admin, String gaul, int disease, String date) {
        setLongitude(longitude);
        setLatitude(latitude);
        setAdmin(precisionAdjuster.adjust(admin, gaul));
        setGaul(gaul);
        setDisease(disease);
        setDate(date);
    }

    public JsonBiasModellingDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                              DiseaseOccurrence occurrence) {
        this(precisionAdjuster,
            occurrence.getLocation().getGeom().getX(),
            occurrence.getLocation().getGeom().getY(),
            occurrence.getLocation().getPrecision().getModelValue(),
            extractGaulString(occurrence.getLocation()),
            occurrence.getDiseaseGroup().getId(),
            extractDateString(occurrence.getOccurrenceDate()));
    }

    /**
     * Gets the string representing a date for use in the model data.
     * @param occurrenceDate The date to format.
     * @return The date string.
     */
    protected static String extractDateString(DateTime occurrenceDate) {
        return ISODateTimeFormat.date().print(occurrenceDate);
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

    public int getDisease() {
        return disease;
    }

    public void setDisease(int disease) {
        this.disease = disease;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
