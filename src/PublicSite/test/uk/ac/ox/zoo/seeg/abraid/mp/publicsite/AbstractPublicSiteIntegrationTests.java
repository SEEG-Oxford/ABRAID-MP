package uk.ac.ox.zoo.seeg.abraid.mp.publicsite;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

/**
 * Base class for integration tests in the PublicSite module.
 *
 * Unfortunately we cannot include @ContextConfiguration here because any beans that are mocked using @ReplaceWithMock
 * need to be in the same class as the @ContextConfiguration.
 *
 * Copyright (c) 2014 University of Oxford
 */
@WebAppConfiguration("file:PublicSite/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AbstractPublicSiteIntegrationTests extends AbstractSpringIntegrationTests {
}
