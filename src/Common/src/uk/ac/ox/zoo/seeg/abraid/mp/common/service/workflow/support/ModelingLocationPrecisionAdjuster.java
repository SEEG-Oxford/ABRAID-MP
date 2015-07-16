package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import java.util.Arrays;
import java.util.List;

/**
 * A utility class to adjust the location precision of occurrence data when serializing (for modelling/download).
 * Replaces the specified location precision with PRECISE, for admin units that are too small to appear in the
 * admin qc level raster files.
 * Copyright (c) 2015 University of Oxford
 */
public class ModelingLocationPrecisionAdjuster {
    private static Integer precise = LocationPrecision.PRECISE.getModelValue();
    private List<String> gaulsToAdjust;

    public ModelingLocationPrecisionAdjuster(String[] gaulsToAdjust) {
        this.gaulsToAdjust = Arrays.asList(gaulsToAdjust);
    }

    /**
     * Adjusts the location precision, if required.
     * @param locationPrecision The precision of the location.
     * @param gaulCode The gaul code of the location.
     * @return The adjusted location precision.
     */
    public int adjust(int locationPrecision, String gaulCode) {
        return gaulsToAdjust.contains(gaulCode) ? precise : locationPrecision;
    }
}
