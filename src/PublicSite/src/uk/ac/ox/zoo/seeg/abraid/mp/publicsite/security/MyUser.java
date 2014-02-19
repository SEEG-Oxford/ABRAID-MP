package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Class to extend Spring User in order to include fullName property on SecurityContext Authentication principal.
 * Copyright (c) 2014 University of Oxford
 */
public class MyUser extends User {

    private final String fullName;

    public MyUser(String username, String fullName, String password,
                  Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
