package uk.ac.ox.zoo.seeg.abraid.mp.common;

import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

/**
 * Base class for integration tests in the Common module.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
public abstract class AbstractCommonSpringIntegrationTests extends AbstractSpringIntegrationTests {
}
