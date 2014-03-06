package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import java.util.List;

/**
 * A DTO for "Feature" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#feature-objects
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonFeature extends GeoJsonObject {
    private final Integer id;
    private final GeoJsonGeometry geometry;
    private final Object properties;

    /**
     * Create a new instance of GeoJsonFeature.
     * @param id OPTIONAL: An identifier for the feature
     * @param geometry The geometry of the feature
     * @param properties The properties of the feature
     * @param crs OPTIONAL: The coordinate reference system of the feature
     * @param bbox OPTIONAL: The bounding box of the feature
     */
    public GeoJsonFeature(Integer id, GeoJsonGeometry geometry, Object properties, GeoJsonCrs crs, List<Double> bbox) {
        super(GeoJsonObjectType.FEATURE, crs, bbox);

        this.id = id;
        this.geometry = geometry;
        this.properties = properties;
    }

    public Integer getId() {
        return id;
    }

    public GeoJsonGeometry getGeometry() {
        return geometry;
    }

    public Object getProperties() {
        return properties;
    }
}
