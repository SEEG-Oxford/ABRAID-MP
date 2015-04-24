package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the data validation controller.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "file:PublicSite/web/WEB-INF/applicationContext.xml" })
public class DataValidationControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    @ReplaceWithMock
    @Autowired
    private DiseaseService diseaseService;

    @ReplaceWithMock
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
        Expert expert = mock(Expert.class);
        ValidatorDiseaseGroup validatorDiseaseGroup = mock(ValidatorDiseaseGroup.class);
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(expert.isSeegMember()).thenReturn(true);
        when(expertService.getExpertById(1)).thenReturn(expert);
        when(occurrence.getValidatorDiseaseGroup()).thenReturn(validatorDiseaseGroup);
        when(diseaseService.getValidatorDiseaseGroupById(anyInt())).thenReturn(validatorDiseaseGroup);
        when(diseaseService.getDiseaseOccurrenceById(anyInt())).thenReturn(occurrence);
        AbstractAuthenticatingTests.setupCurrentUser(loggedInUser);
    }

    @Test
    public void occurrenceResourceAcceptsValidRequest() throws Exception {
        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());
        occurrences.add(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence());

        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(eq(1), eq(true), anyInt())).thenReturn(occurrences);

        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().string(AbstractDiseaseOccurrenceGeoJsonTests.getTwoDiseaseOccurrenceFeaturesAsJson(DisplayJsonView.class)));
    }

    @Test
    public void occurrenceResourceRejectsInvalidNumericId() throws Exception {
        when(expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(anyInt(), eq(true), anyInt()))
                .thenThrow(new IllegalArgumentException());

        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void occurrenceResourceRejectsInvalidNonNumericId() throws Exception {
        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/id/occurrences"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void occurrenceResourceOnlyAcceptsGET() throws Exception {
        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isOk());

        this.mockMvc.perform(
                post(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void extentResourceAcceptsValidRequest() throws Exception {
        AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass = new AdminUnitDiseaseExtentClass(
                createAdminUnitGlobal(), new DiseaseGroup(), new DiseaseExtentClass(DiseaseExtentClass.PRESENCE), 0);
        List<AdminUnitDiseaseExtentClass> map = Arrays.asList(adminUnitDiseaseExtentClass);
        Expert expert = mock(Expert.class);
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(anyInt())).thenReturn(map);
        when(diseaseService.getDiseaseGroupById(2)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);
        when(expertService.getExpertById(1)).thenReturn(expert);
        when(expert.isSeegMember()).thenReturn(true);

        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/2/adminunits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    public void extentResourceRejectsInvalidNonNumericId() throws Exception {
        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/id/adminunits"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void extentResourceOnlyAcceptsGET() throws Exception {
        Expert expert = mock(Expert.class);
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseGroup.isAutomaticModelRunsEnabled()).thenReturn(true);
        when(expertService.getExpertById(1)).thenReturn(expert);
        when(expert.isSeegMember()).thenReturn(true);

        String url = DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/adminunits";

        this.mockMvc.perform(
                get(url)).andExpect(status().isOk());

        this.mockMvc.perform(
                post(url)).andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(url)).andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(url)).andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(url)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void defaultExtentResourceOnlyAcceptsGET() throws Exception {

        String url = DataValidationController.DATA_VALIDATION_BASE_URL + "/defaultadminunits";

        this.mockMvc.perform(
                get(url))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        this.mockMvc.perform(
                post(url)).andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(url)).andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(url)).andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(url)).andExpect(status().isMethodNotAllowed());
    }

    private AdminUnitGlobal createAdminUnitGlobal() {
        AdminUnitGlobal adminUnitGlobal = new AdminUnitGlobal();
        adminUnitGlobal.setGaulCode(1);
        adminUnitGlobal.setPublicName("AUG");
        adminUnitGlobal.setLevel('1');
        adminUnitGlobal.setSimplifiedGeom(createMultiPolygon());
        return adminUnitGlobal;
    }

    private MultiPolygon createMultiPolygon() {
        Polygon polygon = GeometryUtils.createPolygon(1, 1, 2, 2, 3, 3, 1, 1);
        return GeometryUtils.createMultiPolygon(polygon);
    }

    @Test
    public void submitReviewAcceptsValidRequest() throws Exception {
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        this.mockMvc.perform(
                post(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void submitReviewOnlyAcceptsPOST() throws Exception {
        when(expertService.doesDiseaseOccurrenceReviewExist(anyInt(), anyInt())).thenReturn(false);

        this.mockMvc.perform(
                post(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(
                get(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "YES"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void submitReviewRejectsInvalidReviewParameterString() throws Exception {
        this.mockMvc.perform(
                post(DataValidationController.DATA_VALIDATION_BASE_URL + "/diseases/1/occurrences/1/validate")
                .param("review", "InvalidReviewParameter"))
                .andExpect(status().isBadRequest());
    }
}
