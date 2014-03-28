package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.Collections;
import java.util.List;

/**
 * A DTO for "Geometry" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#geometry-objects
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonGeometry extends GeoJsonObject {
    private final List<Double> coordinates;

    public GeoJsonGeometry(GeoJsonGeometryType type, List<Double> coordinates, GeoJsonCrs crs, List<Double> bbox) {
        super(type.getGeoJsonObjectType(), crs, bbox);

        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }
}
