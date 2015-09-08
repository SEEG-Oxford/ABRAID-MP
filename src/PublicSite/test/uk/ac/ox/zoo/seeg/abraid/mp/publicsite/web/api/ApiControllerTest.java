package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.api;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1.JsonApiModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the API controller.
 * Copyright (c) 2014 University of Oxford
 */
public class ApiControllerTest {
    @Test
    public void showPageReturnsCorrectData() {
        // Arrange
        ModelRunService modelRunService = mock(ModelRunService.class);
        List<ModelRun> modelRuns = Arrays.asList(mockRun("a"), mockRun("b"));
        String name = "name";
        int diseaseGroupId = 1;
        Date minDate = new LocalDate("2015-01-01").toDate();
        Date maxDate = new LocalDate("2015-01-02").toDate();
        when(modelRunService.getFilteredModelRuns(eq(name), eq(diseaseGroupId), eq(new LocalDate(minDate)), eq(new LocalDate(maxDate)))).thenReturn(modelRuns);
        ApiController target = new ApiController(modelRunService);

        // Act
        ResponseEntity<WrappedList<JsonApiModelRun>> results = target.getModelRuns(name, diseaseGroupId, minDate, maxDate);

        // Assert
        assertThat(results.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(results.getBody().getList()).hasSize(2);
        assertThat(results.getBody().getList().get(0).getName()).isEqualTo("a");
        assertThat(results.getBody().getList().get(1).getName()).isEqualTo("b");
    }

    @Test
    public void showPageReturnsCorrectDataWithNoFilter() {
        // Arrange
        ModelRunService modelRunService = mock(ModelRunService.class);
        List<ModelRun> modelRuns = Arrays.asList(mockRun("a"), mockRun("b"));

        when(modelRunService.getFilteredModelRuns(null, null, null, null)).thenReturn(modelRuns);
        ApiController target = new ApiController(modelRunService);

        // Act
        ResponseEntity<WrappedList<JsonApiModelRun>> results = target.getModelRuns(null, null, null, null);

        // Assert
        assertThat(results.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(results.getBody().getList()).hasSize(2);
        assertThat(results.getBody().getList().get(0).getName()).isEqualTo("a");
        assertThat(results.getBody().getList().get(1).getName()).isEqualTo("b");
    }

    private ModelRun mockRun(String name) {
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
        when(modelRun.getName()).thenReturn(name);
        when(modelRun.getRequestDate()).thenReturn(new LocalDate("2015-01-01").toDateTimeAtStartOfDay());
        when(modelRun.getResponseDate()).thenReturn(new LocalDate("2015-01-02").toDateTimeAtStartOfDay());
        when(modelRun.getOccurrenceDataRangeStartDate()).thenReturn(new LocalDate("2015-01-03").toDateTimeAtStartOfDay());
        when(modelRun.getOccurrenceDataRangeEndDate()).thenReturn(new LocalDate("2015-01-04").toDateTimeAtStartOfDay());
        when(modelRun.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(modelRun.getCovariateInfluences()).thenReturn(Arrays.asList(covariateInfluence1, covariateInfluence2));
        return modelRun;
    }
}
