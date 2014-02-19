package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.DefaultExecutor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunnerFactory;

import java.io.File;
import java.util.Map;

/**
 * An Apache Commons Exec based implementation of ProcessRunnerFactory.
 * Copyright (c) 2014 University of Oxford
 */
public class CommonsExecProcessRunnerFactory implements ProcessRunnerFactory {
    /**
     * Creates a new ProcessRunner. Properly formatted and escaped versions of the file arguments will be substituted
     * for the equivalent execution arguments.
     * E.g. "${file}" will be replaced with the path to executionArguments.get("file")
     * @param workspaceDirectory The directory in which the process should run.
     * @param executable The executable which should be run.
     * @param executionArguments The arguments that should be passed to the execution.
     * @param fileArguments The file arguments that should be passed to the execution.
     * @param timeout The maximum allowed time (in ms) for which the process should be allow to run.
     * @return The new ProcessRunner.
     */
    @Override
    public ProcessRunner createProcessRunner(File workspaceDirectory, File executable, String[] executionArguments,
                                             Map<String, File> fileArguments, int timeout) {
        return new CommonsExecProcessRunner(
                new DefaultExecutor(), workspaceDirectory, executable, executionArguments, fileArguments, timeout);
    }
}
