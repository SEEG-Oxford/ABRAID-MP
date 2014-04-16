package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the data validation controller.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "classpath:uk/ac/ox/zoo/seeg/abraid/mp/publicsite/web/mockServices.xml" })
@WebAppConfiguration("file:PublicSite/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DataValidationControllerIntegrationTest {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ExpertService expertService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // Setup user
        PublicSiteUser loggedInUser = mock(PublicSiteUser.class);
        when(loggedInUser.getId()).thenReturn(1);
        AbstractAuthenticatingTests.setupCurrentUser(loggedInUser);
    }

    @Test
    public void occurrenceResourceAcceptsValidRequest() throws Exception {
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());

        when(expertService.getDiseaseOccurrencesYetToBeReviewed(anyInt(), anyInt())).thenReturn(occurrences);

        this.mockMvc.perform(
                get(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(AbstractDiseaseOccurrenceGeoJsonTests.getTwoDiseaseOccurrenceFeaturesAsJson(DisplayJsonView.class)));
    }

    @Test
    public void occurrenceResourceRejectsInvalidNumericId() throws Exception {
        when(expertService.getDiseaseOccurrencesYetToBeReviewed(anyInt(), anyInt()))
                .thenThrow(new IllegalArgumentException());

        this.mockMvc.perform(
                get(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void occurrenceResourceRejectsInvalidNonNumericId() throws Exception {
        this.mockMvc.perform(
                get(DataValidationController.GEOWIKI_BASE_URL + "/diseases/id/occurrences"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void occurrenceResourceOnlyAcceptsGET() throws Exception {
        this.mockMvc.perform(
                get(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isOk());

        this.mockMvc.perform(
                post(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void submitReviewAcceptsValidRequest() throws Exception {

        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(true);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        this.mockMvc.perform(
                post(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void submitReviewOnlyAcceptsPOST() throws Exception {
        when(diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(anyInt(), anyInt()))
                .thenReturn(true);
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        this.mockMvc.perform(
                post(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(
                get(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void submitReviewRejectsInvalidReviewParameterString() throws Exception {
        this.mockMvc.perform(
                post(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "InvalidReviewParameter"))
                .andExpect(status().isBadRequest());
    }
}
