package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

/**
 * Helper class for creating instances of the Email class. Mainly just used so that EmailService can be tested with mock
 * instances of Email.
 * Copyright (c) 2014 University of Oxford
 */
public class EmailFactoryImpl implements EmailFactory {
    @Override
    public Email createEmail() {
        return new SimpleEmail();
    }
}
