package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the CurrentUserServiceImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class CurrentUserServiceTest extends AbstractAuthenticatingTests {
    @Test
    public void getCurrentUserReturnsCorrectAuthorizedUser() throws Exception {
        // Arrange
        int expectation = 123;
        PublicSiteUser user = mock(PublicSiteUser.class);
        when(user.getId()).thenReturn(expectation);
        setupCurrentUser(user);
        CurrentUserService target = new CurrentUserServiceImpl();

        // Act
        Integer result = target.getCurrentUserId();

        // Assert
        assertThat(result).isSameAs(expectation);
    }

    @Test
    public void getCurrentUserReturnsNullForAnonymousUser() throws Exception {
        // Arrange
        CurrentUserService target = new CurrentUserServiceImpl();

        // Act
        Integer result = target.getCurrentUserId();

        // Assert
        assertThat(result).isNull();
    }

}
