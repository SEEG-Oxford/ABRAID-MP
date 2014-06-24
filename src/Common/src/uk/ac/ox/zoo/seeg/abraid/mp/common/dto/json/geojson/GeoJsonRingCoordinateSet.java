package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of coordinate pairs, to represent a GeoJson LineString's coordinates.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonRingCoordinateSet extends ArrayList<GeoJsonCoordinate> {
    public GeoJsonRingCoordinateSet() {
    }

    public GeoJsonRingCoordinateSet(List<GeoJsonCoordinate> geoJsonCoordinateList) {
        super(geoJsonCoordinateList);
    }
}
