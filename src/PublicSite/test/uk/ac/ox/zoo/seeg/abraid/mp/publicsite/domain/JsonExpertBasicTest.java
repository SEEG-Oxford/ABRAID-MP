package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonExpertBasic.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertBasicTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        Expert mockExpert = mock(Expert.class);
        when(mockExpert.getEmail()).thenReturn("expected email");
        when(mockExpert.getPassword()).thenReturn("expected password");

        // Act
        JsonExpertBasic result = new JsonExpertBasic(mockExpert);

        // Assert
        assertThat(result.getEmail()).isEqualTo(mockExpert.getEmail());
        assertThat(result.getPassword()).isEqualTo(mockExpert.getPassword());
        assertThat(result.getPasswordConfirmation()).isEqualTo(mockExpert.getPassword());
        assertThat(result.getCaptchaChallenge()).isEqualTo("");
        assertThat(result.getCaptchaResponse()).isEqualTo("");
    }

    @Test
    public void gettersAndSettersTrackCorrectly() {
        // Arrange
        String email = "expected email";
        String password = "expected password";
        String passwordConfirmation = "expected password confirm";
        String captchaChallenge = "expected challenge";
        String captchaResponse = "expected response";

        JsonExpertBasic target = new JsonExpertBasic();

        // Act
        target.setEmail(email);
        target.setPassword(password);
        target.setPasswordConfirmation(passwordConfirmation);
        target.setCaptchaChallenge(captchaChallenge);
        target.setCaptchaResponse(captchaResponse);

        // Assert
        assertThat(target.getEmail()).isEqualTo(email);
        assertThat(target.getPassword()).isEqualTo(password);
        assertThat(target.getPasswordConfirmation()).isEqualTo(passwordConfirmation);
        assertThat(target.getCaptchaChallenge()).isEqualTo(captchaChallenge);
        assertThat(target.getCaptchaResponse()).isEqualTo(captchaResponse);
    }
}
