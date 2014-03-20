package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for the model wrapper's UserDetailService.
 * Copyright (c) 2014 University of Oxford
 */
public class UserDetailsServiceTest {
    @Test
    public void testLoadUserByUsernameThrowsForIncorrectUsername() throws Exception {
        // Arrange
        ConfigurationService mockConfigurationService = mock(ConfigurationService.class);
        UserDetailsService target = new UserDetailsServiceImpl(mockConfigurationService);
        when(mockConfigurationService.getAuthenticationUsername()).thenReturn("correctUsername");

        // Act
        catchException(target).loadUserByUsername("incorrectUsername");

        // Assert
        assertThat(caughtException()).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    public void testLoadUserByUsernameReturnsCorrectUser() throws Exception {
        // Arrange
        ConfigurationService mockConfigurationService = mock(ConfigurationService.class);
        UserDetailsService target = new UserDetailsServiceImpl(mockConfigurationService);
        String expectedUsername = "expectedUsername";
        String expectedPasswordHash = "expectedPasswordHash";
        when(mockConfigurationService.getAuthenticationUsername()).thenReturn(expectedUsername);
        when(mockConfigurationService.getAuthenticationPasswordHash()).thenReturn(expectedPasswordHash);

        // Act
        UserDetails result = target.loadUserByUsername(expectedUsername);

        // Assert
        assertThat(result.getUsername()).isEqualTo(expectedUsername);
        assertThat(result.getPassword()).isEqualTo(expectedPasswordHash);
        assertThat(result.getAuthorities()).containsOnly(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
