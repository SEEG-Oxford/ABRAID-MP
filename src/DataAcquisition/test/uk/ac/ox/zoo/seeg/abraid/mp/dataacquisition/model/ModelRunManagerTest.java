package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.AbstractDataAcquisitionSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.diseaseextent.DiseaseExtentGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings.WeightingsCalculator;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests the ModelRunManager.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunManagerTest extends AbstractDataAcquisitionSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Before
    public void setFixedTime() {
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void modelPrepShouldRunWhenLastModelRunPrepDateIsNull() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setLastModelRunPrepDate(null);

        ModelRunManager target = createModelRunGateKeeper();

        // Act
        target.prepareForAndRequestModelRun(diseaseGroupId);

        // Assert
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isNotNull();
    }

    @Test
    public void modelPrepShouldRunWhenAWeekHasElapsed() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime lastModelRunPrepDate = DateTime.now().minusDays(7);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);

        ModelRunManager target = createModelRunGateKeeper();

        // Act
        target.prepareForAndRequestModelRun(diseaseGroupId);

        // Assert
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isNotNull();
    }

    @Test
    public void modelPrepShouldNotRunWhenAWeekHasNotPassed() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        DateTime lastModelRunPrepDate = DateTime.now().minusDays(1);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);

        ModelRunManager target = createModelRunGateKeeper();

        // Act
        target.prepareForAndRequestModelRun(diseaseGroupId);

        // Assert
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isEqualTo(lastModelRunPrepDate);
    }

    private ModelRunManager createModelRunGateKeeper() {
        return new ModelRunManager(
                new ModelRunGatekeeper(),
                new LastModelRunPrepDateManager(diseaseService),
                new DiseaseExtentGenerator(diseaseService),
                new WeightingsCalculator(diseaseService),
                mock(ModelRunRequester.class));
    }
}
