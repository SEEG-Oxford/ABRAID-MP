package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

/**
 * Created by zool1112 on 05/03/14.
 */
public class GeoJsonCrs {
    private final String type;
    private final Object properties;

    private GeoJsonCrs(String type, Object properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Object getProperties() {
        return properties;
    }

    public static GeoJsonCrs createEPSG4326() {
        return new GeoJsonCrs("EPSG", new GeoJsonCrsProperties("4326"));
    }
}
