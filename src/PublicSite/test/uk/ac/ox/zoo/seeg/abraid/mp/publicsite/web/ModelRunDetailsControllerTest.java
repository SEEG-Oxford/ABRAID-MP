package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
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
                new SubmodelStatistic(4.0, 5.0, 6.0, 7.0, 8.0),
                new SubmodelStatistic(6.0, 7.0, 8.0, 9.0, 10.0)
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
        ModelRun modelRun = mockCompletedModelRunWithCovariates(new ArrayList<CovariateInfluence>(), new ArrayList<EffectCurveCovariateInfluence>());
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
        CovariateFile covariateFile = mock(CovariateFile.class);
        when(covariateFile.getName()).thenReturn("Name");
        when(covariateFile.getId()).thenReturn(4);
        when(covariateFile.getDiscrete()).thenReturn(true);
        CovariateInfluence covariateInfluence = new CovariateInfluence(covariateFile, 12.3);
        EffectCurveCovariateInfluence effectCurveCovariateInfluence = new EffectCurveCovariateInfluence();
        effectCurveCovariateInfluence.setCovariateFile(covariateFile);
        effectCurveCovariateInfluence.setCovariateValue(3.21);
        EffectCurveCovariateInfluence badEffectCurveCovariateInfluence = new EffectCurveCovariateInfluence();
        badEffectCurveCovariateInfluence.setCovariateFile(mock(CovariateFile.class));
        badEffectCurveCovariateInfluence.setCovariateValue(3.14);
        ModelRun modelRun = mockCompletedModelRunWithCovariates(Arrays.asList(covariateInfluence), Arrays.asList(effectCurveCovariateInfluence, badEffectCurveCovariateInfluence));
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<JsonCovariateInfluence> body = (List<JsonCovariateInfluence>) response.getBody();
        assertThat(body).hasSize(1);
        assertThat(body.get(0).getName()).isEqualTo("Name");
        assertThat(body.get(0).getDiscrete()).isEqualTo(true);
        assertThat(body.get(0).getMeanInfluence()).isEqualTo(12.3);
        assertThat(body.get(0).getEffectCurve()).hasSize(1);
        assertThat(body.get(0).getEffectCurve().iterator().next().getCovariateValue()).isEqualTo(3.21);

    }

    @Test
    public void getEffectCurveCovariateInfluencesReturnsBadRequestIfModelDoesNotExist() throws Exception {
        // Arrange
        String name = "modelRun5";
        ModelRunService modelRunService = mockModelRunService(name, null);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getEffectCurveCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getEffectCurveCovariateInfluencesReturnsBadRequestIfModelIsNotComplete() throws Exception {
        // Arrange
        String name = "modelRun6";

        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.IN_PROGRESS);
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getEffectCurveCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getEffectCurveCovariateInfluencesReturnsEmptyDTOIfNoEffectCurveCovariateInfluences() throws Exception {
        // Arrange
        String name = "modelRun7";
        ModelRun modelRun = mockCompletedModelRunWithEffectCurve(new ArrayList<EffectCurveCovariateInfluence>());
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getEffectCurveCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WrappedList<JsonEffectCurveCovariateInfluence> body =
                (WrappedList<JsonEffectCurveCovariateInfluence>) response.getBody();
        assertThat(body.getList()).isEmpty();
    }

    @Test
    public void getEffectCurveCovariateInfluencesReturnsExpectedDTOs() throws Exception {
        // Arrange
        String name = "modelRun7";
        EffectCurveCovariateInfluence covariateInfluence = new EffectCurveCovariateInfluence();
        CovariateFile covariateFile = mock(CovariateFile.class);
        when(covariateFile.getName()).thenReturn("Display name");
        covariateInfluence.setCovariateFile(covariateFile);
        covariateInfluence.setCovariateValue(1.23);
        covariateInfluence.setLowerQuantile(2.3);
        covariateInfluence.setUpperQuantile(3.2);
        covariateInfluence.setMeanInfluence(3.21);
        ModelRun modelRun = mockCompletedModelRunWithEffectCurve(Arrays.asList(covariateInfluence));
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getEffectCurveCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WrappedList<JsonEffectCurveCovariateInfluence> body =
                (WrappedList<JsonEffectCurveCovariateInfluence>) response.getBody();
        assertThat(body.getList()).hasSize(1);
        assertThat(body.getList().get(0).getName()).isEqualTo("Display name");
        assertThat(body.getList().get(0).getMeanInfluence()).isEqualTo(3.21);
        assertThat(body.getList().get(0).getLowerQuantile()).isEqualTo(2.3);
        assertThat(body.getList().get(0).getUpperQuantile()).isEqualTo(3.2);
        assertThat(body.getList().get(0).getCovariateValue()).isEqualTo(1.23);
    }

    @Test
    public void getInputDiseaseOccurrencesReturnsExpectedJson() throws Exception {
        // Arrange
        String name = "modelRun7";
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getLocation()).thenReturn(mock(Location.class));
        when(occurrence.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(occurrence.getLocation().getGeom().getX()).thenReturn(1.0);
        when(occurrence.getLocation().getGeom().getY()).thenReturn(2.0);
        when(occurrence.getFinalWeighting()).thenReturn(3.0);
        when(occurrence.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(occurrence.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);
        when(occurrence.getAlert()).thenReturn(mock(Alert.class));
        when(occurrence.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(occurrence.getAlert().getFeed().getName()).thenReturn("feed");
        when(occurrence.getAlert().getFeed().getProvenance()).thenReturn(mock(Provenance.class));
        when(occurrence.getAlert().getFeed().getProvenance().getName()).thenReturn("provenance");

        ModelRun modelRun = mockCompletedModelRunWithOccurrences(Arrays.asList(occurrence));
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getInputDiseaseOccurrences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WrappedList<JsonDownloadDiseaseOccurrence> body = (WrappedList<JsonDownloadDiseaseOccurrence>) response.getBody();
        assertThat(body.getList()).hasSize(1);
        assertThat(body.getList().get(0).getLongitude()).isEqualTo(1.0);
        assertThat(body.getList().get(0).getLatitude()).isEqualTo(2.0);
        assertThat(body.getList().get(0).getWeight()).isEqualTo(3.0);
        assertThat(body.getList().get(0).getAdmin()).isEqualTo(LocationPrecision.ADMIN1.getModelValue());
        assertThat(body.getList().get(0).getGaul()).isEqualTo("1234");
        assertThat(body.getList().get(0).getProvenance()).isEqualTo("provenance");
        assertThat(body.getList().get(0).getFeed()).isEqualTo("feed");
    }

    @Test
    public void getInputDiseaseOccurrencesReturnsBadRequestIfModelDoesNotExist() throws Exception {
        // Arrange
        String name = "modelRun5";
        ModelRunService modelRunService = mockModelRunService(name, null);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getInputDiseaseOccurrences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getInputDiseaseOccurrencesReturnsBadRequestIfModelIsNotComplete() throws Exception {
        // Arrange
        String name = "modelRun6";

        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.IN_PROGRESS);
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getInputDiseaseOccurrences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void getInputDiseaseOccurrencesReturnsEmptyDTOIfNoInputDiseaseOccurrences() throws Exception {
        // Arrange (eg manual run)
        String name = "modelRun7";
        ModelRun modelRun = mockCompletedModelRunWithOccurrences(new ArrayList<DiseaseOccurrence>());
        ModelRunService modelRunService = mockModelRunService(name, modelRun);
        ModelRunDetailsController controller = new ModelRunDetailsController(modelRunService);

        // Act
        ResponseEntity response = controller.getEffectCurveCovariateInfluences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WrappedList<JsonDownloadDiseaseOccurrence> body =
                (WrappedList<JsonDownloadDiseaseOccurrence>) response.getBody();
        assertThat(body.getList()).isEmpty();
    }

    private ModelRun mockCompletedModelRunWithStatistics(List<SubmodelStatistic> submodelStatistics) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getSubmodelStatistics()).thenReturn(submodelStatistics);
        return modelRun;
    }

    private ModelRun mockCompletedModelRunWithCovariates(List<CovariateInfluence> covariateInfluences, List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getCovariateInfluences()).thenReturn(covariateInfluences);
        when(modelRun.getEffectCurveCovariateInfluences()).thenReturn(effectCurveCovariateInfluences);
        return modelRun;
    }

    private ModelRun mockCompletedModelRunWithEffectCurve(
            List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getEffectCurveCovariateInfluences()).thenReturn(effectCurveCovariateInfluences);
        return modelRun;
    }

    private ModelRun mockCompletedModelRunWithOccurrences(List<DiseaseOccurrence> occurrences) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getInputDiseaseOccurrences()).thenReturn(occurrences);
        return modelRun;
    }

    private ModelRunService mockModelRunService(String name, ModelRun modelRun) {
        ModelRunService modelRunService = mock(ModelRunService.class);
        when(modelRunService.getModelRunByName(name)).thenReturn(modelRun);
        return modelRunService;
    }

    private void assertNullBody(ResponseEntity response) {
        JsonModelRunStatistics body = (JsonModelRunStatistics) response.getBody();
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
    }

    private void assertExpectedBody(ResponseEntity response) {
        JsonModelRunStatistics body = (JsonModelRunStatistics) response.getBody();
        assertThat(body.getKappa()).isEqualTo(5.0);
        assertThat(body.getAuc()).isEqualTo(6.0);
        assertThat(body.getSens()).isEqualTo(7.0);
        assertThat(body.getSpec()).isEqualTo(8.0);
        assertThat(body.getPcc()).isEqualTo(9.0);

        assertSd(body.getKappaSd());
        assertSd(body.getAucSd());
        assertSd(body.getSensSd());
        assertSd(body.getSpecSd());
        assertSd(body.getPccSd());
    }

    private void assertSd(double sd) {
        assertThat(sd).isEqualTo(Math.sqrt(2));
    }
}
