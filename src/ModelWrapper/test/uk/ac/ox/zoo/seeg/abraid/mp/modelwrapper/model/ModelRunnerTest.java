package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

/**
 * Tests the ModelRunnerImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunnerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); // SUPPRESS CHECKSTYLE VisibilityModifier

    @Test
    public void runModelProvisionsADirectoryForTheRun() throws Exception {
        // Arrange
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class)))
                .thenReturn(testFolder.getRoot());

        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        RunConfiguration config = new RunConfiguration(null, null, null, 0, "");

        // Act
        target.runModel(config, null);

        // Assert
        verify(mockWorkspaceProvisioner, times(1)).provisionWorkspace(refEq(config), isNull(GeoJsonDiseaseOccurrenceFeatureCollection.class));
    }

    @Test
    public void runModelTriggersProcess() throws Exception {
        // Arrange
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class)))
                .thenReturn(testFolder.getRoot());

        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        RunConfiguration config = new RunConfiguration(null, null, null, 0, "");

        // Act
        target.runModel(config, null);

        // Assert
        verify(mockProcessRunner, times(1)).run(any(ModelProcessHandler.class));
    }

    @Test
    public void runModelCreatesANewProcessRunnerWithCorrectParameters() throws Exception {
        // Arrange
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        File expectedScript = new File("foo/script");
        when(mockWorkspaceProvisioner.provisionWorkspace(any(RunConfiguration.class), any(GeoJsonDiseaseOccurrenceFeatureCollection.class)))
                .thenReturn(expectedScript);

        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory, mockWorkspaceProvisioner);

        File expectedR = new File("e1");
        File expectedBase = new File("base");
        int expectedTimeout = 10;
        RunConfiguration config = new RunConfiguration(expectedR, expectedBase, null, expectedTimeout, "");

        // Act
        target.runModel(config, null);

        // Assert
        ArgumentCaptor<String[]> stringArgsCaptor = ArgumentCaptor.forClass(String[].class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, File>> fileArgsCaptor = (ArgumentCaptor<Map<String, File>>) (Object) ArgumentCaptor.forClass(Map.class);

        verify(mockProcessRunnerFactory, times(1))
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
}
