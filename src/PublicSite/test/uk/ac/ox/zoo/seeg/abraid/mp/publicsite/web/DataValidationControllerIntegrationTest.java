package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.AbstractDiseaseOccurrenceGeoJsonTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the data validation controller.
 * Copyright (c) 2014 University of Oxford
 */
public class DataValidationControllerIntegrationTest {
    @Mock
    private ExpertService expertServiceMock;

    @Mock
    private CurrentUserService currentUserServiceMock;

    @Mock
    private PublicSiteUser loggedInUser;

    @InjectMocks
    private DataValidationController controller;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // Setup jackson
        MappingJackson2HttpMessageConverter jackson = new MappingJackson2HttpMessageConverter();
        jackson.setObjectMapper(new GeoJsonObjectMapper());

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(jackson)
//                .setLocaleResolver(localeContextResolver)
                .build();

        // Setup user
        when(currentUserServiceMock.getCurrentUser()).thenReturn(loggedInUser);
        when(loggedInUser.getId()).thenReturn(1);
    }

    @Test
    public void occurrenceResourceAcceptsValidRequest() throws Exception {
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());

        when(expertServiceMock.getDiseaseOccurrencesYetToBeReviewed(anyInt(), anyInt())).thenReturn(occurrences);

        this.mockMvc.perform(
                get(DataValidationController.GEOWIKI_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(AbstractDiseaseOccurrenceGeoJsonTests.TWO_DISEASE_OCCURRENCE_FEATURES_AS_JSON));
    }

    @Test
    public void occurrenceResourceRejectsInvalidNumericId() throws Exception {
        when(expertServiceMock.getDiseaseOccurrencesYetToBeReviewed(anyInt(), anyInt()))
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

}
