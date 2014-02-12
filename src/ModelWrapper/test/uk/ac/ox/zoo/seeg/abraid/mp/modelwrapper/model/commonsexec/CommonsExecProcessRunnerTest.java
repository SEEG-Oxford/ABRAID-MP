package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.Executor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunner;

import java.io.File;
import java.util.HashMap;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the CommonsExecProcessRunner class.
 * Copyright (c) 2014 University of Oxford
 */
public class CommonsExecProcessRunnerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void runCallsExecuteWithCorrectArguments() throws Exception {
        // Arrange
        Executor mockExecutor = mock(Executor.class);
        File executable = testFolder.newFile("file1");
        String[] executionArguments = new String[]{"arg1", "arg2", "${arg3}"};
        File script = testFolder.newFile("file2");
        HashMap<String, File> fileArguments = new HashMap<>();
        fileArguments.put("arg3", script);
        ProcessRunner target = new CommonsExecProcessRunner(mockExecutor, testFolder.getRoot(), executable,
                executionArguments, fileArguments, 10);
        String expectation = "[" + executable + ", arg1, arg2, " + script + "]";

        // Act
        target.run();

        // Assert
        ArgumentCaptor<CommandLine> commandLineCaptor = ArgumentCaptor.forClass(CommandLine.class);
        verify(mockExecutor, times(1)).execute(commandLineCaptor.capture());
        assertThat(commandLineCaptor.getValue().toString()).isEqualTo(expectation);
    }
}
