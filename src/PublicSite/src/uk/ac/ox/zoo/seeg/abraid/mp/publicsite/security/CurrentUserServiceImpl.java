package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

/**
 * A service to obtain the currently logged in user.
 * Copyright (c) 2014 University of Oxford
 */
public class CurrentUserServiceImpl implements CurrentUserService {
    /**
     * Obtains the id of the currently logged in user.
     * @return The the id of the currently logged in user or null if user is not logged in
     */
    @Override
    public Integer getCurrentUserId() {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (user instanceof PublicSiteUser) ? ((PublicSiteUser) user).getId() : null;
    }
}
