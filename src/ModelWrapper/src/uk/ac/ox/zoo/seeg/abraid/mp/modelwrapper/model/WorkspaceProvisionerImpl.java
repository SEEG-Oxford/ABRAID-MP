package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.UUID;

/**
 * Provides a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerImpl implements WorkspaceProvisioner {
    private static final Logger LOGGER = Logger.getLogger(WorkspaceProvisionerImpl.class);
    private static final String LOG_DIRECTORY_ERROR = "Workspace directory structure could not be created at %s";

    private static final String MODEL_CODE_DIRECTORY_NAME = "model";
    private static final String MODEL_DATA_DIRECTORY_NAME = "data";
    private static final String LOG_PROVISIONING_WORKSPACE = "Provisioning workspace at %s";
    private static final String LOG_WORKSPACE_SUCCESSFULLY_PROVISIONED = "Workspace successfully provisioned at %s";

    private final ScriptGenerator scriptGenerator;
    private final SourceCodeManager sourceCodeManager;
    private final InputDataManager inputDataManager;

    public WorkspaceProvisionerImpl(ScriptGenerator scriptGenerator,
                                    SourceCodeManager sourceCodeManager, InputDataManager inputDataManager) {
        this.scriptGenerator = scriptGenerator;
        this.sourceCodeManager = sourceCodeManager;
        this.inputDataManager = inputDataManager;
    }

    /**
     * Sets up the directory in which a model will run.
     * @param configuration The model run configuration options.
     * @param occurrenceData The occurrences to use in the model.
     * @param extentData The extents to model with.
     * @return The model wrapper script file to run.
     * @throws IOException Thrown if the directory can not be correctly provisioned.
     */
    @Override
    public File provisionWorkspace(RunConfiguration configuration,
                                   GeoJsonDiseaseOccurrenceFeatureCollection occurrenceData,
                                   Collection<Integer> extentData)
            throws IOException {
        // Create directories
        Path workingDirectoryPath = Paths.get(
                configuration.getBaseDir().getAbsolutePath(),
                configuration.getRunName() + "_" + UUID.randomUUID().toString());
        LOGGER.info(String.format(LOG_PROVISIONING_WORKSPACE, workingDirectoryPath.toString()));

        File workingDirectory = workingDirectoryPath.toFile();
        boolean workingDirectoryCreated = workingDirectory.mkdirs();

        File modelDirectory = null;
        if (workingDirectoryCreated) {
            Path modelDirectoryPath = Paths.get(workingDirectoryPath.toString(), MODEL_CODE_DIRECTORY_NAME);
            modelDirectory = modelDirectoryPath.toFile();
            workingDirectoryCreated = modelDirectory.mkdir();
        }

        File dataDirectory = null;
        if (workingDirectoryCreated) {
            Path dataDirectoryPath = Paths.get(workingDirectoryPath.toString(), MODEL_DATA_DIRECTORY_NAME);
            dataDirectory = dataDirectoryPath.toFile();
            workingDirectoryCreated = dataDirectory.mkdir();
        }

        if (!workingDirectoryCreated) {
            LOGGER.warn(String.format(LOG_DIRECTORY_ERROR, workingDirectoryPath.toString()));
            throw new IOException("Directory structure could not be created.");
        }

        // Rasterize extents
        LOGGER.info(String.format("TODO: Rasterize extent data. Global? %s. GAUL codes: %s",
                configuration.isGlobal(), extentData));

        // Copy input data
        inputDataManager.writeData(occurrenceData, dataDirectory);

        // Copy model
        sourceCodeManager.provisionVersion(configuration.getModelVersion(), modelDirectory);

        // Template script
        File runScript = scriptGenerator.generateScript(configuration, workingDirectory, false);

        LOGGER.info(String.format(LOG_WORKSPACE_SUCCESSFULLY_PROVISIONED, workingDirectoryPath.toString()));
        return runScript;
    }
}
