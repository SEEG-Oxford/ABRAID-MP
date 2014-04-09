package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a country as defined by HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getHealthMapCountryByName",
                query = "from HealthMapCountry where name=:name"
        )
})
@Entity
@Table(name = "healthmap_country")
@Immutable
public class HealthMapCountry {
    // The country ID as used by HealthMap.
    @Id
    private Long id;

    // The country's name.
    @Column(nullable = false)
    private String name;

    // The corresponding SEEG countries.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "healthmap_country_country",
            joinColumns = { @JoinColumn(name = "healthmap_country_id") },
            inverseJoinColumns = { @JoinColumn(name = "gaul_code") })
    @Fetch(FetchMode.SELECT)
    private Set<Country> countries;

    public HealthMapCountry() {
    }

    public HealthMapCountry(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public HealthMapCountry(Long id, String name, Country country) {
        this.id = id;
        this.name = name;
        this.countries = new HashSet<>();
        this.countries.add(country);
    }

    public Long getId() {
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

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapCountry that = (HealthMapCountry) o;

        if (countries != null ? !countries.equals(that.countries) : that.countries != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (countries != null ? countries.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
