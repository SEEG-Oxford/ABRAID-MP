package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import java.util.Collections;
import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
public abstract class GeoJsonGeometry extends GeoJsonObject {
    private final List<Double> coordinates;

    public GeoJsonGeometry(GeoJsonGeometryType type, List<Double> coordinates) {
        super(type.getGeoJsonObjectType());

        this.coordinates = Collections.unmodifiableList(coordinates);
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }
}
