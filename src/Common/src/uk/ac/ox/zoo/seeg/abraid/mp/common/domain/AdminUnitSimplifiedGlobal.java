package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents an admin 0/1 area. Tailored for ABRAID-MP by separating non-contiguous parts of countries, absorbing tiny
 * countries, removing smaller smaller islands etc. Ten large countries have been divided into admin 1 areas, for use
 * with global diseases. Borders have been simplified to improve rendering performance.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "admin_unit_simplified_global")
@Immutable
public class AdminUnitSimplifiedGlobal {
    // The admin unit's GAUL (Global Administrative Unit Layers) code. This is used in the corresponding SEEG shapefile.
    @Id
    @Column(name = "gaul_code")
    private Integer gaulCode;

    // The admin unit's name.
    @Column
    private String name;

    // The admin unit's display name.
    @Column(name = "display_name")
    private String displayName;

    // The admin unit's geometry.
    @Column
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon geom;

    public Integer getGaulCode() {
        return gaulCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public MultiPolygon getGeom() {
        return geom;
    }

    public void setGeom(MultiPolygon geom) {
        this.geom = geom;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminUnitSimplifiedGlobal that = (AdminUnitSimplifiedGlobal) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) return false;
        if (gaulCode != null ? !gaulCode.equals(that.gaulCode) : that.gaulCode != null) return false;
        if (geom != null ? !geom.equals(that.geom) : that.geom != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gaulCode != null ? gaulCode.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
