package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import net.lingala.zip4j.exception.ZipException;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        CovariateService covariateService = mock(CovariateService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(mock(DiseaseGroup.class));
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class), anyListOf(CovariateFile.class), anyString())).thenReturn(mock(JsonModelRunResponse.class));

        ModelRunService runService = mock(ModelRunService.class);
        when(runService.getModelRunRequestServersByUsage()).thenReturn(Arrays.asList("a", "c", "b"));
        ModelRunRequester target = new ModelRunRequester(webService, covariateService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path", "http://api:key@b:1245/path", "http://api:key@c:1245/path"});

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class),  anyListOf(CovariateFile.class), anyString());
    }

    @Test
    public void requestModelRunUsesFirstNewModelWrapper() throws Exception {
        // Arrange
        CovariateService covariateService = mock(CovariateService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(mock(DiseaseGroup.class));
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class),  anyListOf(CovariateFile.class), anyString())).thenReturn(mock(JsonModelRunResponse.class));

        ModelRunService runService = mock(ModelRunService.class);
        when(runService.getModelRunRequestServersByUsage()).thenReturn(Arrays.asList("c", "b"));
        ModelRunRequester target = new ModelRunRequester(webService, covariateService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path", "http://api:key@b:1245/path", "http://api:key@c:1245/path", "http://api:key@d:1245/path"});

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class),  anyListOf(CovariateFile.class), anyString());
    }

    @Test
    public void requestModelRunSavesTheInputOccurrencesForAutomaticModelRun() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, null);

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class)), null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getInputDiseaseOccurrences()).hasSize(3);
    }

    @Test
    public void requestModelRunDoesNotSaveTheInputOccurrencesForManualModelRun() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);

        ModelRunRequester target = createMockModelRunRequester(runService, 87, false, null);

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class)), null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getInputDiseaseOccurrences()).isNull();
    }

    @Test
    public void requestModelRunSavesTheInputExtent() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        List<AdminUnitDiseaseExtentClass> extent = Arrays.asList(
                createMockAdminUnitDiseaseExtentClass(), createMockAdminUnitDiseaseExtentClass(),
                createMockAdminUnitDiseaseExtentClass(), createMockAdminUnitDiseaseExtentClass()
        );
        when(diseaseService.getDiseaseExtentByDiseaseGroupId(87)).thenReturn(extent);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService);

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getInputDiseaseExtent()).hasSize(4);
    }

    @Test
    public void requestModelRunSavesCorrectOccurrenceRangeDates() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService);
        DiseaseOccurrence oldest = mock(DiseaseOccurrence.class);
        DateTime oldDate = DateTime.parse("2013-02-27T08:06:46.000Z");
        when(oldest.getOccurrenceDate()).thenReturn(oldDate);
        DiseaseOccurrence newest = mock(DiseaseOccurrence.class);
        DateTime newDate = DateTime.parse("2014-02-27T08:06:46.000Z");
        when(newest.getOccurrenceDate()).thenReturn(newDate);
        List<DiseaseOccurrence> occurrencesForModelRun = Arrays.asList(newest, oldest);

        // Act
        target.requestModelRun(87, occurrencesForModelRun, null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getOccurrenceDataRangeStartDate()).isEqualTo(oldDate);
        assertThat(value.getOccurrenceDataRangeEndDate()).isEqualTo(newDate);
    }

    private AdminUnitDiseaseExtentClass createMockAdminUnitDiseaseExtentClass() {
        AdminUnitDiseaseExtentClass mock = mock(AdminUnitDiseaseExtentClass.class);
        when(mock.getAdminUnitGlobalOrTropical()).thenReturn(mock(AdminUnitGlobalOrTropical.class));
        when(mock.getDiseaseExtentClass()).thenReturn(mock(DiseaseExtentClass.class));
        return mock;
    }

    private ModelRunRequester createMockModelRunRequester(ModelRunService runService, int diseaseGroupId, boolean automaticRuns, DiseaseService mockDiseaseService) throws IOException, ZipException {
        CovariateService covariateService = mock(CovariateService.class);
        DiseaseService diseaseService = mockDiseaseService == null ? mock(DiseaseService.class) : mockDiseaseService;
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(diseaseGroupId).isAutomaticModelRunsEnabled()).thenReturn(automaticRuns);
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(DiseaseGroup.class), anyListOf(DiseaseOccurrence.class), anyMapOf(Integer.class, Integer.class),  anyListOf(CovariateFile.class), anyString())).thenReturn(mock(JsonModelRunResponse.class));

        when(runService.getModelRunRequestServersByUsage()).thenReturn(new ArrayList<String>());
        return new ModelRunRequester(webService, covariateService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path"});
    }
}
