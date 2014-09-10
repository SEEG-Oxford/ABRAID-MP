package uk.ac.ox.zoo.seeg.abraid.mp.datamanager;

import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

/**
 * Base class for integration tests in the Data Manager module.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/datamanager/config/beans.xml")
public abstract class AbstractDataManagerSpringIntegrationTests extends AbstractSpringIntegrationTests {
}
