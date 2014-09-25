package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.*;

import javax.persistence.*;

/**
 * Represents the influence of a covariate file on a model run.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "covariate_influence")
@NamedQueries({
        @NamedQuery(
                name = "getCovariateInfluencesForModelRun",
                query = "from CovariateInfluence where modelRun=:modelRun"
        )
})
public class CovariateInfluence extends AbstractCovariateInfluence {
    public CovariateInfluence() {
        super();
    }

    public CovariateInfluence(CsvCovariateInfluence dto, ModelRun parentRun) {
        super(dto, parentRun);
    }
}
