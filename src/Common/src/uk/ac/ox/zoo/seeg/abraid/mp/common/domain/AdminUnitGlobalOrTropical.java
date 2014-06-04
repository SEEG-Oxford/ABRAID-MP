package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * The parent class for AdminUnitGlobal and AdminUnitTropical.
 * Copyright (c) 2014 University of Oxford
 */
@MappedSuperclass
public abstract class AdminUnitGlobalOrTropical {
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

    // The admin unit's geometry, with simplified borders for efficient display.
    @Column(name = "simplified_geom")
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon simplifiedGeom;

    // The country that contains this admin unit (if any).
    @Column(name = "country_gaul_code")
    private Integer countryGaulCode;

    protected AdminUnitGlobalOrTropical() {
    }

    protected AdminUnitGlobalOrTropical(Integer gaulCode) {
        this.gaulCode = gaulCode;
    }

    protected AdminUnitGlobalOrTropical(Integer gaulCode, Integer countryGaulCode) {
        this.gaulCode = gaulCode;
        this.countryGaulCode = countryGaulCode;
    }

    public Integer getGaulCode() {
        return gaulCode;
    }

    public void setGaulCode(Integer gaulCode) {
        this.gaulCode = gaulCode;
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

    public MultiPolygon getSimplifiedGeom() {
        return simplifiedGeom;
    }

    public void setSimplifiedGeom(MultiPolygon simplifiedGeom) {
        this.simplifiedGeom = simplifiedGeom;
    }

    public Integer getCountryGaulCode() {
        return countryGaulCode;
    }

    public void setCountryGaulCode(Integer countryGaulCode) {
        this.countryGaulCode = countryGaulCode;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminUnitGlobalOrTropical that = (AdminUnitGlobalOrTropical) o;

        if (level != that.level) return false;
        if (countryGaulCode != null ? !countryGaulCode.equals(that.countryGaulCode) : that.countryGaulCode != null)
            return false;
        if (gaulCode != null ? !gaulCode.equals(that.gaulCode) : that.gaulCode != null) return false;
        if (publicName != null ? !publicName.equals(that.publicName) : that.publicName != null) return false;
        if (simplifiedGeom != null ? !simplifiedGeom.equals(that.simplifiedGeom) : that.simplifiedGeom != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gaulCode != null ? gaulCode.hashCode() : 0;
        result = 31 * result + (int) level;
        result = 31 * result + (publicName != null ? publicName.hashCode() : 0);
        result = 31 * result + (simplifiedGeom != null ? simplifiedGeom.hashCode() : 0);
        result = 31 * result + (countryGaulCode != null ? countryGaulCode.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
