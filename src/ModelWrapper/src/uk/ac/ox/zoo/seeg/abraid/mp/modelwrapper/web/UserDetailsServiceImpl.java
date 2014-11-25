package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;

import java.util.ArrayList;

/**
 * Service to load Spring UserDetails from a properties file.
 * Copyright (c) 2014 University of Oxford
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String ROLE_USER = "ROLE_USER";

    private ConfigurationService configurationService;

    @Autowired
    public UserDetailsServiceImpl(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public UserDetails loadUserByUsername(String providedUsername) throws UsernameNotFoundException {
        String expectedUsername = configurationService.getAuthenticationUsername();
        String expectedPasswordHash = configurationService.getAuthenticationPasswordHash();
        if (!expectedUsername.equals(providedUsername)) {
            throw new UsernameNotFoundException("No such user.");
        }

        ArrayList<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(ROLE_USER));

        return new User(expectedUsername, expectedPasswordHash, roles);
    }
}
