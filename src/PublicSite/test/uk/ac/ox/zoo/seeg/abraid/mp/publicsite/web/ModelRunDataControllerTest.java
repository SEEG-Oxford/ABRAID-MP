package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDownloadDiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelingLocationPrecisionAdjuster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for ModelRunDataController.
 */
public class ModelRunDataControllerTest {
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
        ModelRunDataController controller = new ModelRunDataController(modelRunService, createNoopAdjuster());

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
        ModelRunDataController controller = new ModelRunDataController(modelRunService, createNoopAdjuster());

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
        ModelRunDataController controller = new ModelRunDataController(modelRunService, createNoopAdjuster());

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
        ModelRunDataController controller = new ModelRunDataController(modelRunService, createNoopAdjuster());

        // Act
        ResponseEntity response = controller.getInputDiseaseOccurrences(name);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        WrappedList<JsonDownloadDiseaseOccurrence> body =
                (WrappedList<JsonDownloadDiseaseOccurrence>) response.getBody();
        assertThat(body.getList()).isEmpty();
    }

    private ModelRunService mockModelRunService(String name, ModelRun modelRun) {
        ModelRunService modelRunService = mock(ModelRunService.class);
        when(modelRunService.getModelRunByName(name)).thenReturn(modelRun);
        return modelRunService;
    }

    private ModelRun mockCompletedModelRunWithOccurrences(List<DiseaseOccurrence> occurrences) {
        ModelRun modelRun = mock(ModelRun.class);
        when(modelRun.getStatus()).thenReturn(ModelRunStatus.COMPLETED);
        when(modelRun.getInputDiseaseOccurrences()).thenReturn(occurrences);
        return modelRun;
    }

    private ModelingLocationPrecisionAdjuster createNoopAdjuster() {
        ModelingLocationPrecisionAdjuster adjuster = mock(ModelingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(anyInt(), anyString())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) invocationOnMock.getArguments()[0];
            }
        });
        return adjuster;
    }
}
