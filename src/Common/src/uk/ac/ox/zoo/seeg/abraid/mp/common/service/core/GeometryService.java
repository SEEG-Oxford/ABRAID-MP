package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import com.vividsolutions.jts.geom.Point;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

/**
 * Service interface for admin units, including countries.
 * Copyright (c) 2014 University of Oxford
 */
public interface GeometryService {
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
     * Gets a list of admin units for global or tropical diseases, depending on whether the specified disease group
     * is a global or a tropical disease.
     * @param diseaseGroup The disease group.
     * @return The admin units.
     */
    List<? extends AdminUnitGlobalOrTropical> getAllAdminUnitGlobalsOrTropicalsForDiseaseGroup(
            DiseaseGroup diseaseGroup);

    /**
     * Gets the  global or tropical admin unit for a specific gaul code, depending on whether the
     * specified disease group is a global or a tropical disease.
     * @param diseaseGroup The disease group.
     * @param gaulCode The gaul code.
     * @return The admin unit.
     */
    AdminUnitGlobalOrTropical getAdminUnitGlobalOrTropicalByGaulCode(DiseaseGroup diseaseGroup, Integer gaulCode);

    /**
     * Gets the list of African countries that should be considered when calculating
     * the minimum data spread required for a model run.
     * @return The list of GAUL codes for the African countries used in minimum data spread calculation.
     */
    List<Integer> getCountriesForMinDataSpreadCalculation();

    /**
     * Gets all administrative units for QC.
     * @return All administrative units for QC.
     */
    List<AdminUnitQC> getAllAdminUnitQCs();

    /**
     * Finds the first admin unit for global diseases that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    Integer findAdminUnitGlobalThatContainsPoint(Point point);

    /**
     * Finds the first admin unit for tropical diseases that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the first tropical admin unit that contains the specified point, or null if no
     * admin units found.
     */
    Integer findAdminUnitTropicalThatContainsPoint(Point point);

    /**
     * Finds the country that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the country that contains the specified point.
     */
    Integer findCountryThatContainsPoint(Point point);

    /**
     * Determines whether one of the land-sea border geometries contains the point.
     * @param point The point.
     * @return True if the point is on land, otherwise false.
     */
    boolean doesLandSeaBorderContainPoint(Point point);

    /**
     * Gets all land-sea borders.
     * @return All land-sea borders.
     */
    List<LandSeaBorder> getAllLandSeaBorders();
}
