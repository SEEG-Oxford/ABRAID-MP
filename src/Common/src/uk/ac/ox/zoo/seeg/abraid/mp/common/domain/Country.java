package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Immutable;

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
    @Column
    private String name;

    public Country() {
    }

    public Country(Integer gaulCode, String name) {
        this.gaulCode = gaulCode;
        this.name = name;
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

    @Override
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (gaulCode != null ? !gaulCode.equals(country.gaulCode) : country.gaulCode != null) return false;
        if (name != null ? !name.equals(country.name) : country.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gaulCode != null ? gaulCode.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
