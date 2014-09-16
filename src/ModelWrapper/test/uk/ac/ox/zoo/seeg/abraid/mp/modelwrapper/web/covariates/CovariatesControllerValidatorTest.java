package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.covariates;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.mock.web.MockMultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;

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
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerValidatorTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void validateCovariateUploadRejectsNullFile() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", null, "path", "covdir", mock(JsonCovariateConfiguration.class));

        // Assert
        assertThat(result).contains("File missing.");
    }

    @Test
    public void validateCovariateUploadRejectsEmptyFile() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();
        MockMultipartFile emptyFile = new MockMultipartFile(" ", new byte[0]);

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", emptyFile, "path", "covdir", mock(JsonCovariateConfiguration.class));

        // Assert
        assertThat(result).contains("File missing.");
    }

    @Test
    public void validateCovariateUploadRejectsNullName() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();

        // Act
        Collection<String> result = target.validateCovariateUpload(null, "subdir", null, "path", "covdir", mock(JsonCovariateConfiguration.class));

        // Assert
        assertThat(result).contains("Name missing.");
    }

    @Test
    public void validateCovariateUploadRejectsEmptyName() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();

        // Act
        Collection<String> result = target.validateCovariateUpload("", "subdir", null, "path", "covdir", mock(JsonCovariateConfiguration.class));

        // Assert
        assertThat(result).contains("Name missing.");
    }

    @Test
    public void validateCovariateUploadRejectsNullSubdirectory() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();

        // Act
        Collection<String> result = target.validateCovariateUpload("name", null, null, "path", "covdir", mock(JsonCovariateConfiguration.class));

        // Assert
        assertThat(result).contains("Subdirectory missing.");
    }

    @Test
    public void validateCovariateUploadRejectsEmptySubdirectory() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "", null, "path", "covdir", mock(JsonCovariateConfiguration.class));

        // Assert
        assertThat(result).contains("Subdirectory missing.");
    }

    @Test
    public void validateCovariateUploadRejectsInvalid() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();
        Collection<String> result;

        // Act/Assert
        result = target.validateCovariateUpload("name", "./a/../b", null, "path", "covdir", mock(JsonCovariateConfiguration.class));
        assertThat(result).contains("Subdirectory not valid.");
        result = target.validateCovariateUpload("name", "./a/./b", null, "path", "covdir", mock(JsonCovariateConfiguration.class));
        assertThat(result).contains("Subdirectory not valid.");
        result = target.validateCovariateUpload("name", "./a//b", null, "path", "covdir", mock(JsonCovariateConfiguration.class));
        assertThat(result).contains("Subdirectory not valid.");
        result = target.validateCovariateUpload("name", "./a\\b", null, "path", "covdir", mock(JsonCovariateConfiguration.class));
        assertThat(result).contains("Subdirectory not valid.");
    }

    @Test
    public void validateCovariateUploadRejectsNonUniqueName() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();
        JsonCovariateConfiguration covariateConfiguration = mock(JsonCovariateConfiguration.class);
        JsonCovariateFile covariateFile = mock(JsonCovariateFile.class);
        when(covariateConfiguration.getFiles()).thenReturn(Arrays.asList(covariateFile));
        when(covariateFile.getName()).thenReturn("not unique");

        // Act
        Collection<String> result = target.validateCovariateUpload("not unique", "subdir", null, "path", "covdir", covariateConfiguration);

        // Assert
        assertThat(result).contains("Name not unique.");
    }

    @Test
    public void validateCovariateUploadRejectsPrexistingPath() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();
        JsonCovariateConfiguration covariateConfiguration = mock(JsonCovariateConfiguration.class);
        when(covariateConfiguration.getFiles()).thenReturn(new ArrayList<JsonCovariateFile>());
        MockMultipartFile file = new MockMultipartFile(" ", new byte[1]);
        File existing = testFolder.newFile();
        FileUtils.write(existing, "exists");

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", file, existing.toString(), "covdir", covariateConfiguration);

        // Assert
        assertThat(result).contains("File already exists.");
    }

    @Test
    public void validateCovariateUploadRejectsPathNotUnderCovariateDirectory() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();
        JsonCovariateConfiguration covariateConfiguration = mock(JsonCovariateConfiguration.class);
        when(covariateConfiguration.getFiles()).thenReturn(new ArrayList<JsonCovariateFile>());
        MockMultipartFile file = new MockMultipartFile(" ", new byte[1]);
        String path = Paths.get(testFolder.getRoot().toString(), "asdfas").toFile().getAbsolutePath();
        String covdir = testFolder.newFolder().getAbsolutePath();

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", file, path, covdir, covariateConfiguration);

        // Assert
        assertThat(result).contains("Target path not valid.");
    }

    @Test
    public void validateCovariateUploadAcceptsValidParameters() throws Exception {
        // Arrange
        CovariatesControllerValidator target = new CovariatesControllerValidator();
        JsonCovariateConfiguration covariateConfiguration = mock(JsonCovariateConfiguration.class);
        when(covariateConfiguration.getFiles()).thenReturn(new ArrayList<JsonCovariateFile>());
        MockMultipartFile file = new MockMultipartFile(" ", new byte[1]);
        String path = Paths.get(testFolder.getRoot().toString(), "asdfas").toFile().getAbsolutePath();
        String covdir = testFolder.getRoot().getAbsolutePath();

        // Act
        Collection<String> result = target.validateCovariateUpload("name", "subdir", file, path, covdir, covariateConfiguration);

        // Assert
        assertThat(result).hasSize(0);
    }
}
