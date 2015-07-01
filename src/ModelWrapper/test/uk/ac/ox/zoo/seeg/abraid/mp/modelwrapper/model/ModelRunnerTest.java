package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.AdminUnitRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.CodeRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.ExecutionRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.googlecode.catchexception.CatchException.catchException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.refEq;

/**
 * Tests the ModelRunnerImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunnerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void runModelProvisionsADirectoryForTheRun() throws Exception {
        // Arrange
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class)))
                .thenReturn(testFolder.getRoot());

        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        RunConfiguration config = createBasicRunConfiguration();

        // Act
        target.runModel(config, null, null, null);

        // Assert
        verify(mockWorkspaceProvisioner).provisionWorkspace(refEq(config), isNull(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class));
    }

    @Test
    public void runModelDeletesTheTemporaryDataDirectoryForTheRunAfterProvisioning() throws Exception {
        // Arrange
        RunConfiguration config = createBasicRunConfiguration();
        final File tempDir = config.getTempDataDir();

        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class)))
                .thenAnswer(new Answer<File>() {
                    @Override
                    public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                        assertThat(tempDir).exists();
                        return testFolder.getRoot();
                    }
                });

        final ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenAnswer(new Answer<ProcessRunner>() {
                    @Override
                    public ProcessRunner answer(InvocationOnMock invocationOnMock) throws Throwable {
                        assertThat(tempDir).doesNotExist();
                        return mockProcessRunner;
                    }
                });

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        // Act
        assertThat(tempDir).exists();
        target.runModel(config, null, null, null);

        // Assert
        assertThat(tempDir).doesNotExist();
    }

    @Test
    public void runModelDeletesTheTemporaryDataDirectoryForTheRunEvenIfProvisioningThrows() throws Exception {
        // Arrange
        RunConfiguration config = createBasicRunConfiguration();
        final File tempDir = config.getTempDataDir();

        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class)))
                .thenAnswer(new Answer<File>() {
                    @Override
                    public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                        assertThat(tempDir).exists();
                        throw new IOException();
                    }
                });

        final ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenAnswer(new Answer<ProcessRunner>() {
                    @Override
                    public ProcessRunner answer(InvocationOnMock invocationOnMock) throws Throwable {
                        assertThat(tempDir).doesNotExist();
                        return mockProcessRunner;
                    }
                });

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        // Act
        assertThat(tempDir).exists();
        catchException(target).runModel(config, null, null, null);

        // Assert
        assertThat(tempDir).doesNotExist();
    }

    @Test
    public void runModelTriggersProcess() throws Exception {
        // Arrange
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class)))
                .thenReturn(testFolder.getRoot());

        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        RunConfiguration config = createBasicRunConfiguration();

        // Act
        target.runModel(config, null, null, null);

        // Assert
        verify(mockProcessRunner).run(any(ModelProcessHandler.class));
    }

    @Test
    public void runModelCreatesANewProcessRunnerWithCorrectParameters() throws Exception {
        // Arrange
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        File expectedScript = new File("foo/script");
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class), anyMapOf(Integer.class, Integer.class)))
                .thenReturn(expectedScript);

        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        File expectedR = new File("e1");
        File expectedBase = new File("base");
        int expectedTimeout = 10;
        RunConfiguration config =
                new RunConfiguration(null, expectedBase, null, null, new ExecutionRunConfiguration(expectedR, expectedTimeout, 1, false, true), null);

        // Act
        target.runModel(config, null, null, null);

        // Assert
        ArgumentCaptor<String[]> stringArgsCaptor = ArgumentCaptor.forClass(String[].class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, File>> fileArgsCaptor = (ArgumentCaptor<Map<String, File>>) (Object) ArgumentCaptor.forClass(Map.class);

        verify(mockProcessRunnerFactory)
                .createProcessRunner(eq(new File("foo")), eq(expectedR), stringArgsCaptor.capture(), fileArgsCaptor.capture(), eq(expectedTimeout));

        String[] stringArgs = stringArgsCaptor.getValue();
        Map<String, File> fileArgs = fileArgsCaptor.getValue();

        assertThat(stringArgs).hasSize(4);
        assertThat(stringArgs[0]).isEqualTo("--no-save");
        assertThat(stringArgs[1]).isEqualTo("--slave");
        assertThat(stringArgs[2]).isEqualTo("-f");
        String key = stringArgs[3].substring(2, stringArgs[3].length() - 1);
        assertThat(fileArgs).containsKey(key);
        assertThat(fileArgs.get(key)).isEqualTo(expectedScript);
    }

    private RunConfiguration createBasicRunConfiguration() throws IOException {
        return new RunConfiguration(
                "foo", testFolder.newFolder(), testFolder.newFolder(),
                new CodeRunConfiguration("", ""),
                new ExecutionRunConfiguration(new File(""), 60000, 1, false, false),
                new AdminUnitRunConfiguration(true, "", "", "", "", ""));
    }
}
