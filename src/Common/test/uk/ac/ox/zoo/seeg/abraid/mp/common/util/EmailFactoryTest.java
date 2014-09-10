package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for EmailFactory.
 * Copyright (c) 2014 University of Oxford
 */
public class EmailFactoryTest {
    @Test
    public void createEmailReturnsInstanceOfSimpleEmail() {
        // Arrange
        EmailFactory target = new EmailFactoryImpl();
        // Act
        Email result = target.createEmail();
        // Assert
        assertThat(result).isInstanceOf(SimpleEmail.class);
    }

}
