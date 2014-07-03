package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the ModelRunDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

    @Test
    public void saveAndReloadModelRunByName() {
        // Arrange
        String name = "test name";
        int diseaseGroupId = 87;
        DateTime requestDate = DateTime.now().minusHours(4);
        DateTime responseDate = DateTime.now();
        String outputText = "output text";
        String errorText = "error text";

        ModelRun modelRun = new ModelRun(name, diseaseGroupId, requestDate);
        modelRun.setResponseDate(responseDate);
        modelRun.setOutputText(outputText);
        modelRun.setErrorText(errorText);
        modelRunDao.save(modelRun);

        // Act
        flushAndClear();

        // Assert
        modelRun = modelRunDao.getByName(name);
        assertThat(modelRun).isNotNull();
        assertThat(modelRun.getName()).isEqualTo(name);
        assertThat(modelRun.getStatus()).isEqualTo(ModelRunStatus.IN_PROGRESS);
        assertThat(modelRun.getDiseaseGroupId()).isEqualTo(diseaseGroupId);
        assertThat(modelRun.getRequestDate()).isEqualTo(requestDate);
        assertThat(modelRun.getOutputText()).isEqualTo(outputText);
        assertThat(modelRun.getErrorText()).isEqualTo(errorText);
    }

    @Test
    public void saveAndLoadCascadesToSubmodelStatistics() {
        // Arrange
        ModelRun run = createModelRun("name");
        modelRunDao.save(run);
        flushAndClear();
        run = modelRunDao.getByName("name");

        List<SubmodelStatistic> submodelStatistics = new ArrayList<>();
        submodelStatistics.add(createSubmodelStatistic(run));
        submodelStatistics.add(createSubmodelStatistic(run));
        submodelStatistics.add(createSubmodelStatistic(run));
        run.setSubmodelStatistics(submodelStatistics);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getSubmodelStatistics()).hasSize(3);
    }

    @Test
    public void saveAndLoadCascadesToCovariateInfluence() {
        // Arrange
        ModelRun run = createModelRun("name");
        modelRunDao.save(run);
        flushAndClear();
        run = modelRunDao.getByName("name");

        List<CovariateInfluence> covariateInfluences = new ArrayList<>();
        covariateInfluences.add(createCovariateInfluence("a", run));
        covariateInfluences.add(createCovariateInfluence("b", run));
        covariateInfluences.add(createCovariateInfluence("c", run));
        run.setCovariateInfluences(covariateInfluences);
        modelRunDao.save(run);
        flushAndClear();

        // Act
        run = modelRunDao.getByName("name");

        // Assert
        assertThat(run.getCovariateInfluences()).hasSize(3);
    }

    private SubmodelStatistic createSubmodelStatistic(ModelRun modelRun) {
        SubmodelStatistic submodelStatistic = new SubmodelStatistic();
        submodelStatistic.setModelRun(modelRun);
        return submodelStatistic;
    }

    private CovariateInfluence createCovariateInfluence(String covariateName, ModelRun modelRun) {
        CovariateInfluence covariateInfluence = new CovariateInfluence();
        covariateInfluence.setModelRun(modelRun);
        covariateInfluence.setCovariateName(covariateName);
        return covariateInfluence;
    }

    private ModelRun createModelRun(String name) {
        return new ModelRun(name, 87, DateTime.now());
    }
}
