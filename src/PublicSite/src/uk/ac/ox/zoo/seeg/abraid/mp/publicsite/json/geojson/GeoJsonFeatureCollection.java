package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import java.util.Collections;
import java.util.List;

/**
 * A DTO for "FeatureCollection" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#feature-collection-objects
 * Copyright (c) 2014 University of Oxford
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
