package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents a country.
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
public class Country {
    // The country's ID. This is the three-letter ISO 3166 code.
    @Id
    private String id;

    // The country's name. Currently this is as specified in the MAP database.
    @Column
    private String name;

    public Country() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
