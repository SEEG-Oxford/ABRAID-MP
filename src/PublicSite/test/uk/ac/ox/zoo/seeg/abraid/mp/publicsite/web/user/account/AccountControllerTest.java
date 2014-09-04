package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.user.account;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonExpertDetails;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator.ValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests for AccountController.
 * Copyright (c) 2014 University of Oxford
 */
public class AccountControllerTest {
    private static AccountController createTarget(int userId, ExpertService expertServiceIn,
                                                  DiseaseService diseaseService,
                                                  AccountControllerValidator adminControllerValidator,
                                                  AccountControllerHelper accountControllerTransactionHelper) {
        CurrentUserService userService = mock(CurrentUserService.class);
        PublicSiteUser user = mock(PublicSiteUser.class);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(userId);

        ExpertService expertService = expertServiceIn;
        if (expertService == null) {
            expertService = mock(ExpertService.class);
            when(expertService.getExpertById(userId)).thenReturn(mock(Expert.class));
        }

        return new AccountController(
                userService,
                expertService,
                diseaseService == null ? mock(DiseaseService.class) : diseaseService,
                new GeoJsonObjectMapper(),
                adminControllerValidator == null ? mock(AccountControllerValidator.class) : adminControllerValidator,
                accountControllerTransactionHelper == null ? mock(AccountControllerHelper.class) : accountControllerTransactionHelper);
    }

    @Test
    public void getAccountEditPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        AccountController target = createTarget(1, null, null, null, null);

        // Act
        String result = target.getAccountEditPage(mock(ModelMap.class));

        // Assert
        assertThat(result).isEqualTo("account/edit");
    }

    @Test
    public void getAccountEditPageAssignsCorrectModelData() throws Exception {
        // Arrange
        ExpertService expertService = mock(ExpertService.class);
        Expert expert = mock(Expert.class);
        when(expertService.getExpertById(anyInt())).thenReturn(expert);
        when(expert.getName()).thenReturn("name");
        when(expert.getJobTitle()).thenReturn("job");
        when(expert.getInstitution()).thenReturn("institute");
        when(expert.getVisibilityRequested()).thenReturn(true);
        when(expert.getValidatorDiseaseGroups()).thenReturn(createMockValidatorDiseaseGroups());

        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(createMockValidatorDiseaseGroups());

        AccountController target = createTarget(1, expertService, diseaseService, null, null);

        // Act
        ModelMap model = mock(ModelMap.class);
        String result = target.getAccountEditPage(model);

        // Assert
        verify(model, times(1)).addAttribute("diseases", "[{\"id\":1,\"name\":\"1\"},{\"id\":2,\"name\":\"2\"},{\"id\":3,\"name\":\"3\"}]");
        verify(model, times(1)).addAttribute("jsonExpert", "{\"name\":\"name\",\"visibilityRequested\":true,\"diseaseInterests\":[1,2,3],\"jobTitle\":\"job\",\"institution\":\"institute\"}");
    }

    @Test
    public void submitAccountEditPageReturnsBadRequestIfValidationFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountController target = createTarget(1, null, null, validator, null);
        when(validator.validate(any(JsonExpertDetails.class))).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<Collection<String>> result = target.submitAccountEditPage(mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL1", "FAIL2");
    }


    @Test
    public void submitAccountEditPageReturnsBadRequestIfSaveFails() throws Exception {
        // Arrange
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        AccountController target = createTarget(1, null, null, validator, helper);
        when(validator.validate(any(JsonExpertDetails.class))).thenReturn(new ArrayList<String>());
        doThrow(new ValidationException(Arrays.asList("FAIL3")))
                .when(helper).processExpertProfileUpdateAsTransaction(anyInt(), any(JsonExpertDetails.class));

        // Act
        ResponseEntity<Collection<String>> result = target.submitAccountEditPage(mock(JsonExpertDetails.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).containsOnly("FAIL3");
    }

    @Test
    public void submitAccountEditPageSavesExpertAndReturnsNoContentForValidRequest() throws Exception {
        AccountControllerValidator validator = mock(AccountControllerValidator.class);
        AccountControllerHelper helper = mock(AccountControllerHelper.class);
        int userId = 99;
        AccountController target = createTarget(userId, null, null, validator, helper);
        when(validator.validate(any(JsonExpertDetails.class))).thenReturn(new ArrayList<String>());

        // Act
        JsonExpertDetails expert = mock(JsonExpertDetails.class);
        ResponseEntity<Collection<String>> result = target.submitAccountEditPage(expert);

        // Assert
        verify(helper, times(1)).processExpertProfileUpdateAsTransaction(userId, expert);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private List<ValidatorDiseaseGroup> createMockValidatorDiseaseGroups() {
        ArrayList<ValidatorDiseaseGroup> groups = new ArrayList<>();
        groups.add(createMockValidatorDiseaseGroup(1, "1"));
        groups.add(createMockValidatorDiseaseGroup(2, "2"));
        groups.add(createMockValidatorDiseaseGroup(3, "3"));
        return groups;
    }

    private ValidatorDiseaseGroup createMockValidatorDiseaseGroup(int id, String name) {
        ValidatorDiseaseGroup mock = new ValidatorDiseaseGroup(id, name);
        return mock;
    }
}
