package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.registration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.SessionStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertBasic;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import javax.servlet.ServletRequest;
import java.util.*;

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
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private RegistrationControllerValidator validator;
    private UserDetailsService userDetailService;

    private static JsonExpertBasic mockJsonExpertBasic() {
        JsonExpertBasic result = mock(JsonExpertBasic.class);
        when(result.getEmail()).thenReturn("a@b.com");
        when(result.getPassword()).thenReturn("qwe123Q");
        when(result.getPasswordConfirmation()).thenReturn("qwe123Q");
        when(result.getCaptchaChallenge()).thenReturn("challenge");
        when(result.getCaptchaResponse()).thenReturn("response");
        return result;
    }

    @Before
    public void setup() {
        currentUserService = mock(CurrentUserService.class);
        when(currentUserService.getCurrentUser()).thenReturn(null);
        expertService = mock(ExpertService.class);
        diseaseService = mock(DiseaseService.class);
        emailService = mock(EmailService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        AbraidJsonObjectMapper json = new AbraidJsonObjectMapper();
        validator = mock(RegistrationControllerValidator.class);
        userDetailService = mock(UserDetailsService.class);

        SecurityContextHolder.getContext().setAuthentication(null);

        target = new RegistrationController(
                currentUserService, expertService, diseaseService, emailService, passwordEncoder, json, validator, userDetailService);
    }

    @Test
    public void getAccountPageRedirectsLoggedInUsers() throws Exception {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(mock(PublicSiteUser.class));
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        String result = target.getAccountPage(mock(ModelMap.class), sessionStatus);

        // Assert
        verify(sessionStatus).setComplete();
        assertThat(result).isEqualTo("redirect:/");
    }

    @Test
    public void getAccountPageAddsANewExpertToTheModelIfNotPresent() throws Exception {
        // Arrange
        ModelMap modelMap = mock(ModelMap.class);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(modelMap).addAttribute(eq("expert"), any(Expert.class));
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageValidatesExpertInModelIfPresentAndHasEmail() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setEmail("a@b.com");
        modelMap.addAttribute("expert", expert);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(validator).validateBasicFields(expert);
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageValidatesExpertInModelIfPresentAndHasPassword() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setPassword("qwe123Q");
        modelMap.addAttribute("expert", expert);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(validator).validateBasicFields(expert);
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageValidatesExpertInModelIfPresentAndHasEmailAndPassword() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setEmail("a@b.com");
        expert.setPassword("qwe123Q");
        modelMap.addAttribute("expert", expert);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(validator).validateBasicFields(expert);
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageValidatesExpertInModelIfPresentUnlessEmailAndPasswordAreStillNull() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);

        // Act
        String result = target.getAccountPage(modelMap, mock(SessionStatus.class));

        // Assert
        verify(validator, never()).validateBasicFields(expert);
        assertThat(result).isEqualTo("register/account");
    }

    @Test
    public void getAccountPageAddsTheNeededModelValuesAndReturnsTheCorrectTemplate() throws Exception {
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
        assertThat((String) (modelMap.get("alerts"))).isEqualTo("[\"m1\",\"m2\"]");
        assertThat((String) (modelMap.get("captcha"))).isEqualTo("captcha");
        assertThat((String) (modelMap.get("jsonExpert"))).isEqualTo("{\"email\":\"a@b.com\",\"captchaChallenge\":\"\",\"captchaResponse\":\"\"}");
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
        verify(sessionStatus).setComplete();
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
        verify(validator).validateBasicFields(expert);
        assertThat(result).isEqualTo("redirect:/register/account");
    }

    @Test
    public void getDetailsPageAddsTheNeededModelValuesAndReturnsTheCorrectTemplate() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
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
        assertThat((String) (modelMap.get("diseases")))
                .isEqualTo("[{\"id\":1,\"name\":\"a\"},{\"id\":2,\"name\":\"b\"},{\"id\":3,\"name\":\"c\"}]");
        assertThat((String) (modelMap.get("jsonExpert"))).isEqualTo("{\"visibilityRequested\":false," +
                "\"diseaseInterests\":[]}");

        assertThat(result).isEqualTo("register/details");
    }

    @Test
    public void submitAccountPageRejectsLoggedInUsers() throws Exception {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(mock(PublicSiteUser.class));
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        ResponseEntity<List<String>> result = target.submitAccountPage(
                mock(ModelMap.class), sessionStatus, mock(JsonExpertBasic.class), mock(ServletRequest.class));

        // Assert
        verify(sessionStatus).setComplete();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).containsOnly("Logged in users cannot create new accounts");
    }

    @Test
    public void submitAccountPageRejectsInvalidSessions() throws Exception {
        // Arrange
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        ResponseEntity<List<String>> result = target.submitAccountPage(
                mock(ModelMap.class), sessionStatus, mock(JsonExpertBasic.class), mock(ServletRequest.class));

        // Assert
        verify(sessionStatus).setComplete();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).containsOnly("Invalid registration session");
    }

    @Test
    public void submitAccountPageRejectsWithoutUpdatingExpertIfBasicFieldNotValid() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(Arrays.asList("1", "2"));
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());

        // Act
        ResponseEntity<List<String>> result = target.submitAccountPage(
                modelMap, mock(SessionStatus.class), mockJsonExpertBasic(), mock(ServletRequest.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("1", "2");
        assertThat(expert.getEmail()).isNull(); // Not updated
        assertThat(expert.getPassword()).isNull(); // Not updated
    }

    @Test
    public void submitAccountPageRejectsWithoutUpdatingExpertIfTransientFieldNotValid() throws Exception {
        // Arrange
        ServletRequest request = mock(ServletRequest.class);
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);
        JsonExpertBasic expertBasic = mockJsonExpertBasic();

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(expertBasic, request))
                .thenReturn(Arrays.asList("1", "2"));

        // Act
        ResponseEntity<List<String>> result = target.submitAccountPage(
                modelMap, mock(SessionStatus.class), expertBasic, request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("1", "2");
        assertThat(expert.getEmail()).isNull(); // Not updated
        assertThat(expert.getPassword()).isNull(); // Not updated
    }

    @Test
    public void submitAccountPageReturnsNoContentStatusForSuccess() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());

        // Act
        ResponseEntity<List<String>> result = target.submitAccountPage(
                modelMap, mock(SessionStatus.class), mockJsonExpertBasic(), mock(ServletRequest.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
    }

    @Test
    public void submitDetailsPageRejectsLoggedInUsers() throws Exception {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(mock(PublicSiteUser.class));
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                mock(ModelMap.class), sessionStatus, mock(JsonExpertDetails.class));

        // Assert
        verify(sessionStatus).setComplete();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).containsOnly("Logged in users cannot create new accounts");
    }

    @Test
    public void submitDetailsPageRejectsInvalidSessions() throws Exception {
        // Arrange
        SessionStatus sessionStatus = mock(SessionStatus.class);

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                mock(ModelMap.class), sessionStatus, mock(JsonExpertDetails.class));

        // Assert
        verify(sessionStatus).setComplete();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).containsOnly("Invalid registration session");
    }

    @Test
    public void submitDetailsPageRejectsAsConflictIfBasicFieldNotValid() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(Arrays.asList("1", "2"));

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getBody()).containsOnly("1", "2");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void submitDetailsPageRejectsIfDetailsFieldNotValid() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateDetailsFields(any(Expert.class))).thenReturn(Arrays.asList("1", "2"));

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("1", "2");
    }

    @Test
    public void submitDetailsPageExtractsUpdatesSavesExpertAndReturnsCreatedStatusForSuccess()
            throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setEmail("a@b.com");
        expert.setPassword("qwe123Q");

        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());
        when(passwordEncoder.encode("qwe123Q")).thenReturn("hash");
        when(userDetailService.loadUserByUsername("a@b.com")).thenReturn(mock(UserDetails.class));

        JsonExpertDetails jsonExpert = new JsonExpertDetails();
        jsonExpert.setJobTitle("job");
        jsonExpert.setInstitution("institution");
        jsonExpert.setVisibilityRequested(true);
        jsonExpert.setName("name");

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), jsonExpert);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Updates expert before save
        assertThat(expert.getJobTitle()).isEqualTo(jsonExpert.getJobTitle());
        assertThat(expert.getInstitution()).isEqualTo(jsonExpert.getInstitution());
        assertThat(expert.getVisibilityRequested()).isEqualTo(jsonExpert.getVisibilityRequested());
        assertThat(expert.getName()).isEqualTo(jsonExpert.getName());

        // Hashes password before save
        InOrder inOrder = inOrder(passwordEncoder, expertService);
        inOrder.verify(passwordEncoder).encode("qwe123Q");
        inOrder.verify(expertService).saveExpert(expert);
        assertThat(expert.getPassword()).isEqualTo("hash");
    }

    @Test
    public void submitDetailsPageHashesPasswordSavesExpertAndReturnsCreatedStatusForSuccess() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setPassword("qwe123Q");
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());
        when(passwordEncoder.encode("qwe123Q")).thenReturn("hash");
        when(userDetailService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        InOrder inOrder = inOrder(passwordEncoder, expertService);
        inOrder.verify(passwordEncoder).encode("qwe123Q");
        inOrder.verify(expertService).saveExpert(expert);
        assertThat(expert.getPassword()).isEqualTo("hash");
    }

    @Test
    public void submitDetailsPageLogsUserInForSuccess() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setPassword("qwe123Q");
        modelMap.addAttribute("expert", expert);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());
        when(passwordEncoder.encode("qwe123Q")).thenReturn("hash");
        UserDetails expectedUser = mock(UserDetails.class);
        when(userDetailService.loadUserByUsername(anyString())).thenReturn(expectedUser);
        Collection<GrantedAuthority> expectedAuthorities = new ArrayList<>();
        expectedAuthorities.add(new SimpleGrantedAuthority("a"));
        expectedAuthorities.add(new SimpleGrantedAuthority("b"));
        doReturn(expectedAuthorities).when(expectedUser).getAuthorities();

        // Act
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(expectedUser);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).isEqualTo(expectedAuthorities);
    }

    @Test
    public void submitDetailsPageSendsEmailForNewUserRequiringVisibilityApproval() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setPassword("qwe123Q");
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());
        when(userDetailService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));

        // Act
        JsonExpertDetails jsonExpertDetails = mock(JsonExpertDetails.class);
        when(jsonExpertDetails.getVisibilityRequested()).thenReturn(true);
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), jsonExpertDetails);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map<String, Object> data = new HashMap<>();
        data.put("expert", expert);
        verify(emailService).sendEmailInBackground(
                "New user requiring visibility sign off",
                "registration/newUserEmail.ftl",
                data);
    }

    @Test
    public void submitDetailsPageSkipsEmailForNewUserNotRequiringVisibilityApproval() throws Exception {
        // Arrange
        ModelMap modelMap = new ModelMap();
        Expert expert = new Expert();
        expert.setPassword("qwe123Q");
        modelMap.addAttribute("expert", expert);

        when(validator.validateBasicFields(any(Expert.class))).thenReturn(new ArrayList<String>());
        when(validator.validateTransientFields(any(JsonExpertBasic.class), any(ServletRequest.class)))
                .thenReturn(new ArrayList<String>());
        when(userDetailService.loadUserByUsername(anyString())).thenReturn(mock(UserDetails.class));

        // Act
        JsonExpertDetails jsonExpertDetails = mock(JsonExpertDetails.class);
        when(jsonExpertDetails.getVisibilityRequested()).thenReturn(false);
        ResponseEntity<List<String>> result = target.submitDetailsPage(
                modelMap, mock(SessionStatus.class), jsonExpertDetails);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(emailService, never()).sendEmailInBackground(anyString(), anyString(), anyMapOf(String.class, Object.class));
    }
}
