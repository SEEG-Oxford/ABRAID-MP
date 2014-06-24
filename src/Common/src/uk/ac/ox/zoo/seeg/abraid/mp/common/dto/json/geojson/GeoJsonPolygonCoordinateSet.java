package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of GeoJson LinearRing coordinate arrays, to represent a GeoJson Polygon's coordinates.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonPolygonCoordinateSet extends ArrayList<GeoJsonRingCoordinateSet> {
    public GeoJsonPolygonCoordinateSet() {
    }

    public GeoJsonPolygonCoordinateSet(List<GeoJsonRingCoordinateSet> ringCoordinateSetList) {
        super(ringCoordinateSetList);
    }
}
