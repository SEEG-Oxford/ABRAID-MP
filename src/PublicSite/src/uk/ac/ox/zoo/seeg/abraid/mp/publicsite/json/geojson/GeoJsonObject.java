package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class GeoJsonObject {
    private final GeoJsonObjectType type;
    private final GeoJsonCrs crs;
    private final List<Double> bbox;

    public GeoJsonObject(GeoJsonObjectType type, GeoJsonCrs crs, List<Double> bbox) {
        this.type = type;
        this.crs = crs;
        this.bbox = bbox == null ? null : Collections.unmodifiableList(bbox);
    }

    public GeoJsonObjectType getType() {
        return type;
    }

    public GeoJsonCrs getCrs() {
        return crs;
    }

    public List<Double> getBBox() {
        return bbox;
    }
}
