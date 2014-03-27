package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

/**
 * A DTO for base structure of GeoJSON objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#geojson-objects
 * Copyright (c) 2014 University of Oxford
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class GeoJsonObject {
    private final GeoJsonObjectType type;
    private final GeoJsonCrs crs;
    private final List<Double> bbox;

    /**
     * Create a new instance of GeoJsonObject.
     * @param type The object type.
     * @param crs OPTIONAL: the coordinate reference system for the object.
     * @param bbox OPTIONAL: the bounding box for the object.
     */
    public GeoJsonObject(GeoJsonObjectType type, GeoJsonCrs crs, List<Double> bbox) {
        this.type = type;
        this.crs = crs;
        this.bbox = (bbox == null) ? null : Collections.unmodifiableList(bbox);
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
