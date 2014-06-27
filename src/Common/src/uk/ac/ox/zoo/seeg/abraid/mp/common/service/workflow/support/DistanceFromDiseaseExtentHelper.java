package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Helper class for determining the distance between a location and the disease extent.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DistanceFromDiseaseExtentHelper {
    private NativeSQL nativeSQL;

    public DistanceFromDiseaseExtentHelper(NativeSQL nativeSQL) {
        this.nativeSQL = nativeSQL;
    }

    /**
     * Finds the distance between the occurrence's location and the occurrence's disease group's current extent.
     * @param occurrence The occurrence.
     * @return The distance from the disease extent.
     */
    public Double findDistanceFromDiseaseExtent(DiseaseOccurrence occurrence) {
        Point locationPoint = occurrence.getLocation().getGeom();
        int diseaseGroupId = occurrence.getDiseaseGroup().getId();
        Double distance = null;

        if (occurrence.getDiseaseGroup().isGlobal() != null) {
            boolean isGlobal = occurrence.getDiseaseGroup().isGlobal();

            // We find the distance using a PostGIS query instead of using routines in the GeometryUtils class, because
            // loading the entire disease extent geometry into memory is likely to be inefficient
            distance = nativeSQL.findDistanceOutsideDiseaseExtent(diseaseGroupId, locationPoint);

            if (distance != null && distance == 0) {
                // If the distance is 0, the location is within the disease extent and we need to find the closest point
                // on the geometry via a different method
                distance = nativeSQL.findDistanceWithinDiseaseExtent(diseaseGroupId, isGlobal, locationPoint);
            }
        }

        return distance;
    }
}
