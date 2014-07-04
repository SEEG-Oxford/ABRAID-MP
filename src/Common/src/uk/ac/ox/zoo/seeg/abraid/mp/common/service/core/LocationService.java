package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;
import java.util.Map;

/**
 * Service interface for locations, including countries.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface LocationService {
    /**
     * Gets a location by GeoNames ID.
     * @param geoNameId The GeoNames ID.
     * @return The location with the specified GeoNames ID, or null if not found.
     */
    Location getLocationByGeoNameId(int geoNameId);

    /**
     * Gets a list of locations that have the specified point and precision. This returns a list of locations as there
     * may be several at the same point with the same precision.
     * @param point The point.
     * @param precision The precision.
     * @return The locations at this point. If none is found, the list is empty.
     */
    List<Location> getLocationsByPointAndPrecision(Point point, LocationPrecision precision);

    /**
     * Gets all countries.
     * @return All countries.
     */
    List<Country> getAllCountries();

    /**
     * Gets all HealthMap countries.
     * @return All HealthMap countries.
     */
    List<HealthMapCountry> getAllHealthMapCountries();

    /**
     * Gets all administrative units for QC.
     * @return All administrative units for QC.
     */
    List<AdminUnitQC> getAllAdminUnitQCs();

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

    /**
     * Finds the country that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the country that contains the specified point.
     */
    Integer findCountryThatContainsPoint(Point point);

    /**
     * Gets all land-sea borders.
     * @return All land-sea borders.
     */
    List<LandSeaBorder> getAllLandSeaBorders();

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
}
