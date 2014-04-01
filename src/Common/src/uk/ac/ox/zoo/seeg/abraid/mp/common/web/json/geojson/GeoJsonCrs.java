package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

/**
 * A DTO for Coordinate Reference System objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#coordinate-reference-system-objects
 * @param <TProp> The type of the properties field.
 *
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonCrs<TProp> {
    private String type;
    private TProp properties;

    public GeoJsonCrs() {
    }

    public GeoJsonCrs(String type, TProp properties) {
        setType(type);
        setProperties(properties);
    }

    public String getType() {
        return type;
    }

    public TProp getProperties() {
        return properties;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProperties(TProp properties) {
        this.properties = properties;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoJsonCrs that = (GeoJsonCrs) o;

        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
