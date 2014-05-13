package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;

/**
 * Contains integration tests for the ModelRunRequester class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterIntegrationTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private ModelRunRequester modelRunRequester;

    @Test
    public void requestModelRun() {
        // TODO - expand
        modelRunRequester.requestModelRun();
    }
}
