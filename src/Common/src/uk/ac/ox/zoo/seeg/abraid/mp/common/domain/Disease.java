package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer healthMapDiseaseId;

    public Disease() {
    }

    public Disease(String name, Integer healthMapDiseaseId) {
        this.name = name;
        this.healthMapDiseaseId = healthMapDiseaseId;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
