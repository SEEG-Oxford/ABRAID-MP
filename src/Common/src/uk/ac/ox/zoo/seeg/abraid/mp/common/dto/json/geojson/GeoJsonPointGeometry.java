package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import java.util.List;

/**
 * A DTO for "Point Geometry" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#point
 * @param <TCrs> The Coordinate Reference System type.
 *
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonPointGeometry<TCrs extends GeoJsonCrs> extends GeoJsonGeometry<TCrs, GeoJsonCoordinate> {
    public GeoJsonPointGeometry() {
    }

    public GeoJsonPointGeometry(double longitude, double latitude, TCrs crs, List<Double> bbox) {
        super(GeoJsonGeometryType.POINT, new GeoJsonCoordinate(longitude, latitude), crs, bbox);
    }
}
