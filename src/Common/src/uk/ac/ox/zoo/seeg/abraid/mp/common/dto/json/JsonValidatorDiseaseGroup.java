package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

/**
 * A DTO for uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonValidatorDiseaseGroup {
    private final String name;
    private final Integer id;

    public JsonValidatorDiseaseGroup(ValidatorDiseaseGroup domainObject) {
        this.id = domainObject.getId();
        this.name = domainObject.getName();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
