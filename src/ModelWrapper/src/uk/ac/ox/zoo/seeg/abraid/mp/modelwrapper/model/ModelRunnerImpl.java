package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model;

import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Provides an entry point for model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunnerImpl implements ModelRunner {
    // The id/key to use for script file path substitution
    private static final String SCRIPT_FILE_ID = "script_file";

    // The arguments to pass to R
    private static final String[] R_OPTIONS = {"--no-save", "--slave", "-f", "${" + SCRIPT_FILE_ID + "}"};

    private ProcessRunnerFactory processRunnerFactory;

    public ModelRunnerImpl(ProcessRunnerFactory processRunnerFactory) {
        this.processRunnerFactory = processRunnerFactory;
    }

    /**
     * Starts a new model run with the given configuration.
     * @param configuration The model run configuration.
     * @param modelStatusReporter The status reporter to call with the results of the model.
     * @return The process handler for the launched process.
     * @throws ProcessException Thrown in response to errors in the model.
     * @throws IOException Thrown if the workspace cannot be correctly provisioned.
     */
    @Override
    public ModelProcessHandler runModel(RunConfiguration configuration,
                                        ModelStatusReporter modelStatusReporter)
            throws ProcessException, IOException {
        File scriptFile = Paths.get(configuration.getWorkingDirectoryPath().toString(), "modelRun.R").toFile();

        // Run model
        HashMap<String, File> fileArguments = new HashMap<>();
        fileArguments.put(SCRIPT_FILE_ID, scriptFile);
        ProcessRunner processRunner = processRunnerFactory.createProcessRunner(
                scriptFile.getParentFile(),
                configuration.getExecutionConfig().getRPath(),
                R_OPTIONS,
                fileArguments,
                configuration.getExecutionConfig().getMaxRuntime());

        ModelProcessHandler processHandler = new ModelProcessHandler(modelStatusReporter);
        processRunner.run(processHandler);
        return processHandler;
    }
}
