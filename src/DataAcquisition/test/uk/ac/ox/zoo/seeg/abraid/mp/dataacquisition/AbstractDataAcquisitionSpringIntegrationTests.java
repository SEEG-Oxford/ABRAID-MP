package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

/**
 * Base class for integration tests in the Data Acquisition module.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(locations = {
        "classpath:uk/ac/ox/zoo/seeg/abraid/mp/testutils/test-context.xml",
        "classpath:uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/config/beans.xml"
})
public abstract class AbstractDataAcquisitionSpringIntegrationTests extends AbstractSpringIntegrationTests {
}
