package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import java.util.Collections;
import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
public abstract class GeoJsonFeatureCollection extends GeoJsonObject {

    private final List<GeoJsonFeature> features;

    public GeoJsonFeatureCollection(List<GeoJsonFeature> features, GeoJsonCrs crs, List<Double> bbox) {
        super(GeoJsonObjectType.FEATURE_COLLECTION, crs, bbox);

        this.features = Collections.unmodifiableList(features);
    }

    public List<GeoJsonFeature> getFeatures() {
        return features;
    }
}
