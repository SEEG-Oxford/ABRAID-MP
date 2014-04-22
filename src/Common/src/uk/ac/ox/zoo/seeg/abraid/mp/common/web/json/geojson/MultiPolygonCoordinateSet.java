package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of GeoJson Polygon coordinate arrays, to represent a GeoJson MultiPolygon's coordinates.
 * Copyright (c) 2014 University of Oxford
 */
public class MultiPolygonCoordinateSet extends ArrayList<PolygonCoordinateSet> {
    MultiPolygonCoordinateSet() {
    }

    MultiPolygonCoordinateSet(List<PolygonCoordinateSet> polygonCoordinateSetList) {
        super(polygonCoordinateSetList);
    }
}
