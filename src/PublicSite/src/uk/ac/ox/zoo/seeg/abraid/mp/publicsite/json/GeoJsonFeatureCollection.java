package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import java.util.Collections;
import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
public abstract class GeoJsonFeatureCollection extends GeoJsonObject {

    private final List<GeoJsonFeature> features;
    private final GeoJsonCrs crs;

    public GeoJsonFeatureCollection(List<GeoJsonFeature> features, GeoJsonCrs crs) {
        super(GeoJsonObjectType.FEATURE_COLLECTION);

        this.features = Collections.unmodifiableList(features);
        this.crs = crs;
    }

    public List<GeoJsonFeature> getFeatures() {
        return features;
    }

    public GeoJsonCrs getCrs() {
        // Technically CRS is an optional attribute of GeoJsonObject, but this is the only place we will use it.
        return crs;
    }
}
