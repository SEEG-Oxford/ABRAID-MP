package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;

/**
 * Interface for routines that interact with the PostGIS database using native SQL.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface NativeSQL {
    /**
     * Finds the first admin unit for global diseases that contains the specified point.
     * @param point The point.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    Integer findAdminUnitGlobalThatContainsPoint(Point point, Character adminLevel);

    /**
     * Finds the first admin unit for tropical diseases that contains the specified point.
     * @param point The point.
     * @param adminLevel Only considers admin units at this level. Specify null to consider all admin units.
     * @return The GAUL code of the first tropical admin unit that contains the specified point, or null if no
     * admin units found.
     */
    Integer findAdminUnitTropicalThatContainsPoint(Point point, Character adminLevel);
}
