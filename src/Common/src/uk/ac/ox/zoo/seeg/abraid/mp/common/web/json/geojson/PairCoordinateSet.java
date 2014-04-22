package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import java.util.ArrayList;

/**
 * A pair of (longitude, latitude) coordinates to represent GeoJson Point's position.
 * Copyright (c) 2014 University of Oxford
 */
public class PairCoordinateSet extends ArrayList<Double> {
    PairCoordinateSet() {
    }

    PairCoordinateSet(double longitude, double latitude) {
        super(2);
        add(longitude);
        add(latitude);
    }
}
