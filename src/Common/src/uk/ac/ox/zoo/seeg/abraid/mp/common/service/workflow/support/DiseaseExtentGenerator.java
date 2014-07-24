package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;

import java.util.Iterator;
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
            "for disease group %d (%s) using %d disease occurrence(s)";

    private static final Logger LOGGER = Logger.getLogger(DiseaseExtentGenerator.class);

    private DiseaseService diseaseService;
    private ExpertService expertService;

    public DiseaseExtentGenerator(DiseaseService diseaseService, ExpertService expertService) {
        this.diseaseService = diseaseService;
        this.expertService = expertService;
    }

    /**
     * Generates a disease extent for a single disease group.
     * @param diseaseGroupId The disease group.
     * @param parameters Parameters used in generating the disease extent.
     */
    public void generateDiseaseExtent(Integer diseaseGroupId, DiseaseExtentParameters parameters) {
        DiseaseExtentGeneratorHelper helper = createHelper(diseaseGroupId, parameters);

        // If there is currently no disease extent for this disease group, create an initial extent, otherwise
        // update existing extent
        if (helper.getCurrentDiseaseExtent().size() == 0) {
            createInitialExtent(helper);
        } else {
            updateExistingExtent(helper);
        }
    }

    private DiseaseExtentGeneratorHelper createHelper(Integer diseaseGroupId, DiseaseExtentParameters parameters) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);

        // Find current disease extent
        List<AdminUnitDiseaseExtentClass> currentDiseaseExtent =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);

        // Find all admin units, for either global or tropical diseases depending on the disease group
        // This query is necessary so that admin units with no occurrences appear in the disease extent
        List<? extends AdminUnitGlobalOrTropical> adminUnits =
                diseaseService.getAllAdminUnitGlobalsOrTropicalsForDiseaseGroupId(diseaseGroupId);

        // Retrieve a lookup table of disease extent classes
        List<DiseaseExtentClass> diseaseExtentClasses = diseaseService.getAllDiseaseExtentClasses();

        return new DiseaseExtentGeneratorHelper(diseaseGroup, parameters, currentDiseaseExtent, adminUnits,
                diseaseExtentClasses);
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

    private void updateExistingExtent(DiseaseExtentGeneratorHelper helper) {
        List<AdminUnitReview> reviews = getRelevantReviews(helper);
        setUpdatedExtentOccurrences(helper);
        LOGGER.info(String.format(UPDATING_MESSAGE, getDiseaseGroupAndOccurrencesLogMessage(helper), reviews.size()));

        helper.groupOccurrencesByAdminUnit();
        helper.groupOccurrencesByCountry();
        helper.groupReviewsByAdminUnit();
        helper.computeUpdatedDiseaseExtentClasses();
        writeDiseaseExtent(helper.getDiseaseExtentToSave());
        updateAggregatedDiseaseExtent(helper.getDiseaseGroup());
    }

    private void setInitialExtentOccurrences(DiseaseExtentGeneratorHelper helper) {
        List<DiseaseOccurrenceForDiseaseExtent> occurrences =
                diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                        helper.getDiseaseGroup().getId(), null, null);
        helper.setOccurrences(occurrences);
    }

    private void setUpdatedExtentOccurrences(DiseaseExtentGeneratorHelper helper) {
        DiseaseExtentParameters parameters = helper.getParameters();

        List<DiseaseOccurrenceForDiseaseExtent> occurrences = diseaseService.getDiseaseOccurrencesForDiseaseExtent(
                helper.getDiseaseGroup().getId(),
                parameters.getMinimumValidationWeighting(),
                DateTime.now().minusYears(parameters.getMaximumYearsAgo())
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
        removeReviewsWithNullExpertWeightings(reviews);
        helper.setReviews(reviews);
        return reviews;
    }

    private void removeReviewsWithNullExpertWeightings(List<AdminUnitReview> reviews) {
        // Reviews with null expert weightings are not used when scoring the reviews for disease extent generation
        Iterator<AdminUnitReview> iterator = reviews.iterator();
        while (iterator.hasNext()) {
            AdminUnitReview review = iterator.next();
            if (review.getExpert().getWeighting() == null) {
                iterator.remove();
            }
        }
    }

    private String getDiseaseGroupAndOccurrencesLogMessage(DiseaseExtentGeneratorHelper helper) {
        DiseaseGroup diseaseGroup = helper.getDiseaseGroup();
        return String.format(DISEASE_GROUP_AND_OCCURRENCES_MESSAGE, diseaseGroup.getId(), diseaseGroup.getName(),
                helper.getOccurrences().size());
    }

    private void updateAggregatedDiseaseExtent(DiseaseGroup diseaseGroup) {
        diseaseService.updateAggregatedDiseaseExtent(diseaseGroup.getId(), diseaseGroup.isGlobal());
    }
}
