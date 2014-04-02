package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents a GeoName.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class GeoName {
    // The GeoNames ID.
    @Id
    private Integer id;

    // The GeoNames feature code for this ID.
    @Column(name = "feature_code", nullable = false)
    private String featureCode;

    public GeoName() {
    }

    public GeoName(Integer id, String featureCode) {
        this.id = id;
        this.featureCode = featureCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String geoNamesFeatureCode) {
        this.featureCode = geoNamesFeatureCode;
    }

    //CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoName geoName = (GeoName) o;

        if (featureCode != null ? !featureCode.equals(geoName.featureCode) : geoName.featureCode != null)
            return false;
        if (id != null ? !id.equals(geoName.id) : geoName.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (featureCode != null ? featureCode.hashCode() : 0);
        return result;
    }
    //CHECKSTYLE:ON
}
