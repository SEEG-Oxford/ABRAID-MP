package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

/**
 * A DTO for the properties on a "Named CRS" object.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#named-crs
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonNamedCrsProperties {
    private String name;

    public GeoJsonNamedCrsProperties() {
    }

    GeoJsonNamedCrsProperties(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoJsonNamedCrsProperties that = (GeoJsonNamedCrsProperties) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
