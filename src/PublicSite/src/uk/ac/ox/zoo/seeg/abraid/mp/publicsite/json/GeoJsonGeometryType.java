package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

/**
 * Created by zool1112 on 05/03/14.
 */
//@JsonSerialize
public enum GeoJsonGeometryType {
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

    public String getJsonName() {
        return getGeoJsonObjectType().getJsonName();
    }
}
