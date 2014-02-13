package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;

/**
 * A shim to map ProcessHandler methods onto Commons Exec ExecuteResultHandler calls.
 * Copyright (c) 2014 University of Oxford
 */
public class ForwardingExecuteResultHandler extends DefaultExecuteResultHandler {
    // ProcessHandler to forward calls to.
    private final ProcessHandler processHandler;

    public ForwardingExecuteResultHandler(ProcessHandler processHandler) {
        this.processHandler = processHandler;
    }

    /**
     * Called when asynchronous execution completes.
     * @param exitValue The return code of the process.
     */
    @Override
    public void onProcessComplete(int exitValue) {
        this.processHandler.onProcessComplete(exitValue);
    }

    /**
     * Called when asynchronous execution fails.
     * @param e Cause of failure.
     */
    @Override
    public void onProcessFailed(ExecuteException e) {
        this.processHandler.onProcessFailed(new ProcessException(e));
    }

}
