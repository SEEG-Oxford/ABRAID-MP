package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

/**
 * Created by zool1112 on 05/03/14.
 */
public abstract class GeoJsonObject {
    private final GeoJsonObjectType type;

    public GeoJsonObject(GeoJsonObjectType type) {
        this.type = type;
    }

    public GeoJsonObjectType getType() {
        return type;
    }

}
