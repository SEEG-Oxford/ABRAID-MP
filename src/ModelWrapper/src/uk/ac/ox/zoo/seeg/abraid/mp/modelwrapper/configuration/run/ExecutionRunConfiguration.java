package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run;

import java.io.File;

/**
 * An immutable data structure to hold the execution environment related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class ExecutionRunConfiguration {
    private final File rPath;
    private final int maxRuntime;
    private final int maxCPUs;
    private final boolean dryRun;

    public ExecutionRunConfiguration(File rPath, int maxRuntime, int maxCPUs, boolean dryRun) {
        this.rPath = rPath;
        this.maxRuntime = maxRuntime;
        this.maxCPUs = maxCPUs;
        this.dryRun = dryRun;
    }

    public File getRPath() {
        return rPath;
    }

    public int getMaxRuntime() {
        return maxRuntime;
    }

    public int getMaxCPUs() {
        return maxCPUs;
    }

    public boolean getDryRunFlag() {
        return dryRun;
    }
}
