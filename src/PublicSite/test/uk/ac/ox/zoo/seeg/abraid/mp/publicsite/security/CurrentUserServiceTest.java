package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the CurrentUserServiceImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class CurrentUserServiceTest extends AbstractAuthenticatingTests {
    @Test
    public void getCurrentUserReturnsCorrectAuthorizedUser() throws Exception {
        // Arrange
        PublicSiteUser expectation = mock(PublicSiteUser.class);
        setupCurrentUser(expectation);
        CurrentUserService target = new CurrentUserServiceImpl();

        // Act
        PublicSiteUser result = target.getCurrentUser();

        // Assert
        assertThat(result).isSameAs(expectation);
    }

    @Test
    public void getCurrentUserReturnsNullForAnonymousUser() throws Exception {
        // Arrange
        CurrentUserService target = new CurrentUserServiceImpl();

        // Act
        PublicSiteUser result = target.getCurrentUser();

        // Assert
        assertThat(result).isNull();
    }

}
