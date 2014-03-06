package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

/**
 * Created by zool1112 on 05/03/14.
 */
public abstract class GeoJsonCrs {
    private final String type;
    private final Object properties;

    public GeoJsonCrs(String type, Object properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Object getProperties() {
        return properties;
    }


}
