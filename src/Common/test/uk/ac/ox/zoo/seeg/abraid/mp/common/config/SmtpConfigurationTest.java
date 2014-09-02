package uk.ac.ox.zoo.seeg.abraid.mp.common.config;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SmtpConfiguration.
 * Copyright (c) 2014 University of Oxford
 */
public class SmtpConfigurationTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        SmtpConfiguration result = new SmtpConfiguration("address", 1234, true, "username", "password");
        assertThat(result.getAddress()).isEqualTo("address");
        assertThat(result.getPort()).isEqualTo(1234);
        assertThat(result.useSSL()).isEqualTo(true);
        assertThat(result.getUsername()).isEqualTo("username");
        assertThat(result.getPassword()).isEqualTo("password");
    }
}
