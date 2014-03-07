package uk.ac.ox.zoo.seeg.abraid.mp.publicsite;

import org.junit.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by zool1112 on 07/03/14.
 */
public abstract class AbstractAuthenticatingTests {
    @Before
    public void setupSecurityContext() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
        setupAnonymousUser();
    }

    protected static void setupCurrentUser(PublicSiteUser user) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected static void setupAnonymousUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("anonymousUser"); // This is the documented behavior of spring
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
