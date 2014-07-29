package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Before;
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

    private ModelRun modelRunDengue1;
    private ModelRun modelRunDengue2;
    private ModelRun modelRunDengue3;
    private ModelRun modelRunDengue4;
    private ModelRun modelRunDengue5;
    private ModelRun modelRunMalarias1;

    @Before
    public void setUp() {
        modelRunDengue1 = createModelRun("dengue 1", 87, ModelRunStatus.IN_PROGRESS, "2014-07-01", null);
        modelRunDengue2 = createModelRun("dengue 2", 87, ModelRunStatus.COMPLETED, "2014-07-01", "2014-07-04");
        modelRunDengue3 = createModelRun("dengue 3", 87, ModelRunStatus.COMPLETED, "2014-07-02", "2014-07-03");
        modelRunDengue4 = createModelRun("dengue 4", 87, ModelRunStatus.IN_PROGRESS, "2014-07-05", null);
        modelRunDengue5 = createModelRun("dengue 5", 87, ModelRunStatus.FAILED, "2014-07-06", "2014-07-05");
        modelRunMalarias1 = createModelRun("malarias 1", 202, ModelRunStatus.COMPLETED, "2014-07-07", "2014-07-08");
    }

    @Test
    public void saveAndReloadModelRunByName() {
        // Arrange
        String name = "test name";
        int diseaseGroupId = 87;
        DateTime requestDate = DateTime.now().minusHours(4);
        DateTime responseDate = DateTime.now();
        String outputText = "output text";
        String errorText = "error text";
        DateTime batchEndDate = DateTime.now().minusHours(6);
        DateTime batchingCompletionDate = DateTime.now().plusHours(2);

        ModelRun modelRun = new ModelRun(name, diseaseGroupId, requestDate);
        modelRun.setResponseDate(responseDate);
        modelRun.setOutputText(outputText);
        modelRun.setErrorText(errorText);
        modelRun.setBatchEndDate(batchEndDate);
        modelRun.setBatchingCompletedDate(batchingCompletionDate);
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
        assertThat(modelRun.getBatchEndDate()).isEqualTo(batchEndDate);
        assertThat(modelRun.getBatchingCompletedDate()).isEqualTo(batchingCompletionDate);
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

    @Test
    public void getLastRequestedModelRunReturnsNullIfNoModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        ModelRun modelRun = modelRunDao.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getLastRequestedModelRunIgnoresIDsAndDoesNotRequireResponseDateToBeSet() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue1);
        modelRunDao.save(modelRunMalarias1);

        // Act
        ModelRun modelRun = modelRunDao.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue4);
    }

    @Test
    public void getLastRequestedModelRunDoesNotRequireResponseDateToBeNull() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue1);

        // Act
        ModelRun modelRun = modelRunDao.getLastRequestedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue5);
    }

    @Test
    public void getLastCompletedModelRunReturnsNullIfNoModelRuns() {
        // Arrange
        int diseaseGroupId = 87;

        // Act
        ModelRun modelRun = modelRunDao.getLastCompletedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getLastCompletedModelRunReturnsNullIfNoCompletedModelRuns() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);
        modelRunDao.save(modelRunDengue1);

        // Act
        ModelRun modelRun = modelRunDao.getLastCompletedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isNull();
    }

    @Test
    public void getLastCompletedModelRunReturnsNonNullIfCompletedModelRuns() {
        // Arrange
        int diseaseGroupId = 87;
        modelRunDao.save(modelRunMalarias1);
        modelRunDao.save(modelRunDengue1);
        modelRunDao.save(modelRunDengue2);
        modelRunDao.save(modelRunDengue3);
        modelRunDao.save(modelRunDengue4);
        modelRunDao.save(modelRunDengue5);

        // Act
        ModelRun modelRun = modelRunDao.getLastCompletedModelRun(diseaseGroupId);

        // Assert
        assertThat(modelRun).isEqualTo(modelRunDengue2);
    }

    @Test
    public void hasBatchingEverCompletedReturnsFalseIfNoBatchCompletedDatesExistForTheDiseaseGroup() {
        // Arrange
        modelRunMalarias1.setBatchingCompletedDate(DateTime.now());
        modelRunDao.save(modelRunMalarias1);

        // Act
        boolean result = modelRunDao.hasBatchingEverCompleted(87);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void hasBatchingEverCompletedReturnsTrueIfAtLeastOneBatchCompletedDateExists() {
        // Arrange
        modelRunDengue3.setBatchingCompletedDate(DateTime.now());
        modelRunDao.save(modelRunDengue3);

        // Act
        boolean result = modelRunDao.hasBatchingEverCompleted(87);

        // Assert
        assertThat(result).isTrue();
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

    private static ModelRun createModelRun(String name, int diseaseGroupId, ModelRunStatus status, String requestDate,
                                    String responseDate) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, new DateTime(requestDate));
        modelRun.setStatus(status);
        modelRun.setResponseDate(new DateTime(responseDate));
        return modelRun;
    }
}
