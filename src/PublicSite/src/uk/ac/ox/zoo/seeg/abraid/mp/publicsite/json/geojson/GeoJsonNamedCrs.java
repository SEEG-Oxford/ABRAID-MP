package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

/**
 * Created by zool1112 on 06/03/14.
 */
public class GeoJsonNamedCrs extends GeoJsonCrs {
    public static final String EPSG4326 = "urn:ogc:def:crs:EPSG::4326";
    public static final String CRS_TYPE = "name";

    private GeoJsonNamedCrs(GeoJsonNamedCrsProperties properties) {
        super(CRS_TYPE, properties);
    }

    public static GeoJsonNamedCrs createEPSG4326() {
        return new GeoJsonNamedCrs(new GeoJsonNamedCrsProperties(EPSG4326));
    }
}
