package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

/**
 * A DTO for "Named CRS" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#named-crs
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonNamedCrs extends GeoJsonCrs {
    private static final String EPSG4326 = "urn:ogc:def:crs:EPSG::4326";
    private static final String CRS_TYPE = "name";

    private GeoJsonNamedCrs(GeoJsonNamedCrsProperties properties) {
        super(CRS_TYPE, properties);
    }

    /**
     * A factory method to create the EPSG4326 CRS.
     * @return The EPSG4326 CRS
     */
    public static GeoJsonNamedCrs createEPSG4326() {
        return new GeoJsonNamedCrs(new GeoJsonNamedCrsProperties(EPSG4326));
    }
}
