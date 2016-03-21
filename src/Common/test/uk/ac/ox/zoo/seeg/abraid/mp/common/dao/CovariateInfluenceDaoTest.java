package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvCovariateInfluence;

import java.util.Arrays;
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
    private CovariateFileDao covariateFileDao;

    @Autowired
    private CovariateInfluenceDao covariateInfluenceDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void canSaveAndReload() {
        // Arrange
        ModelRun modelRun = createModelRun("foo");
        CovariateFile covariate = createCovariateFile(1);
        CovariateInfluence expectation = createCovariateInfluence(covariate, modelRun);

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
        assertThat(result.getCovariateFile()).isEqualTo(covariate);
        assertThat(result.getMeanInfluence()).isEqualTo(expectation.getMeanInfluence());
        assertThat(result.getLowerQuantile()).isEqualTo(expectation.getLowerQuantile());
        assertThat(result.getUpperQuantile()).isEqualTo(expectation.getUpperQuantile());
    }

    @Test
    public void getCovariateInfluenceForModelRunReturnsCorrectSubset() {
        // Arrange
        ModelRun run1 = createModelRun("foo1");
        ModelRun run2 = createModelRun("foo2");
        CovariateFile covariate1 = createCovariateFile(1);
        CovariateFile covariate2 = createCovariateFile(2);
        covariateInfluenceDao.save(createCovariateInfluence(covariate1, run1));
        covariateInfluenceDao.save(createCovariateInfluence(covariate1, run2));
        covariateInfluenceDao.save(createCovariateInfluence(covariate2, run1));

        // Act
        List<CovariateInfluence> results1 = covariateInfluenceDao.getCovariateInfluencesForModelRun(run1);
        List<CovariateInfluence> results2 = covariateInfluenceDao.getCovariateInfluencesForModelRun(run2);

        // Assert
        assertThat(results1).hasSize(2);
        assertThat(results2).hasSize(1);
    }

    private CovariateInfluence createCovariateInfluence(CovariateFile covariate, ModelRun modelRun) {
        CsvCovariateInfluence dto = new CsvCovariateInfluence();
        dto.setMeanInfluence(3.0);
        dto.setLowerQuantile(4.0);
        dto.setUpperQuantile(5.0);

        return new CovariateInfluence(covariate, dto, modelRun);
    }

    private ModelRun createModelRun(String name) {
        ModelRun run = new ModelRun(name, diseaseGroupDao.getById(87), "host", DateTime.now(), DateTime.now(), DateTime.now());
        modelRunDao.save(run);
        return run;
    }

    private CovariateFile createCovariateFile(int idx) {
        CovariateFile covariate = new CovariateFile("name" + idx, false, false, "info" + idx);
        CovariateSubFile covariateSubFile = new CovariateSubFile(covariate, null, "file" + idx);
        covariate.setFiles(Arrays.asList(covariateSubFile));
        covariateFileDao.save(covariate);
        return covariate;
    }
}
