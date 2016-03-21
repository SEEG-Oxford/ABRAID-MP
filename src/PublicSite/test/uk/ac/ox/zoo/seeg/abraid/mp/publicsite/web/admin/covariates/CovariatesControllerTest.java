package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import org.apache.commons.io.FilenameUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;

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
* Copyright (c) 2015 University of Oxford
*/
public class CovariatesControllerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void showCovariatesPageReturnsCorrectModelData() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariatesControllerHelper.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariatesControllerHelper.getCovariateConfiguration().setDiseases(new ArrayList<JsonModelDisease>());
        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model).addAttribute("initialData", "{\"diseases\":[],\"files\":[]}");
    }

    @Test
    public void showCovariatesPageReturnsSortedDiseases() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariatesControllerHelper.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariatesControllerHelper.getCovariateConfiguration().setDiseases(new ArrayList<JsonModelDisease>());
        covariatesControllerHelper.getCovariateConfiguration().getDiseases().add(new JsonModelDisease(23, "aaa"));
        covariatesControllerHelper.getCovariateConfiguration().getDiseases().add(new JsonModelDisease(24, "zzz"));
        covariatesControllerHelper.getCovariateConfiguration().getDiseases().add(new JsonModelDisease(25, "ggg"));

        Model model = mock(Model.class);

        // Act
        target.showCovariatesPage(model);

        // Assert
        verify(model).addAttribute("initialData", "{\"diseases\":[{\"id\":23,\"name\":\"aaa\",\"global\":false},{\"id\":25,\"name\":\"ggg\",\"global\":false},{\"id\":24,\"name\":\"zzz\",\"global\":false}],\"files\":[]}");
    }

    @Test
    public void showCovariatesPageReturnsCorrectTemplate() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariatesControllerHelper.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariatesControllerHelper.getCovariateConfiguration().setDiseases(new ArrayList<JsonModelDisease>());
        Model model = mock(Model.class);

        // Act
        String result = target.showCovariatesPage(model);

        // Assert
        assertThat(result).isEqualTo("admin/covariates");
    }

    @Test
    public void showCovariatesPageThrowsForInvalidCovariateConfig() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, null, new AbraidJsonObjectMapper());
        when(covariatesControllerHelper.getCovariateConfiguration()).thenThrow(new IOException());
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
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesControllerValidator covariatesControllerValidator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, covariatesControllerValidator, new AbraidJsonObjectMapper());
        JsonCovariateConfiguration invalidConf = new JsonCovariateConfiguration();

        when(covariatesControllerValidator.validateCovariateConfiguration(any(JsonCovariateConfiguration.class))).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity result = target.updateCovariates(invalidConf);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateCovariatesRejectsNullInputs() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesControllerValidator covariatesControllerValidator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, covariatesControllerValidator, new AbraidJsonObjectMapper());

        when(covariatesControllerValidator.validateCovariateConfiguration(any(JsonCovariateConfiguration.class))).thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity result = target.updateCovariates(null);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateCovariatesThrowsIfConfigurationCanNotBeSaved() throws Exception {
        // Arrange
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesControllerValidator covariatesControllerValidator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, covariatesControllerValidator, new AbraidJsonObjectMapper());
        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
        when(covariatesControllerValidator.validateCovariateConfiguration(conf)).thenReturn(new ArrayList<String>());
        doThrow(new RuntimeException()).when(covariatesControllerHelper).setCovariateConfiguration(conf);

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
        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        CovariatesControllerValidator covariatesControllerValidator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, covariatesControllerValidator, new AbraidJsonObjectMapper());
        JsonCovariateConfiguration conf = mock(JsonCovariateConfiguration.class);
        when(covariatesControllerValidator.validateCovariateConfiguration(conf)).thenReturn(new ArrayList<String>());

        // Act
        ResponseEntity result = target.updateCovariates(conf);

        // Assert
        verify(covariatesControllerHelper).setCovariateConfiguration(conf);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void addCovariateFileValidatesItsInputsCorrectly() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        when(expectedFile.getOriginalFilename()).thenReturn("file.ext");
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        String expectedQualifier = "expectedQualifier";
        Integer expectedParent = 123;
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final boolean expectedIsDiscrete = true;
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/file.ext");
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);


        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), anyInt(), anyString(), any(MultipartFile.class), anyString()))
            .thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedQualifier, expectedParent, expectedIsDiscrete, expectedSubdirectory, expectedFile);

        // Assert
        verify(validator).validateCovariateUpload(
                expectedName, expectedQualifier, expectedParent, expectedSubdirectory, expectedFile, expectedPath
        );
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody().getStatus()).isEqualTo("FAIL");
        assertThat(result.getBody().getMessages()).containsOnly("FAIL1", "FAIL2");
    }

    @Test
    public void addCovariateFileValidatesItsInputsCorrectlyWithoutParent() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        when(expectedFile.getOriginalFilename()).thenReturn("file.ext");
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        String expectedQualifier = "expectedQualifier";
        final String expectedSubdirectory = "dir";
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final boolean expectedIsDiscrete = true;
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/file.ext");
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);


        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), anyInt(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(Arrays.asList("FAIL1", "FAIL2"));

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedQualifier, -1, expectedIsDiscrete, expectedSubdirectory, expectedFile);

        // Assert
        verify(validator).validateCovariateUpload(
                expectedName, expectedQualifier, null, expectedSubdirectory, expectedFile, expectedPath
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
        String expectedQualifier = "expectedQualifier";
        Integer expectedParent = 123;
        final String expectedSubdirectory = "dir";
        final boolean expectedIsDiscrete = false;
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/" + expectedFileName);
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);

        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), anyInt(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(new ArrayList<String>());

        // Act
        target.addCovariateFile(expectedName, expectedQualifier, expectedParent, expectedIsDiscrete, expectedSubdirectory, expectedFile);

        // Assert
        verify(covariatesControllerHelper).saveNewCovariateFile(expectedName, expectedQualifier, expectedParent, expectedIsDiscrete, expectedPath, expectedFile);
    }

    @Test
    public void addCovariateFileSavesTheFileCorrectlyWithoutParent() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        String expectedQualifier = "expectedQualifier";
        final String expectedSubdirectory = "dir";
        final boolean expectedIsDiscrete = false;
        final String expectedCovariateDir = testFolder.newFolder().toString();
        final String expectedPath = FilenameUtils.separatorsToUnix(expectedCovariateDir + "/" + expectedSubdirectory + "/" + expectedFileName);
        final JsonCovariateConfiguration expectedCovariateConf = mock(JsonCovariateConfiguration.class);

        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.getCovariateConfiguration()).thenReturn(expectedCovariateConf);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn(expectedPath);
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), anyInt(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(new ArrayList<String>());

        // Act
        target.addCovariateFile(expectedName, expectedQualifier, -1, expectedIsDiscrete, expectedSubdirectory, expectedFile);

        // Assert
        verify(covariatesControllerHelper).saveNewCovariateFile(expectedName, expectedQualifier, null, expectedIsDiscrete, expectedPath, expectedFile);
    }

    @Test
    public void addCovariateFileReturnsAnAppropriateStatusForSuccess() throws Exception {
        // Arrange
        final MultipartFile expectedFile = mock(MultipartFile.class);
        final String expectedFileName = "file.ext";
        when(expectedFile.getOriginalFilename()).thenReturn(expectedFileName);
        when(expectedFile.getBytes()).thenReturn("Test content".getBytes());
        final String expectedName = "name";
        String expectedQualifier = "expectedQualifier";
        Integer expectedParent = -1;
        final String expectedSubdirectory = "dir";
        final boolean expectedIsDiscrete = true;
        final List<JsonCovariateFile> covariateFileList = new ArrayList<>();

        CovariatesControllerHelper covariatesControllerHelper = mock(CovariatesControllerHelper.class);
        when(covariatesControllerHelper.extractTargetPath(expectedSubdirectory, expectedFile)).thenReturn("xyz");
        CovariatesControllerValidator validator = mock(CovariatesControllerValidator.class);
        CovariatesController target = new CovariatesController(covariatesControllerHelper, validator, new AbraidJsonObjectMapper());
        when(validator.validateCovariateUpload(anyString(), anyString(), anyInt(), anyString(), any(MultipartFile.class), anyString()))
                .thenReturn(new ArrayList<String>());

        // Act
        ResponseEntity<JsonFileUploadResponse> result = target.addCovariateFile(expectedName, expectedQualifier, expectedParent, expectedIsDiscrete, expectedSubdirectory, expectedFile);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getBody().getMessages()).hasSize(0);
    }
}
