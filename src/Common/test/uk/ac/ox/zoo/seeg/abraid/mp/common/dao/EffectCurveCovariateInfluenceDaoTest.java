package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvEffectCurveCovariateInfluence;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the EffectCurveCovariateInfluenceDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class EffectCurveCovariateInfluenceDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

    @Autowired
    private CovariateFileDao covariateFileDao;

    @Autowired
    private EffectCurveCovariateInfluenceDao effectCurveCovariateInfluenceDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void canSaveAndReload() {
        // Arrange
        ModelRun modelRun = createModelRun("foo");
        CovariateFile covariateFile = createCovariateFile(1);
        EffectCurveCovariateInfluence expectation = createEffectCurveCovariateInfluence(covariateFile, modelRun);

        // Act
        effectCurveCovariateInfluenceDao.save(expectation);

        // Assert
        Integer id = expectation.getId();
        assertThat(id).isNotNull();
        flushAndClear();
        EffectCurveCovariateInfluence result = effectCurveCovariateInfluenceDao.getById(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getModelRun()).isEqualTo(modelRun);
        assertThat(result.getCovariateFile()).isEqualTo(expectation.getCovariateFile());
        assertThat(result.getMeanInfluence()).isEqualTo(expectation.getMeanInfluence());
        assertThat(result.getLowerQuantile()).isEqualTo(expectation.getLowerQuantile());
        assertThat(result.getUpperQuantile()).isEqualTo(expectation.getUpperQuantile());
        assertThat(result.getCovariateValue()).isEqualTo(expectation.getCovariateValue());
    }

    @Test
    public void getCovariateInfluenceForModelRunReturnsCorrectSubset() {
        // Arrange
        ModelRun run1 = createModelRun("foo1");
        ModelRun run2 = createModelRun("foo2");
        CovariateFile covariate1 = createCovariateFile(1);
        CovariateFile covariate2 = createCovariateFile(2);
        effectCurveCovariateInfluenceDao.save(createEffectCurveCovariateInfluence(covariate1, run1));
        effectCurveCovariateInfluenceDao.save(createEffectCurveCovariateInfluence(covariate1, run2));
        effectCurveCovariateInfluenceDao.save(createEffectCurveCovariateInfluence(covariate2, run1));

        // Act
        List<EffectCurveCovariateInfluence> results1 =
                effectCurveCovariateInfluenceDao.getEffectCurveCovariateInfluencesForModelRun(run1);
        List<EffectCurveCovariateInfluence> results2 =
                effectCurveCovariateInfluenceDao.getEffectCurveCovariateInfluencesForModelRun(run2);

        // Assert
        assertThat(results1).hasSize(2);
        assertThat(results2).hasSize(1);
    }

    private EffectCurveCovariateInfluence createEffectCurveCovariateInfluence(CovariateFile covariateFile, ModelRun modelRun) {
        CsvEffectCurveCovariateInfluence dto = new CsvEffectCurveCovariateInfluence();
        dto.setMeanInfluence(3.0);
        dto.setLowerQuantile(4.0);
        dto.setUpperQuantile(5.0);
        dto.setCovariateValue(6.0);

        return new EffectCurveCovariateInfluence(covariateFile, dto, modelRun);
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
