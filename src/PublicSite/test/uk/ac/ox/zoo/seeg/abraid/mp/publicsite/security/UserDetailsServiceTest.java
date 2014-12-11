package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security;

import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the UserDetailsService class.
 * Copyright (c) 2014 University of Oxford
 */
public class UserDetailsServiceTest {
    @Test
    public void loadUserByUsername() {

        AdminUnitReviewDao adminUnitReviewDao = mock(AdminUnitReviewDao.class);
        ExpertDao expertDao = mock(ExpertDao.class);
        DiseaseOccurrenceDao diseaseOccurrenceDao = mock(DiseaseOccurrenceDao.class);
        DiseaseGroupDao diseaseGroupDao = mock(DiseaseGroupDao.class);
        DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao = mock(DiseaseOccurrenceReviewDao.class);

        UserDetailsService userDetailsService = new UserDetailsServiceImpl(new ExpertServiceImpl(adminUnitReviewDao,
                expertDao, diseaseGroupDao, diseaseOccurrenceDao, diseaseOccurrenceReviewDao, null, null));

        String fullName = "Helena Patching";
        String email = "helena.patching@zoo.ox.ac.uk";
        int id = 1;
        String hashedPassword = "$2a$10$JdJkIeiqwA8Kso3WfvYlT.//vz2M1Tu5iXSy3w0opBtf8cBQEik3y";
        boolean isAdministrator = true;
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UserDetails user = new PublicSiteUser(id, email, fullName, hashedPassword, authorities);
        Expert expert = mock(Expert.class);
        when(expert.getName()).thenReturn(fullName);
        when(expert.getEmail()).thenReturn(email);
        when(expert.getPassword()).thenReturn(hashedPassword);
        when(expert.isAdministrator()).thenReturn(isAdministrator);
        when(expert.getId()).thenReturn(id);

        when(expertDao.getByEmail(email)).thenReturn(expert);
        UserDetails testUser = userDetailsService.loadUserByUsername(email);

        assertThat(testUser).isEqualTo(user);
    }
}
