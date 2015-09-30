package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

import java.io.File;

/**
 * An immutable data structure to hold the execution environment related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class ExecutionRunConfiguration {
    private final File rPath;
    private final int maxRuntime;

    public ExecutionRunConfiguration(File rPath, int maxRuntime) {
        this.rPath = rPath;
        this.maxRuntime = maxRuntime;
    }

    public File getRPath() {
        return rPath;
    }

    public int getMaxRuntime() {
        return maxRuntime;
    }
}
