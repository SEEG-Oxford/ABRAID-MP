package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents a provenance, i.e. the source of a disease outbreak alert.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class Provenance {
    // The provenance ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The name of the provenance.
    @Column
    private String name;

    // The weight (significance) that is assigned to this provenance.
    @ManyToOne
    @JoinColumn(name="provenanceweightId")
    private ProvenanceWeight weight;

    // The feed ID used for this provenance in HealthMap.
    @Column
    private Integer healthMapFeedId;

    public Provenance() {
    }

    public Provenance(String name) {
        this.name = name;
    }

    public Provenance(String name, ProvenanceWeight weight, Integer healthMapFeedId) {
        this.name = name;
        this.weight = weight;
        this.healthMapFeedId = healthMapFeedId;
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

    public ProvenanceWeight getWeight() {
        return weight;
    }

    public void setWeight(ProvenanceWeight weight) {
        this.weight = weight;
    }

    public Integer getHealthMapFeedId() {
        return healthMapFeedId;
    }

    public void setHealthMapFeedId(Integer healthMapFeedId) {
        this.healthMapFeedId = healthMapFeedId;
    }
}
