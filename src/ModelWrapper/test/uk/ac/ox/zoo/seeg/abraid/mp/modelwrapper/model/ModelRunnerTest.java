package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.ExecutionRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

/**
* Tests the ModelRunnerImpl class.
* Copyright (c) 2014 University of Oxford
*/
public class ModelRunnerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void runModelTriggersProcess() throws Exception {
        // Arrange
        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory);

        RunConfiguration config = createBasicRunConfiguration();

        // Act
        target.runModel(config, null);

        // Assert
        verify(mockProcessRunner).run(any(ModelProcessHandler.class));
    }

    @Test
    public void runModelCreatesANewProcessRunnerWithCorrectParameters() throws Exception {
        // Arrange
        ProcessRunner mockProcessRunner = mock(ProcessRunner.class);
        ProcessRunnerFactory mockProcessRunnerFactory = mock(ProcessRunnerFactory.class);
        when(mockProcessRunnerFactory.createProcessRunner(any(File.class), any(File.class), any(String[].class), anyMapOf(String.class, File.class), anyInt()))
                .thenReturn(mockProcessRunner);

        ModelRunnerImpl target = new ModelRunnerImpl(mockProcessRunnerFactory);

        File expectedR = new File("e1");
        File expectedBase = testFolder.newFolder();
        String runName = "name";
        FileUtils.writeStringToFile(Paths.get(expectedBase.toString(), runName, "modelRun.R").toFile(), "\"Hello, World\"");
        int expectedTimeout = 10;
        RunConfiguration config =
                new RunConfiguration(runName, expectedBase, new ExecutionRunConfiguration(expectedR, expectedTimeout));

        // Act
        target.runModel(config, null);

        // Assert
        ArgumentCaptor<String[]> stringArgsCaptor = ArgumentCaptor.forClass(String[].class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, File>> fileArgsCaptor = (ArgumentCaptor<Map<String, File>>) (Object) ArgumentCaptor.forClass(Map.class);

        verify(mockProcessRunnerFactory)
                .createProcessRunner(eq(Paths.get(expectedBase.toString(), runName).toFile()), eq(expectedR), stringArgsCaptor.capture(), fileArgsCaptor.capture(), eq(expectedTimeout));

        String[] stringArgs = stringArgsCaptor.getValue();
        Map<String, File> fileArgs = fileArgsCaptor.getValue();

        assertThat(stringArgs).hasSize(4);
        assertThat(stringArgs[0]).isEqualTo("--no-save");
        assertThat(stringArgs[1]).isEqualTo("--slave");
        assertThat(stringArgs[2]).isEqualTo("-f");
        String key = stringArgs[3].substring(2, stringArgs[3].length() - 1);
        assertThat(fileArgs).containsKey(key);
        assertThat(fileArgs.get(key)).isEqualTo(Paths.get(expectedBase.toString(), runName, "modelRun.R").toFile());
    }

    private RunConfiguration createBasicRunConfiguration() throws IOException {
        File baseDir = testFolder.newFolder();
        String run = "foo";
        FileUtils.writeStringToFile(Paths.get(baseDir.toString(), run, "modelRun.R").toFile(), "\"Hello, World\"");
        return new RunConfiguration(
                run, baseDir,
                new ExecutionRunConfiguration(new File(""), 60000));
    }
}
