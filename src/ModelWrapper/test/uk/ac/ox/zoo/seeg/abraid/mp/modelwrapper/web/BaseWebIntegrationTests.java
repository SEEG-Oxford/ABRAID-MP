package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Base class for model wrapper WAC based integration tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class BaseWebIntegrationTests {
    @Before
    public void makeConfigBackup() throws IOException {
        Files.copy(
                Paths.get("ModelWrapper/web/WEB-INF/modelwrapper-managed-testing.properties"),
                Paths.get("ModelWrapper/web/WEB-INF/modelwrapper-managed-testing.properties.old"),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @After
    public void rollbackConfig() throws IOException {
        Files.copy(
                Paths.get("ModelWrapper/web/WEB-INF/modelwrapper-managed-testing.properties.old"),
                Paths.get("ModelWrapper/web/WEB-INF/modelwrapper-managed-testing.properties"),
                StandardCopyOption.REPLACE_EXISTING);
        Files.delete(Paths.get("ModelWrapper/web/WEB-INF/modelwrapper-managed-testing.properties.old"));
    }
}
