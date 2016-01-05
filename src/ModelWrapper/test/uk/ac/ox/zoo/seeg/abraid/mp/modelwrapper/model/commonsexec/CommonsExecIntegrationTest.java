package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.FreemarkerScriptGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.ScriptGenerator;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.ExecutionRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunnerImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelStatusReporter;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSChecker;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.util.OSCheckerImpl;

import java.io.File;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Integration tests for the commons exec based R script runner.
* Copyright (c) 2014 University of Oxford
*/
public class CommonsExecIntegrationTest {
    private static final int SUCCESSFUL = 0;

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private RunConfiguration createRunConfig() throws IOException {
        return new RunConfiguration("foo", testDir.getRoot(), true, new ExecutionRunConfiguration(findR(), 60000));
    }

    /**
     * Verifies that subprocesses can be started.
     */
    @Test
    public void shouldBeAbleToRunEmptyScript() throws Exception {
        // Arrange
        RunConfiguration config = createRunConfig();
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory());
        createScript(testDir, new String[]{""});

        // Act
        ProcessHandler processHandler = runner.runModel(config, mock(ModelStatusReporter.class));
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
        RunConfiguration config = createRunConfig();
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory());

        createScript(testDir, new String[]{"cat('Hello, world!\n')"});

        // Act
        ProcessHandler processHandler = runner.runModel(config, mock(ModelStatusReporter.class));
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
        RunConfiguration config = createRunConfig();
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory());
        createScript(testDir, new String[]{"write('Hello, world!\n', stderr())"});

        // Act
        ProcessHandler processHandler = runner.runModel(config, mock(ModelStatusReporter.class));
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
        RunConfiguration config = createRunConfig();
        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory());
        createScript(testDir, new String[]{
                        "name <- readLines(file(\"stdin\"),1)",
                        "cat('Hello, ', name, '!\n', sep='')"});
        String expectedName = "Bob";
        PipedOutputStream writer = new PipedOutputStream();

        // Act
        ProcessHandler processHandler = runner.runModel(config, mock(ModelStatusReporter.class));
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
        final RunConfiguration config = createRunConfig();
        Files.createDirectories(Paths.get(config.getWorkingDirectoryPath().toString(), "covariates"));
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(diseaseGroup.getModelMode()).thenReturn("bhatt");
        when(diseaseGroup.getId()).thenReturn(123);
        Collection<CovariateFile> covariates = new ArrayList<>();

        ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
        scriptGenerator.generateScript(new ModellingConfiguration(1, true, true), config.getWorkingDirectoryPath().toFile(), diseaseGroup, covariates);

        ModelRunner runner = new ModelRunnerImpl(new CommonsExecProcessRunnerFactory());

        // Act
        ProcessHandler processHandler = runner.runModel(config, mock(ModelStatusReporter.class));
        int exitCode = processHandler.waitForCompletion();

        // Assert
        assertThat(exitCode).isEqualTo(SUCCESSFUL);
    }

    /**
     * Find the R executable. This is not very robust and should be reconsidered at some point.
     * @return The R executable
     */
    private static File findR() {
        OSChecker osChecker = new OSCheckerImpl();
        if (osChecker.isWindows()) {
            return new File(System.getenv("R_HOME") + "\\bin\\x64\\R.exe");
        } else {
            return new File("/usr/bin/R");
        }
    }

    private static File createScript(TemporaryFolder baseDir, String[] lines) throws IOException {
        File directory = baseDir.getRoot();
        Files.createDirectories(Paths.get(directory.getPath(), "foo")).toFile();
        File script = Files.createFile(Paths.get(directory.getPath(), "foo", "modelRun.R")).toFile();
        PrintWriter writer = new PrintWriter(script);
        for (String line : lines) {
            writer.println(line);
        }
        writer.close();
        return script;
    }
}
