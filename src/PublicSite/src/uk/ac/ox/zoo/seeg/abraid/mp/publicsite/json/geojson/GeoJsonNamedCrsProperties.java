package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

/**
 * A DTO for the properties on a "Named CRS" object.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#named-crs
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonNamedCrsProperties {
    private final String name;

    public GeoJsonNamedCrsProperties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
