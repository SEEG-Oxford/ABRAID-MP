package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.extent;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobalOrTropical;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.Collection;

/**
 * A wrapper for all of the data required to perform a disease extent generation.
 * Copyright (c) 2015 University of Oxford
 */
public class DiseaseExtentGenerationInputData {
    private final Collection<DiseaseExtentClass> diseaseExtentClasses;
    private final Collection<? extends AdminUnitGlobalOrTropical> adminUnits;
    private final Collection<AdminUnitReview> reviews;
    private final Collection<DiseaseOccurrence> occurrences;

    public DiseaseExtentGenerationInputData(
            Collection<DiseaseExtentClass> diseaseExtentClasses,
            Collection<? extends AdminUnitGlobalOrTropical> adminUnits,
            Collection<AdminUnitReview> reviews,
            Collection<DiseaseOccurrence> occurrences) {

        this.diseaseExtentClasses = diseaseExtentClasses;
        this.adminUnits = adminUnits;
        this.reviews = reviews;
        this.occurrences = occurrences;
    }

    public Collection<DiseaseExtentClass> getDiseaseExtentClasses() {
        return diseaseExtentClasses;
    }

    public Collection<? extends AdminUnitGlobalOrTropical> getAdminUnits() {
        return adminUnits;
    }

    public Collection<AdminUnitReview> getReviews() {
        return reviews;
    }

    public Collection<DiseaseOccurrence> getOccurrences() {
        return occurrences;
    }
}
