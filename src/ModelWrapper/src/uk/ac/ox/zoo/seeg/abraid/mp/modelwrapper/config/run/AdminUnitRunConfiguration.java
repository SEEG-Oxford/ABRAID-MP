package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run;

/**
 * An immutable data structure to hold the admin units related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitRunConfiguration {
    private final boolean useGlobalRasterFile;
    private final String admin0RasterFile;
    private final String admin1RasterFile;
    private final String admin2RasterFile;
    private final String tropicalRasterFile;
    private final String globalRasterFile;

    public AdminUnitRunConfiguration(boolean useGlobalRasterFile,
                                     String admin0RasterFile, String admin1RasterFile, String admin2RasterFile,
                                     String tropicalRasterFile, String globalRasterFile) {
        this.useGlobalRasterFile = useGlobalRasterFile;
        this.admin0RasterFile = admin0RasterFile;
        this.admin1RasterFile = admin1RasterFile;
        this.admin2RasterFile = admin2RasterFile;
        this.tropicalRasterFile = tropicalRasterFile;
        this.globalRasterFile = globalRasterFile;
    }

    public boolean getUseGlobalRasterFile() {
        return useGlobalRasterFile;
    }

    public String getAdmin0RasterFile() {
        return admin0RasterFile;
    }

    public String getAdmin1RasterFile() {
        return admin1RasterFile;
    }

    public String getAdmin2RasterFile() {
        return admin2RasterFile;
    }

    public String getTropicalRasterFile() {
        return tropicalRasterFile;
    }

    public String getGlobalRasterFile() {
        return globalRasterFile;
    }
}

