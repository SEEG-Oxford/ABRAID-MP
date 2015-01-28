package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EffectCurveCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvEffectCurveCovariateInfluence;

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
    private EffectCurveCovariateInfluenceDao effectCurveCovariateInfluenceDao;

    @Test
    public void canSaveAndReload() {
        // Arrange
        ModelRun modelRun = createModelRun("foo");
        EffectCurveCovariateInfluence expectation = createEffectCurveCovariateInfluence("a", modelRun);

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
        assertThat(result.getCovariateFilePath()).isEqualTo(expectation.getCovariateFilePath());
        assertThat(result.getCovariateDisplayName()).isEqualTo(expectation.getCovariateDisplayName());
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
        effectCurveCovariateInfluenceDao.save(createEffectCurveCovariateInfluence("a", run1));
        effectCurveCovariateInfluenceDao.save(createEffectCurveCovariateInfluence("a", run2));
        effectCurveCovariateInfluenceDao.save(createEffectCurveCovariateInfluence("b", run1));

        // Act
        List<EffectCurveCovariateInfluence> results1 =
                effectCurveCovariateInfluenceDao.getEffectCurveCovariateInfluencesForModelRun(run1);
        List<EffectCurveCovariateInfluence> results2 =
                effectCurveCovariateInfluenceDao.getEffectCurveCovariateInfluencesForModelRun(run2);

        // Assert
        assertThat(results1).hasSize(2);
        assertThat(results2).hasSize(1);
    }

    private EffectCurveCovariateInfluence createEffectCurveCovariateInfluence(String name, ModelRun modelRun) {
        CsvEffectCurveCovariateInfluence dto = new CsvEffectCurveCovariateInfluence();
        dto.setCovariateFilePath(name);
        dto.setCovariateDisplayName("2");
        dto.setMeanInfluence(3.0);
        dto.setLowerQuantile(4.0);
        dto.setUpperQuantile(5.0);
        dto.setCovariateValue(6.0);

        return new EffectCurveCovariateInfluence(dto, modelRun);
    }

    private ModelRun createModelRun(String name) {
        ModelRun run = new ModelRun(name, 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        modelRunDao.save(run);
        return run;
    }
}
