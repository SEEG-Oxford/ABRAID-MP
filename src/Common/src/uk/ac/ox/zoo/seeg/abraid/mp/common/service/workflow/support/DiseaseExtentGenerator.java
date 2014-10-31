package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.util.List;

/**
 * Generates disease extents for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerator {
    private static final String INITIAL_MESSAGE = "Creating initial disease extent %s";
    private static final String UPDATING_MESSAGE = "Updating disease extent %s and %d review(s)";
    private static final String DISEASE_GROUP_AND_OCCURRENCES_MESSAGE =
            "for disease group %d (%s) using %d %sdisease occurrence(s)";
    private static final String GOLD_STANDARD_MESSAGE = "gold standard ";

    private static final Logger LOGGER = Logger.getLogger(DiseaseExtentGenerator.class);

    private DiseaseService diseaseService;
    private ExpertService expertService;
    private ModelRunService modelRunService;

    public DiseaseExtentGenerator(DiseaseService diseaseService, ExpertService expertService,
                                  ModelRunService modelRunService) {
        this.diseaseService = diseaseService;
        this.expertService = expertService;
        this.modelRunService = modelRunService;
    }

    /**
     * Generates a disease extent for a single disease group.
     * @param diseaseGroup The disease group.
     * @param minimumOccurrenceDate The minimum occurrence date for the disease extent, as calculated from minimum
     *                              occurrence date of all the occurrences that can be sent to the model.
     * @param useGoldStandardOccurrences True if only "gold standard" occurrences should be used, otherwise false.
     */
    public void generateDiseaseExtent(DiseaseGroup diseaseGroup, DateTime minimumOccurrenceDate,
                                      boolean useGoldStandardOccurrences) {
        DiseaseExtentGeneratorHelper helper = createHelper(diseaseGroup, useGoldStandardOccurrences);

        // If there is currently no disease extent for this disease group, create an initial extent, otherwise
        // update existing extent
        if (helper.getCurrentDiseaseExtent().size() == 0) {
            createInitialExtent(helper);
        } else {
            updateExistingExtent(helper, minimumOccurrenceDate);
        }
    }

    private DiseaseExtentGeneratorHelper createHelper(DiseaseGroup diseaseGroup, boolean useGoldStandardOccurrences) {
        int diseaseGroupId = diseaseGroup.getId();

        // Find current disease extent
        List<AdminUnitDiseaseExtentClass> currentDiseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // Find all admin units, for either global or tropical diseases depending on the disease group
        // This query is necessary so that admin units with no occurrences appear in the disease extent
        List<? extends AdminUnitGlobalOrTropical> adminUnits =
                diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId);

        // Retrieve a lookup table of disease extent classes
        List<DiseaseExtentClass> diseaseExtentClasses = diseaseService.getAllDiseaseExtentClasses();

        // Determine whether the model has been successfully run
        ModelRun modelRun = modelRunService.getLastCompletedModelRun(diseaseGroupId);
        boolean hasModelBeenSuccessfullyRun = (modelRun != null);

        return new DiseaseExtentGeneratorHelper(diseaseGroup, currentDiseaseExtent, adminUnits, diseaseExtentClasses,
                hasModelBeenSuccessfullyRun, useGoldStandardOccurrences);
    }

    private void createInitialExtent(DiseaseExtentGeneratorHelper helper) {
        setInitialExtentOccurrences(helper);
        LOGGER.info(String.format(INITIAL_MESSAGE, getDiseaseGroupAndOccurrencesLogMessage(helper)));

        helper.groupOccurrencesByAdminUnit();
        helper.groupOccurrencesByCountry();
        helper.computeInitialDiseaseExtentClasses();
        writeDiseaseExtent(helper.getDiseaseExtentToSave());
        updateAggregatedDiseaseExtent(helper.getDiseaseGroup());
    }

    private void updateExistingExtent(DiseaseExtentGeneratorHelper helper, DateTime minimumOccurrenceDate) {
        List<AdminUnitReview> reviews = getRelevantReviews(helper);
        setUpdatedExtentOccurrences(helper, minimumOccurrenceDate);
        LOGGER.info(String.format(UPDATING_MESSAGE, getDiseaseGroupAndOccurrencesLogMessage(helper), reviews.size()));

        helper.groupOccurrencesByAdminUnit();
        helper.groupOccurrencesByCountry();
        helper.groupReviewsByAdminUnit();
        helper.computeUpdatedDiseaseExtentClasses();
        writeDiseaseExtent(helper.getDiseaseExtentToSave());
        updateAggregatedDiseaseExtent(helper.getDiseaseGroup());
    }

    private void setInitialExtentOccurrences(DiseaseExtentGeneratorHelper helper) {
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                helper.getDiseaseGroup().getId(),
                null,
                null,
                helper.useGoldStandardOccurrences());
        helper.setOccurrences(occurrences);
    }

    private void setUpdatedExtentOccurrences(DiseaseExtentGeneratorHelper helper, DateTime minimumOccurrenceDate) {
        // The minimum occurrence date is only relevant if automatic model runs are enabled for the disease
        if (!helper.getDiseaseGroup().isAutomaticModelRunsEnabled()) {
            minimumOccurrenceDate = null;
        }

        // The minimum validation weighting is only relevant if the model has successfully run at least once. This is
        // so that the disease extent can be generated multiple times before the initial model run, using all
        // disease occurrences.
        Double minimumValidationWeighting = null;
        if (helper.hasModelBeenSuccessfullyRun()) {
            minimumValidationWeighting = helper.getParameters().getMinValidationWeighting();
        }

        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                helper.getDiseaseGroup().getId(),
                minimumValidationWeighting,
                minimumOccurrenceDate,
                helper.useGoldStandardOccurrences()
        );
        helper.setOccurrences(occurrences);
    }

    private void writeDiseaseExtent(List<AdminUnitDiseaseExtentClass> adminUnitDiseaseExtentClassesToSave) {
        for (AdminUnitDiseaseExtentClass row : adminUnitDiseaseExtentClassesToSave) {
            diseaseService.saveAdminUnitDiseaseExtentClass(row);
        }
    }

    private List<AdminUnitReview> getRelevantReviews(DiseaseExtentGeneratorHelper helper) {
        Integer diseaseGroupId = helper.getDiseaseGroup().getId();
        List<AdminUnitReview> reviews = expertService.getAllAdminUnitReviewsForDiseaseGroup(diseaseGroupId);
        helper.setRelevantReviews(reviews);
        return reviews;
    }

    private String getDiseaseGroupAndOccurrencesLogMessage(DiseaseExtentGeneratorHelper helper) {
        DiseaseGroup diseaseGroup = helper.getDiseaseGroup();
        String goldStandardMessage = helper.useGoldStandardOccurrences() ? GOLD_STANDARD_MESSAGE : "";
        return String.format(DISEASE_GROUP_AND_OCCURRENCES_MESSAGE, diseaseGroup.getId(), diseaseGroup.getName(),
                helper.getOccurrences().size(), goldStandardMessage);
    }

    private void updateAggregatedDiseaseExtent(DiseaseGroup diseaseGroup) {
        diseaseService.updateAggregatedDiseaseExtent(diseaseGroup.getId(), diseaseGroup.isGlobal());
    }
}
