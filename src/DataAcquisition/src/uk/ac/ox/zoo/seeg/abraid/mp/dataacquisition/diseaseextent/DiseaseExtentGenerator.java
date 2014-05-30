package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ExpertService;

import java.util.List;

/**
 * Generates disease extents for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseExtentGenerator {
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

        // Retrieve relevant disease occurrences
        List<DiseaseOccurrenceForDiseaseExtent> occurrences =
                diseaseService.getDiseaseOccurrencesForDiseaseExtent(diseaseGroupId,
                        parameters.getMinimumValidationWeighting(),
                        DateTime.now().minusYears(parameters.getMaximumYearsAgo()),
                        parameters.getFeedIds());

        // Retrieve a lookup table of disease extent classes
        List<DiseaseExtentClass> diseaseExtentClasses = diseaseService.getAllDiseaseExtentClasses();

        return new DiseaseExtentGeneratorHelper(diseaseGroup, parameters, currentDiseaseExtent, adminUnits,
                occurrences, diseaseExtentClasses);
    }

    private void createInitialExtent(DiseaseExtentGeneratorHelper helper) {
        helper.groupOccurrencesByAdminUnit();
        helper.groupOccurrencesByCountry();
        helper.computeInitialDiseaseExtentClasses();
        writeDiseaseExtent(helper.getDiseaseExtentToSave());
    }

    private void updateExistingExtent(DiseaseExtentGeneratorHelper helper) {
        helper.groupOccurrencesByAdminUnit();
        helper.groupOccurrencesByCountry();
        helper.groupReviewsByAdminUnit(getRelevantReviews(helper));
        helper.computeUpdatedDiseaseExtentClasses();
        writeDiseaseExtent(helper.getDiseaseExtentToSave());
    }

    private void writeDiseaseExtent(List<AdminUnitDiseaseExtentClass> adminUnitDiseaseExtentClassesToSave) {
        for (AdminUnitDiseaseExtentClass row : adminUnitDiseaseExtentClassesToSave) {
            diseaseService.saveAdminUnitDiseaseExtentClass(row);
        }
    }

    private List<AdminUnitReview> getRelevantReviews(DiseaseExtentGeneratorHelper helper) {
        Integer diseaseGroupId = helper.getDiseaseGroup().getId();
        return expertService.getAllAdminUnitReviewsForDiseaseGroup(diseaseGroupId);
    }
}
