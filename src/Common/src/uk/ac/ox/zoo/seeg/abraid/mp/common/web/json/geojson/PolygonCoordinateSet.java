package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of GeoJson LinearRing coordinate arrays, to represent a GeoJson Polygon's coordinates.
 * Copyright (c) 2014 University of Oxford
 */
public class PolygonCoordinateSet extends ArrayList<RingCoordinateSet> {
    PolygonCoordinateSet() {
    }

    PolygonCoordinateSet(List<RingCoordinateSet> ringCoordinateSetList) {
        super(ringCoordinateSetList);
    }
}
