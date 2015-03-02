package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Represents a country as defined by SEEG.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getCountryByName",
                query = "from Country where name=:name"
        ),
        @NamedQuery(
                name = "getCountriesForMinDataSpreadCalculation",
                query = "select gaulCode from Country where forMinDataSpread = true"
        )
})
@Entity
@Immutable
public class Country {
    // The country's GAUL (Global Administrative Unit Layers) code. This is used in the corresponding SEEG shapefile.
    @Id
    @Column(name = "gaul_code")
    private Integer gaulCode;

    // The country's name.
    @Column(nullable = false)
    private String name;

    // A field indicating whether the country is in Africa and should be considered
    // when calculating the minimum data spread required to run a model.
    @Column(name = "for_min_data_spread")
    private boolean forMinDataSpread;

    // The country's area (square km).
    @Column(nullable = false)
    private double area;

    @Column
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon geom;

    public Country() {
    }

    public Country(Integer gaulCode, String name) {
        this.gaulCode = gaulCode;
        this.name = name;
    }

    public Country(Integer gaulCode, String name, boolean forMinDataSpread) {
        this(gaulCode, name);
        this.forMinDataSpread = forMinDataSpread;
    }

    public Country(Integer gaulCode, String name, MultiPolygon geom) {
        this(gaulCode, name);
        this.geom = geom;
    }

    public Integer getGaulCode() {
        return gaulCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isForMinDataSpread() {
        return forMinDataSpread;
    }

    public void setForMinDataSpread(boolean forMinDataSpread) {
        this.forMinDataSpread = forMinDataSpread;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
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

        Country country = (Country) o;

        if (gaulCode != null ? !gaulCode.equals(country.gaulCode) : country.gaulCode != null) return false;
        if (geom != null ? !geom.equals(country.geom) : country.geom != null) return false;
        if (name != null ? !name.equals(country.name) : country.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gaulCode != null ? gaulCode.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
