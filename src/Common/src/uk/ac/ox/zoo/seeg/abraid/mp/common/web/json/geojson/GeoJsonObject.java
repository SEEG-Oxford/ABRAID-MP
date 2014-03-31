package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

/**
 * A DTO for base structure of GeoJSON objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#geojson-objects
 * @param <TCrs> The type of crs.
 *
 * Copyright (c) 2014 University of Oxford
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class GeoJsonObject<TCrs extends GeoJsonCrs> {
    private GeoJsonObjectType type;
    private TCrs crs;
    private List<Double> bbox;

    public GeoJsonObject() {
    }

    /**
     * Create a new instance of GeoJsonObject.
     * @param type The object type.
     * @param crs OPTIONAL: the coordinate reference system for the object.
     * @param bbox OPTIONAL: the bounding box for the object.
     */
    public GeoJsonObject(GeoJsonObjectType type, TCrs crs, List<Double> bbox) {
        setType(type);
        setCrs(crs);
        setBbox(bbox);
    }

    public GeoJsonObjectType getType() {
        return type;
    }

    public TCrs getCrs() {
        return crs;
    }

    public List<Double> getBBox() {
        return bbox;
    }

    public void setType(GeoJsonObjectType type) {
        this.type = type;
    }

    public void setCrs(TCrs crs) {
        this.crs = crs;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = (bbox == null) ? null : Collections.unmodifiableList(bbox);
    }

    ///COVERAGE:OFF generated code
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoJsonObject that = (GeoJsonObject) o;

        if (bbox != null ? !bbox.equals(that.bbox) : that.bbox != null) return false;
        if (crs != null ? !crs.equals(that.crs) : that.crs != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (crs != null ? crs.hashCode() : 0);
        result = 31 * result + (bbox != null ? bbox.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
    ///COVERAGE:ON
}
