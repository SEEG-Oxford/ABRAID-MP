package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.List;

/**
 * Interface for the AdminUnitDiseaseExtentClass entity's Data Access Object.
 * Copyright (c) 2014 University of Oxford
 */
public interface AdminUnitDiseaseExtentClassDao {

    /**
     * Gets the latest disease extent class change date for the specified disease group.
     * @param diseaseGroupId The ID of the disease group.
     * @return The latest change date.
     */
    DateTime getLatestDiseaseExtentClassChangeDateByDiseaseGroupId(Integer diseaseGroupId);

    /**
     * Gets the list of most recent disease occurrences on the admin unit disease extent class (defined by disease group
     * id and admin unit gaul code).
     * @param diseaseGroupId The id of the disease group the admin unit disease extent class represents.
     * @param isGlobal True if the disease group is considered global, false if considered tropical.
     * @param gaulCode The gaul code the admin unit disease extent class represents.
     * @return The list of latest disease occurrences for the given admin unit disease extent class.
     */
    List<DiseaseOccurrence> getLatestValidatorOccurrencesForAdminUnitDiseaseExtentClass(
            Integer diseaseGroupId, boolean isGlobal, Integer gaulCode);

    /**
     * Gets all global AdminUnitDiseaseExtentClass objects for the specified DiseaseGroup.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the global AdminUnitDiseaseExtentClasses.
     */
    List<AdminUnitDiseaseExtentClass> getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(Integer diseaseGroupId);

    /**
     * Gets all tropical AdminUnitDiseaseExtentClass objects for the specified DiseaseGroup.
     * @param diseaseGroupId The id of the disease group.
     * @return A list of the tropical AdminUnitDiseaseExtentClasses.
     */
    List<AdminUnitDiseaseExtentClass> getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(
            Integer diseaseGroupId);

    /**
     * Gets an AdminUnitDiseaseExtentClass object by ID.
     * @param id The ID.
     * @return The matching AdminUnitDiseaseExtentClass object, or null if not found.
     */
    AdminUnitDiseaseExtentClass getById(Integer id);

    /**
     * Saves a disease extent class that is associated with an admin unit (global or tropical).
     * @param adminUnitDiseaseExtentClass The object to save.
     */
    void save(AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass);

    /**
     * Gets the disease extent class for all admin units within a specific country.
     * @param diseaseGroupId The id of the disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param countryGaulCode The gaul code of the parent country.
     * @return A extent classes.
     */
    List<AdminUnitDiseaseExtentClass> getAllAdminUnitDiseaseExtentClassesByCountryGaulCode(
            int diseaseGroupId, boolean isGlobal, int countryGaulCode);

    /**
     * Gets the disease extent class for specific global or tropical admin unit.
     * @param diseaseGroupId The id of the disease group.
     * @param isGlobal True to use admin units for global diseases, false for tropical diseases.
     * @param gaulCode The gaul code of the admin unit.
     * @return A extent class.
     */
    AdminUnitDiseaseExtentClass getDiseaseExtentClassByGaulCode(
            int diseaseGroupId, boolean isGlobal, int gaulCode);
}
