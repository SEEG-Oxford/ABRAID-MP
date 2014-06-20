package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson;

import java.util.List;

/**
 * A DTO for "Feature" objects.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Implements the specification available from http://geojson.org/geojson-spec.html#feature-objects
 * @param <TProp> The type of the properties field.
 * @param <TGeom> The type of geometry.
 * @param <TCrs> The type of crs.
 *
 * Copyright (c) 2014 University of Oxford
 */
public abstract class GeoJsonFeature<TProp, TGeom extends GeoJsonGeometry, TCrs extends GeoJsonCrs>
        extends GeoJsonObject<TCrs> {
    private Integer id;
    private TGeom geometry;
    private TProp properties;

    public GeoJsonFeature() {
    }

    /**
     * Create a new instance of GeoJsonFeature.
     * @param id OPTIONAL: An identifier for the feature
     * @param geometry The geometry of the feature
     * @param properties The properties of the feature
     * @param crs OPTIONAL: The coordinate reference system of the feature
     * @param bbox OPTIONAL: The bounding box of the feature
     */
    public GeoJsonFeature(Integer id, TGeom geometry, TProp properties, TCrs crs, List<Double> bbox) {
        super(GeoJsonObjectType.FEATURE, crs, bbox);

        setId(id);
        setGeometry(geometry);
        setProperties(properties);
    }

    public Integer getId() {
        return id;
    }

    public TGeom getGeometry() {
        return geometry;
    }

    public TProp getProperties() {
        return properties;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setGeometry(TGeom geometry) {
        this.geometry = geometry;
    }

    public void setProperties(TProp properties) {
        this.properties = properties;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GeoJsonFeature that = (GeoJsonFeature) o;

        if (geometry != null ? !geometry.equals(that.geometry) : that.geometry != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (geometry != null ? geometry.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
