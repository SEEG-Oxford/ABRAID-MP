package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

/**
 * A DTO for Coordinate Reference System objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#coordinate-reference-system-objects
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonCrs {
    private final String type;
    private final Object properties;

    public GeoJsonCrs(String type, Object properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Object getProperties() {
        return properties;
    }
}
