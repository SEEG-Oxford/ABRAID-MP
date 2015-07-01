package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import jersey.repackaged.com.google.common.collect.Iterables;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.AdminUnitRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.CodeRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.ExecutionRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data.InputDataManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsArgAt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests the WorkspaceProvisionerImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void provisionWorkspaceShouldCreateDirectoryAtCorrectPath() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, mock(SourceCodeManager.class), mock(InputDataManager.class));
        File expectedBasePath = testFolder.getRoot();
        String expectedRunName = "bar";
        File tempDataDir = testFolder.newFolder();
        RunConfiguration config = new RunConfiguration(
                expectedRunName, expectedBasePath, tempDataDir,
                new CodeRunConfiguration("", ""),
                new ExecutionRunConfiguration(new File(""), 60000, 1, false, false),
                new AdminUnitRunConfiguration(true, "", "", "", "", ""));
        Paths.get(tempDataDir.getAbsolutePath(), "covariates").toFile().mkdir();

        // Act
        File script = target.provisionWorkspace(config, null, null);
        File result = script.getParentFile();

        // Assert
        assertThat(result.getParentFile()).isEqualTo(expectedBasePath);
        assertThat(result.getName()).isEqualTo(expectedRunName);
        assertThat(result).exists();
        assertThat(result).isDirectory();
    }

    @Test
    public void provisionWorkspaceShouldGenerateRunScript() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        File expectedScript = new File("foobar");
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, mock(SourceCodeManager.class), mock(InputDataManager.class));
        RunConfiguration config = createRunConfiguration("foo", testFolder.newFolder(), testFolder.newFolder());
        when(scriptGenerator.generateScript(eq(config), any(File.class))).thenReturn(expectedScript);

        // Act
        File script = target.provisionWorkspace(config, null, null);

        // Assert
        verify(scriptGenerator).generateScript(eq(config), any(File.class));
        assertThat(script).isEqualTo(expectedScript);
    }

    @Test
    public void provisionWorkspaceProvisionsModelCode() throws Exception {
        // Arrange
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        InputDataManager inputDataManager = mock(InputDataManager.class);
        when(scriptGenerator.generateScript(any(RunConfiguration.class), any(File.class))).then(returnsArgAt(1));
        String expectedVersion = "foobar";
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, sourceCodeManager, inputDataManager);
        File tempDataDir = testFolder.newFolder();
        RunConfiguration config =
                new RunConfiguration("foo", testFolder.newFolder(), tempDataDir, new CodeRunConfiguration(expectedVersion, ""), null, null);
        Paths.get(tempDataDir.getAbsolutePath(), "covariates").toFile().mkdir();

        // Act
        File runDir = target.provisionWorkspace(config, null, null);

        // Assert
        ArgumentCaptor<String> versionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<File> directoryCaptor = ArgumentCaptor.forClass(File.class);
        verify(sourceCodeManager).provisionVersion(versionCaptor.capture(), directoryCaptor.capture());
        assertThat(versionCaptor.getValue()).isEqualTo(expectedVersion);
        assertThat(directoryCaptor.getValue().getParentFile()).isEqualTo(runDir);
        assertThat(directoryCaptor.getValue().getName()).isEqualTo("model");
    }

    @Test
    public void provisionWorkspaceProvisionsInputData() throws Exception {
        // Arrange
        SourceCodeManager sourceCodeManager = mock(SourceCodeManager.class);
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        InputDataManager inputDataManager = mock(InputDataManager.class);
        when(scriptGenerator.generateScript(any(RunConfiguration.class), any(File.class))).then(returnsArgAt(1));
        GeoJsonDiseaseOccurrenceFeatureCollection expectedData = mock(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, sourceCodeManager, inputDataManager);
        RunConfiguration config = createRunConfiguration("foo", testFolder.newFolder(), testFolder.newFolder());

        // Act
        File runDir = target.provisionWorkspace(config, expectedData, null);

        // Assert
        ArgumentCaptor<GeoJsonDiseaseOccurrenceFeatureCollection> dataCaptor = ArgumentCaptor.forClass(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        ArgumentCaptor<File> directoryCaptor = ArgumentCaptor.forClass(File.class);
        verify(inputDataManager).writeOccurrenceData(dataCaptor.capture(), directoryCaptor.capture());
        assertThat(dataCaptor.getValue()).isEqualTo(expectedData);
        assertThat(directoryCaptor.getValue().getParentFile()).isEqualTo(runDir);
        assertThat(directoryCaptor.getValue().getName()).isEqualTo("data");
    }

    @Test
    public void provisionWorkspaceShouldThrowIfWorkspaceDirectoryCanNotBeCreated() throws Exception {
        // Arrange
        File notAValidDirectory = testFolder.newFile();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(mock(ScriptGenerator.class), mock(SourceCodeManager.class), mock(InputDataManager.class));
        RunConfiguration conf = new RunConfiguration("", notAValidDirectory, null, null, null, null);

        // Act
        catchException(target).provisionWorkspace(conf, null, null);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    @Test
    public void provisionWorkspaceShouldCopyCovariates() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, mock(SourceCodeManager.class), mock(InputDataManager.class));
        File expectedBasePath = testFolder.newFolder();
        File tempDir = testFolder.newFolder();
        String expectedRunName = "bar";
        RunConfiguration config = new RunConfiguration(
                expectedRunName, expectedBasePath, tempDir,
                new CodeRunConfiguration("", ""),
                new ExecutionRunConfiguration(new File(""), 60000, 1, false, false),
                new AdminUnitRunConfiguration(true, "", "", "", "", ""));
        File tempCovDir = Paths.get(tempDir.getAbsolutePath(), "covariates").toFile();
        tempCovDir.mkdir();
        Paths.get(tempCovDir.getAbsolutePath(), "sub").toFile().mkdir();
        FileUtils.writeStringToFile(Paths.get(tempCovDir.getAbsolutePath(), "a").toFile(), "Abc");
        FileUtils.writeStringToFile(Paths.get(tempCovDir.getAbsolutePath(), "b").toFile(), "aBc");
        FileUtils.writeStringToFile(Paths.get(tempCovDir.getAbsolutePath(), "sub", "c").toFile(), "abC");

        // Act
        File script = target.provisionWorkspace(config, null, null);
        File result = script.getParentFile();

        // Assert
        File covDir = Paths.get(result.getAbsolutePath(), "covariates").toFile();
        assertThat(covDir).exists();
        assertThat(covDir).isDirectory();
        Collection<File> files = FileUtils.listFiles(covDir, null, true);
        Collection<File> expectedFiles = FileUtils.listFiles(tempCovDir, null, true);
        assertThat(files).hasSameSizeAs(expectedFiles);
        assertThat(covDir.toPath().relativize(Iterables.get(files, 0).toPath())).isEqualTo(tempCovDir.toPath().relativize(Iterables.get(expectedFiles, 0).toPath()));
        assertThat(covDir.toPath().relativize(Iterables.get(files, 1).toPath())).isEqualTo(tempCovDir.toPath().relativize(Iterables.get(expectedFiles, 1).toPath()));
        assertThat(covDir.toPath().relativize(Iterables.get(files, 2).toPath())).isEqualTo(tempCovDir.toPath().relativize(Iterables.get(expectedFiles, 2).toPath()));
    }

    @Test
    public void provisionWorkspaceShouldThrowIfCovariatesCanNotBeCopied() throws Exception {
        ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator, mock(SourceCodeManager.class), mock(InputDataManager.class));
        File expectedBasePath = testFolder.newFolder();
        File tempDir = testFolder.newFolder();
        String expectedRunName = "bar";
        RunConfiguration config = new RunConfiguration(
                expectedRunName, expectedBasePath, tempDir,
                new CodeRunConfiguration("", ""),
                new ExecutionRunConfiguration(new File(""), 60000, 1, false, false),
                new AdminUnitRunConfiguration(true, "", "", "", "", ""));
        File tempCovDir = Paths.get(tempDir.getAbsolutePath(), "covariates").toFile();

        // Act
        catchException(target).provisionWorkspace(config, null, null);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

    private RunConfiguration createRunConfiguration(String runName, File baseDir, File tempDir) {
        Paths.get(tempDir.getAbsolutePath(), "covariates").toFile().mkdir();
        RunConfiguration conf = mock(RunConfiguration.class);
        when(conf.getWorkingDirectoryPath()).thenReturn(Paths.get(baseDir.getAbsolutePath(), runName));
        when(conf.getTempDataDir()).thenReturn(tempDir);
        when(conf.getCodeConfig()).thenReturn(mock(CodeRunConfiguration.class));
        return conf;
    }
}
