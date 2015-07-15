package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateValueBin;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for CovariatesControllerHelper.
 * Copyright (c) 2015 University of Oxford
 */
public class CovariatesControllerHelperTest extends BaseCovariatesControllerTests {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier
    private static final String TEST_DATA_PATH = "PublicSite/test/uk/ac/ox/zoo/seeg/abraid/mp/publicsite/web/admin/covariates";

    @Test
    public void extractTargetPathReturnsCorrectPath() throws Exception {
        // Arrange
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        CovariatesControllerHelper target = new CovariatesControllerHelperImpl(covariateService, mock(DiseaseService.class));

        // Act
        String result = target.extractTargetPath("subdir/as", new MockMultipartFile("fn.g", "ofn.g", "application/octet-stream", new byte[0]));

        // Assert
        assertThat(result).isEqualTo(FilenameUtils.separatorsToUnix(testFolder.getRoot().getAbsolutePath()) + "/subdir/as/ofn.g");
    }

    @Test
    public void getCovariateConfigurationReturnsCorrectConfig() throws Exception {
        // Arrange
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerHelper target = new CovariatesControllerHelperImpl(covariateService, diseaseService);

        // Act
        JsonCovariateConfiguration result = target.getCovariateConfiguration();

        // Assert
        JsonCovariateConfiguration expected = createValidMockConfig();
        assertThat(result.getFiles().size()).isEqualTo(4);
        assertThat(result.getFiles().get(0).getPath()).isEqualTo(expected.getFiles().get(0).getPath());
        assertThat(result.getFiles().get(0).getName()).isEqualTo(expected.getFiles().get(0).getName());
        assertThat(result.getFiles().get(0).getHide()).isEqualTo(expected.getFiles().get(0).getHide());
        assertThat(result.getFiles().get(0).getInfo()).isEqualTo(expected.getFiles().get(0).getInfo());
        assertThat(result.getFiles().get(0).getEnabled()).isEqualTo(expected.getFiles().get(0).getEnabled());
        assertThat(result.getDiseases().size()).isEqualTo(2);
        assertThat(result.getDiseases().get(0).getId()).isEqualTo(22);
    }

    @Test
    public void setCovariateConfigurationUpdatesDBCorrectly() throws Exception {
        // Arrange
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        JsonCovariateConfiguration config = createValidMockConfig();
        when(config.getFiles().get(0).getName()).thenReturn("new Name");
        when(config.getFiles().get(0).getHide()).thenReturn(true);
        when(config.getFiles().get(0).getDiscrete()).thenReturn(true);
        when(config.getFiles().get(2).getEnabled()).thenReturn(Arrays.asList(22, 60));
        when(config.getFiles().get(2).getInfo()).thenReturn("new");
        CovariatesControllerHelper target = new CovariatesControllerHelperImpl(covariateService, diseaseService);

        // Act
        target.setCovariateConfiguration(config);

        // Assert
        CovariateFile c1 = covariateService.getAllCovariateFiles().get(0);
        verify(c1).setName("new Name");
        verify(c1).setHide(true);
        verify(c1, never()).setDiscrete(true); // Read only
        verify(covariateService).saveCovariateFile(c1);
        CovariateFile c2 = covariateService.getAllCovariateFiles().get(2);
        verify(c2).setEnabledDiseaseGroups(eq(diseaseService.getAllDiseaseGroups()));
        verify(c2).setInfo("new");
        verify(covariateService).saveCovariateFile(c2);
    }

    @Test
    public void saveNewCovariateFileStoresCorrectlyForContinuousRasterIncludingHistogramData() throws Exception {
        // Arrange
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerHelper target = new CovariatesControllerHelperImpl(covariateService, diseaseService);
        File refFile = new File(TEST_DATA_PATH, "continuous_raster.tif");
        byte[] bytes = FileUtils.readFileToByteArray(refFile);

        // Act
        target.saveNewCovariateFile("name", false, covariateService.getCovariateDirectory() + "/asd/fas", new MockMultipartFile("foo", "oof", "application/octet-stream", bytes));

        // Assert
        assertThat(new File(covariateService.getCovariateDirectory() + "/asd/fas")).hasContentEqualTo(refFile);
        ArgumentCaptor<CovariateFile> captor = ArgumentCaptor.forClass(CovariateFile.class);
        verify(covariateService).saveCovariateFile(captor.capture());
        CovariateFile saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("name");
        assertThat(saved.getFile()).isEqualTo("asd/fas");
        assertThat(saved.getHide()).isEqualTo(false);
        assertThat(saved.getDiscrete()).isEqualTo(false);
        assertThat(saved.getInfo()).isEqualTo("");
        assertThat(saved.getEnabledDiseaseGroups()).isNullOrEmpty();
        assertThat(saved.getCovariateValueHistogramData()).hasSize(10);
        List<Object> sorted = sort(saved.getCovariateValueHistogramData(), on(CovariateValueBin.class).getMin());
        assertThat(extractProperty("min").from(sorted)).containsExactly(0.0, 0.09996433036867529, 0.19992866073735058, 0.29989299110602585, 0.39985732147470116, 0.49982165184337646, 0.5997859822120517, 0.6997503125807271, 0.7997146429494023, 0.8996789733180776);
        assertThat(extractProperty("max").from(sorted)).containsExactly(0.09996433036867529, 0.19992866073735058, 0.29989299110602585, 0.39985732147470116, 0.49982165184337646, 0.5997859822120517, 0.6997503125807271, 0.7997146429494023, 0.8996789733180776, 0.9996433036867529);
        assertThat(extractProperty("count").from(sorted)).containsExactly(686, 130, 138, 129, 136, 131, 114, 161, 126, 130);
        assertThat(extractProperty("covariateFile").from(sorted)).containsExactly(saved, saved, saved, saved, saved, saved, saved, saved, saved, saved);
    }

    @Test
    public void saveNewCovariateFileStoresCorrectlyForDiscreteRasterIncludingHistogramData() throws Exception {
        // Arrange
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerHelper target = new CovariatesControllerHelperImpl(covariateService, diseaseService);
        File refFile = new File(TEST_DATA_PATH, "discrete_raster.tif");
        byte[] bytes = FileUtils.readFileToByteArray(refFile);

        // Act
        target.saveNewCovariateFile("name", true, covariateService.getCovariateDirectory() + "/asd/fas", new MockMultipartFile("foo", "oof", "application/octet-stream", bytes));

        // Assert
        assertThat(new File(covariateService.getCovariateDirectory() + "/asd/fas")).hasContentEqualTo(refFile);
        ArgumentCaptor<CovariateFile> captor = ArgumentCaptor.forClass(CovariateFile.class);
        verify(covariateService).saveCovariateFile(captor.capture());
        CovariateFile saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("name");
        assertThat(saved.getFile()).isEqualTo("asd/fas");
        assertThat(saved.getHide()).isEqualTo(false);
        assertThat(saved.getDiscrete()).isEqualTo(true);
        assertThat(saved.getInfo()).isEqualTo("");
        assertThat(saved.getEnabledDiseaseGroups()).isNullOrEmpty();
        assertThat(saved.getCovariateValueHistogramData()).hasSize(5);
        List<Object> sorted = sort(saved.getCovariateValueHistogramData(), on(CovariateValueBin.class).getMin());
        assertThat(extractProperty("min").from(sorted)).containsExactly(0.0, 0.25, 0.5, 0.75, 1.0);
        assertThat(extractProperty("max").from(sorted)).containsExactly(0.0, 0.25, 0.5, 0.75, 1.0);
        assertThat(extractProperty("count").from(sorted)).containsExactly(557, 327, 337, 329, 331);
        assertThat(extractProperty("covariateFile").from(sorted)).containsExactly(saved, saved, saved, saved, saved);
    }

    @Test
    public void saveNewCovariateThrowsIfDirectoryCanNotBeCreated() throws Exception {
        // Arrange
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerHelper target = new CovariatesControllerHelperImpl(covariateService, diseaseService);
        File refFile = new File(TEST_DATA_PATH, "continuous_raster.tif");
        byte[] bytes = FileUtils.readFileToByteArray(refFile);

        // Act
        target.saveNewCovariateFile("name", true, covariateService.getCovariateDirectory() + "/asd/fas", new MockMultipartFile("foo", "oof", "application/octet-stream", bytes));

        // Assert
        assertThat(new File(covariateService.getCovariateDirectory() + "/asd/fas")).hasContentEqualTo(refFile);
        verify(covariateService).saveCovariateFile(eq(new CovariateFile("name", "asd/fas", false, true, "")));
    }
}
