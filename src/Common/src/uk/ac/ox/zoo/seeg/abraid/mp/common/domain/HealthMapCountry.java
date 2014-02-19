package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents a country as defined by HealthMap.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class HealthMapCountry {
    // The country ID as used by HealthMap.
    @Id
    private Long id;

    // The country's name.
    @Column
    private String name;

    // The corresponding SEEG country.
    @ManyToOne
    @JoinColumn(name = "countryId")
    private Country country;

    public HealthMapCountry() {
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapCountry that = (HealthMapCountry) o;

        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
