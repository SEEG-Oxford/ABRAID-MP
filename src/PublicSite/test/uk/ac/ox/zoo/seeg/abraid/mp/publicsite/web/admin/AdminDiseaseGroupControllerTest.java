package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.ModelRunWorkflowService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.BatchDatesValidator;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.SourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.GeneralTestUtils.captorForClass;

/**
 * Tests the AdminDiseaseGroupController class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminDiseaseGroupControllerTest {
    private DiseaseService diseaseService;
    private AbraidJsonObjectMapper objectMapper;
    private ModelRunWorkflowService modelRunWorkflowService;
    private ModelRunService modelRunService;
    private AdminDiseaseGroupController controller;
    private DiseaseOccurrenceSpreadHelper helper;
    private BatchDatesValidator batchDatesValidator;
    private SourceCodeManager sourceCodeManager;

    @Before
    public void setUp() {
        diseaseService = mock(DiseaseService.class);
        objectMapper = new AbraidJsonObjectMapper();
        modelRunWorkflowService = mock(ModelRunWorkflowService.class);
        modelRunService = mock(ModelRunService.class);
        helper = mock(DiseaseOccurrenceSpreadHelper.class);
        batchDatesValidator = mock(BatchDatesValidator.class);
        sourceCodeManager = mock(SourceCodeManager.class);
        controller = new AdminDiseaseGroupController(diseaseService, objectMapper, modelRunWorkflowService,
                modelRunService, helper, batchDatesValidator, sourceCodeManager);
    }

    @Test
    public void showPageAddsDiseaseGroupsAndValidatorDiseaseGroupsToModel() throws IOException {
        // Arrange
        // NB. Expected JSONs sort disease groups by name, ignoring case.
        Model model = mock(Model.class);

        DiseaseGroup diseaseGroup1 = createDiseaseGroup(188, 5, "Leishmaniases", "leishmaniases", DiseaseGroupType.MICROCLUSTER,
                "leishmaniases", "leishmaniases", "leish", true, 9, 0.5, 7);
        DiseaseGroup diseaseGroup2 = createDiseaseGroup(87, null, "dengue", null, DiseaseGroupType.SINGLE,
                "dengue", "dengue", "deng", false, 4, 1, 6);
        List<DiseaseGroup> diseaseGroups = Arrays.asList(diseaseGroup1, diseaseGroup2);
        when(diseaseService.getAllDiseaseGroups()).thenReturn(diseaseGroups);
        when(sourceCodeManager.getSupportedModesForCurrentVersion()).thenReturn(new HashSet<String>(Arrays.asList("mode1", "mode2", "mode3")));
        String expectedJson = "[" +
                "{\"id\":87,\"name\":\"dengue\",\"publicName\":\"dengue\",\"shortName\":\"dengue\",\"abbreviation\":\"deng\",\"groupType\":\"SINGLE\",\"maxDaysBetweenModelRuns\":6,\"isGlobal\":false,\"filterBiasDataByAgentType\":false,\"validatorDiseaseGroup\":{\"id\":4},\"weighting\":1.0,\"automaticModelRuns\":false,\"minDataVolume\":0,\"useMachineLearning\":true}," +
                "{\"id\":188,\"name\":\"Leishmaniases\",\"publicName\":\"leishmaniases\",\"shortName\":\"leishmaniases\",\"abbreviation\":\"leish\",\"groupType\":\"MICROCLUSTER\",\"maxDaysBetweenModelRuns\":7,\"isGlobal\":true,\"filterBiasDataByAgentType\":false,\"parentDiseaseGroup\":{\"id\":5,\"name\":\"leishmaniases\"},\"validatorDiseaseGroup\":{\"id\":9},\"weighting\":0.5,\"automaticModelRuns\":false,\"minDataVolume\":0,\"useMachineLearning\":true}]";

        ValidatorDiseaseGroup validator1 = new ValidatorDiseaseGroup(3, "Japanese encephalitis");
        ValidatorDiseaseGroup validator2 = new ValidatorDiseaseGroup(2, "cholera");
        when(diseaseService.getAllValidatorDiseaseGroups()).thenReturn(Arrays.asList(validator1, validator2));
        String expectedValidatorJson = "[{\"id\":2,\"name\":\"cholera\"},{\"id\":3,\"name\":\"Japanese encephalitis\"}]";
        String expectedModesJson = "[\"mode3\",\"mode1\",\"mode2\"]";

        // Act
        String result = controller.showPage(model);

        // Assert
        assertThat(result).isEqualTo("admin/diseasegroups/index");
        verify(model).addAttribute("diseaseGroups", expectedJson);
        verify(model).addAttribute("validatorDiseaseGroups", expectedValidatorJson);
        verify(model).addAttribute("supportedModes", expectedModesJson);
    }

    @Test
    public void getModelRunInformationWithBespokeBias() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun lastRequestedModelRun = new ModelRun("dengue 1", createMockDiseaseGroup(87), "host", new DateTime("2014-07-02T14:15:16"), DateTime.now(), DateTime.now());
        ModelRun lastCompletedModelRun = new ModelRun();
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(0, 0, null, null);
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);

        when(modelRunService.getLastRequestedModelRun(diseaseGroupId)).thenReturn(lastRequestedModelRun);
        when(modelRunService.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId))
                .thenReturn(lastCompletedModelRun);
        when(diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId)).thenReturn(statistics);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        when(diseaseService.modelModeRequiresBiasDataForDisease(diseaseGroup)).thenReturn(true);
        when(diseaseService.getCountOfUnfilteredBespokeBiasOccurrences(diseaseGroup)).thenReturn(1L);
        when(diseaseService.getEstimateCountOfFilteredBespokeBiasOccurrences(diseaseGroup)).thenReturn(2L);
        when(diseaseService.getEstimateCountOfFilteredDefaultBiasOccurrences(diseaseGroup)).thenReturn(3L);


        // Act
        ResponseEntity<JsonModelRunInformation> entity = controller.getModelRunInformation(diseaseGroupId);

        // Assert
        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getBody().getLastModelRunText()).isEqualTo("requested on 2 Jul 2014 14:15:16");
        assertThat(entity.getBody().getDiseaseOccurrencesText()).isEqualTo("none");
        assertThat(entity.getBody().isHasModelBeenSuccessfullyRun()).isTrue();
        assertThat(entity.getBody().isCanRunModel()).isFalse();
        assertThat(entity.getBody().getCannotRunModelReason()).isEqualTo("the public name is missing");
        assertThat(entity.getBody().getSampleBiasText()).isEqualTo("1 bespoke background data points have been provided, approximately 2 of which are suitable.");
    }

    @Test
    public void getModelRunInformationWithoutBespokeBias() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun lastRequestedModelRun = new ModelRun("dengue 1", createMockDiseaseGroup(87), "host", new DateTime("2014-07-02T14:15:16"), DateTime.now(), DateTime.now());
        ModelRun lastCompletedModelRun = new ModelRun();
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(0, 0, null, null);
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);

        when(modelRunService.getLastRequestedModelRun(diseaseGroupId)).thenReturn(lastRequestedModelRun);
        when(modelRunService.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId))
                .thenReturn(lastCompletedModelRun);
        when(diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId)).thenReturn(statistics);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        when(diseaseService.modelModeRequiresBiasDataForDisease(diseaseGroup)).thenReturn(true);
        when(diseaseService.getCountOfUnfilteredBespokeBiasOccurrences(diseaseGroup)).thenReturn(0L);
        when(diseaseService.getEstimateCountOfFilteredBespokeBiasOccurrences(diseaseGroup)).thenReturn(2L);
        when(diseaseService.getEstimateCountOfFilteredDefaultBiasOccurrences(diseaseGroup)).thenReturn(3L);

        // Act
        ResponseEntity<JsonModelRunInformation> entity = controller.getModelRunInformation(diseaseGroupId);

        // Assert
        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getBody().getLastModelRunText()).isEqualTo("requested on 2 Jul 2014 14:15:16");
        assertThat(entity.getBody().getDiseaseOccurrencesText()).isEqualTo("none");
        assertThat(entity.getBody().isHasModelBeenSuccessfullyRun()).isTrue();
        assertThat(entity.getBody().isCanRunModel()).isFalse();
        assertThat(entity.getBody().getCannotRunModelReason()).isEqualTo("the public name is missing");
        assertThat(entity.getBody().getSampleBiasText()).isEqualTo("0 bespoke background data points have been provided, approximately 3 ABRAID occurrences are suitable.");
    }

    @Test
    public void getModelRunInformationNotUsingBias() {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun lastRequestedModelRun = new ModelRun("dengue 1", createMockDiseaseGroup(87), "host", new DateTime("2014-07-02T14:15:16"), DateTime.now(), DateTime.now());
        ModelRun lastCompletedModelRun = new ModelRun();
        DiseaseOccurrenceStatistics statistics = new DiseaseOccurrenceStatistics(0, 0, null, null);
        DiseaseGroup diseaseGroup = new DiseaseGroup(87);


        when(modelRunService.getLastRequestedModelRun(diseaseGroupId)).thenReturn(lastRequestedModelRun);
        when(modelRunService.getMostRecentlyFinishedModelRunWhichCompleted(diseaseGroupId))
                .thenReturn(lastCompletedModelRun);
        when(diseaseService.getDiseaseOccurrenceStatistics(diseaseGroupId)).thenReturn(statistics);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        when(diseaseService.modelModeRequiresBiasDataForDisease(diseaseGroup)).thenReturn(false);
        when(diseaseService.getCountOfUnfilteredBespokeBiasOccurrences(diseaseGroup)).thenReturn(1L);
        when(diseaseService.getEstimateCountOfFilteredBespokeBiasOccurrences(diseaseGroup)).thenReturn(2L);
        when(diseaseService.getEstimateCountOfFilteredDefaultBiasOccurrences(diseaseGroup)).thenReturn(3L);


        // Act
        ResponseEntity<JsonModelRunInformation> entity = controller.getModelRunInformation(diseaseGroupId);

        // Assert
        assertThat(entity.getStatusCode().value()).isEqualTo(200);
        assertThat(entity.getBody().getLastModelRunText()).isEqualTo("requested on 2 Jul 2014 14:15:16");
        assertThat(entity.getBody().getDiseaseOccurrencesText()).isEqualTo("none");
        assertThat(entity.getBody().isHasModelBeenSuccessfullyRun()).isTrue();
        assertThat(entity.getBody().isCanRunModel()).isFalse();
        assertThat(entity.getBody().getCannotRunModelReason()).isEqualTo("the public name is missing");
        assertThat(entity.getBody().getSampleBiasText()).isEqualTo("The current model mode for this disease group does not use background data.");
    }

    @Test
    public void generateDiseaseExtentCallsWorkflowServiceForValidDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        ResponseEntity response = controller.generateDiseaseExtent(diseaseGroupId, false);

        // Assert
        verify(modelRunWorkflowService).generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.MANUAL);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void generateDiseaseExtentForGoldStandardOccurrencesCallsWorkflowServiceForValidDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        ResponseEntity response = controller.generateDiseaseExtent(diseaseGroupId, true);

        // Assert
        verify(modelRunWorkflowService).generateDiseaseExtent(diseaseGroupId, DiseaseProcessType.MANUAL_GOLD_STANDARD);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void generateDiseaseExtentCallsWorkflowServiceReturnsNotFoundForInvalidDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = -1;

        // Act
        ResponseEntity response = controller.generateDiseaseExtent(diseaseGroupId, false);

        // Assert
        verify(modelRunWorkflowService, never()).generateDiseaseExtent(anyInt(), any(DiseaseProcessType.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void generateDiseaseExtentForGoldStandardOccurrencesCallsWorkflowServiceReturnsNotFoundForInvalidDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = -1;

        // Act
        ResponseEntity response = controller.generateDiseaseExtent(diseaseGroupId, false);

        // Assert
        verify(modelRunWorkflowService, never()).generateDiseaseExtent(anyInt(), any(DiseaseProcessType.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void requestModelRun() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        // Set the time zone to UTC, to allow the dates to be compared for equality after converting to/from string
        DateTime batchStartDate = DateTime.now().withZone(DateTimeZone.UTC);
        DateTime batchEndDate = DateTime.now().withZone(DateTimeZone.UTC).plusDays(1);

        // Act
        controller.requestModelRun(diseaseGroupId, batchStartDate.toString(), batchEndDate.toString(), false);

        // Assert
        verify(modelRunWorkflowService).prepareForAndRequestModelRun(
                eq(diseaseGroupId),
                eq(DiseaseProcessType.MANUAL),
                eq(batchStartDate.withTimeAtStartOfDay()),
                eq(batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1)));
    }

    @Test
    public void requestModelRunReturnsNotFoundForInvalidDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = -1;
        // Set the time zone to UTC, to allow the dates to be compared for equality after converting to/from string
        DateTime batchStartDate = DateTime.now().withZone(DateTimeZone.UTC);
        DateTime batchEndDate = DateTime.now().withZone(DateTimeZone.UTC).plusDays(1);

        // Act
        ResponseEntity response = controller.requestModelRun(diseaseGroupId, batchStartDate.toString(), batchEndDate.toString(), false);

        // Assert
        verify(modelRunWorkflowService, never()).prepareForAndRequestModelRun(anyInt(), any(DiseaseProcessType.class), any(DateTime.class), any(DateTime.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void requestModelRunReturnsBadRequestForInvalidBatchingParameters() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        // Set the time zone to UTC, to allow the dates to be compared for equality after converting to/from string
        DateTime batchStartDate = DateTime.now().withZone(DateTimeZone.UTC);
        DateTime batchEndDate = DateTime.now().withZone(DateTimeZone.UTC).plusDays(1);
        ResponseEntity response;

        // Act/Assert
        response = controller.requestModelRun(diseaseGroupId, null, batchEndDate.toString(), false);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid combination of inputs");

        response = controller.requestModelRun(diseaseGroupId, batchStartDate.toString(), null, false);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid combination of inputs");

        response = controller.requestModelRun(diseaseGroupId, null, null, false);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid combination of inputs");

        response = controller.requestModelRun(diseaseGroupId, null, batchEndDate.toString(), true);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid combination of inputs");

        response = controller.requestModelRun(diseaseGroupId, batchStartDate.toString(), null, true);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid combination of inputs");

        verify(modelRunWorkflowService, never()).prepareForAndRequestModelRun(anyInt(), any(DiseaseProcessType.class), any(DateTime.class), any(DateTime.class));
    }

    @Test
    public void requestModelRunReturnsBadRequestForRejectedBatchingParameters() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        // Set the time zone to UTC, to allow the dates to be compared for equality after converting to/from string
        DateTime batchStartDate = DateTime.now().withZone(DateTimeZone.UTC);
        DateTime batchEndDate = DateTime.now().withZone(DateTimeZone.UTC).plusDays(1);

        doThrow(new ModelRunWorkflowException("msg")).when(batchDatesValidator).validate(
                eq(diseaseGroup),
                eq(batchStartDate.withTimeAtStartOfDay()),
                eq(batchEndDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1)));


        // Act/Assert
        ResponseEntity<String> response = controller.requestModelRun(diseaseGroupId, batchStartDate.toString(), batchEndDate.toString(), false);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("msg");


        verify(modelRunWorkflowService, never()).prepareForAndRequestModelRun(anyInt(), any(DiseaseProcessType.class), any(DateTime.class), any(DateTime.class));
    }

    @Test
    public void requestModelRunForGoldStandardOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        controller.requestModelRun(diseaseGroupId, null, null, true);

        // Assert
        verify(modelRunWorkflowService).prepareForAndRequestModelRun(
                diseaseGroupId, DiseaseProcessType.MANUAL_GOLD_STANDARD, null, null);
    }

    @Test
    public void requestModelRunForGoldStandardOccurrencesReturnsNotFoundForInvalidDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = -1;

        // Act
        ResponseEntity response = controller.requestModelRun(diseaseGroupId, null, null, true);

        // Assert
        verify(modelRunWorkflowService, never()).prepareForAndRequestModelRun(anyInt(), any(DiseaseProcessType.class), any(DateTime.class), any(DateTime.class));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void enableAutomaticModelRunsCallsWorkflowServiceForValidDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);

        // Act
        ResponseEntity response = controller.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService).enableAutomaticModelRuns(eq(diseaseGroupId));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void enableAutomaticModelRunsCallsWorkflowServiceReturnsNotFoundForInvalidDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = -1;

        // Act
        ResponseEntity response = controller.enableAutomaticModelRuns(diseaseGroupId);

        // Assert
        verify(modelRunWorkflowService, never()).enableAutomaticModelRuns(anyInt());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void saveCallsSaveForDiseaseGroupAndReturnsNoContent() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = createDiseaseGroup(diseaseGroupId, 87, "Name", "Parent Name", DiseaseGroupType.SINGLE, "Public name", "Short name", "ABBREV", true, 4, 1.0, 7);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(diseaseGroup);
        when(diseaseService.getValidatorDiseaseGroupById(4)).thenReturn(new ValidatorDiseaseGroup());
        JsonDiseaseGroup newValues = createJsonDiseaseGroup("New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4, "not_Bhatt2013");

        // Act
        ResponseEntity result = controller.save(1, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(diseaseService).saveDiseaseGroup(diseaseGroup);
    }

    @Test
    public void saveReturnsBadRequestForInvalidDiseaseGroup() throws Exception {
        // Arrange
        when(diseaseService.getDiseaseGroupById(anyInt())).thenReturn(null);
        JsonDiseaseGroup newValues = createJsonDiseaseGroup("New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4, "Bhatt2013");

        // Act
        ResponseEntity result = controller.save(1, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveReturnsBadRequestForMissingName() throws Exception {
        // Arrange
        when(diseaseService.getDiseaseGroupById(anyInt())).thenReturn(new DiseaseGroup());
        JsonDiseaseGroup newValues = createJsonDiseaseGroup(null, "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, 4, "Bhatt2013");

        // Act
        ResponseEntity result = controller.save(1, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveReturnsBadRequestForMissingGroupType() throws Exception {
        // Arrange
        when(diseaseService.getDiseaseGroupById(anyInt())).thenReturn(new DiseaseGroup());
        JsonDiseaseGroup newValues = createJsonDiseaseGroup("New name", "New public name", "New short name", "NEWABBREV", null, false, 87, 4, "Bhatt2013");

        // Act
        ResponseEntity result = controller.save(1, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveReturnsBadRequestForInvalidParentDiseaseGroup() throws Exception {
        // Arrange
        int diseaseGroupId = 1;
        int parentId = 87;
        DiseaseGroup diseaseGroup = createDiseaseGroup(diseaseGroupId, parentId, "Name", "Parent name", DiseaseGroupType.SINGLE, "Public name", "Short name", "ABBREV", true, 4, 1.0, 7);
        when(diseaseService.getDiseaseGroupById(1)).thenReturn(diseaseGroup);
        when(diseaseService.getDiseaseGroupById(parentId)).thenReturn(null);
        JsonDiseaseGroup newValues = createJsonDiseaseGroup("New name", "New public name", "New short name", "NEWABBREV", "MICROCLUSTER", false, parentId, 4, "Bhatt2013");

        // Act
        ResponseEntity result = controller.save(diseaseGroupId, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void saveReturnsBadRequestForInvalidValidatorDiseaseGroup() throws Exception {
        // Arrange
        int validatorId = 4;
        when(diseaseService.getValidatorDiseaseGroupById(validatorId)).thenReturn(null);
        JsonDiseaseGroup newValues = createJsonDiseaseGroup("New name", "New public name", "New short name", "NEWABBREV", "CLUSTER", false, 87, validatorId, "Bhatt2013");

        // Act
        ResponseEntity result = controller.save(1, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void saveReturnsBadRequestForInvalidGroupType() throws Exception {
        // Arrange
        int validatorId = 4;
        when(diseaseService.getValidatorDiseaseGroupById(validatorId)).thenReturn(null);
        JsonDiseaseGroup newValues = createJsonDiseaseGroup("New name", "New public name", "New short name", "NEWABBREV", "invalid", false, 87, validatorId, "Bhatt2013");

        // Act
        ResponseEntity result = controller.save(1, newValues);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addCallsSaveForNewDiseaseGroupAndReturnsNoContent() throws Exception {
        // Arrange
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(new DiseaseGroup());
        when(diseaseService.getValidatorDiseaseGroupById(4)).thenReturn(new ValidatorDiseaseGroup());
        JsonDiseaseGroup values = createJsonDiseaseGroup("Name", "Public name", "Short name", "ABBREV", "MICROCLUSTER", false, 87, 4, "Bhatt2013");

        // Act
        ResponseEntity result = controller.add(values);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ArgumentCaptor<DiseaseGroup> diseaseGroupArgumentCaptor = captorForClass(DiseaseGroup.class);
        verify(diseaseService).saveDiseaseGroup(diseaseGroupArgumentCaptor.capture());
        DiseaseGroup value = diseaseGroupArgumentCaptor.getValue();
        assertThat(value.getName()).isEqualTo("Name");
    }

    @Test
    public void addReturnsBadRequestForMissingName() throws Exception {
        JsonDiseaseGroup values = createJsonDiseaseGroup(null, "Public name", "Short name", "ABBREV", "MICROCLUSTER", false, 87, 4, "Bhatt2013");
        ResponseEntity result = controller.add(values);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addReturnsBadRequestForMissingGroupType() throws Exception {
        JsonDiseaseGroup values = createJsonDiseaseGroup("Name", "Public name", "Short name", "ABBREV", null, false, 87, 4, "Bhatt2013");
        ResponseEntity result = controller.add(values);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addReturnsBadRequestForInvalidGroupType() throws Exception {
        JsonDiseaseGroup values = createJsonDiseaseGroup("Name", "Public name", "Short name", "ABBREV", "invalid", false, 87, 4, "Bhatt2013");
        ResponseEntity result = controller.add(values);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void addReturnsBadRequestForInvalidParentDiseaseGroup() throws Exception {
        // Arrange
        int parentId = 87;
        when(diseaseService.getDiseaseGroupById(parentId)).thenReturn(null);
        JsonDiseaseGroup values = createJsonDiseaseGroup("Name", "Public name", "Short name", "ABBREV", "MICROCLUSTER", false, parentId, 4, "Bhatt2013");

        // Act
        ResponseEntity result = controller.add(values);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    public void addReturnsBadRequestForInvalidValidatorDiseaseGroup() throws Exception {
        // Arrange
        int validatorId = 4;
        when(diseaseService.getValidatorDiseaseGroupById(validatorId)).thenReturn(null);
        JsonDiseaseGroup values = createJsonDiseaseGroup("Name", "Public name", "Short name", "ABBREV", "MICROCLUSTER", false, 87, validatorId, "Bhatt2013");

        // Act
        ResponseEntity result = controller.add(values);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getDiseaseOccurrenceSpreadAddsTableToModel() {
        // Arrange
        Model model = mock(Model.class);
        DiseaseOccurrenceSpreadTable table = mock(DiseaseOccurrenceSpreadTable.class);
        int diseaseGroupId = 87;

        when(helper.getDiseaseOccurrenceSpreadTable(diseaseGroupId)).thenReturn(table);

        // Act
        String view = controller.getDiseaseOccurrenceSpread(model, diseaseGroupId);

        // Assert
        assertThat(view).isEqualTo("admin/diseasegroups/occurrencespread");
        verify(model).addAttribute("table", table);
    }

    private DiseaseGroup createMockDiseaseGroup(int id) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.getId()).thenReturn(id);
        return mock;
    }

    ///CHECKSTYLE:OFF ParameterNumber - constructor for tests
    private DiseaseGroup createDiseaseGroup(int id, Integer parentGroupId, String name, String parentName,
                                            DiseaseGroupType groupType, String publicName, String shortName,
                                            String abbreviation, boolean isGlobal, Integer validatorDiseaseGroupId,
                                            double weighting, int maxDaysBetweenModelRuns) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(id);
        if (parentGroupId != null) {
            DiseaseGroup parentGroup = createParentDiseaseGroup(parentGroupId, parentName);
            diseaseGroup.setParentGroup(parentGroup);
        }
        diseaseGroup.setName(name);
        diseaseGroup.setGroupType(groupType);
        diseaseGroup.setPublicName(publicName);
        diseaseGroup.setShortName(shortName);
        diseaseGroup.setAbbreviation(abbreviation);
        diseaseGroup.setGlobal(isGlobal);
        diseaseGroup.setMaxDaysBetweenModelRuns(maxDaysBetweenModelRuns);
        if (validatorDiseaseGroupId != null) {
            ValidatorDiseaseGroup validatorDiseaseGroup = new ValidatorDiseaseGroup(validatorDiseaseGroupId);
            diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        }
        diseaseGroup.setWeighting(weighting);
        return diseaseGroup;
    }
    ///CHECKSTYLE:ON ParameterNumber

    private DiseaseGroup createParentDiseaseGroup(int id, String name) {
        DiseaseGroup diseaseGroup = new DiseaseGroup(id);
        diseaseGroup.setName(name);
        return diseaseGroup;
    }

    private JsonDiseaseGroup createJsonDiseaseGroup(String name, String publicName, String shortName,
                                                    String abbreviation, String groupType, boolean isGlobal, Integer parentId, Integer validatorId, String mode) {
        JsonDiseaseGroup json = new JsonDiseaseGroup();
        json.setName(name);
        json.setPublicName(publicName);
        json.setShortName(shortName);
        json.setAbbreviation(abbreviation);
        json.setGroupType(groupType);
        json.setIsGlobal(isGlobal);
        json.setModelMode(mode);

        if (parentId != null) {
            JsonParentDiseaseGroup parent = new JsonParentDiseaseGroup();
            parent.setId(parentId);
            json.setParentDiseaseGroup(parent);
        }

        if (validatorId != null) {
            JsonValidatorDiseaseGroup validator = new JsonValidatorDiseaseGroup();
            validator.setId(validatorId);
            json.setValidatorDiseaseGroup(validator);
        }
        return json;
    }
}
