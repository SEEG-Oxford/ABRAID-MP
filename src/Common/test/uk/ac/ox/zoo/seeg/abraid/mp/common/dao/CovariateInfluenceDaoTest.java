package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvCovariateInfluence;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the CovariateInfluenceDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateInfluenceDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

    @Autowired
    private CovariateInfluenceDao covariateInfluenceDao;

    @Test
    public void canSaveAndReload() {
        // Arrange
        ModelRun modelRun = createModelRun("foo");
        CovariateInfluence expectation = createCovariateInfluence("a", modelRun);

        // Act
        covariateInfluenceDao.save(expectation);

        // Assert
        Integer id = expectation.getId();
        assertThat(id).isNotNull();
        flushAndClear();
        CovariateInfluence result = covariateInfluenceDao.getById(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getModelRun()).isEqualTo(modelRun);
        assertThat(result.getCovariateName()).isEqualTo(expectation.getCovariateName());
        assertThat(result.getCovariateDisplayName()).isEqualTo(expectation.getCovariateDisplayName());
        assertThat(result.getMeanInfluence()).isEqualTo(expectation.getMeanInfluence());
        assertThat(result.getLowerQuantile()).isEqualTo(expectation.getLowerQuantile());
        assertThat(result.getUpperQuantile()).isEqualTo(expectation.getUpperQuantile());
    }

    @Test
    public void getCovariateInfluenceForModelRunReturnsCorrectSubset() {
        // Arrange
        ModelRun run1 = createModelRun("foo1");
        ModelRun run2 = createModelRun("foo2");
        covariateInfluenceDao.save(createCovariateInfluence("a", run1));
        covariateInfluenceDao.save(createCovariateInfluence("a", run2));
        covariateInfluenceDao.save(createCovariateInfluence("b", run1));

        // Act
        List<CovariateInfluence> results1 = covariateInfluenceDao.getCovariateInfluencesForModelRun(run1);
        List<CovariateInfluence> results2 = covariateInfluenceDao.getCovariateInfluencesForModelRun(run2);

        // Assert
        assertThat(results1).hasSize(2);
        assertThat(results2).hasSize(1);
    }

    private CovariateInfluence createCovariateInfluence(String name, ModelRun modelRun) {
        CsvCovariateInfluence dto = new CsvCovariateInfluence();
        dto.setCovariateName(name);
        dto.setCovariateDisplayName("2");
        dto.setMeanInfluence(3.0);
        dto.setLowerQuantile(4.0);
        dto.setUpperQuantile(5.0);

        return new CovariateInfluence(dto, modelRun);
    }

    private ModelRun createModelRun(String name) {
        ModelRun run = new ModelRun(name, 87, DateTime.now());
        modelRunDao.save(run);
        return run;
    }
}
