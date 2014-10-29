package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunStatistics;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the ModelRunDetails controller.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunDetailsControllerTest {

    @Test
    public void getModelRunSummaryStatisticsReturnsBadRequestIfModelRunDoesNotExist() throws Exception {
        // Arrange
        String name = "modelRun1";
        ModelRunService modelRunService = mockModelRunService(name, null);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getModelRunSummaryStatistics(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getModelRunSummaryStatisticsReturnsBadRequestIfModelRunIsNotComplete() throws Exception {
        // Arrange
        String name = "modelRun2";

        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.FAILED);
        ModelRunService modelRunService = mockModelRunService(name, modelRun);

        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getModelRunSummaryStatistics(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getModelRunSummaryStatisticsReturnsEmptyJsonIfNoSubmodelStatistics() throws Exception {
        // Arrange
        String name = "modelRun3";
        ModelRun modelRun = mockCompletedModelRunWithStatistics(new ArrayList<SubmodelStatistic>());
        ModelRunService modelRunService = mockModelRunService(name, modelRun);

        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getModelRunSummaryStatistics(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getClass()).isEqualTo(JsonModelRunStatistics.class);
        assertNullBody(response);
    }

    @Test
    public void getModelRunSummaryStatisticsReturnsExpectedJson() throws Exception {
        // Arrange
        String name = "modelRun4";
        ModelRun modelRun = mockCompletedModelRunWithStatistics(Arrays.asList(
                new SubmodelStatistic(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0),
                new SubmodelStatistic(3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
        ));
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getModelRunSummaryStatistics(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getClass()).isEqualTo(JsonModelRunStatistics.class);
        assertExpectedBody(response);
    }

    @Test
    public void getCovariateInfluencesReturnsBadRequestIfModelDoesNotExist() throws Exception {
        // Arrange
        String name = "modelRun5";
        ModelRunService modelRunService = mockModelRunService(name, null);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getCovariateInfluencesReturnsBadRequestIfModelIsNotComplete() throws Exception {
        // Arrange
        String name = "modelRun6";

        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.IN_PROGRESS);
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getCovariateInfluencesReturnsEmptyJsonIfNoCovariateInfluences() throws Exception {
        // Arrange
        String name = "modelRun7";
        ModelRun modelRun = mockCompletedModelRunWithCovariates(new ArrayList<CovariateInfluence>());
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).isEqualTo("[]");
    }

    @Test
    public void getCovariateInfluencesReturnsExpectedJson() throws Exception {
        // Arrange
        String name = "modelRun7";
        CovariateInfluence covariateInfluence = new CovariateInfluence("Name", 12.3);
        ModelRun modelRun = mockCompletedModelRunWithCovariates(Arrays.asList(covariateInfluence));
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<JsonCovariateInfluence> body = (List<JsonCovariateInfluence>) response.getBody();
        assertThat(body).hasSize(1);
        assertThat(body.get(0).getName()).isEqualTo("Name");
        assertThat(body.get(0).getMeanInfluence()).isEqualTo(12.3);

    }

    private ModelRun mockCompletedModelRunWithStatistics(List<SubmodelStatistic> submodelStatistics) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getSubmodelStatistics()).thenReturn(submodelStatistics);
        return modelRun;
    }

    private ModelRun mockCompletedModelRunWithCovariates(List<CovariateInfluence> covariateInfluences) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getCovariateInfluences()).thenReturn(covariateInfluences);
        return modelRun;
    }

    private ModelRunService mockModelRunService(String name, ModelRun modelRun) {
        ModelRunService modelRunService = mock(ModelRunService.class);
        when(modelRunService.getModelRunByName(name)).thenReturn(modelRun);
        return modelRunService;
    }

    private void assertNullBody(ResponseEntity response) {
        JsonModelRunStatistics body = (JsonModelRunStatistics) response.getBody();
        assertThat(body.getDeviance()).isNull();
        assertThat(body.getDevianceSd()).isNull();
        assertThat(body.getRmse()).isNull();
        assertThat(body.getRmseSd()).isNull();
        assertThat(body.getKappa()).isNull();
        assertThat(body.getKappaSd()).isNull();
        assertThat(body.getAuc()).isNull();
        assertThat(body.getAucSd()).isNull();
        assertThat(body.getSens()).isNull();
        assertThat(body.getSensSd()).isNull();
        assertThat(body.getSpec()).isNull();
        assertThat(body.getSpecSd()).isNull();
        assertThat(body.getPcc()).isNull();
        assertThat(body.getPccSd()).isNull();
        assertThat(body.getThreshold()).isNull();
        assertThat(body.getThresholdSd()).isNull();
    }

    private void assertExpectedBody(ResponseEntity response) {
        JsonModelRunStatistics body = (JsonModelRunStatistics) response.getBody();
        assertThat(body.getDeviance()).isEqualTo(2.0);
        assertThat(body.getRmse()).isEqualTo(3.0);
        assertThat(body.getKappa()).isEqualTo(4.0);
        assertThat(body.getAuc()).isEqualTo(5.0);
        assertThat(body.getSens()).isEqualTo(6.0);
        assertThat(body.getSpec()).isEqualTo(7.0);
        assertThat(body.getPcc()).isEqualTo(8.0);
        assertThat(body.getThreshold()).isEqualTo(9.0);

        assertSd(body.getDevianceSd());
        assertSd(body.getRmseSd());
        assertSd(body.getKappaSd());
        assertSd(body.getAucSd());
        assertSd(body.getSensSd());
        assertSd(body.getSpecSd());
        assertSd(body.getPccSd());
        assertSd(body.getThresholdSd());
    }

    private void assertSd(double sd) {
        assertThat(sd).isEqualTo(Math.sqrt(2));
    }
}
