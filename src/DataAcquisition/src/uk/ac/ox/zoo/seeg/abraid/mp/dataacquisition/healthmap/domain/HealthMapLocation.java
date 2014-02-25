package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Represents a location from the HealthMap web service.

 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocation {
    private String country;
    @JsonProperty("place_name")
    private String placeName;
    private Double lat;
    private Double lng;
    @JsonProperty("geonameid")
    private Integer geoNameId;
    @JsonProperty("place_basic_type")
    private String placeBasicType;

    private List<HealthMapAlert> alerts;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = StringUtils.trimWhitespace(country);
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = StringUtils.trimWhitespace(placeName);
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getGeoNameId() {
        return geoNameId;
    }

    public void setGeoNameId(Integer geoNameId) {
        this.geoNameId = geoNameId;
    }

    public String getPlaceBasicType() {
        return placeBasicType;
    }

    public void setPlaceBasicType(String placeBasicType) {
        this.placeBasicType = StringUtils.trimWhitespace(placeBasicType);
    }

    public List<HealthMapAlert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<HealthMapAlert> alerts) {
        this.alerts = alerts;
    }
}
