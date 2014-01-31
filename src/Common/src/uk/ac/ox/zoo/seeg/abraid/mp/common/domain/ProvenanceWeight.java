package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents the weight (significance) of a provenance.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getProvenanceByName",
                query = "from Provenance where name=:name"
        )
})
@Entity
public class ProvenanceWeight {
    // The ID of the provenance weight.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The name of the provenance weight.
    @Column
    private String name;

    public ProvenanceWeight() {
    }

    public ProvenanceWeight(String name) {
        this.name = name;
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
}
