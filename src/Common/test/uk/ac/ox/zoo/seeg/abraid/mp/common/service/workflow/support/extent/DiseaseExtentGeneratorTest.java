package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.AdditionalMatchers.and;
import static org.mockito.Mockito.*;

/**
 * Tests for DiseaseExtentGenerator.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGeneratorTest extends BaseDiseaseExtentGenerationTests {
    private DiseaseService diseaseService;
    private ModelRunService modelRunService;
    private GeometryService geometryService;
    private LocationService locationService;
    private DiseaseExtentGenerationInputDataSelector dataSelector;
    private DiseaseExtentGeneratorHelperFactory helperFactory;

    private DiseaseExtentGenerator diseaseExtentGenerator;
    private DateTime minimumOccurrenceDate;
    private DiseaseExtentGenerationInputData validatorInputData;
    private DiseaseExtentGenerationInputData modellingInputData;
    private DiseaseExtentGeneratorHelper helper;
    private List<DiseaseOccurrence> validatorInputOccurrences;

    @Before
    public void setUp() {
        baseSetup();

        diseaseService = mock(DiseaseService.class);
        modelRunService = mock(ModelRunService.class);
        geometryService = mock(GeometryService.class);
        locationService = mock(LocationService.class);
        dataSelector = mock(DiseaseExtentGenerationInputDataSelector.class);
        helperFactory = mock(DiseaseExtentGeneratorHelperFactory.class);
        validatorInputData = mock(DiseaseExtentGenerationInputData.class);
        validatorInputOccurrences = createOccurrences();
        when(validatorInputData.getOccurrences()).thenReturn(validatorInputOccurrences);
        modellingInputData = mock(DiseaseExtentGenerationInputData.class);
        helper = mock(DiseaseExtentGeneratorHelper.class);

        when(dataSelector.selectForValidatorExtent(eq(diseaseGroup), eq(adminUnits), anyBoolean(), any(DiseaseProcessType.class), any(DateTime.class))).thenReturn(validatorInputData);
        when(dataSelector.selectForModellingExtent(eq(diseaseGroup), eq(validatorInputData))).thenReturn(modellingInputData);

        diseaseExtentGenerator = new DiseaseExtentGenerator(dataSelector, helperFactory, geometryService, diseaseService, modelRunService, locationService);
        minimumOccurrenceDate = DateTime.now().minusHours(1);
    }

    @Test
    public void generateDiseaseExtentCorrectlyDetectsIfThisIsAnInitialExtent() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(false);
        setupHelperFactory(validatorInputData, helper, createRandomDiseaseExtentResults());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.AUTOMATIC);

        // Assert
        verify(dataSelector).selectForValidatorExtent(diseaseGroup, adminUnits, true, DiseaseProcessType.AUTOMATIC, minimumOccurrenceDate);
        verify(helper).computeDiseaseExtent(true);
    }

    @Test
    public void generateDiseaseExtentCalculatesNewValidatorExtentWithCorrectData() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(true);
        setupHelperFactory(validatorInputData, helper, createRandomDiseaseExtentResults());
        setupHelperFactory(modellingInputData, mock(DiseaseExtentGeneratorHelper.class), createRandomDiseaseExtentResults());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.AUTOMATIC);

        // Assert
        verify(dataSelector).selectForValidatorExtent(diseaseGroup, adminUnits, false, DiseaseProcessType.AUTOMATIC, minimumOccurrenceDate);
        verify(helperFactory).createHelper(diseaseGroup, validatorInputData);
        verify(helper).computeDiseaseExtent(false);
    }

    @Test
    public void generateDiseaseExtentCalculatesNewModelingExtentWithCorrectDataForAutomaticNonInitial() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(true);
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), createRandomDiseaseExtentResults());
        setupHelperFactory(modellingInputData, helper, createRandomDiseaseExtentResults());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.AUTOMATIC);

        // Assert
        verify(dataSelector).selectForModellingExtent(diseaseGroup, validatorInputData);
        verify(helperFactory).createHelper(diseaseGroup, modellingInputData);
        verify(helper).computeDiseaseExtent(false);
    }

    @Test
    public void generateDiseaseExtentSkipsNewModelingExtentWithCorrectDataForAutomaticInitial() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(false);
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), createRandomDiseaseExtentResults());
        setupHelperFactory(modellingInputData, helper, createRandomDiseaseExtentResults());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.AUTOMATIC);

        // Assert
        verify(dataSelector, never()).selectForModellingExtent(diseaseGroup, validatorInputData);
        verify(helperFactory, never()).createHelper(diseaseGroup, modellingInputData);
        verify(helper, never()).computeDiseaseExtent(false);
    }

    @Test
    public void generateDiseaseExtentSkipsNewModelingExtentWithCorrectDataForManualInitial() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(false);
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), createRandomDiseaseExtentResults());
        setupHelperFactory(modellingInputData, helper, createRandomDiseaseExtentResults());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.MANUAL);

        // Assert
        verify(dataSelector, never()).selectForModellingExtent(diseaseGroup, validatorInputData);
        verify(helperFactory, never()).createHelper(diseaseGroup, modellingInputData);
        verify(helper, never()).computeDiseaseExtent(false);
    }

    @Test
    public void generateDiseaseExtentSkipsNewModelingExtentWithCorrectDataForManualNonInitial() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(true);
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), createRandomDiseaseExtentResults());
        setupHelperFactory(modellingInputData, helper, createRandomDiseaseExtentResults());

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.MANUAL);

        // Assert
        verify(dataSelector, never()).selectForModellingExtent(diseaseGroup, validatorInputData);
        verify(helperFactory, never()).createHelper(diseaseGroup, modellingInputData);
        verify(helper, never()).computeDiseaseExtent(false);
    }

    @Test
    public void generateDiseaseExtentSavesCalculatedResultsCorrectWhenBothResultsSetsCreated() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(true);
        DiseaseExtentGenerationOutputData validatorResult = createRandomDiseaseExtentResults();
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), validatorResult);
        DiseaseExtentGenerationOutputData modellingResult = createRandomDiseaseExtentResults();
        setupHelperFactory(modellingInputData, mock(DiseaseExtentGeneratorHelper.class), modellingResult);

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.AUTOMATIC);

        // Assert
        assertSaved(adminUnits, validatorResult, modellingResult, validatorInputOccurrences, new ArrayList<AdminUnitDiseaseExtentClass>());
    }

    @Test
    public void generateDiseaseExtentSavesCalculatedResultsCorrectWhenOnlyValidatorResultsSetCreated() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(true);
        DiseaseExtentGenerationOutputData validatorResult = createRandomDiseaseExtentResults();
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), validatorResult);
        DiseaseExtentGenerationOutputData modellingResult = createRandomDiseaseExtentResults();
        setupHelperFactory(modellingInputData, mock(DiseaseExtentGeneratorHelper.class), modellingResult);

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.MANUAL);

        // Assert
        assertSaved(adminUnits, validatorResult, validatorResult, validatorInputOccurrences, new ArrayList<AdminUnitDiseaseExtentClass>());
    }

    @Test
    public void generateDiseaseExtentSavesOverExistingResults() throws Exception {
        // Arrange
        setupAdminUnits(adminUnits);
        setupOldModelRun(true);
        DiseaseExtentGenerationOutputData validatorResult = createRandomDiseaseExtentResults();
        setupHelperFactory(validatorInputData, mock(DiseaseExtentGeneratorHelper.class), validatorResult);
        DiseaseExtentGenerationOutputData modellingResult = createRandomDiseaseExtentResults();
        setupHelperFactory(modellingInputData, mock(DiseaseExtentGeneratorHelper.class), modellingResult);
        List<AdminUnitDiseaseExtentClass> existingDiseaseExtent = createRandomExistingDiseaseExtent();
        setupExistingExtent(existingDiseaseExtent);

        // Act
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroup, minimumOccurrenceDate, DiseaseProcessType.MANUAL);

        // Assert
        assertSaved(adminUnits, validatorResult, validatorResult, validatorInputOccurrences, existingDiseaseExtent);
    }

    private void assertSaved(List<? extends AdminUnitGlobalOrTropical> adminUnits, DiseaseExtentGenerationOutputData validatorResult, DiseaseExtentGenerationOutputData modellingResult, List<DiseaseOccurrence> occurrencesForLastValidatorExtent, List<AdminUnitDiseaseExtentClass> existingDiseaseExtent) {
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            AdminUnitDiseaseExtentClass existingObject = findExistingObject(adminUnit.getGaulCode(), existingDiseaseExtent);
            AdminUnitDiseaseExtentClass expectation = new AdminUnitDiseaseExtentClass();
            expectation.setDiseaseGroup(diseaseGroup);
            expectation.setAdminUnitGlobalOrTropical(adminUnit);
            expectation.setValidatorDiseaseExtentClass(validatorResult.getDiseaseExtentClassByGaulCode().get(adminUnit.getGaulCode()));
            expectation.setDiseaseExtentClass(modellingResult.getDiseaseExtentClassByGaulCode().get(adminUnit.getGaulCode()));
            if (existingObject != null && existingObject.getValidatorDiseaseExtentClass() == validatorResult.getDiseaseExtentClassByGaulCode().get(adminUnit.getGaulCode())) {
                expectation.setClassChangedDate(existingObject.getClassChangedDate());
            } else {
                expectation.setClassChangedDate(DateTime.now());
            }
            expectation.setValidatorOccurrenceCount(validatorResult.getOccurrenceCounts().get(adminUnit.getGaulCode()));
            expectation.setLatestValidatorOccurrences(validatorResult.getLatestOccurrencesByGaulCode().get(adminUnit.getGaulCode()));

            if (existingObject == null) {
                verify(diseaseService).saveAdminUnitDiseaseExtentClass(eq(expectation));
            } else {
                verify(diseaseService).saveAdminUnitDiseaseExtentClass(and(eq(expectation), same(existingObject)));
            }
        }

        verify(locationService).clearDistanceToExtentCacheForDisease(diseaseGroup.getId());
        verify(diseaseGroup.getDiseaseExtentParameters()).setLastValidatorExtentUpdateInputOccurrences(occurrencesForLastValidatorExtent);
        verify(diseaseGroup).setLastExtentGenerationDate(eq(DateTime.now()));
        verify(diseaseService).saveDiseaseGroup(diseaseGroup);
    }

    private AdminUnitDiseaseExtentClass findExistingObject(int gaulCode, List<AdminUnitDiseaseExtentClass> existingDiseaseExtent) {
        for (AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass : existingDiseaseExtent) {
            if (adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical().getGaulCode() == gaulCode) {
                return adminUnitDiseaseExtentClass;
            }
        }
        return null;
    }

    private void setupAdminUnits(final List<? extends AdminUnitGlobalOrTropical> expectedAdminUnits) {
        when(geometryService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroup(diseaseGroup)).thenAnswer(new Answer<List<? extends AdminUnitGlobalOrTropical>>() {
            @Override
            public List<? extends AdminUnitGlobalOrTropical> answer(InvocationOnMock invocationOnMock) throws Throwable {
                return expectedAdminUnits;
            }
        });
    }

    private void setupOldModelRun(boolean hasRun) {
        when(modelRunService.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroupId)).thenReturn(hasRun ? mock(ModelRun.class) : null);
    }

    private void setupHelperFactory(DiseaseExtentGenerationInputData inputData, DiseaseExtentGeneratorHelper helperResult, DiseaseExtentGenerationOutputData result) {
        when(helperFactory.createHelper(diseaseGroup, inputData)).thenReturn(helperResult);
        when(helperResult.computeDiseaseExtent(anyBoolean())).thenReturn(result);
    }

    private void setupExistingExtent(List<AdminUnitDiseaseExtentClass> existingDiseaseExtent) {
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroup.getId())).thenReturn(existingDiseaseExtent);
    }
}
