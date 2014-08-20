package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AdminDiseaseGroupController class.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "file:PublicSite/web/WEB-INF/applicationContext.xml" })
public class AdminDiseaseGroupControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    public static final String MODELWRAPPER_URL_PREFIX = "http://username:password@localhost:8080/modelwrapper";

    @ReplaceWithMock
    @Autowired
    private WebServiceClient webServiceClient;

    @Autowired
    private ModelRunService modelRunService;

    @Autowired
    private DiseaseService diseaseService;

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
    public void getModelRunInformation() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"lastModelRunText\":\"never\",\"diseaseOccurrencesText\":\"total 45, occurring between 24 Feb 2014 and 27 Feb 2014\",\"hasModelBeenSuccessfullyRun\":false,\"canRunModel\":true,\"batchEndDateMinimum\":\"24 Feb 2014\",\"batchEndDateMaximum\":\"27 Feb 2014\",\"batchEndDateDefault\":\"27 Feb 2014\"}"));
    }

    @Test
    public void getModelRunInformationRejectsNonGETRequests() throws Exception {
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/modelruninformation"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void requestModelRun() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(diseaseGroupId);
        mockModelWrapperWebServiceCall();
        assertThat(modelRunService.getLastRequestedModelRun(diseaseGroupId)).isNull();

        // Act
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/requestmodelrun";
        DateTime batchEndDate = DateTime.now();
        this.mockMvc.perform(post(url).param("batchEndDate", batchEndDate.toString()))
                .andExpect(status().isOk());

        // Assert
        ModelRun modelRun = modelRunService.getLastRequestedModelRun(diseaseGroupId);
        assertThat(modelRun).isNotNull();
        assertThat(modelRun.getName()).isEqualTo("testname");
        assertThat(modelRun.getStatus()).isEqualTo(ModelRunStatus.IN_PROGRESS);
        assertThat(modelRun.getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(modelRun.getBatchEndDate().getMillis()).isEqualTo(batchEndDate.getMillis());
    }

    @Test
    public void requestModelRunRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/requestmodelrun"))
                .andExpect(status().isMethodNotAllowed());
    }

    private void setDiseaseGroupParametersToEnsureHelperReturnsOccurrences(int diseaseGroupId) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(27);
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(null);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private void mockModelWrapperWebServiceCall() {
        when(webServiceClient.makePostRequestWithJSON(startsWith(MODELWRAPPER_URL_PREFIX), anyString()))
                .thenReturn("{\"modelRunName\":\"testname\"}");
    }

    @Test
    public void enableAutomaticModelRunsRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/automaticmodelruns"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/automaticmodelruns"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/automaticmodelruns"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/automaticmodelruns"))
                .andExpect(status().isMethodNotAllowed());
    }


    @Test
    public void enableAutomaticModelRunsRejectsNonIntegerPathVariables() throws Exception {
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/a/automaticmodelruns"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/0.2/automaticmodelruns"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/null/automaticmodelruns"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void enableAutomaticModelRunsGivesNotFoundForInvalidIntegerPathVariables() throws Exception {
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/-1/automaticmodelruns"))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/999999/automaticmodelruns"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveDiseaseGroupSetsParameters() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        int parentId = 1;
        int validatorId = 1;
        String groupType = "MICROCLUSTER";
        String settings = "{ \"name\": \"Name\", " +
                "\"publicName\": \"Public name\", " +
                "\"shortName\": \"Short name\", " +
                "\"abbreviation\": \"ABBREV\", " +
                "\"groupType\": \"MICROCLUSTER\", " +
                "\"parentDiseaseGroup\": { \"id\": \"" + parentId + "\"}, " +
                "\"validatorDiseaseGroup\": { \"id\": \"" + validatorId + "\"}, " +
                "\"isGlobal\": false, " +
                "\"diseaseExtentParameters\": {" +
                    "\"maxMonthsAgo\": 12, " +
                    "\"maxMonthsAgoForHigherOccurrenceScore\": 6, " +
                    "\"lowerOccurrenceScore\": 3, " +
                    "\"higherOccurrenceScore\": 4 } }";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/save";

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isNoContent());

        // Assert
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DiseaseGroup parentDiseaseGroup = diseaseService.getDiseaseGroupById(parentId);
        ValidatorDiseaseGroup validatorDiseaseGroup = diseaseService.getValidatorDiseaseGroupById(validatorId);
        assertThat(diseaseGroup.getName()).isEqualTo("Name");
        assertThat(diseaseGroup.getPublicName()).isEqualTo("Public name");
        assertThat(diseaseGroup.getShortName()).isEqualTo("Short name");
        assertThat(diseaseGroup.getAbbreviation()).isEqualTo("ABBREV");
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.valueOf(groupType));
        assertThat(diseaseGroup.isGlobal()).isEqualTo(false);
        assertThat(diseaseGroup.getParentGroup()).isEqualTo(parentDiseaseGroup);
        assertThat(diseaseGroup.getValidatorDiseaseGroup()).isEqualTo(validatorDiseaseGroup);
        // Disease group already had a disease extent (parameters) object - assert that values are updated as expected
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroup.getId(), 12, 6, 3, 4);
    }

    @Test
    public void saveDiseaseGroupSetsParametersWithoutParentDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        String groupType = "MICROCLUSTER";
        String settings = "{ \"name\": \"Name\", " +
                "\"publicName\": \"Public name\", " +
                "\"shortName\": \"Short name\", " +
                "\"abbreviation\": \"ABBREV\", " +
                "\"groupType\": \"MICROCLUSTER\", " +
                "\"isGlobal\": false, " +
                "\"diseaseExtentParameters\": {" +
                    "\"maxMonthsAgo\": 12, " +
                    "\"maxMonthsAgoForHigherOccurrenceScore\": 6, " +
                    "\"lowerOccurrenceScore\": 3, " +
                    "\"higherOccurrenceScore\": 4 } }";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/save";

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isNoContent());

        // Assert
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        assertThat(diseaseGroup.getName()).isEqualTo("Name");
        assertThat(diseaseGroup.getPublicName()).isEqualTo("Public name");
        assertThat(diseaseGroup.getShortName()).isEqualTo("Short name");
        assertThat(diseaseGroup.getAbbreviation()).isEqualTo("ABBREV");
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.valueOf(groupType));
        assertThat(diseaseGroup.isGlobal()).isEqualTo(false);
        assertThat(diseaseGroup.getParentGroup()).isEqualTo(null);
        // Disease group already had a disease extent (parameters) object - assert that values are updated as expected
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroupId, 12, 6, 3, 4);
    }

    @Test
    public void saveDiseaseGroupSetsParametersWithNewDiseaseExtent() throws Exception {
        // Arrange
        int diseaseGroupId = 2;
        int parentId = 1;
        int validatorId = 1;
        String groupType = "MICROCLUSTER";
        String settings = "{ \"name\": \"Name\", " +
                "\"publicName\": \"Public name\", " +
                "\"shortName\": \"Short name\", " +
                "\"abbreviation\": \"ABBREV\", " +
                "\"groupType\": \"MICROCLUSTER\", " +
                "\"parentDiseaseGroup\": { \"id\": \"" + parentId + "\"}, " +
                "\"validatorDiseaseGroup\": { \"id\": \"" + validatorId + "\"}, " +
                "\"isGlobal\": false, " +
                "\"diseaseExtentParameters\": {" +
                    "\"maxMonthsAgo\": 12, " +
                    "\"maxMonthsAgoForHigherOccurrenceScore\": 6, " +
                    "\"lowerOccurrenceScore\": 3, " +
                    "\"higherOccurrenceScore\": 4 } }";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/save";

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isNoContent());

        // Assert
        flushAndClear();
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DiseaseGroup parentDiseaseGroup = diseaseService.getDiseaseGroupById(parentId);
        ValidatorDiseaseGroup validatorDiseaseGroup = diseaseService.getValidatorDiseaseGroupById(validatorId);
        assertThat(diseaseGroup.getName()).isEqualTo("Name");
        assertThat(diseaseGroup.getPublicName()).isEqualTo("Public name");
        assertThat(diseaseGroup.getShortName()).isEqualTo("Short name");
        assertThat(diseaseGroup.getAbbreviation()).isEqualTo("ABBREV");
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.valueOf(groupType));
        assertThat(diseaseGroup.isGlobal()).isEqualTo(false);
        assertThat(diseaseGroup.getParentGroup()).isEqualTo(parentDiseaseGroup);
        assertThat(diseaseGroup.getValidatorDiseaseGroup()).isEqualTo(validatorDiseaseGroup);
        // Disease group didn't previously have disease extent (parameters) defined - assert that extent has been added
        // with the same disease group id and values set as expected
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroupId, 12, 6, 3, 4);
    }

    @Test
     public void saveDiseaseGroupReturnsBadRequestForMissingName() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        String settings = "{ \"publicName\": \"Public name\", \"groupType\": \"MICROCLUSTER\"}";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/save";

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveDiseaseGroupReturnsBadRequestForMissingGroupType() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        String settings = "{ \"name\": \"Name\", \"publicName\": \"Public name\"}";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/save";

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/save"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/save"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/save"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/save"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void addRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/add"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/add"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/add"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/add"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void addDiseaseGroupAddsNewDiseaseGroupToDatabase() throws Exception {
        // Arrange
        int parentId = 1;
        int validatorId = 1;
        String settings = "{ \"name\": \"Name\", " +
                "\"publicName\": \"Public name\", " +
                "\"shortName\": \"Short name\", " +
                "\"abbreviation\": \"ABBREV\", " +
                "\"groupType\": \"MICROCLUSTER\", " +
                "\"parentDiseaseGroup\": { \"id\": \"" + parentId + "\"}, " +
                "\"validatorDiseaseGroup\": { \"id\": \"" + validatorId + "\"}, " +
                "\"isGlobal\": false }";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/add";
        int expectedSize = (diseaseService.getAllDiseaseGroups()).size() + 1;

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isNoContent());

        // Assert
        assertThat(diseaseService.getAllDiseaseGroups()).hasSize(expectedSize);
    }

    @Test
    public void addDiseaseGroupAddsNewDiseaseExtentWithSameDiseaseGroupId() throws Exception {
        // Arrange
        int parentId = 1;
        int validatorId = 1;
        String settings = "{ \"name\": \"Name\", " +
                "\"publicName\": \"Public name\", " +
                "\"shortName\": \"Short name\", " +
                "\"abbreviation\": \"ABBREV\", " +
                "\"groupType\": \"MICROCLUSTER\", " +
                "\"parentDiseaseGroup\": { \"id\": \"" + parentId + "\"}, " +
                "\"validatorDiseaseGroup\": { \"id\": \"" + validatorId + "\"}, " +
                "\"isGlobal\": false, " +
                "\"diseaseExtentParameters\": {" +
                    "\"maxMonthsAgo\": 12, " +
                    "\"maxMonthsAgoForHigherOccurrenceScore\": 6, " +
                    "\"lowerOccurrenceScore\": 3, " +
                    "\"higherOccurrenceScore\": 4 } }";
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/add";

        // Act
        this.mockMvc.perform(
                post(url).contentType(MediaType.APPLICATION_JSON).content(settings))
                .andExpect(status().isNoContent());

        // Assert
        DiseaseGroup diseaseGroup = getMostRecentlyAddedDiseaseGroup();
        assertThat(diseaseGroup.getId()).isNotNull();
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroup.getId(), 12, 6, 3, 4);
    }

    private void assertDiseaseExtentParameters(DiseaseGroup diseaseGroup, int diseaseGroupId,
                                               int maxMonthsAgo, int maxMonthsAgoForHigherOccurrenceScore,
                                               int lowerOccurrenceScore, int higherOccurrenceScore) {
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isNotNull();
        assertThat(diseaseGroup.getDiseaseExtentParameters().getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getMaxMonthsAgo()).isEqualTo(maxMonthsAgo);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getMaxMonthsAgoForHigherOccurrenceScore()).isEqualTo(maxMonthsAgoForHigherOccurrenceScore);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getLowerOccurrenceScore()).isEqualTo(lowerOccurrenceScore);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getHigherOccurrenceScore()).isEqualTo(higherOccurrenceScore);
    }

    private DiseaseGroup getMostRecentlyAddedDiseaseGroup() {
        List<DiseaseGroup> sortedList = sort(diseaseService.getAllDiseaseGroups(), on(DiseaseGroup.class).getId());
        return sortedList.get(sortedList.size() - 1);
    }
}
