package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A (longitude, latitude) coordinate pair to represent GeoJson Point's position.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonCoordinate {
    private double longitude;
    private double latitude;

    @JsonCreator
    public GeoJsonCoordinate(List<Double> values) {
        setValues(values);
    }

    public GeoJsonCoordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @JsonValue
    public List<Double> getValues() {
        return Collections.unmodifiableList(Arrays.asList(longitude, latitude));
    }

    /**
     * Coordinate setter.
     * @param values The list of coordinates.
     */
    public void setValues(List<Double> values) {
        if (values.size() != 2) {
            throw new IllegalArgumentException();
        }
        this.longitude = values.get(0);
        this.latitude = values.get(1);
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoJsonCoordinate)) return false;

        GeoJsonCoordinate that = (GeoJsonCoordinate) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(longitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
