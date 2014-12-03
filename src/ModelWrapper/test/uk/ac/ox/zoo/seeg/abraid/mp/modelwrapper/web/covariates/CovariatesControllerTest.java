package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.covariates;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonDisease;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for CovariatesController.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void showCovariatesPageReturnsCorrectModelData() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        when(configurationService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        configurationService.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        configurationService.getCovariateConfiguration().setDiseases(new ArrayList<JsonDisease>());
        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model).addAttribute("initialData", "{\"diseases\":[],\"files\":[]}");
    }

    @Test
    public void showCovariatesPageReturnsSortedDiseases() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        when(configurationService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        configurationService.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        configurationService.getCovariateConfiguration().setDiseases(new ArrayList<JsonDisease>());
        configurationService.getCovariateConfiguration().getDiseases().add(new JsonDisease(23, "aaa"));
        configurationService.getCovariateConfiguration().getDiseases().add(new JsonDisease(24, "zzz"));
        configurationService.getCovariateConfiguration().getDiseases().add(new JsonDisease(25, "ggg"));

        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model).addAttribute("initialData", "{\"diseases\":[{\"id\":23,\"name\":\"aaa\"},{\"id\":25,\"name\":\"ggg\"},{\"id\":24,\"name\":\"zzz\"}],\"files\":[]}");
    }

    @Test
    public void showCovariatesPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        when(configurationService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        configurationService.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        configurationService.getCovariateConfiguration().setDiseases(new ArrayList<JsonDisease>());
        Model model = mock(Model.class);

        // Act
        String result = target.showCovariatesPage(model);

        // Assert
        assertThat(result).isEqualTo("covariates");
    }

    @Test
    public void showCovariatesPageThrowsForInvalidCovariateConfig() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        when(configurationService.getCovariateConfiguration()).thenThrow(new IOException());
        Model model = mock(Model.class);

        // Act
        catchException(target).showCovariatesPage(model);

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Existing covariate configuration is invalid.");
    }

    @Test
    public void updateCovariatesRejectsInvalidInputs() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        JsonCovariateConfiguration invalidConf = new JsonCovariateConfiguration();

        for (JsonCovariateConfiguration conf : Arrays.asList(invalidConf, null)) {
            // Act
            ResponseEntity result = target.updateCovariates(conf);

            // Assert
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    public void updateCovariatesThrowsIfConfigurationCanNotBeSaved() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
        when(conf.isValid()).thenReturn(true);
        doThrow(new IOException()).when(configurationService).setCovariateConfiguration(conf);

        // Act
        catchException(target).updateCovariates(conf);

        // Assert
        assertThat(caughtException())
                .isInstanceOf(IOException.class)
                .hasMessage("Covariate configuration update failed.");
    }

    @Test
    public void updateCovariatesSavedConfiguration() throws Exception {
        // Arrange
        ConfigurationService configurationService = mock(ConfigurationService.class);
        CovariatesController target = new CovariatesController(configurationService, null);
        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
        when(conf.isValid()).thenReturn(true);

        // Act
        ResponseEntity result = target.updateCovariates(conf);

        // Assert
        verify(configurationService).setCovariateConfiguration(conf);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void addCovariateFileValidatesItsInputsCorrectly() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        when(expectedFile.getOriginalFilename()).thenReturn("file.ext");
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/file.ext");
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);


        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(configurationService.getCovariateDirectory()).thenReturn(expectedCovariateDir);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(configurationService, validator);
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString(), anyString(), any(JsonCovariateConfiguration.class)))
            .thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        verify(validator).validateCovariateUpload(
                expectedName, expectedSubdirectory, expectedFile, expectedPath, expectedCovariateDir, expectedCovariateConf
        );
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().getStatus()).isEqualTo("FAIL");
        assertThat(result.getBody().getMessages()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void addCovariateFileSavesTheFileCorrectly() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/" + expectedFileName);
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);

        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(configurationService.getCovariateDirectory()).thenReturn(expectedCovariateDir);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(configurationService, validator);
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString(), anyString(), any(JsonCovariateConfiguration.class)))
                .thenReturn(new ArrayList<String>());

        // Act
        target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        assertThat(FileUtils.readFileToString(new File(expectedPath))).isEqualTo("Test content");
    }

    @Test
    public void addCovariateFileUpdatesTheCovariateConfigurationCorrectly() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);
        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();
        when(expectedCovariateConf.getFiles()).thenReturn(covariateFileList);

        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(configurationService.getCovariateDirectory()).thenReturn(expectedCovariateDir);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(configurationService, validator);
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString(), anyString(), any(JsonCovariateConfiguration.class)))
                .thenReturn(new ArrayList<String>());

        // Act
        target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        assertThat(covariateFileList).hasSize(1);
        assertThat(covariateFileList.get(0).getName()).isEqualTo(expectedName);
        assertThat(covariateFileList.get(0).getPath()).isEqualTo(expectedSubdirectory + "/" + expectedFileName);
        assertThat(covariateFileList.get(0).getHide()).isEqualTo(false);
        assertThat(covariateFileList.get(0).getInfo()).isEqualTo(null);
        assertThat(covariateFileList.get(0).getEnabled()).hasSize(0);
    }

    @Test
    public void addCovariateFileCorrectlyNormalizesPaths() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "/one\\two/dir";
        final String expectedCovariateDir = testFolder.getRoot().toString();
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);
        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();
        when(expectedCovariateConf.getFiles()).thenReturn(covariateFileList);

        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(configurationService.getCovariateDirectory()).thenReturn(expectedCovariateDir);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(configurationService, validator);
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString(), anyString(), any(JsonCovariateConfiguration.class)))
                .thenReturn(new ArrayList<String>());

        // Act
        target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        assertThat(covariateFileList).hasSize(1);
        assertThat(covariateFileList.get(0).getPath()).isEqualTo("one/two/dir/file.ext");
    }

    @Test
    public void addCovariateFileReturnsAnAppropriateStatusForSuccess() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);
        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();
        when(expectedCovariateConf.getFiles()).thenReturn(covariateFileList);

        ConfigurationService configurationService = mock(ConfigurationService.class);
        when(configurationService.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(configurationService.getCovariateDirectory()).thenReturn(expectedCovariateDir);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(configurationService, validator);
        when(validator.validateCovariateUpload(anyString(), anyString(), any(MultipartFile.class), anyString(), anyString(), any(JsonCovariateConfiguration.class)))
                .thenReturn(new ArrayList<String>());

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedSubdirectory, expectedFile);

        // Assert
        assertThat(covariateFileList).hasSize(1);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getBody().getMessages()).hasSize(0);
    }
}
