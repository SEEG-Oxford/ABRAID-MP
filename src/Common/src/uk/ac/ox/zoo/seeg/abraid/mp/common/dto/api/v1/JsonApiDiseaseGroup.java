package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * Represents a disease in the v1 JSON API.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiDiseaseGroup {
    private int id;
    private String name;

    public JsonApiDiseaseGroup(DiseaseGroup diseaseGroup) {
        this.id = diseaseGroup.getId();
        this.name = diseaseGroup.getPublicName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
