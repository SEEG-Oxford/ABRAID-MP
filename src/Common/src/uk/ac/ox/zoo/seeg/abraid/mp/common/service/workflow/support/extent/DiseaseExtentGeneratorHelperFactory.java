package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * A factory for DiseaseExtentGeneratorHelper objects, this is only split out to improve the testability of
 * disease extent generator.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGeneratorHelperFactory {
    /**
     * Create a new helper.
     * @param diseaseGroup The disease group.
     * @param extentInputs The data for which to generate an extent.
     * @return A new helper.
     */
    public DiseaseExtentGeneratorHelper createHelper(
            DiseaseGroup diseaseGroup, DiseaseExtentGenerationInputData extentInputs) {
        return new DiseaseExtentGeneratorHelper(extentInputs, diseaseGroup.getDiseaseExtentParameters());
    }
}
