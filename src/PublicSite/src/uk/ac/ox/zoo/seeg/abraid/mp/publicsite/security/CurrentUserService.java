package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.springframework.security.core.userdetails.UserDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

/**
 * Created by zool1112 on 06/03/14.
 */
public interface CurrentUserService {
    PublicSiteUser getCurrentUser();
}
