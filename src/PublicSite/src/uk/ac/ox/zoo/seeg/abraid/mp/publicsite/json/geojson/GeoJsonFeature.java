package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
public abstract class GeoJsonFeature extends GeoJsonObject {
    private final int id;
    private final GeoJsonGeometry geometry;
    private final Object properties;

    public GeoJsonFeature(int id, GeoJsonGeometry geometry, Object properties, GeoJsonCrs crs, List<Double> bbox) {
        super(GeoJsonObjectType.FEATURE, crs, bbox);

        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public int getId() {
        // Technically the id attribute of a feature is optional, but as we should have it in all circumstances, we can treat it as mandatory.
        return id;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public Object getProperties() {
        return properties;
    }
}
