package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentParameters;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings.WeightingsCalculator;

/**
 * Prepares the model run by updating the disease extent and recalculating weightings.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunGateKeeper {
    private DiseaseExtentGenerator diseaseExtentGenerator;
    private WeightingsCalculator weightingsCalculator;
    private ModelRunRequester modelRunRequester;

    public ModelRunGateKeeper(DiseaseExtentGenerator diseaseExtentGenerator, WeightingsCalculator weightingsCalculator,
                              ModelRunRequester modelRunRequester) {
        this.diseaseExtentGenerator = diseaseExtentGenerator;
        this.weightingsCalculator = weightingsCalculator;
        this.modelRunRequester = modelRunRequester;
    }

    /**
     * Prepares the model run by updating the disease extent, recalculating weightings and making the request.
     */
    @Transactional
    public void prepareModelRun() {
        ///CHECKSTYLE:OFF MagicNumberCheck - Dengue hard-coded for now
        int diseaseGroupId = 87;
        diseaseExtentGenerator.generateDiseaseExtent(diseaseGroupId, new DiseaseExtentParameters(null, 5, 0.6, 5, 1));
        prepareDiseaseOccurrenceWeightings(diseaseGroupId);
        modelRunRequester.requestModelRun(diseaseGroupId);
        ///CHECKSTYLE:ON
    }

    private void prepareDiseaseOccurrenceWeightings(int diseaseGroupId) {
        DateTime modelRunPrepStartTime = DateTime.now();
        weightingsCalculator.updateDiseaseOccurrenceExpertWeightings(diseaseGroupId);
        // Here, determine whether occurrences should come off DataValidator, and set their is_validated value to true
        weightingsCalculator.updateDiseaseOccurrenceValidationWeightingsAndFinalWeightings(diseaseGroupId);
        weightingsCalculator.updateModelRunPrepDate(diseaseGroupId, modelRunPrepStartTime);
    }
}
