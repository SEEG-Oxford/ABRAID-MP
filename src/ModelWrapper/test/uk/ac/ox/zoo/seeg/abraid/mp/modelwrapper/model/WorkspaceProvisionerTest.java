package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the WorkspaceProvisionerImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerTest {
    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void provisionWorkspaceShouldCreateDirectoryAtCorrectPath() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = new FreemarkerScriptGenerator();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator);
        File expectedBasePath = testFolder.getRoot();
        String expectedRunName = "bar";
        RunConfiguration config = new RunConfiguration(null, expectedBasePath, expectedRunName, 0);

        // Act
        File script = target.provisionWorkspace(config);
        File result = script.getParentFile();

        // Assert
        assertThat(result.getParentFile()).isEqualTo(expectedBasePath);
        assertThat(result.getName()).startsWith(expectedRunName);
        assertThat(result.getName()).matches(expectedRunName + "-" + UUID_REGEX);
        assertThat(result).exists();
        assertThat(result).isDirectory();
    }

    @Test
    public void provisionWorkspaceShouldGenerateRunScript() throws Exception {
        // Arrange
        ScriptGenerator scriptGenerator = mock(ScriptGenerator.class);
        File expectedScript = new File("foobar");
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(scriptGenerator);
        RunConfiguration config = new RunConfiguration(null, testFolder.getRoot(), "", 0);
        when(scriptGenerator.generateScript(eq(config), any(File.class), eq(false))).thenReturn(expectedScript);

        // Act
        File script = target.provisionWorkspace(config);

        // Assert
        verify(scriptGenerator, times(1)).generateScript(eq(config), any(File.class), eq(false));
        assertThat(script).isEqualTo(expectedScript);
    }

    @Test
    public void provisionWorkspaceShouldThrowIfDirectoryCanNotBeCreated() throws Exception {
        // Arrange
        File notAValidDirectory = testFolder.newFile();
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl(mock(ScriptGenerator.class));
        RunConfiguration conf = new RunConfiguration(null, notAValidDirectory, "", 0);

        // Act
        catchException(target).provisionWorkspace(conf);
        Exception result = caughtException();

        // Assert
        assertThat(result).isInstanceOf(IOException.class);
    }

}
