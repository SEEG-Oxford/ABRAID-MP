package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the WorkspaceProvisionerImpl class.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerTest {
    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void provisionWorkspaceShouldCreateDirectoryAtCorrectPath() {
        // Arrange
        WorkspaceProvisioner target = new WorkspaceProvisionerImpl();
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

}
