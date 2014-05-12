package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.run;

/**
 * An immutable data structure to hold the admin units related configuration for a single model run.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitRunConfiguration {
    private final boolean useGlobalShapefile;
    private final String admin1RasterFile;
    private final String admin2RasterFile;
    private final String tropicalShapeFile;
    private final String globalShapeFile;

    public AdminUnitRunConfiguration(boolean useGlobalShapefile,
                                     String admin1RasterFile, String admin2RasterFile,
                                     String tropicalShapeFile, String globalShapeFile) {
        this.useGlobalShapefile = useGlobalShapefile;
        this.admin1RasterFile = admin1RasterFile;
        this.admin2RasterFile = admin2RasterFile;
        this.tropicalShapeFile = tropicalShapeFile;
        this.globalShapeFile = globalShapeFile;
    }

    public boolean getUseGlobalShapefile() {
        return useGlobalShapefile;
    }

    public String getAdmin1RasterFile() {
        return admin1RasterFile;
    }

    public String getAdmin2RasterFile() {
        return admin2RasterFile;
    }

    public String getTropicalShapeFile() {
        return tropicalShapeFile;
    }

    public String getGlobalShapeFile() {
        return globalShapeFile;
    }
}
