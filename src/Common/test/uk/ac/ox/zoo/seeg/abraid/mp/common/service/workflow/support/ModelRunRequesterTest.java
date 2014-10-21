package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.net.URI;
import java.util.Arrays;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@b:1245/path")), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class));
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
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class));
    }
}
