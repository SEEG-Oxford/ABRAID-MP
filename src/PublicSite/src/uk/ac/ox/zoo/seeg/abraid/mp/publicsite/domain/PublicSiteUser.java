package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Class to extend Spring User in order to include fullName property on SecurityContext Authentication principal.
 * Copyright (c) 2014 University of Oxford
 */
public class PublicSiteUser extends User {

    private final String fullName;

    public PublicSiteUser(String username, String fullName, String password,
                          Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PublicSiteUser publicSiteUser = (PublicSiteUser) o;

        if (fullName != null ? !fullName.equals(publicSiteUser.fullName) : publicSiteUser.fullName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
