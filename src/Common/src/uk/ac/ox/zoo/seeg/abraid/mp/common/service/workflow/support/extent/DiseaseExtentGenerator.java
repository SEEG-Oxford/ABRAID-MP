package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.*;

import java.util.Collection;
import java.util.Map;

import static ch.lambdaj.Lambda.index;
import static ch.lambdaj.Lambda.on;

/**
 * Generates disease extents for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerator {
    private static final String MODE_MESSAGE = "%s disease extent for disease group %d (%s)%s";
    private static final String INITIAL_STRING = "Creating initial";
    private static final String UPDATING_STRING = "Updating existing";
    private static final String GOLD_STANDARD_STRING = " using gold standard";
    private static final String DATA_MESSAGE = "Generating extent with %d disease occurrence(s) and %d review(s)";

    private static final Logger LOGGER = Logger.getLogger(DiseaseExtentGenerator.class);

    private ModelRunService modelRunService;
    private ValidationParameterCacheService cacheService;
    private DiseaseService diseaseService;
    private DiseaseExtentGenerationInputDataSelector extentDataSelector;
    private DiseaseExtentGeneratorHelperFactory helperFactory;
    private GeometryService geometryService;

    public DiseaseExtentGenerator(DiseaseExtentGenerationInputDataSelector extentDataSelector,
                                  DiseaseExtentGeneratorHelperFactory helperFactory,
                                  GeometryService geometryService,
                                  DiseaseService diseaseService,
                                  ModelRunService modelRunService,
                                  ValidationParameterCacheService cacheService) {
        this.extentDataSelector = extentDataSelector;
        this.helperFactory = helperFactory;
        this.geometryService = geometryService;
        this.diseaseService = diseaseService;
        this.modelRunService = modelRunService;
        this.cacheService = cacheService;
    }

    /**
     * Generates a disease extent for a single disease group.
     * @param diseaseGroup The disease group.
     * @param minimumOccurrenceDate The minimum occurrence date for the disease extent, as calculated from minimum
     *                              occurrence date of all the occurrences that can be sent to the model.
     * @param process The type of process that is being performed (auto/manual/gold).
     */
    public void generateDiseaseExtent(
            DiseaseGroup diseaseGroup, DateTime minimumOccurrenceDate, DiseaseProcessType process) {
        // Initial extents are any before the first model run. This is so that the disease extent can be generated m
        // multiple times before the initial model run, using all disease occurrences.
        boolean isInitial = modelHasNeverSuccessfullyRun(diseaseGroup);
        logModeMessage(diseaseGroup, process, isInitial);
        Collection<? extends AdminUnitGlobalOrTropical> adminUnits = findReferenceAdminUnitSet(diseaseGroup);

        // Calculate new validator extent
        DiseaseExtentGenerationInputData validatorExtentInputs = extentDataSelector.selectForValidatorExtent(
                diseaseGroup, adminUnits, isInitial, process, minimumOccurrenceDate);
        DiseaseExtentGenerationOutputData validatorExtentResults = generateExtent(
                diseaseGroup, validatorExtentInputs);

        // Calculate new modelling extent (if required)
        DiseaseExtentGenerationOutputData modellingExtentResults;
        if (isInitial || !process.isAutomatic()) {
            modellingExtentResults = validatorExtentResults;
        } else {
            DiseaseExtentGenerationInputData modellingExtentInputs = extentDataSelector.selectForModellingExtent(
                    diseaseGroup, validatorExtentInputs);
            modellingExtentResults = generateExtent(diseaseGroup, modellingExtentInputs);
        }

        // Save everything
        saveExtent(diseaseGroup, adminUnits,
                validatorExtentResults, modellingExtentResults, validatorExtentInputs.getOccurrences());
    }

    private boolean modelHasNeverSuccessfullyRun(DiseaseGroup diseaseGroup) {
        return modelRunService.getMostRecentlyRequestedModelRunWhichCompleted(diseaseGroup.getId()) == null;
    }

    private Collection<? extends AdminUnitGlobalOrTropical> findReferenceAdminUnitSet(DiseaseGroup diseaseGroup) {
        // Find all admin units, for either global or tropical diseases depending on the disease group
        // This query is necessary so that admin units with no occurrences appear in the disease extent
        return geometryService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroup(diseaseGroup);
    }

    private DiseaseExtentGenerationOutputData generateExtent(
            DiseaseGroup diseaseGroup, DiseaseExtentGenerationInputData extentInputs) {
        logDataMessage(extentInputs);

        DiseaseExtentGeneratorHelper helper =
                helperFactory.createHelper(diseaseGroup, extentInputs);

        return helper.computeDiseaseExtent();
    }

    private void saveExtent(DiseaseGroup diseaseGroup,
                            Collection<? extends AdminUnitGlobalOrTropical> adminUnits,
                            DiseaseExtentGenerationOutputData validatorExtentResults,
                            DiseaseExtentGenerationOutputData modellingExtentResults,
                            Collection<DiseaseOccurrence> occurrencesUsedInValidatorExtent) {
        // Update admin unit disease extent classes
        saveAdminUnitDiseaseExtentClasses(diseaseGroup, adminUnits, validatorExtentResults, modellingExtentResults);

        // Update aggregated extent geometry
        diseaseService.updateAggregatedDiseaseExtent(diseaseGroup);

        // Clear distance cache
        cacheService.clearDistanceToExtentCacheForDisease(diseaseGroup.getId());

        // Save input occurrences
        diseaseGroup.getDiseaseExtentParameters()
                .setLastValidatorExtentUpdateInputOccurrences(occurrencesUsedInValidatorExtent);

        // Update last extent generation date
        diseaseGroup.setLastExtentGenerationDate(DateTime.now());

        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private void saveAdminUnitDiseaseExtentClasses(DiseaseGroup diseaseGroup,
                                                   Collection<? extends AdminUnitGlobalOrTropical> adminUnits,
                                                   DiseaseExtentGenerationOutputData validatorExtentResults,
                                                   DiseaseExtentGenerationOutputData modellingExtentResults) {
        // Get data to be overwritten
        Map<Integer, AdminUnitDiseaseExtentClass> currentDiseaseExtent = index(
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroup.getId()),
                on(AdminUnitDiseaseExtentClass.class).getAdminUnitGlobalOrTropical().getGaulCode());

        // Update disease extent classes
        for (AdminUnitGlobalOrTropical adminUnit : adminUnits) {
            Integer gaulCode = adminUnit.getGaulCode();

            AdminUnitDiseaseExtentClass row;
            if (currentDiseaseExtent.containsKey(gaulCode)) {
                row = currentDiseaseExtent.get(gaulCode);
            } else {
                row = new AdminUnitDiseaseExtentClass();
                row.setDiseaseGroup(diseaseGroup);
                row.setAdminUnitGlobalOrTropical(adminUnit);
            }

            DiseaseExtentClass newModellingClass =
                    modellingExtentResults.getDiseaseExtentClassByGaulCode().get(gaulCode);
            if (!newModellingClass.equals(row.getDiseaseExtentClass())) {
                row.setClassChangedDate(DateTime.now());
            }
            row.setDiseaseExtentClass(newModellingClass);
            row.setValidatorDiseaseExtentClass(validatorExtentResults.getDiseaseExtentClassByGaulCode().get(gaulCode));
            row.setValidatorOccurrenceCount(validatorExtentResults.getOccurrenceCounts().get(gaulCode));
            row.setLatestValidatorOccurrences(validatorExtentResults.getLatestOccurrencesByGaulCode().get(gaulCode));
            diseaseService.saveAdminUnitDiseaseExtentClass(row);
        }
    }

    private void logModeMessage(DiseaseGroup diseaseGroup, DiseaseProcessType process, boolean isInitial) {
        LOGGER.info(String.format(MODE_MESSAGE,
                isInitial ? INITIAL_STRING : UPDATING_STRING,
                diseaseGroup.getId(),
                diseaseGroup.getName(),
                (process.isGoldStandard()) ? GOLD_STANDARD_STRING : ""));
    }

    private void logDataMessage(DiseaseExtentGenerationInputData extentInputs) {
        LOGGER.info(String.format(DATA_MESSAGE,
                extentInputs.getOccurrences().size(),
                (extentInputs.getReviews() == null) ? 0 : extentInputs.getReviews().size()));
    }
}
