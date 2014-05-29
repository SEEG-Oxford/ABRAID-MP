package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.commonsexec;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessException;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ProcessWaiter;

/**
 * A shim to map ProcessHandler methods onto Commons Exec ExecuteResultHandler calls.
 * Copyright (c) 2014 University of Oxford
 */
public class ForwardingExecuteResultHandler extends DefaultExecuteResultHandler implements ProcessWaiter {
    // ProcessHandler to forward calls to.
    private final ProcessHandler processHandler;

    public ForwardingExecuteResultHandler(ProcessHandler processHandler) {
        this.processHandler = processHandler;
        processHandler.setProcessWaiter(this);
    }

    /**
     * Called when asynchronous execution completes.
     * @param exitValue The return code of the process.
     */
    @Override
    public void onProcessComplete(int exitValue) {
        super.onProcessComplete(exitValue);
        this.processHandler.onProcessComplete();
    }

    /**
     * Called when asynchronous execution fails.
     * @param e Cause of failure.
     */
    @Override
    public void onProcessFailed(ExecuteException e) {
        super.onProcessFailed(e);
        this.processHandler.onProcessFailed(new ProcessException(e.getMessage(), e));
    }

    /**
     * Causes the current thread to wait, if necessary, until the owned process has terminated.
     * @return The exit code of the process.
     * @throws InterruptedException Thrown if the current thread is interrupted by another thread while it is waiting.
     */
    @Override
    public int waitForProcess() throws InterruptedException {
        this.waitFor();
        return this.getExitValue();
    }
}
