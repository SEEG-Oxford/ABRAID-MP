package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.DiseaseOccurrenceValidationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Set model runs to be triggered automatically for a given disease group.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AutomaticModelRunsEnabler {
    private DiseaseService diseaseService;
    private DiseaseOccurrenceValidationService diseaseOccurrenceValidationService;
    private ModelRunService modelRunService;

    public AutomaticModelRunsEnabler(DiseaseService diseaseService,
                                     DiseaseOccurrenceValidationService diseaseOccurrenceValidationService,
                                     ModelRunService modelRunService) {
        this.diseaseService = diseaseService;
        this.diseaseOccurrenceValidationService = diseaseOccurrenceValidationService;
        this.modelRunService = modelRunService;
    }

    /**
     * Set model runs to be triggered automatically for the specified disease group.
     * @param diseaseGroupId The disease group ID.
     */
    public void enable(int diseaseGroupId) {
        DateTime now = DateTime.now();
        saveAutomaticModelRunsStartDate(diseaseGroupId, now);
        setAdminUnitDiseaseExtentClassChangedDate(diseaseGroupId, now);

        List<DiseaseOccurrence> occurrences = getOccurrencesWithoutAFinalWeighting(diseaseGroupId);
        addValidationParametersOrWeightings(occurrences);
        saveOccurrences(occurrences);
    }

    private void saveAutomaticModelRunsStartDate(int diseaseGroupId, DateTime now) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(now);
        diseaseService.saveDiseaseGroup(diseaseGroup);
    }

    private void setAdminUnitDiseaseExtentClassChangedDate(int diseaseGroupId, DateTime now) {
        // Update the ClassChangedDate of all AdminUnitDiseaseExtentClasses for this DiseaseGroup
        // so that all polygons are available for review on DataValidator.
        List<AdminUnitDiseaseExtentClass> extentClasses =
                diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);
        for (AdminUnitDiseaseExtentClass extentClass : extentClasses) {
            extentClass.setClassChangedDate(now);
            diseaseService.saveAdminUnitDiseaseExtentClass(extentClass);
        }
    }

    private List<DiseaseOccurrence> getOccurrencesWithoutAFinalWeighting(int diseaseGroupId) {
        return diseaseService.getDiseaseOccurrencesYetToHaveFinalWeightingAssigned(diseaseGroupId, false);
    }

    private void addValidationParametersOrWeightings(List<DiseaseOccurrence> occurrences) {
        // Adds validation parameters for occurrences without a final weighting, using a cutoff date of the number of
        // days between model runs. This ensures that that experts are not overwhelmed with occurrences to validate.
        // Occurrences before the cutoff date are permanently ignored by setting their final weighting to zero.
        DateTime earliestDateForValidationParameters = modelRunService.subtractDaysBetweenModelRuns(DateTime.now());
        List<DiseaseOccurrence> occurrencesForValidationParameters = new ArrayList<>();

        for (DiseaseOccurrence occurrence : occurrences) {
            if (occurrence.getOccurrenceDate().isBefore(earliestDateForValidationParameters)) {
                clearParameters(occurrence);
            } else {
                occurrencesForValidationParameters.add(occurrence);
            }
        }

        diseaseOccurrenceValidationService.addValidationParameters(occurrencesForValidationParameters);
    }

    private void clearParameters(DiseaseOccurrence occurrence) {
        occurrence.setValidated(null);
        occurrence.setFinalWeighting(null);
        occurrence.setFinalWeightingExcludingSpatial(null);
    }

    private void saveOccurrences(List<DiseaseOccurrence> occurrences) {
        for (DiseaseOccurrence occurrence : occurrences) {
            diseaseService.saveDiseaseOccurrence(occurrence);
        }
    }
}
