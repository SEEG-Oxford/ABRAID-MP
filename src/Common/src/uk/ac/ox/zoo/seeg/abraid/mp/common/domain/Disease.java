package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents a disease.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getDiseaseByName",
                query = "from Disease where name=:name"
        )
})
@Entity
public class Disease {
    // The disease ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease name.
    @Column
    private String name;

    // The unique ID for the disease assigned by HealthMap.
    @Column
    private Integer healthMapDiseaseId;

    public Disease() {
    }

    public Disease(String name) {
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

    public Integer getHealthMapDiseaseId() {
        return healthMapDiseaseId;
    }

    public void setHealthMapDiseaseId(Integer healthMapDiseaseId) {
        this.healthMapDiseaseId = healthMapDiseaseId;
    }
}
