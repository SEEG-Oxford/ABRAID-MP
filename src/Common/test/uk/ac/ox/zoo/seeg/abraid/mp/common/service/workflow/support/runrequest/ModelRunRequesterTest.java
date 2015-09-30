package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
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
        CovariateService covariateService = mock(CovariateService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ModelRunPackageBuilder modelRunPackageBuilder = mock(ModelRunPackageBuilder.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(87).getAbbreviation()).thenReturn("deng");
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(File.class))).thenReturn(mock(JsonModelRunResponse.class));

        ModelRunService runService = mock(ModelRunService.class);
        when(runService.getModelRunRequestServersByUsage()).thenReturn(Arrays.asList("a", "c", "b"));
        ModelRunRequester target = new ModelRunRequester(webService, modelRunPackageBuilder, covariateService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path", "http://api:key@b:1245/path", "http://api:key@c:1245/path"});

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(File.class));
    }

    @Test
    public void requestModelRunUsesFirstNewModelWrapper() throws Exception {
        // Arrange
        CovariateService covariateService = mock(CovariateService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        ModelRunPackageBuilder modelRunPackageBuilder = mock(ModelRunPackageBuilder.class);
        when(diseaseService.getDiseaseGroupById(87)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(87).getAbbreviation()).thenReturn("deng");
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        when(webService.startRun(any(URI.class), any(File.class))).thenReturn(mock(JsonModelRunResponse.class));

        ModelRunService runService = mock(ModelRunService.class);
        when(runService.getModelRunRequestServersByUsage()).thenReturn(Arrays.asList("c", "b"));
        ModelRunRequester target = new ModelRunRequester(webService, modelRunPackageBuilder, covariateService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path", "http://api:key@b:1245/path", "http://api:key@c:1245/path", "http://api:key@d:1245/path"});

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class)), null, null);

        // Assert
        verify(webService).startRun(eq(URI.create("http://api:key@a:1245/path")), any(File.class));
    }

    @Test
    public void requestModelRunSavesTheInputOccurrencesForAutomaticModelRun() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, null, mock(ModelRunPackageBuilder.class), mock(CovariateService.class), mock(ModelWrapperWebService.class));

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

        ModelRunRequester target = createMockModelRunRequester(runService, 87, false, null, mock(ModelRunPackageBuilder.class), mock(CovariateService.class), mock(ModelWrapperWebService.class));

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
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService, mock(ModelRunPackageBuilder.class), mock(CovariateService.class), mock(ModelWrapperWebService.class));

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
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService, mock(ModelRunPackageBuilder.class), mock(CovariateService.class), mock(ModelWrapperWebService.class));
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

    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Test
    public void requestModelRunHandlesLongNames() throws Exception {
        // Arrange
        DiseaseService diseaseService = mock(DiseaseService.class);
        ModelRunService runService = mock(ModelRunService.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService, mock(ModelRunPackageBuilder.class), mock(CovariateService.class), mock(ModelWrapperWebService.class));


        DateTimeUtils.setCurrentMillisFixed(0);

        String longName = RandomStringUtils.randomAlphanumeric(300);
        when(diseaseService.getDiseaseGroupById(87).getAbbreviation()).thenReturn(longName);
        String expectedRunNameStart =
                longName.substring(0, 195) + "_" + LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + "_";

        // Act
        target.requestModelRun(87, Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class)), null, null);

        // Assert
        ArgumentCaptor<ModelRun> modelRunArgumentCaptor = ArgumentCaptor.forClass(ModelRun.class);
        verify(runService).saveModelRun(modelRunArgumentCaptor.capture());
        ModelRun value = modelRunArgumentCaptor.getValue();
        assertThat(value.getName()).startsWith(expectedRunNameStart);
        assertThat(value.getName()).matches(expectedRunNameStart + UUID_REGEX);
    }

    @Test
    public void requestModelRunBuildsZipWithCorrectData() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        CovariateService covService = mock(CovariateService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        List<DiseaseOccurrence> occurrences = Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class));
        List<DiseaseOccurrence> supplementaryOccurrences = Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class));
        List<AdminUnitDiseaseExtentClass> extent = Arrays.asList(
                createMockAdminUnitDiseaseExtentClass(), createMockAdminUnitDiseaseExtentClass(),
                createMockAdminUnitDiseaseExtentClass(), createMockAdminUnitDiseaseExtentClass()
        );
        List<CovariateFile> covariateFiles = Arrays.asList(
                mock(CovariateFile.class),
                mock(CovariateFile.class),
                mock(CovariateFile.class)
        );

        when(covService.getCovariateDirectory()).thenReturn("covDir");
        when(diseaseService.getSupplementaryOccurrencesForModelRun(eq(87), any(DateTime.class), any(DateTime.class))).thenReturn(supplementaryOccurrences);

        when(diseaseService.getDiseaseExtentByDiseaseGroupId(87)).thenReturn(extent);

        ModelRunPackageBuilder zipBuilder = mock(ModelRunPackageBuilder.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService, zipBuilder, covService, mock(ModelWrapperWebService.class));
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(87);
        when(covService.getCovariateFilesByDiseaseGroup(diseaseGroup)).thenReturn(covariateFiles);

        // Act
        target.requestModelRun(87, occurrences, null, null);

        // Assert
        verify(zipBuilder).buildPackage(startsWith("deng_"), same(diseaseGroup), same(occurrences), same(extent), same(supplementaryOccurrences), same(covariateFiles), eq("covDir"));
    }

    @Test
    public void requestModelRunBuildsZipWithCorrectDataSubmitsCorrectZipToWebService() throws Exception {
        // Arrange
        ModelRunService runService = mock(ModelRunService.class);
        CovariateService covService = mock(CovariateService.class);
        DiseaseService diseaseService = mock(DiseaseService.class);
        List<DiseaseOccurrence> occurrences = Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class));
        List<DiseaseOccurrence> supplementaryOccurrences = Arrays.asList(mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class), mock(DiseaseOccurrence.class));
        List<AdminUnitDiseaseExtentClass> extent = Arrays.asList(
                createMockAdminUnitDiseaseExtentClass(), createMockAdminUnitDiseaseExtentClass(),
                createMockAdminUnitDiseaseExtentClass(), createMockAdminUnitDiseaseExtentClass()
        );
        List<CovariateFile> covariateFiles = Arrays.asList(
                mock(CovariateFile.class),
                mock(CovariateFile.class),
                mock(CovariateFile.class)
        );

        when(covService.getCovariateDirectory()).thenReturn("covDir");

        when(diseaseService.getDiseaseExtentByDiseaseGroupId(87)).thenReturn(extent);
        when(diseaseService.getSupplementaryOccurrencesForModelRun(eq(87), any(DateTime.class), any(DateTime.class))).thenReturn(supplementaryOccurrences);

        ModelRunPackageBuilder zipBuilder = mock(ModelRunPackageBuilder.class);
        ModelWrapperWebService webService = mock(ModelWrapperWebService.class);
        ModelRunRequester target = createMockModelRunRequester(runService, 87, true, diseaseService, zipBuilder, covService, webService);
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(87);
        when(covService.getCovariateFilesByDiseaseGroup(diseaseGroup)).thenReturn(covariateFiles);

        File zipFile = Files.createTempFile("abc", "xzy").toFile();
        when(zipBuilder.buildPackage(startsWith("deng_"), same(diseaseGroup), same(occurrences), same(extent), same(supplementaryOccurrences), same(covariateFiles), eq("covDir"))).thenReturn(zipFile);

        // Act
        target.requestModelRun(87, occurrences, null, null);

        // Assert
        verify(webService).startRun(any(URI.class), same(zipFile));
        assertThat(zipFile).doesNotExist(); // Should have been deleted
    }

    private AdminUnitDiseaseExtentClass createMockAdminUnitDiseaseExtentClass() {
        AdminUnitDiseaseExtentClass mock = mock(AdminUnitDiseaseExtentClass.class);
        when(mock.getAdminUnitGlobalOrTropical()).thenReturn(mock(AdminUnitGlobalOrTropical.class));
        when(mock.getDiseaseExtentClass()).thenReturn(mock(DiseaseExtentClass.class));
        return mock;
    }

    private ModelRunRequester createMockModelRunRequester(ModelRunService runService, int diseaseGroupId, boolean automaticRuns, DiseaseService mockDiseaseService, ModelRunPackageBuilder modelRunPackageBuilder1, CovariateService covariateService, ModelWrapperWebService webService) throws IOException, ZipException {
        DiseaseService diseaseService = mockDiseaseService == null ? mock(DiseaseService.class) : mockDiseaseService;
        when(diseaseService.getDiseaseGroupById(diseaseGroupId)).thenReturn(mock(DiseaseGroup.class));
        when(diseaseService.getDiseaseGroupById(diseaseGroupId).getAbbreviation()).thenReturn("deng");
        when(diseaseService.getDiseaseGroupById(diseaseGroupId).isAutomaticModelRunsEnabled()).thenReturn(automaticRuns);
        when(webService.startRun(any(URI.class), any(File.class))).thenReturn(mock(JsonModelRunResponse.class));
        when(runService.getModelRunRequestServersByUsage()).thenReturn(new ArrayList<String>());
        return new ModelRunRequester(webService, modelRunPackageBuilder1, covariateService, diseaseService, runService,
                new String[]{"http://api:key@a:1245/path"});
    }
}
