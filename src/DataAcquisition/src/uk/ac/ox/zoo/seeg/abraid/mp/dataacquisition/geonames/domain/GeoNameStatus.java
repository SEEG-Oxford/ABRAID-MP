package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain;

/**
 * Represents the status of a call to the GeoNames web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoNameStatus {
    private String message;
    private int value;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
