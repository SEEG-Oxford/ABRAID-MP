package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * An Apache Commons Exec based implementation of ProcessRunner.
 * Copyright (c) 2014 University of Oxford
 */
class CommonsExecProcessRunner implements ProcessRunner {
    private final Executor executor;

    // The command used to start the process.
    private final CommandLine commandLine;

    // The directory the process runs in.
    private final File workspace;

    // The max allowable run time for the process in ms.
    private final int timeout;

    CommonsExecProcessRunner(Executor executor, File workspaceDirectory, File executable, String[] executionArguments,
                             Map<String, File> fileArguments, int timeout) {
        this.executor = executor;
        this.workspace = workspaceDirectory;
        this.timeout = timeout;
        this.commandLine = new CommandLine(executable);

        for (String executionArgument : executionArguments) {
            commandLine.addArgument(executionArgument);
        }

        commandLine.setSubstitutionMap(fileArguments);
    }

    /**
     * Starts the external process.
     * @throws ProcessException Throw in response to problems in the external process.
     */
    @Override
    public void run() throws ProcessException {
        executor.setWorkingDirectory(workspace);
        executor.setExitValue(1);

        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);

        try {
            int exitValue = executor.execute(commandLine);
        } catch (IOException e) {
            throw new ProcessException(e);
        }
    }
}
