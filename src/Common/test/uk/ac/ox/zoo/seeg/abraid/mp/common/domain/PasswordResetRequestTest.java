package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the PasswordResetRequest class.
 * Copyright (c) 2014 University of Oxford
 */
public class PasswordResetRequestTest {

    @Test
    public void createPasswordResetRequestKeyCreatesCorrectlySizedKeys() throws Exception {
        // Act
        String key = PasswordResetRequest.createPasswordResetRequestKey();

        // Assert
        assertThat(key).hasSize(24);
    }

    @Test
    public void createPasswordResetRequestKeyCreatesKeysWithOnlyBase64Chars() throws Exception {
        // Act
        String key = PasswordResetRequest.createPasswordResetRequestKey();

        // Assert
        assertThat(key).matches("^[+/=0-9A-Za-z]*$");
    }

    @Test
    public void createPasswordResetRequestKeyCreatesUniqueKeys() throws Exception {
        // Arrange
        String[] keys = new String[100];

        // Act
        for (int i = 0; i < keys.length; i++) {
            keys[i] = PasswordResetRequest.createPasswordResetRequestKey();
        }

        // Assert
        Arrays.sort(keys);
        for (int i = 0; i < keys.length - 1; i++) {
            assertThat(keys[i]).isNotEqualTo(keys[i + 1]);
        }
    }
}
