package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ExpertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

import java.util.ArrayList;
import java.util.Collection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the UserDetailsService class.
 * Copyright (c) 2014 University of Oxford
 */
public class UserDetailsServiceTest {
    @Test
    public void loadUserByUsername() {

        ExpertDao expertDao = mock(ExpertDao.class);
        DiseaseOccurrenceDao diseaseOccurrenceDao = mock(DiseaseOccurrenceDao.class);

        UserDetailsService userDetailsService = new UserDetailsServiceImpl(new ExpertServiceImpl(expertDao, diseaseOccurrenceDao));

        String fullName = "Helena Patching";
        String email = "zool1250@zoo.ox.ac.uk";
        String hashedPassword = "$2a$10$JdJkIeiqwA8Kso3WfvYlT.//vz2M1Tu5iXSy3w0opBtf8cBQEik3y";
        boolean isAdministrator = true;
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));

        UserDetails user = new PublicSiteUser(email, fullName, hashedPassword, authorities);
        Expert expert = new Expert();
        expert.setName(fullName);
        expert.setEmail(email);
        expert.setPassword(hashedPassword);
        expert.setAdministrator(isAdministrator);

        when(expertDao.getByEmail(email)).thenReturn(expert);
        UserDetails testUser = userDetailsService.loadUserByUsername(email);

        assertThat(testUser).isEqualTo(user);
    }
}
