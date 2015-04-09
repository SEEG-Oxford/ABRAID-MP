package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;

import java.util.List;

/**
 * Return the set of occurrences to be used in the model run (acts as stateless facade for the stateful
 * ModelRunOccurrencesSelectorHelper).
 *
 * Copyright (c) 2015 University of Oxford
 */
public class ModelRunOccurrencesSelector {
    private DiseaseService diseaseService;
    private GeometryService geometryService;
    private EmailService emailService;

    public ModelRunOccurrencesSelector(
            DiseaseService diseaseService, GeometryService geometryService, EmailService emailService) {
        this.diseaseService = diseaseService;
        this.geometryService = geometryService;
        this.emailService = emailService;
    }

    /**
     * Gets the list of occurrences to be used in the model run.
     * @param diseaseGroupId The disease group ID for the model run.
     * @param onlyUseGoldStandardOccurrences If only gold standard occurrences should be used for the model run.
     * @return The occurrences for the model run.
     */
    public List<DiseaseOccurrence> selectOccurrencesForModelRun(
            int diseaseGroupId, boolean onlyUseGoldStandardOccurrences) {
        ModelRunOccurrencesSelectorHelper helper = new ModelRunOccurrencesSelectorHelper(
                diseaseService, geometryService, emailService, diseaseGroupId, onlyUseGoldStandardOccurrences);
        return helper.selectModelRunDiseaseOccurrences();
    }
}
