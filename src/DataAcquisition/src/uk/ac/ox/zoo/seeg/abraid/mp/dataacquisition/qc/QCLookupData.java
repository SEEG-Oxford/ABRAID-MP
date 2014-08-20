package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapLookupData;

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
    private MultiPolygon landSeaBorders;
    private Map<Integer, MultiPolygon> healthMapCountryGeometryMap;

    private LocationService locationService;
    private HealthMapLookupData healthMapLookupData;

    public QCLookupData(LocationService locationService, HealthMapLookupData healthMapLookupData) {
        this.locationService = locationService;
        this.healthMapLookupData = healthMapLookupData;
    }

    /**
     * Gets a list of administrative units.
     * @return A list of administrative units.
     */
    public List<AdminUnitQC> getAdminUnits() {
        if (adminUnits == null) {
            adminUnits = locationService.getAllAdminUnitQCs();
        }
        return adminUnits;
    }

    /**
     * Gets a multipolygon representing the concatenation of the land-sea borders.
     * @return A multipolygon representing the concatenation of the land-sea borders.
     */
    public MultiPolygon getLandSeaBorders() {
        if (landSeaBorders == null) {
            List<LandSeaBorder> landSeaBorderList = locationService.getAllLandSeaBorders();
            List<MultiPolygon> multiPolygons = extract(landSeaBorderList, on(LandSeaBorder.class).getGeom());
            landSeaBorders = GeometryUtils.concatenate(multiPolygons);
        }
        return landSeaBorders;
    }

    /**
     * Gets a mapping between HealthMap country ID and the geometry of the associated GAUL countries.
     * If the HealthMap country has no associated geometries, it does not appear in the map.
     * @return A mapping as described above.
     */
    public Map<Integer, MultiPolygon> getHealthMapCountryGeometryMap() {
        if (healthMapCountryGeometryMap == null) {
            Map<Integer, HealthMapCountry> countryMap = healthMapLookupData.getCountryMap();
            healthMapCountryGeometryMap = new HashMap<>();

            for (HealthMapCountry country : countryMap.values()) {
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

    public HealthMapLookupData getHealthMapLookupData() {
        return healthMapLookupData;
    }
}
