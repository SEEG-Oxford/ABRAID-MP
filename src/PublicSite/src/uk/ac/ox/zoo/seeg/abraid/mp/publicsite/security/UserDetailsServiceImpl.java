package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Service to load users from hibernate.
 * Copyright (c) 2014 University of Oxford
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";

    private ExpertService expertService;

    public UserDetailsServiceImpl(ExpertService expertService) {
        this.expertService = expertService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        Expert expert = expertService.getExpertByEmail(s);
        if (expert == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return buildUserFromExpert(expert);
    }

    private User buildUserFromExpert(Expert expert) {

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(ROLE_USER));
        if (expert.isAdministrator()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_ADMINISTRATOR));
        }

        return new User(expert.getEmail(), expert.getPassword(), authorities);
    }
}
