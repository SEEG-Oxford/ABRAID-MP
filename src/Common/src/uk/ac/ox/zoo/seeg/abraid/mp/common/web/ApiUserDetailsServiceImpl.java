package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/**
 * A Spring UserDetailsService implementation for use with private single client APIs.
 * Expected usage is with HTTP Basic Auth over SSL.
 * E.g. PublicSite's interactions with ModelWrapper & ModelWrapper's interactions with ModelOutputHandler
 * Copyright (c) 2014 University of Oxford
 */
public class ApiUserDetailsServiceImpl implements UserDetailsService {
    private static final String ROLE_API = "ROLE_API";
    private static final String API_USERNAME = "api";
    private final String apiKeyHash;

    public ApiUserDetailsServiceImpl(String apiKeyHash) {
        this.apiKeyHash = apiKeyHash;
    }

    @Override
    public UserDetails loadUserByUsername(String providedUsername) throws UsernameNotFoundException {
        if (!API_USERNAME.equals(providedUsername)) {
            throw new UsernameNotFoundException("Incorrect username specified");
        }

        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(ROLE_API));

        return new User(API_USERNAME, apiKeyHash, roles);
    }
}
