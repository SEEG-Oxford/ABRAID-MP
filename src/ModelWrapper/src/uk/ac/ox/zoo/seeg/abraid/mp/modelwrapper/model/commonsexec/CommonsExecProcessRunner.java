package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.*;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessRunner;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * An Apache Commons Exec based implementation of ProcessRunner.
 * Copyright (c) 2014 University of Oxford
 */
class CommonsExecProcessRunner implements ProcessRunner {
    private static final Logger LOGGER = Logger.getLogger(CommonsExecProcessRunner.class);

    public static final int SUCCESS = 0;
    private final Executor executor;

    // The command used to start the process.
    private final CommandLine commandLine;

    // The directory the process runs in.
    private final File workspace;

    // The max allowable run time for the process in ms.
    private final int timeout;

    private boolean hasRun = false;

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
     * Starts the external process asynchronously.
     * @param processHandler The handler for execution complete callbacks and datastreams.
     * @throws ProcessException Throw in response to problems in the external process.
     */
    @Override
    public void run(ProcessHandler processHandler) throws ProcessException {
        if (hasRun) {
            throw new ProcessException(new Throwable("Can not use same process runner twice"));
        }

        LOGGER.info("Starting background process:");
        LOGGER.info("-> directory: " + workspace.toString());
        LOGGER.info("-> command: " + commandLine.toString());
        LOGGER.info("-> timeout: " + timeout);

        hasRun = true;

        executor.setWorkingDirectory(workspace);
        executor.setExitValue(SUCCESS);
        executor.setStreamHandler(new PumpStreamHandler(
                processHandler.getOutputStream(),
                processHandler.getErrorStream(),
                processHandler.getInputStream()));

        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);

        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());

        try {
            executor.execute(commandLine, new ForwardingExecuteResultHandler(processHandler));
        } catch (IOException e) {
            throw new ProcessException(e);
        }

    }
}
