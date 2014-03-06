package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by zool1112 on 05/03/14.
 */
@JsonSerialize(using = GeoJsonNamedEnumSerializer.class)
public enum GeoJsonObjectType implements GeoJsonNamedEnum {
    POINT("Point"),
    //MULTI_POINT("MultiPoint"),
    //LINE_STRING("LineString"),
    //MULTI_LINE_STRING("MultiLineString"),
    //POLYGON("Polygon"),
    //MULTI_POLYGON("MultiPolygon"),
    //GEOMETRY_COLLECTION("GeometryCollection"),
    FEATURE("Feature"),
    FEATURE_COLLECTION("FeatureCollection");

    private String jsonName;

    GeoJsonObjectType(String jsonName) {
        this.jsonName = jsonName;
    }

    public String getGeoJsonName() {
        return jsonName;
    }
}
