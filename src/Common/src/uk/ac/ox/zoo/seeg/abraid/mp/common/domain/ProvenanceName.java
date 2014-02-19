package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * An enumeration of the provenance names.
 *
 * Copyright (c) 2014 University of Oxford
 */
public enum ProvenanceName {
    /**
     * The HealthMap provenance.
     */
    HEALTHMAP("HealthMap");

    private String name;

    private ProvenanceName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
