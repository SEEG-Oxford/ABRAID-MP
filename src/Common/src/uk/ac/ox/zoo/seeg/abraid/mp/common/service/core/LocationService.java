package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Service interface for locations and associated geoname objects.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LocationService {
    /**
     * Gets a list of locations that have the specified point and precision. This returns a list of locations as there
     * may be several at the same point with the same precision.
     * @param point The point.
     * @param precision The precision.
     * @return The locations at this point. If none is found, the list is empty.
     */
    List<Location> getLocationsByPointAndPrecision(Point point, LocationPrecision precision);

    /**
     * Gets mappings between GeoNames feature codes and location precision.
     * @return A set of mappings.
     */
    Map<String, LocationPrecision> getGeoNamesLocationPrecisionMappings();

    /**
     * Gets a GeoName by ID.
     * @param geoNameId The GeoNames ID.
     * @return The GeoName, or null if not found.
     */
    GeoName getGeoNameById(int geoNameId);

    /**
     * Saves a GeoName.
     * @param geoName The GeoName to save.
     */
    void saveGeoName(GeoName geoName);

    /**
     * Gets one or more extent classes that corresponded to the specified location (multiple for countries that are
     * split in to admin units in the extent map).
     * @param diseaseId The disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param location The location
     * @return A list of extent classes.
     */
    List<AdminUnitDiseaseExtentClass> getAdminUnitDiseaseExtentClassesForLocation(
            int diseaseId, boolean isGlobal, Location location);

    /**
     * Saves the specified location.
     * @param location The location to save.
     */
    void saveLocation(Location location);
}
