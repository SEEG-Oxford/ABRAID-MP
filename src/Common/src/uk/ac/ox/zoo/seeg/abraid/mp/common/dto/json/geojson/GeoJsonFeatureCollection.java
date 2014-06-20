package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import java.util.Collections;
import java.util.List;

/**
 * A DTO for "FeatureCollection" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#feature-collection-objects
 * @param <TFeature> The type of feature the collection contains.
 * @param <TCrs> The type of crs.
 *
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonFeatureCollection<TFeature extends GeoJsonFeature, TCrs extends GeoJsonCrs>
        extends GeoJsonObject<TCrs> {
    private List<TFeature> features;

    public GeoJsonFeatureCollection() {
    }

    public GeoJsonFeatureCollection(List<TFeature> features, TCrs crs, List<Double> bbox) {
        super(GeoJsonObjectType.FEATURE_COLLECTION, crs, bbox);

        setFeatures(features);
    }

    public List<TFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<TFeature> features) {
        this.features = Collections.unmodifiableList(features);
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GeoJsonFeatureCollection that = (GeoJsonFeatureCollection) o;

        if (features != null ? !features.equals(that.features) : that.features != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (features != null ? features.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
