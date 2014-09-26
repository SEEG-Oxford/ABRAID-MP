package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroupType;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
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
    @Autowired
    private DiseaseService diseaseService;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private ModelRunWorkflowService modelRunWorkflowService;

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
                .andExpect(content().string("{\"lastModelRunText\":\"never\",\"diseaseOccurrencesText\":\"total 45, occurring between 24 Feb 2014 and 27 Feb 2014\",\"hasModelBeenSuccessfullyRun\":false,\"canRunModel\":true,\"batchEndDateMinimum\":\"24 Feb 2014\",\"batchEndDateMaximum\":\"27 Feb 2014\",\"batchEndDateDefault\":\"27 Feb 2014\",\"hasGoldStandardOccurrences\":false}"));
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
    public void generateDiseaseExtentRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(
                get(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/generatediseaseextent"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                delete(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/generatediseaseextent"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                put(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/generatediseaseextent"))
                .andExpect(status().isMethodNotAllowed());

        this.mockMvc.perform(
                patch(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/87/generatediseaseextent"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void generateDiseaseExtentRejectsNonIntegerPathVariables() throws Exception {
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/a/generatediseaseextent"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/0.2/generatediseaseextent"))
                .andExpect(status().isBadRequest());

        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/null/generatediseaseextent"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void generateDiseaseExtentGivesNotFoundForInvalidIntegerPathVariables() throws Exception {
        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/-1/generatediseaseextent"))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(
                post(AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/999999/generatediseaseextent"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void requestModelRun() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchEndDate = DateTime.now();
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/requestmodelrun";

        // Act
        MockHttpServletRequestBuilder requestBuilder = post(url)
                .param("batchEndDate", batchEndDate.toString())
                .param("useGoldStandardOccurrences", "false");
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk());

        // Assert
        verify(modelRunWorkflowService).prepareForAndRequestManuallyTriggeredModelRun(eq(diseaseGroupId),
                argThat(new DateTimeMatcher(batchEndDate)));
    }

    @Test
    public void requestModelRunUsingGoldStandardOccurrences() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DateTime batchEndDate = DateTime.now();
        String url = AdminDiseaseGroupController.ADMIN_DISEASE_GROUP_BASE_URL + "/" + diseaseGroupId + "/requestmodelrun";

        // Act
        MockHttpServletRequestBuilder requestBuilder = post(url)
                .param("batchEndDate", batchEndDate.toString())
                .param("useGoldStandardOccurrences", "true");
        this.mockMvc.perform(requestBuilder).andExpect(status().isOk());

        // Assert
        verify(modelRunWorkflowService).prepareForAndRequestModelRunUsingGoldStandardOccurrences(eq(diseaseGroupId));
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
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroup.getId(), 6, 3, 4);
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
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroupId, 6, 3, 4);
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
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroupId, 6, 3, 4);
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
        assertDiseaseExtentParameters(diseaseGroup, diseaseGroup.getId(), 6, 3, 4);
    }

    private void assertDiseaseExtentParameters(DiseaseGroup diseaseGroup, int diseaseGroupId,
                                               int maxMonthsAgoForHigherOccurrenceScore,
                                               int lowerOccurrenceScore, int higherOccurrenceScore) {
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isNotNull();
        assertThat(diseaseGroup.getDiseaseExtentParameters().getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getMaxMonthsAgoForHigherOccurrenceScore()).isEqualTo(maxMonthsAgoForHigherOccurrenceScore);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getLowerOccurrenceScore()).isEqualTo(lowerOccurrenceScore);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getHigherOccurrenceScore()).isEqualTo(higherOccurrenceScore);
    }

    private DiseaseGroup getMostRecentlyAddedDiseaseGroup() {
        List<DiseaseGroup> sortedList = sort(diseaseService.getAllDiseaseGroups(), on(DiseaseGroup.class).getId());
        return sortedList.get(sortedList.size() - 1);
    }

    /**
     * Compares DateTimes for equality by milliseconds (thus avoiding time zone issues).
     */
    static class DateTimeMatcher extends ArgumentMatcher<DateTime> {
        private final DateTime expected;

        public DateTimeMatcher(DateTime expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(Object actual) {
            DateTime actualDateTime = (DateTime) actual;
            return expected.getMillis() == actualDateTime.getMillis();
        }
    }
}
