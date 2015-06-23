package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;

import java.io.IOException;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public interface CovariateService {
    JsonCovariateConfiguration getCovariateConfiguration();

    void setCovariateConfiguration(JsonCovariateConfiguration config) throws IOException;

    String getCovariateDirectory();
}
