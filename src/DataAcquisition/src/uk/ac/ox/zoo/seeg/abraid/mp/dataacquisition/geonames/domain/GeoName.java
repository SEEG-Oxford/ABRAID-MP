package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.geonames.domain;

/**
 * Represents a GeoName, as returned by the GeoNames web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class GeoName {
    private Integer geonameId;
    private String fcode;

    public Integer getGeonameId() {
        return geonameId;
    }

    public void setGeonameId(Integer geonameId) {
        this.geonameId = geonameId;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }
}
