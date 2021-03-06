package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

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
    private AdminUnitGlobalDao adminUnitGlobalDao;
    private AdminUnitTropicalDao adminUnitTropicalDao;

    public GeometryServiceImpl(CountryDao countryDao, HealthMapCountryDao healthMapCountryDao,
                               AdminUnitQCDao adminUnitQCDao, NativeSQL nativeSQL, LandSeaBorderDao landSeaBorderDao,
                               AdminUnitGlobalDao adminUnitGlobalDao, AdminUnitTropicalDao adminUnitTropicalDao) {

        this.countryDao = countryDao;
        this.healthMapCountryDao = healthMapCountryDao;
        this.adminUnitQCDao = adminUnitQCDao;
        this.nativeSQL = nativeSQL;
        this.landSeaBorderDao = landSeaBorderDao;
        this.adminUnitGlobalDao = adminUnitGlobalDao;
        this.adminUnitTropicalDao = adminUnitTropicalDao;
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
     * Gets the names of all countries to be shown in the HealthMap country report (sorted).
     * @return The country names.
     */
    @Override
    public List<String> getCountryNamesForHealthMapReport() {
        return countryDao.getCountryNamesForHealthMapReport();
    }

    /**
     * Gets a list of admin units for global or tropical diseases, depending on whether the specified disease group
     * is a global or a tropical disease.
     * @param diseaseGroup The disease group.
     * @return The admin units.
     */
    @Override
    public List<? extends AdminUnitGlobalOrTropical> getAllAdminUnitGlobalsOrTropicalsForDiseaseGroup(
            DiseaseGroup diseaseGroup) {
        if (diseaseGroup == null) {
            return null;
        } else if (isGlobalDiseaseGroup(diseaseGroup)) {
            return adminUnitGlobalDao.getAll();
        } else {
            return adminUnitTropicalDao.getAll();
        }
    }

    /**
     * Gets the  global or tropical admin unit for a specific gaul code, depending on whether the
     * specified disease group is a global or a tropical disease.
     * @param diseaseGroup The disease group.
     * @param gaulCode The gaul code.
     * @return The admin unit.
     */
    @Override
    public AdminUnitGlobalOrTropical getAdminUnitGlobalOrTropicalByGaulCode(
            DiseaseGroup diseaseGroup, Integer gaulCode) {
        if (diseaseGroup == null) {
            return null;
        } else if (isGlobalDiseaseGroup(diseaseGroup)) {
            return adminUnitGlobalDao.getByGaulCode(gaulCode);
        } else {
            return adminUnitTropicalDao.getByGaulCode(gaulCode);
        }
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
     * @return The country that contains the specified point.
     */
    public Country findCountryThatContainsPoint(Point point) {
        Integer gaul = nativeSQL.findCountryThatContainsPoint(point);
        return (gaul == null) ? null : countryDao.getByGaulCode(gaul);
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

    private boolean isGlobalDiseaseGroup(DiseaseGroup diseaseGroup) {
        return diseaseGroup.isGlobal() != null && diseaseGroup.isGlobal();
    }
}
