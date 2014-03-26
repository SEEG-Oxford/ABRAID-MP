package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.*;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;

import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the commons exec based R script runner.
 * Copyright (c) 2014 University of Oxford
 */
public class CommonsExecIntegrationTest {
    private static final int SUCCESSFUL = 0;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); // SUPPRESS CHECKSTYLE VisibilityModifier

    /**
     * Verifies that subprocesses can be started.
     */
    @Test
    public void shouldBeAbleToRunEmptyScript() throws Exception {
        // Arrange
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 60000, "");
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[]{""});
            }
        });

        // Act
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();

        // Assert
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
    }

    /**
     * Verifies that stdout is correctly wired.
     */
    @Test
    public void shouldBeAbleToRunHelloWorldScript() throws Exception {
        // Arrange
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 60000, "");
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[]{"cat('Hello, world!\n')"});
            }
        });

        // Act
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();
        String result = processHandler.getOutputStream().toString();

        // Assert
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        assertThat(result).startsWith("Hello, world!");
    }

    /**
     * Verifies that stderr is correctly wired.
     */
    @Test
    public void shouldBeAbleToRunHelloErrorScript() throws Exception {
        // Arrange
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 60000, "");
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[]{"write('Hello, world!\n', stderr())"});
            }
        });

        // Act
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();
        String result = processHandler.getErrorStream().toString();

        // Assert
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        assertThat(result).startsWith("Hello, world!");
    }

    /**
     * Verifies that stdin is correctly wired.
     */
    @Test
    public void shouldBeAbleToRunHelloNameScript() throws Exception {
        // Arrange
        RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 60000, "");
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                return createScript(testFolder, new String[]{
                        "name <- readLines(file(\"stdin\"),1)",
                        "cat('Hello, ', name, '!\n', sep='')"});
            }
        });
        String expectedName = "Bob";
        PipedOutputStream writer = new PipedOutputStream();

        // Act
        ProcessHandler processHandler = runner.runModel(config);
        processHandler.getInputStream().connect(writer);
        writer.write(expectedName.getBytes());
        writer.flush();
        writer.close();
        int exitCode = processHandler.waitForCompletion();
        String result = processHandler.getOutputStream().toString();

        // Assert
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        assertThat(result).startsWith("Hello, " + expectedName + "!");
    }

    /**
     * Verifies that the generated modelRun script valid R and can be run.
     */
    @Test
    public void shouldBeAbleToDoDryRunOfModel() throws Exception {
        // Arrange
        final RunConfiguration config = new RunConfiguration(findR(), testFolder.getRoot(), "foo", 60000, "");
        WorkspaceProvisioner mockWorkspaceProvisioner = mock(WorkspaceProvisioner.class);
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory(), mockWorkspaceProvisioner);
        when(mockWorkspaceProvisioner.provisionWorkspace(config)).thenAnswer(new Answer<File>() {
            public File answer(InvocationOnMock invocationOnMock) throws Throwable {
                ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
                return scriptGenerator.generateScript(config, testFolder.getRoot(), true);
            }
        });

        // Act
        ProcessHandler processHandler = runner.runModel(config);
        int exitCode = processHandler.waitForCompletion();
        String output = processHandler.getOutputStream().toString();

        // Assert
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
        //assertThat(output)
    }

    /**
     * Find the R executable. This is not very robust and should be reconsidered at some point.
     * @return The R executable
     */
    private static File findR() {
        OSChecker osChecker = new OSCheckerImpl();
        if (osChecker.isWindows()) {
            return new File("C:\\Program Files\\R\\R-3.0.2\\bin\\x64\\R.exe");
        } else {
            return new File("/usr/bin/R");
        }
    }

    private static File createScript(TemporaryFolder baseDir, String[] lines) throws IOException {
        File directory = baseDir.newFolder();
        File script = Files.createFile(Paths.get(directory.getPath(), "script.R")).toFile();
        PrintWriter writer = new PrintWriter(script);
        for (String line : lines) {
            writer.println(line);
        }
        writer.close();
        return script;
    }
}
