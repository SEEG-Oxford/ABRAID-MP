package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

/**
 * An interface to define a service to obtain the currently logged in user.
 * Copyright (c) 2014 University of Oxford
 */
public interface CurrentUserService {
    /**
     * Obtains the currently logged in user.
     * @return The currently logged in user or null if user is not logged in
     */
    PublicSiteUser getCurrentUser();
}
