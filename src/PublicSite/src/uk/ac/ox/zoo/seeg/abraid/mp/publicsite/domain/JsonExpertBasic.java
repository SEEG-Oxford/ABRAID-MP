package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

/**
 * A DTO for the parts of uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert provided on the account registration page,
 * along with addition transited validation fields.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertBasic {
    private String email;
    private String password;
    private String passwordConfirmation;
    private String captchaChallenge;
    private String captchaResponse;

    public JsonExpertBasic() {
    }

    public JsonExpertBasic(Expert expert) {
        setEmail(expert.getEmail());
        setPassword(expert.getPassword());
        setPassword(expert.getPassword());
        setCaptchaChallenge("");
        setCaptchaResponse("");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public String getCaptchaChallenge() {
        return captchaChallenge;
    }

    public void setCaptchaChallenge(String captchaChallenge) {
        this.captchaChallenge = captchaChallenge;
    }

    public String getCaptchaResponse() {
        return captchaResponse;
    }

    public void setCaptchaResponse(String captchaResponse) {
        this.captchaResponse = captchaResponse;
    }
}
