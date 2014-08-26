package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.apache.commons.mail.Email;

/**
 * Defines a helper class for creating instances of the Email class. Mainly just used so that EmailService can be tested
 * with mock instances of Email.
 * Copyright (c) 2014 University of Oxford
 */
public interface EmailFactory {
    Email createEmail();
}
