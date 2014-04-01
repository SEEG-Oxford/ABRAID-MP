package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Provides a trigger for setting up the directory in which a model will run.
 * Copyright (c) 2014 University of Oxford
 */
public class WorkspaceProvisionerImpl implements WorkspaceProvisioner {
    private static final String MODEL_CODE_DIRECTORY_NAME = "model";
    private static final String MODEL_DATA_DIRECTORY_NAME = "data";

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
     * @param modelData The data to use in the model.
     * @throws IOException Thrown if the directory can not be correctly provisioned.
     * @return The model wrapper script file to run.
     */
    @Override
    public File provisionWorkspace(RunConfiguration configuration, GeoJsonDiseaseOccurrenceFeatureCollection modelData)
            throws IOException {
        // Create directories
        Path workingDirectoryPath = Paths.get(
                configuration.getBaseDir().getAbsolutePath(),
                configuration.getRunName() + "-" + UUID.randomUUID().toString());

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
            throw new IOException("Directory structure could not be created.");
        }

        // Copy input data
        inputDataManager.writeData(modelData, dataDirectory);

        // Copy model
        sourceCodeManager.provisionVersion(configuration.getModelVersion(), modelDirectory);

        // Template script
        File runScript = scriptGenerator.generateScript(configuration, workingDirectory, false);
        return runScript;
    }
}
