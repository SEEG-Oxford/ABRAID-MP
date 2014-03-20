package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.ParseUtils;

import java.util.List;

/**
 * Represents a location from the HealthMap web service.

 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapLocation {
    private String country;
    @JsonProperty("country_id")
    private Long countryId;
    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("lat")
    private Double latitude;
    @JsonProperty("lng")
    private Double longitude;
    @JsonProperty("geonameid")
    private Integer geoNameId;
    @JsonProperty("place_basic_type")
    private String placeBasicType;

    private List<HealthMapAlert> alerts;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = ParseUtils.parseLong(countryId);
    }

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = ParseUtils.parseDouble(latitude);
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = ParseUtils.parseDouble(longitude);
    }

    public Integer getGeoNameId() {
        return geoNameId;
    }

    public void setGeoNameId(String geoNameId) {
        this.geoNameId = ParseUtils.parseInteger(geoNameId);
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
