package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Provides the valid GeoJSON geometry object types.
 * Commented out enum entries are parts of the GeoJSON, which are not yet supported.
 * Copyright (c) 2014 University of Oxford
 */
@JsonSerialize(using = GeoJsonNamedEnumSerializer.class)
@JsonDeserialize(using = GeoJsonGeometryTypeDeserializer.class)
public enum GeoJsonGeometryType implements GeoJsonNamedEnum {
    /**
     * A GeoJSON Point.
     */
    POINT(GeoJsonObjectType.POINT),
    //MULTI_POINT(GeoJsonObjectType.MULTI_POINT),
    //LINE_STRING(GeoJsonObjectType.LINE_STRING),
    //MULTI_LINE_STRING(GeoJsonObjectType.MULTI_LINE_STRING),
    //POLYGON(GeoJsonObjectType.POLYGON),
    /**
     * A GeoJSON MultiPolygon.
     */
    MULTI_POLYGON(GeoJsonObjectType.MULTI_POLYGON);
    //GEOMETRY_COLLECTION(GeoJsonObjectType.GEOMETRY_COLLECTION);

    private GeoJsonObjectType objectType;

    GeoJsonGeometryType(GeoJsonObjectType objectType) {
        this.objectType = objectType;
    }

    public GeoJsonObjectType getGeoJsonObjectType() {
        return objectType;
    }

    public String getGeoJsonName() {
        return getGeoJsonObjectType().getGeoJsonName();
    }

}
