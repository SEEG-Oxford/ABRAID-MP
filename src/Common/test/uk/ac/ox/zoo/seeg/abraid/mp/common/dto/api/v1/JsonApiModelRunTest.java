package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import org.joda.time.LocalDate;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonApiCovariateInfluence.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiModelRunTest {
    @Test
    public void constructorBindsFieldsCorrectly() {
        // Arrange
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getId()).thenReturn(123);
        when(diseaseGroup.getPublicName()).thenReturn("dg 123");

        CovariateInfluence covariateInfluence1 = mock(CovariateInfluence.class);
        when(covariateInfluence1.getMeanInfluence()).thenReturn(1.5);
        when(covariateInfluence1.getCovariateFile()).thenReturn(mock(CovariateFile.class));
        when(covariateInfluence1.getCovariateFile().getName()).thenReturn("c1");

        CovariateInfluence covariateInfluence2 = mock(CovariateInfluence.class);
        when(covariateInfluence2.getMeanInfluence()).thenReturn(2.5);
        when(covariateInfluence2.getCovariateFile()).thenReturn(mock(CovariateFile.class));
        when(covariateInfluence2.getCovariateFile().getName()).thenReturn("c2");

        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getName()).thenReturn("run_a");
        when(modelRun.getRequestDate()).thenReturn(new LocalDate("2015-01-01").toDateTimeAtStartOfDay());
        when(modelRun.getResponseDate()).thenReturn(new LocalDate("2015-01-02").toDateTimeAtStartOfDay());
        when(modelRun.getOccurrenceDataRangeStartDate()).thenReturn(new LocalDate("2015-01-03").toDateTimeAtStartOfDay());
        when(modelRun.getOccurrenceDataRangeEndDate()).thenReturn(new LocalDate("2015-01-04").toDateTimeAtStartOfDay());
        when(modelRun.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(modelRun.getCovariateInfluences()).thenReturn(Arrays.asList(covariateInfluence1, covariateInfluence2));

        // Act
        JsonApiModelRun dto = new JsonApiModelRun(modelRun);

        // Assert
        assertThat(dto.getName()).isEqualTo("run_a");
        assertThat(dto.getDiseaseGroup().getId()).isEqualTo(123);
        assertThat(dto.getDiseaseGroup().getName()).isEqualTo("dg 123");
        assertThat(dto.getTriggerDate()).isEqualTo(new LocalDate("2015-01-01").toDateTimeAtStartOfDay());
        assertThat(dto.getCompletionDate()).isEqualTo(new LocalDate("2015-01-02").toDateTimeAtStartOfDay());
        assertThat(dto.getCovariateInfluences()).hasSize(2);
        assertThat(dto.getCovariateInfluences().get(0).getName()).isEqualTo("c1");
        assertThat(dto.getCovariateInfluences().get(0).getMeanInfluence()).isEqualTo(1.5);
        assertThat(dto.getCovariateInfluences().get(1).getName()).isEqualTo("c2");
        assertThat(dto.getCovariateInfluences().get(1).getMeanInfluence()).isEqualTo(2.5);
        assertThat(dto.getOccurrenceDateRange().getStart()).isEqualTo(new LocalDate("2015-01-03").toDateTimeAtStartOfDay());
        assertThat(dto.getOccurrenceDateRange().getEnd()).isEqualTo(new LocalDate("2015-01-04").toDateTimeAtStartOfDay());
        assertThat(dto.getPredictionData()).isEqualTo("/atlas/results/run_a_mean.tif");
        assertThat(dto.getExtentData()).isEqualTo("/atlas/results/run_a_extent.tif");
    }
}
