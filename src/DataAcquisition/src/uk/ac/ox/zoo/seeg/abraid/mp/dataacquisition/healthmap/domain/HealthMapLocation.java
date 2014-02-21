package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain;

import java.util.List;

/**
 * Represents a location from the HealthMap web service.

 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocation {
    private String country;
    private String place_name;
    private Double lat;
    private Double lng;
    private Integer geonameid;
    private String place_basic_type;

    private List<HealthMapAlert> alerts;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
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

    public Integer getGeonameid() {
        return geonameid;
    }

    public void setGeonameid(Integer geonameid) {
        this.geonameid = geonameid;
    }

    public String getPlace_basic_type() {
        return place_basic_type;
    }

    public void setPlace_basic_type(String place_basic_type) {
        this.place_basic_type = place_basic_type;
    }

    public List<HealthMapAlert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<HealthMapAlert> alerts) {
        this.alerts = alerts;
    }
}
