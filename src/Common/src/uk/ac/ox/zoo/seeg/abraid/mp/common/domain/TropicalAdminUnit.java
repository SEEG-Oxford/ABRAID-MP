package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents an admin unit from the tropical shapefile.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "admin_unit_tropical")
@Immutable
public class TropicalAdminUnit {
    // The admin unit's GAUL code.
    @Id
    @Column(name = "gaul_code")
    private Integer gaulCode;

    @Column
    // The admin unit's level (1 or 2).
    private char level;

    @Column
    // The admin unit's name.
    private String name;

    @Column(name = "pub_name")
    // The admin unit's public name for display.
    private String publicName;

    @Column(nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon geom;

    public Integer getGaulCode() {
        return gaulCode;
    }

    public char getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getPublicName() {
        return publicName;
    }

    public MultiPolygon getGeom() {
        return geom;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TropicalAdminUnit)) return false;

        TropicalAdminUnit that = (TropicalAdminUnit) o;

        if (level != that.level) return false;
        if (!gaulCode.equals(that.gaulCode)) return false;
        if (geom != null ? !geom.equals(that.geom) : that.geom != null) return false;
        if (!name.equals(that.name)) return false;
        if (!publicName.equals(that.publicName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gaulCode.hashCode();
        result = 31 * result + (int) level;
        result = 31 * result + name.hashCode();
        result = 31 * result + publicName.hashCode();
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
