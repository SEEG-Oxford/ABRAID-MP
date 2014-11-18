package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ModelRunRequester.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequesterTest {

    @Test
    public void requestModelRunUsesLeastBusyModelWrapper() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(mock(DiseaseGroup.class));
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class))).thenReturn(mock(JsonModelRunResponse.class));

        ModelRunService runService = mock(ModelRunService.class);
        when(runService.getModelRunRequestServersByUsage()).thenReturn(Arrays.asList("a", "c", "b"));
        ModelRunRequester target = new ModelRunRequester(webService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path", "http://api:key@b:1245/path", "http://api:key@c:1245/path"});

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class));
    }

    @Test
    public void requestModelRunUsesFirstNewModelWrapper() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(mock(DiseaseGroup.class));
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class))).thenReturn(mock(JsonModelRunResponse.class));

        ModelRunService runService = mock(ModelRunService.class);
        when(runService.getModelRunRequestServersByUsage()).thenReturn(Arrays.asList("c", "b"));
        ModelRunRequester target = new ModelRunRequester(webService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path", "http://api:key@b:1245/path", "http://api:key@c:1245/path", "http://api:key@d:1245/path"});

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class));
    }

    @Test
    public void requestModelRunSavesTheInputOccurrencesForAutomaticModelRun() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true);

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class)), null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getInputDiseaseOccurrences()).hasSize(3);
    }

    @Test
    public void requestModelRunDoesNotSavesTheInputOccurrencesForManualModelRun() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);

        ModelRunRequester target = createMockModelRunRequester(runService, 87, false);

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class)), null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getInputDiseaseOccurrences()).isNull();
    }

    private ModelRunRequester createMockModelRunRequester(ModelRunService runService, int diseaseGroupId, boolean automaticRuns) {
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(diseaseGroupId).isAutomaticModelRunsEnabled()).thenReturn(automaticRuns);
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class))).thenReturn(mock(JsonModelRunResponse.class));

        when(runService.getModelRunRequestServersByUsage()).thenReturn(new ArrayList<String>());
        return new ModelRunRequester(webService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path"});
    }
}
