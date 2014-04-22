package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of coordinate pairs, to represent a GeoJson LineString's coordinates.
 * Copyright (c) 2014 University of Oxford
 */
public class RingCoordinateSet extends ArrayList<PairCoordinateSet> {
    RingCoordinateSet() {
    }

    RingCoordinateSet(List<PairCoordinateSet> pairCoordinateSetList) {
        super(pairCoordinateSetList);
    }
}
