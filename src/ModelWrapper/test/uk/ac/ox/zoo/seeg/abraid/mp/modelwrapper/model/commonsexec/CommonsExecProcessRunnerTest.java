package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.Executor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunner;

import java.io.File;
import java.util.HashMap;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the CommonsExecProcessRunner class.
 * Copyright (c) 2014 University of Oxford
 */
public class CommonsExecProcessRunnerTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void runCallsExecuteWithCorrectCommand() throws Exception {
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
        target.run(mock(ProcessHandler.class));

        // Assert
        ArgumentCaptor<CommandLine> commandLineCaptor = ArgumentCaptor.forClass(CommandLine.class);
        verify(mockExecutor).execute(commandLineCaptor.capture(), any(ExecuteResultHandler.class));
        assertThat(commandLineCaptor.getValue().toString()).isEqualTo(expectation);
    }

    @Test
    public void runSetsCorrectDirectory() throws Exception {
        // Arrange
        Executor mockExecutor = mock(Executor.class);
        File expectedWorkingDir = testFolder.getRoot();
        ProcessRunner target = new CommonsExecProcessRunner(mockExecutor, expectedWorkingDir, new File("exe"),
                new String[0], new HashMap<String, File>(), 10);

        // Act
        target.run(mock(ProcessHandler.class));

        // Assert
        verify(mockExecutor).setWorkingDirectory(eq(expectedWorkingDir));
    }

    @Test
    public void runWrapsInnerExceptionInErrorCase() throws Exception {
        // Arrange
        Executor mockExecutor = mock(Executor.class);
        ExecuteException expectedCause = new ExecuteException("foo", -1);
        doThrow(expectedCause).when(mockExecutor).execute(any(CommandLine.class), any(ExecuteResultHandler.class));

        ProcessRunner target = new CommonsExecProcessRunner(mockExecutor, testFolder.getRoot(), new File("exe"),
                new String[0], new HashMap<String, File>(), 10);

        // Act
        catchException(target).run(mock(ProcessHandler.class));
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(ProcessException.class);
        assertThat(result.getCause()).isEqualTo(expectedCause);
    }

    @Test
    public void runCanNotBeCalledMoreThanOnce() throws Exception {
        // Arrange
        Executor mockExecutor = mock(Executor.class);
        ProcessRunner target = new CommonsExecProcessRunner(mockExecutor, testFolder.getRoot(), new File("exe"),
                new String[0], new HashMap<String, File>(), 10);

        // Act
        catchException(target).run(mock(ProcessHandler.class)); // Once
        Exception firstRunException = caughtException();

        catchException(target).run(mock(ProcessHandler.class)); //Twice
        Exception secondRunException = caughtException();

        // Assert
        assertThat(firstRunException).isNull();
        assertThat(secondRunException).isInstanceOf(ProcessException.class);
    }
}
