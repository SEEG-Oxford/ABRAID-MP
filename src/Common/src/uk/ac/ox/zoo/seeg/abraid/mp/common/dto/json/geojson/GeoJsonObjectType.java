package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Provides the valid GeoJSON object types.
 * Commented out enum entries are parts of the GeoJSON, which are not yet supported.
 * Copyright (c) 2014 University of Oxford
 */
@JsonSerialize(using = GeoJsonNamedEnumSerializer.class)
@JsonDeserialize(using = GeoJsonObjectTypeDeserializer.class)
public enum GeoJsonObjectType implements GeoJsonNamedEnum {
    /**
     * A GeoJSON Point.
     */
    POINT("Point"),
    //MULTI_POINT("MultiPoint"),
    //LINE_STRING("LineString"),
    //MULTI_LINE_STRING("MultiLineString"),
    //POLYGON("Polygon"),
    /**
     * A GeoJSON MultiPolygon.
     */
    MULTI_POLYGON("MultiPolygon"),
    //GEOMETRY_COLLECTION("GeometryCollection"),
    /**
     * A GeoJSON Feature.
     */
    FEATURE("Feature"),
    /**
     * A GeoJSON Feature Collection.
     */
    FEATURE_COLLECTION("FeatureCollection");

    private String jsonName;

    GeoJsonObjectType(String jsonName) {
        this.jsonName = jsonName;
    }

    public String getGeoJsonName() {
        return jsonName;
    }
}
