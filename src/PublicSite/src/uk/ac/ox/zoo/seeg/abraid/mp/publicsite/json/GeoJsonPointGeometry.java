package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
public class GeoJsonPointGeometry extends GeoJsonGeometry {

    public GeoJsonPointGeometry(double longitude, double latitude) {
        super(GeoJsonGeometryType.POINT, extractCoordinates(longitude, latitude));
    }

    private static List<Double> extractCoordinates(double longitude, double latitude) {
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(longitude);
        coordinates.add(latitude);
        return coordinates;
    }
}
