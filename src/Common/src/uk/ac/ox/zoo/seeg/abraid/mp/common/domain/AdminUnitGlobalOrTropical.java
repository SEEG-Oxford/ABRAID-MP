package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * The parent class for AdminUnitGlobal and AdminUnitTropical.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AdminUnitGlobalOrTropical {
    // The admin unit's GAUL code.
    @Id
    @Column(name = "gaul_code")
    private Integer gaulCode;

    // The admin unit's level (1 or 2).
    @Column
    private char level;

    // The admin unit's public name for display.
    @Column(name = "pub_name")
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

    public void setLevel(char level) {
        this.level = level;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
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
        if (!(o instanceof AdminUnitGlobalOrTropical)) return false;

        AdminUnitGlobalOrTropical that = (AdminUnitGlobalOrTropical) o;

        if (level != that.level) return false;
        if (!gaulCode.equals(that.gaulCode)) return false;
        if (geom != null ? !geom.equals(that.geom) : that.geom != null) return false;
        if (!publicName.equals(that.publicName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gaulCode.hashCode();
        result = 31 * result + (int) level;
        result = 31 * result + publicName.hashCode();
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
