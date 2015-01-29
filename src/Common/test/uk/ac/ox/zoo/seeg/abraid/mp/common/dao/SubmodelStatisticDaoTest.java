package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.csv.CsvSubmodelStatistic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the SubmodelStatisticDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class SubmodelStatisticDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

    @Autowired
    private SubmodelStatisticDao submodelStatisticDao;

    @Test
    public void canSaveAndReload() {
        // Arrange
        ModelRun modelRun = createModelRun("foo");
        SubmodelStatistic expectation = createSubmodelStatistic(modelRun);

        // Act
        submodelStatisticDao.save(expectation);

        // Assert
        Integer id = expectation.getId();
        assertThat(id).isNotNull();
        flushAndClear();
        SubmodelStatistic result = submodelStatisticDao.getById(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getModelRun()).isEqualTo(modelRun);
        assertThat(result.getKappa()).isEqualTo(expectation.getKappa());
        assertThat(result.getAreaUnderCurve()).isEqualTo(expectation.getAreaUnderCurve());
        assertThat(result.getSensitivity()).isEqualTo(expectation.getSensitivity());
        assertThat(result.getSpecificity()).isEqualTo(expectation.getSpecificity());
        assertThat(result.getProportionCorrectlyClassified()).isEqualTo(expectation.getProportionCorrectlyClassified());
        assertThat(result.getKappaStandardDeviation()).isEqualTo(expectation.getKappaStandardDeviation());
        assertThat(result.getAreaUnderCurveStandardDeviation()).isEqualTo(expectation.getAreaUnderCurveStandardDeviation());
        assertThat(result.getSensitivityStandardDeviation()).isEqualTo(expectation.getSensitivityStandardDeviation());
        assertThat(result.getSpecificityStandardDeviation()).isEqualTo(expectation.getSpecificityStandardDeviation());
        assertThat(result.getProportionCorrectlyClassifiedStandardDeviation()).isEqualTo(expectation.getProportionCorrectlyClassifiedStandardDeviation());
    }

    @Test
    public void getSubmodelStatisticsForModelRunReturnsCorrectSubset() {
        // Arrange
        ModelRun run1 = createModelRun("foo1");
        ModelRun run2 = createModelRun("foo2");
        submodelStatisticDao.save(createSubmodelStatistic(run1));
        submodelStatisticDao.save(createSubmodelStatistic(run2));
        submodelStatisticDao.save(createSubmodelStatistic(run1));

        // Act
        List<SubmodelStatistic> results1 = submodelStatisticDao.getSubmodelStatisticsForModelRun(run1);
        List<SubmodelStatistic> results2 = submodelStatisticDao.getSubmodelStatisticsForModelRun(run2);

        // Assert
        assertThat(results1).hasSize(2);
        assertThat(results2).hasSize(1);
    }

    private SubmodelStatistic createSubmodelStatistic(ModelRun modelRun) {
        CsvSubmodelStatistic dto = new CsvSubmodelStatistic();
        dto.setKappa(3.0);
        dto.setAreaUnderCurve(4.0);
        dto.setSensitivity(5.0);
        dto.setSpecificity(6.0);
        dto.setProportionCorrectlyClassified(7.0);
        dto.setKappaStandardDeviation(8.0);
        dto.setAreaUnderCurveStandardDeviation(9.0);
        dto.setSensitivityStandardDeviation(10.0);
        dto.setSpecificityStandardDeviation(11.0);
        dto.setProportionCorrectlyClassifiedStandardDeviation(12.0);
        return new SubmodelStatistic(dto, modelRun);
    }

    private ModelRun createModelRun(String name) {
        ModelRun run = new ModelRun(name, 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        modelRunDao.save(run);
        return run;
    }
}
