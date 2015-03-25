package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitQC;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;

import java.util.List;

/**
 * Service class for admin units, including countries.
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class GeometryServiceImpl implements GeometryService {
    private CountryDao countryDao;
    private HealthMapCountryDao healthMapCountryDao;
    private AdminUnitQCDao adminUnitQCDao;
    private NativeSQL nativeSQL;
    private LandSeaBorderDao landSeaBorderDao;

    public GeometryServiceImpl(CountryDao countryDao, HealthMapCountryDao healthMapCountryDao,
                               AdminUnitQCDao adminUnitQCDao, NativeSQL nativeSQL, LandSeaBorderDao landSeaBorderDao) {

        this.countryDao = countryDao;
        this.healthMapCountryDao = healthMapCountryDao;
        this.adminUnitQCDao = adminUnitQCDao;
        this.nativeSQL = nativeSQL;
        this.landSeaBorderDao = landSeaBorderDao;
    }

    /**
     * Gets all countries.
     * @return All countries.
     */
    @Override
    public List<Country> getAllCountries() {
        return countryDao.getAll();
    }

    /**
     * Gets all HealthMap countries.
     * @return All HealthMap countries.
     */
    @Override
    public List<HealthMapCountry> getAllHealthMapCountries() {
        return healthMapCountryDao.getAll();
    }

    /**
     * Gets the list of African countries that should be considered when calculating
     * the minimum data spread required for a model run.
     * @return The list of GAUL codes for the African countries used in minimum data spread calculation.
     */
    @Override
    public List<Integer> getCountriesForMinDataSpreadCalculation() {
        return countryDao.getCountriesForMinDataSpreadCalculation();
    }

    /**
     * Gets all administrative units.
     * @return All administrative units.
     */
    @Override
    public List<AdminUnitQC> getAllAdminUnitQCs() {
        return adminUnitQCDao.getAll();
    }

    /**
     * Finds the first admin unit for global diseases that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    public Integer findAdminUnitGlobalThatContainsPoint(Point point) {
        return nativeSQL.findAdminUnitThatContainsPoint(point, true);
    }

    /**
     * Finds the first admin unit for tropical diseases that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the first tropical admin unit that contains the specified point, or null if no
     * admin units found.
     */
    public Integer findAdminUnitTropicalThatContainsPoint(Point point) {
        return nativeSQL.findAdminUnitThatContainsPoint(point, false);
    }

    /**
     * Finds the country that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the country that contains the specified point.
     */
    public Integer findCountryThatContainsPoint(Point point) {
        return nativeSQL.findCountryThatContainsPoint(point);
    }

    /**
     * Determines whether one of the land-sea border geometries contains the point.
     * @param point The point.
     * @return True if the point is on land, otherwise false.
     */
    @Override
    public boolean doesLandSeaBorderContainPoint(Point point) {
        return nativeSQL.doesLandSeaBorderContainPoint(point);
    }

    /**
     * Gets all land-sea borders.
     * @return All land-sea borders.
     */
    @Override
    public List<LandSeaBorder> getAllLandSeaBorders() {
        return landSeaBorderDao.getAll();
    }
}
