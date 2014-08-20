package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a country as defined by HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "healthmap_country")
@Immutable
public class HealthMapCountry {
    // The country ID as used by HealthMap.
    @Id
    private Integer id;

    // The country's name.
    @Column(nullable = false)
    private String name;

    // If the country's centroid is not on land, this point should be used instead.
    @Column(name = "centroid_override")
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point centroidOverride;

    // The corresponding SEEG countries.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "healthmap_country_country",
            joinColumns = { @JoinColumn(name = "healthmap_country_id") },
            inverseJoinColumns = { @JoinColumn(name = "gaul_code") })
    @Fetch(FetchMode.SELECT)
    private Set<Country> countries;

    public HealthMapCountry() {
    }

    public HealthMapCountry(Integer id, String name, Country... countries) {
        this.id = id;
        this.name = name;
        this.countries = new HashSet<>();
        if (countries != null) {
            Collections.addAll(this.countries, countries);
        }
    }

    public HealthMapCountry(Integer id, String name, double x, double y, Country... countries) {
        this(id, name, countries);
        this.centroidOverride = GeometryUtils.createPoint(x, y);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    public Point getCentroidOverride() {
        return centroidOverride;
    }

    public void setCentroidOverride(Point centroidOverride) {
        this.centroidOverride = centroidOverride;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapCountry that = (HealthMapCountry) o;

        if (centroidOverride != null ? !centroidOverride.equals(that.centroidOverride) : that.centroidOverride != null)
            return false;
        if (countries != null ? !countries.equals(that.countries) : that.countries != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (centroidOverride != null ? centroidOverride.hashCode() : 0);
        result = 31 * result + (countries != null ? countries.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
