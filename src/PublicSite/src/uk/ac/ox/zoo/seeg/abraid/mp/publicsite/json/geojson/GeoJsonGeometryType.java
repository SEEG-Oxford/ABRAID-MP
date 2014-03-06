package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by zool1112 on 05/03/14.
 */
@JsonSerialize(using = GeoJsonNamedEnumSerializer.class)
public enum GeoJsonGeometryType implements GeoJsonNamedEnum {
    POINT(GeoJsonObjectType.POINT);
    //MULTI_POINT(GeoJsonObjectType.MULTI_POINT),
    //LINE_STRING(GeoJsonObjectType.LINE_STRING),
    //MULTI_LINE_STRING(GeoJsonObjectType.MULTI_LINE_STRING),
    //POLYGON(GeoJsonObjectType.POLYGON),
    //MULTI_POLYGON(GeoJsonObjectType.MULTI_POLYGON),
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
