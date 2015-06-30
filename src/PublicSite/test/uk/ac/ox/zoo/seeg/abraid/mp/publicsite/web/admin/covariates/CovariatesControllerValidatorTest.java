package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for CovariatesControllerValidator.
 * Copyright (c) 2015 University of Oxford
 */
public class CovariatesControllerValidatorTest extends BaseCovariatesControllerTests {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void validateCovariateUploadRejectsNullFile() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", null, "path");

        // Assert
        assertThat(result).contains("File missing.");
    }

    @Test
    public void validateCovariateUploadRejectsEmptyFile() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));
        MockMultipartFile emptyFile = new MockMultipartFile(" ", new byte[0]);

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", emptyFile, "path");

        // Assert
        assertThat(result).contains("File missing.");
    }

    @Test
    public void validateCovariateUploadRejectsNullName() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload(null, "subdir", null, "path");

        // Assert
        assertThat(result).contains("Name missing.");
    }

    @Test
    public void validateCovariateUploadRejectsEmptyName() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload("", "subdir", null, "path");

        // Assert
        assertThat(result).contains("Name missing.");
    }

    @Test
    public void validateCovariateUploadRejectsNullSubdirectory() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload("name", null, null, "path");

        // Assert
        assertThat(result).contains("Subdirectory missing.");
    }

    @Test
    public void validateCovariateUploadRejectsEmptySubdirectory() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "", null, "path");

        // Assert
        assertThat(result).contains("Subdirectory missing.");
    }

    @Test
    public void validateCovariateUploadRejectsInvalidSubdirectory() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));
        Collection<String> result;

        // Act/Assert
        result = target.validateCovariateUpload("name", "./a/../b", null, "path");
        assertThat(result).contains("Subdirectory not valid.");
        result = target.validateCovariateUpload("name", "./a/./b", null, "path");
        assertThat(result).contains("Subdirectory not valid.");
        result = target.validateCovariateUpload("name", "./a//b", null, "path");
        assertThat(result).contains("Subdirectory not valid.");
        result = target.validateCovariateUpload("name", "./a\\b", null, "path");
        assertThat(result).contains("Subdirectory not valid.");
    }

    @Test
    public void validateCovariateUploadRejectsNonUniqueName() throws Exception {
        // Arrange
        CovariateService covariateService = mock(CovariateService.class);
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, mock(DiseaseService.class));
        CovariateFile covariateFile = mock(CovariateFile.class);
        when(covariateService.getAllCovariateFiles()).thenReturn(Arrays.asList(covariateFile));
        when(covariateFile.getName()).thenReturn("not unique");

        // Act
        Collection<String> result = target.validateCovariateUpload("not unique", "subdir", null, "path");

        // Assert
        assertThat(result).contains("Name not unique.");
    }

    @Test
    public void validateCovariateUploadRejectsPrexistingPath() throws Exception {
        // Arrange
        CovariateService covariateService = mock(CovariateService.class);
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, mock(DiseaseService.class));
        when(covariateService.getAllCovariateFiles()).thenReturn(new ArrayList<CovariateFile>());
        MockMultipartFile file = new MockMultipartFile(" ", new byte[1]);
        File existing = testFolder.newFile();
        FileUtils.write(existing, "exists");
        when(covariateService.getCovariateDirectory()).thenReturn(testFolder.getRoot().getAbsolutePath());

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", file, existing.toString());

        // Assert
        assertThat(result).contains("File already exists.");
    }

    @Test
    public void validateCovariateUploadRejectsPathNotUnderCovariateDirectory() throws Exception {
        // Arrange
        CovariateService covariateService = mock(CovariateService.class);
        when(covariateService.getAllCovariateFiles()).thenReturn(new ArrayList<CovariateFile>());
        MockMultipartFile file = new MockMultipartFile(" ", new byte[1]);
        String path = Paths.get(testFolder.getRoot().toString(), "asdfas").toFile().getAbsolutePath();
        String covdir = testFolder.newFolder().getAbsolutePath();
        when(covariateService.getCovariateDirectory()).thenReturn(covdir);
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", file, path);

        // Assert
        assertThat(result).contains("Target path not valid.");
    }

    @Test
    public void validateCovariateUploadAcceptsValidParameters() throws Exception {
        // Arrange
        CovariateService covariateService = mock(CovariateService.class);
        when(covariateService.getAllCovariateFiles()).thenReturn(new ArrayList<CovariateFile>());
        MockMultipartFile file = new MockMultipartFile(" ", new byte[1]);
        String path = Paths.get(testFolder.getRoot().toString(), "asdfas").toFile().getAbsolutePath();
        String covdir = testFolder.getRoot().getAbsolutePath();
        when(covariateService.getCovariateDirectory()).thenReturn(covdir);
        when(covariateService.getCovariateDirectory()).thenReturn(covdir);
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", file, path);

        // Assert
        assertThat(result).hasSize(0);
    }

    @Test
    public void validateCovariateConfigurationRejectsNull() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateConfiguration(null);

        // Assert
        assertThat(result).contains("'files' is null.");
    }

    @Test
    public void validateCovariateConfigurationRejectsNullFile() throws Exception {
        // Arrange
        JsonCovariateConfiguration config = mock(JsonCovariateConfiguration.class);
        when(config.getFiles()).thenReturn(null);
        CovariatesControllerValidator target = new CovariatesControllerValidator(mock(CovariateService.class), mock(DiseaseService.class));

        // Act
        Collection<String> result = target.validateCovariateConfiguration(config);

        // Assert
        assertThat(result).contains("'files' is null.");
    }

    @Test
      public void validateCovariateConfigurationRejectsUnknownFilePath() throws Exception {
        // Arrange
        JsonCovariateConfiguration config = createValidMockConfig();
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, diseaseService);
        config.getFiles().add(createMockJsonCovariateFile("Wrong", "foo", true, new ArrayList<Integer>()));

        // Act
        Collection<String> result = target.validateCovariateConfiguration(config);

        // Assert
        assertThat(result).contains("Unexpected file listed or missing.");
    }

    @Test
    public void validateCovariateConfigurationRejectsMissingFilePath() throws Exception {
        // Arrange
        JsonCovariateConfiguration config = createValidMockConfig();
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, diseaseService);
        covariateService.getAllCovariateFiles().add(createMockCovariateFile("Missing", "foo", true, new ArrayList<DiseaseGroup>()));

        // Act
        Collection<String> result = target.validateCovariateConfiguration(config);

        // Assert
        assertThat(result).contains("Unexpected file listed or missing.");
    }

    @Test
    public void validateCovariateConfigurationRejectsDuplicateEnabledDiseases() throws Exception {
        // Arrange
        JsonCovariateConfiguration config = createValidMockConfig();
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, diseaseService);
        config.getFiles().get(0).getEnabled().add(22);

        // Act
        Collection<String> result = target.validateCovariateConfiguration(config);

        // Assert
        assertThat(result).contains("Enabled disease ids contains duplicates.");
    }

    @Test
    public void validateCovariateConfigurationRejectsUnknownEnabledDiseases() throws Exception {
        // Arrange
        JsonCovariateConfiguration config = createValidMockConfig();
        CovariateService covariateService = createMockCovariateService(testFolder.getRoot());
        DiseaseService diseaseService = createMockDiseaseService();
        CovariatesControllerValidator target = new CovariatesControllerValidator(covariateService, diseaseService);
        config.getFiles().get(0).getEnabled().add(23);

        // Act
        Collection<String> result = target.validateCovariateConfiguration(config);

        // Assert
        assertThat(result).contains("One or more files specify usage for an unknown disease id.");
    }
}
