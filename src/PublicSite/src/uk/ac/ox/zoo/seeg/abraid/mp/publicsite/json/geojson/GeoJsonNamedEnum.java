package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

/**
 * An interface to specify a method which should be used to serialize enums.
 * Copyright (c) 2014 University of Oxford
 */
public interface GeoJsonNamedEnum {
    /**
     * Get the name that should be used for a enum member in the serialized GeoJSON.
     * @return The name.
     */
    String getGeoJsonName();
}
