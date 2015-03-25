package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

/**
 * Contains lookup data that is used when performing quality control checks.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCLookupData {
    private List<AdminUnitQC> adminUnits;
    private Map<Integer, AdminUnitQC> adminUnitsMap;
    private Map<Integer, Country> countryMap;
    private Map<Integer, MultiPolygon> countryGeometryMap;
    private MultiPolygon landSeaBorders;
    private Map<Integer, HealthMapCountry> healthMapCountryMap;
    private Map<Integer, MultiPolygon> healthMapCountryGeometryMap;

    private GeometryService geometryService;

    public QCLookupData(GeometryService geometryService) {
        this.geometryService = geometryService;
    }

    /**
     * Gets a list of administrative units.
     * @return A list of administrative units.
     */
    public List<AdminUnitQC> getAdminUnits() {
        if (adminUnits == null) {
            adminUnits = geometryService.getAllAdminUnitQCs();
        }
        return adminUnits;
    }

    /**
     * Gets a list of administrative units, indexed by gaul code.
     * @return A list of administrative units.
     */
    public Map<Integer, AdminUnitQC> getAdminUnitsMap() {
        if (adminUnitsMap == null) {
            adminUnitsMap = index(getAdminUnits(), on(AdminUnitQC.class).getGaulCode());
        }
        return adminUnitsMap;
    }

    /**
     * Gets a list of countries, indexed by gaul code.
     * @return A list of countries, indexed by gaul code.
     */
    public Map<Integer, Country> getCountryMap() {
        if (countryMap == null) {
            // This will retrieve the countries from the Hibernate cache if already obtained by CountryGeometryMap
            List<Country> countries = geometryService.getAllCountries();
            countryMap = index(countries, on(Country.class).getGaulCode());
        }
        return countryMap;
    }

    /**
     * Gets a list of country geometries, indexed by GAUL code.
     * @return A list of country geometries, indexed by GAUL code.
     */
    public Map<Integer, MultiPolygon> getCountryGeometryMap() {
        if (countryGeometryMap == null) {
            // This will retrieve the countries from the Hibernate cache if already obtained by CsvLookupData
            List<Country> countries = geometryService.getAllCountries();
            countryGeometryMap = new HashMap<>();
            for (Country country : countries) {
                countryGeometryMap.put(country.getGaulCode(), country.getGeom());
            }

        }
        return countryGeometryMap;
    }

    /**
     * Gets a multipolygon representing the concatenation of the land-sea borders.
     * @return A multipolygon representing the concatenation of the land-sea borders.
     */
    public MultiPolygon getLandSeaBorders() {
        if (landSeaBorders == null) {
            List<LandSeaBorder> landSeaBorderList = geometryService.getAllLandSeaBorders();
            List<MultiPolygon> multiPolygons = extract(landSeaBorderList, on(LandSeaBorder.class).getGeom());
            landSeaBorders = GeometryUtils.concatenate(multiPolygons);
        }
        return landSeaBorders;
    }

    /**
     * Gets a list of HealthMap countries, indexed by HealthMap country ID.
     * @return A list of HealthMap countries, indexed by HealthMap country ID.
     */
    public Map<Integer, HealthMapCountry> getHealthMapCountryMap() {
        if (healthMapCountryMap == null) {
            // This will retrieve the countries from the Hibernate cache if already obtained by HealthMapLookupData
            List<HealthMapCountry> countries = geometryService.getAllHealthMapCountries();
            healthMapCountryMap = index(countries, on(HealthMapCountry.class).getId());
        }
        return healthMapCountryMap;
    }

    /**
     * Gets a mapping between HealthMap country ID and the geometry of the associated GAUL countries.
     * If the HealthMap country has no associated geometries, it does not appear in the map.
     * @return A mapping as described above.
     */
    public Map<Integer, MultiPolygon> getHealthMapCountryGeometryMap() {
        if (healthMapCountryGeometryMap == null) {
            // This will retrieve the countries from the Hibernate cache if already obtained by HealthMapLookupData
            List<HealthMapCountry> countries = geometryService.getAllHealthMapCountries();
            healthMapCountryGeometryMap = new HashMap<>();

            for (HealthMapCountry country : countries) {
                if (country.getCountries() != null) {
                    List<MultiPolygon> countryGeometries = extract(country.getCountries(), on(Country.class).getGeom());
                    MultiPolygon countryGeometry = GeometryUtils.concatenate(countryGeometries);
                    if (countryGeometry.getNumGeometries() > 0) {
                        healthMapCountryGeometryMap.put(country.getId(), countryGeometry);
                    }
                }
            }
        }

        return healthMapCountryGeometryMap;
    }
}
