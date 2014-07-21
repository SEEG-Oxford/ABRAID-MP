package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.SessionStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ExpertForRegistrationValidator;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for RegistrationController.
 * Copyright (c) 2014 University of Oxford
 */
public class RegistrationControllerTest {
    private RegistrationController target;
    private CurrentUserService currentUserService;
    private ExpertService expertService;
    private DiseaseService diseaseService;
    private PasswordEncoder passwordEncoder;
    private ExpertForRegistrationValidator validator;

    @Before
    public void setup() {
        currentUserService = mock(CurrentUserService.class);
        when(currentUserService.getCurrentUser()).thenReturn(null);
        expertService = mock(ExpertService.class);
        diseaseService = mock(DiseaseService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        ObjectMapper json = new ObjectMapper();
        validator = mock(ExpertForRegistrationValidator.class);

        target = new RegistrationController(
                currentUserService, expertService, diseaseService, passwordEncoder, json, validator);
    }

    @Test
    public void getAccountPageRedirectsLoggedInUsers() throws Exception {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(mock(PublicSiteUser.class));
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        String result = target.getAccountPage(mock(ModelMap.class), sessionStatus);

        // Assert
        verify(sessionStatus, times(1)).setComplete();
        assertThat(result).isEqualTo("redirect:/");
    }

    @Test
    public void getAccountPageAddsANewExpertToTheModelIfNotPresent() throws Exception {
        // Arrange
        ModelMap modelMap = mock(ModelMap.class);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(modelMap, times(1)).addAttribute(eq("expert"), any(Expert.class));
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageValidatesExpertInModelIfPresent() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setEmail("a@b.com");
        modelMap.addAttribute("expert", expert);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(validator, times(1)).validateBasicFields(expert);
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageValidatesAddsTheNeededModelValuesAndReturnsTheCorrectTemplate() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setEmail("a@b.com");
        modelMap.addAttribute("expert", expert);

        when(validator.createValidationCaptcha()).thenReturn("captcha");
        when(validator.validateBasicFields(any(Expert.class))).thenReturn(Arrays.asList("m1", "m2"));

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        assertThat((String)modelMap.get("alerts")).isEqualTo("[\"m1\",\"m2\"]");
        assertThat((String)modelMap.get("captcha")).isEqualTo("captcha");
        assertThat((String)modelMap.get("jsonExpert")).isEqualTo("{\"email\":\"a@b.com\",\"password\":null,\"password" +
                "Confirmation\":null,\"captchaChallenge\":\"\",\"captchaResponse\":\"\"}");
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getDetailsPageRedirectsLoggedInUsers() throws Exception {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(mock(PublicSiteUser.class));
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        String result = target.getDetailsPage(mock(ModelMap.class), sessionStatus);

        // Assert
        verify(sessionStatus, times(1)).setComplete();
        assertThat(result).isEqualTo("redirect:/");
    }

    @Test
    public void getDetailsPageRedirectToTheAccountPageIfExpertNotPresentInModel() throws Exception {
        // Arrange

        // Act
        String result = target.getDetailsPage(mock(ModelMap.class), mock(SessionStatus.class));

        // Assert
        assertThat(result).isEqualTo("redirect:/register/account");
    }

    @Test
    public void getDetailsPageValidatesExpertInModelAndRedirectToTheAccountPageIfNotValid() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(Arrays.asList("1"));

        // Act
        String result = target.getDetailsPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(validator, times(1)).validateBasicFields(expert);
        assertThat(result).isEqualTo("redirect:/register/account");
    }

    @Test
    public void getDetailsPageAddsTheNeededModelValuesAndReturnsTheCorrectTemplate() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setPubliclyVisible(false); // SET EXPERT VISIBILITY FIELD temp workaround
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());

        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(Arrays.asList(
                new ValidatorDiseaseGroup(1, "a"),
                new ValidatorDiseaseGroup(2, "b"),
                new ValidatorDiseaseGroup(3, "c")
        ));

        // Act
        String result = target.getDetailsPage(modelMap, mock(SessionStatus.class));

        // Assert
        assertThat((String)modelMap.get("diseases"))
                .isEqualTo("[{\"id\":1,\"name\":\"a\"},{\"id\":2,\"name\":\"b\"},{\"id\":3,\"name\":\"c\"}]");
        assertThat((String)modelMap.get("jsonExpert")).isEqualTo("{\"name\":null,\"diseaseInterests\":[]," +
                "\"jobTitle\":null,\"institution\":null,\"publiclyVisible\":false}");

        assertThat(result).isEqualTo("register/details");
    }
}
