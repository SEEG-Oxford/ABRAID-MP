package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.Collections;
import java.util.List;

/**
 * A DTO for "Geometry" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#geometry-objects
 * @param <TCrs> The type of crs.
 *
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonGeometry<TCrs extends GeoJsonCrs> extends GeoJsonObject<TCrs> {
    private List<Double> coordinates;

    public GeoJsonGeometry() {
    }

    public GeoJsonGeometry(GeoJsonGeometryType type, List<Double> coordinates, TCrs crs, List<Double> bbox) {
        super(type.getGeoJsonObjectType(), crs, bbox);

        setCoordinates(coordinates);
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GeoJsonGeometry that = (GeoJsonGeometry) o;

        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (coordinates != null ? coordinates.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
