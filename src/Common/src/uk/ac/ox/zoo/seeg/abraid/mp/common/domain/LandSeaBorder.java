package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Represents a land-sea border, to a 5km resolution as used by the niche model.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "land_sea_border")
@Immutable
public class LandSeaBorder {
    @Id
    private Integer id;

    @Column
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon geom;

    public LandSeaBorder() {
    }

    public LandSeaBorder(MultiPolygon geom) {
        this.geom = geom;
    }

    public Integer getId() {
        return id;
    }

    public MultiPolygon getGeom() {
        return geom;
    }

    public void setGeom(MultiPolygon geom) {
        this.geom = geom;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LandSeaBorder that = (LandSeaBorder) o;

        if (geom != null ? !geom.equals(that.geom) : that.geom != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
