package uk.ac.ox.zoo.seeg.abraid.mp.common.config;

/**
 * An immutable data structure to hold modelling related configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class ModellingConfiguration {
    private final int maxCPUs;
    private final boolean dryRun;
    private final boolean verbose;

    public ModellingConfiguration(int maxCPUs, boolean verbose, boolean dryRun) {
        this.maxCPUs = maxCPUs;
        this.verbose = verbose;
        this.dryRun = dryRun;
    }

    public int getMaxCPUs() {
        return maxCPUs;
    }

    public boolean getDryRunFlag() {
        return dryRun;
    }

    public boolean getVerboseFlag() {
        return verbose;
    }
}
