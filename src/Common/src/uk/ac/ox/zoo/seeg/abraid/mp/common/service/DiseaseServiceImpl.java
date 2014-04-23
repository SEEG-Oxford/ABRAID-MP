package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.*;

/**
 * Service class for diseases, including disease occurrences.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Transactional
public class DiseaseServiceImpl implements DiseaseService {
    private DiseaseOccurrenceDao diseaseOccurrenceDao;
    private DiseaseGroupDao diseaseGroupDao;
    private HealthMapDiseaseDao healthMapDiseaseDao;
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;
    private AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao;

    public DiseaseServiceImpl(DiseaseOccurrenceDao diseaseOccurrenceDao,
                              DiseaseGroupDao diseaseGroupDao,
                              HealthMapDiseaseDao healthMapDiseaseDao,
                              ValidatorDiseaseGroupDao validatorDiseaseGroupDao,
                              AdminUnitDiseaseExtentClassDao adminUnitDiseaseExtentClassDao) {
        this.diseaseOccurrenceDao = diseaseOccurrenceDao;
        this.diseaseGroupDao = diseaseGroupDao;
        this.healthMapDiseaseDao = healthMapDiseaseDao;
        this.validatorDiseaseGroupDao = validatorDiseaseGroupDao;
        this.adminUnitDiseaseExtentClassDao = adminUnitDiseaseExtentClassDao;
    }

    /**
     * Saves a disease occurrence.
     * @param diseaseOccurrence The disease occurrence to save.
     */
    @Override
    @Transactional
    public void saveDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence) {
        diseaseOccurrenceDao.save(diseaseOccurrence);
    }

    /**
     * Saves a HealthMap disease.
     * @param disease The disease to save.
     */
    @Override
    @Transactional
    public void saveHealthMapDisease(HealthMapDisease disease) {
        healthMapDiseaseDao.save(disease);
    }

    /**
     * Gets all HealthMap diseases.
     * @return All HealthMap diseases.
     */
    @Override
    public List<HealthMapDisease> getAllHealthMapDiseases() {
        return healthMapDiseaseDao.getAll();
    }

    /**
     * Gets the disease group by its id.
     * @param diseaseGroupId The id of the disease group.
     * @return The disease group.
     */
    @Override
    public DiseaseGroup getDiseaseGroupById(Integer diseaseGroupId) {
        return diseaseGroupDao.getById(diseaseGroupId);
    }

    /**
     * Gets all disease groups.
     * @return All disease groups.
     */
    @Override
    public List<DiseaseGroup> getAllDiseaseGroups() {
        return diseaseGroupDao.getAll();
    }

    /**
     * Gets all the validator disease groups.
     * @return A list of all validator disease groups.
     */
    @Override
    public List<ValidatorDiseaseGroup> getAllValidatorDiseaseGroups() {
        return validatorDiseaseGroupDao.getAll();
    }

    /**
     * Gets the list of disease groups, for each validator disease group.
     * @return The map, from the name of the validator disease group, to the disease groups belonging to it.
     */
    @Override
    public Map<String, List<DiseaseGroup>> getValidatorDiseaseGroupMap() {
        List<DiseaseGroup> allDiseaseGroups = getAllDiseaseGroups();
        Map<String, List<DiseaseGroup>> map = new HashMap<>();
        for (DiseaseGroup diseaseGroup : allDiseaseGroups) {
            if (diseaseGroup.getValidatorDiseaseGroup() != null) {
                String name = diseaseGroup.getValidatorDiseaseGroup().getName();
                if (map.containsKey(name)) {
                    map.get(name).add(diseaseGroup);
                } else {
                    map.put(name, new ArrayList<>(Arrays.asList(diseaseGroup)));
                }
            }
        }
        return map;
    }

    /**
     * For each admin unit, get the disease extent class for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @return The map, from admin unit, to its disease extent class, for the specified disease group.
     */
    @Override
    public Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> getAdminUnitDiseaseExtentClassMap(
            Integer diseaseGroupId) {
        Map<AdminUnitGlobalOrTropical, DiseaseExtentClass> map = new HashMap<>();
        List<AdminUnitDiseaseExtentClass> list;
        boolean isDiseaseGroupGlobal = isDiseaseGroupGlobal(diseaseGroupId);
        if (isDiseaseGroupGlobal) {
            list =
            adminUnitDiseaseExtentClassDao.getAllGlobalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);
        } else {
            list =
            adminUnitDiseaseExtentClassDao.getAllTropicalAdminUnitDiseaseExtentClassesByDiseaseGroupId(diseaseGroupId);
        }
        for (AdminUnitDiseaseExtentClass adminUnitDiseaseExtentClass : list) {
            AdminUnitGlobalOrTropical adminUnit = adminUnitDiseaseExtentClass.getAdminUnitGlobalOrTropical();
            DiseaseExtentClass diseaseExtentClass = adminUnitDiseaseExtentClass.getDiseaseExtentClass();
            map.put(adminUnit, diseaseExtentClass);
        }
        return map;
    }

    private boolean isDiseaseGroupGlobal(Integer diseaseGroupId) {
        DiseaseGroup diseaseGroup = getDiseaseGroupById(diseaseGroupId);
        return diseaseGroup.isGlobal();
    }

    /**
     * Determines whether the specified disease occurrence already exists in the database. This is true if an
     * occurrence exists with the same disease group, location, alert and occurrence start date.
     * @param occurrence The disease occurrence.
     * @return True if the occurrence already exists in the database, otherwise false.
     */
    public boolean doesDiseaseOccurrenceExist(DiseaseOccurrence occurrence) {
        // These are not-null fields in the database, so if any of them are null then there cannot possibly be a
        // matching disease occurrence in the database
        if (occurrence.getDiseaseGroup() == null ||
                occurrence.getDiseaseGroup().getId() == null ||
                occurrence.getLocation() == null ||
                occurrence.getLocation().getId() == null ||
                occurrence.getAlert() == null ||
                occurrence.getAlert().getId() == null) {
            return false;
        }

        List<DiseaseOccurrence> matchingOccurrences = diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(
                occurrence.getDiseaseGroup(), occurrence.getLocation(), occurrence.getAlert(),
                occurrence.getOccurrenceStartDate());

        return matchingOccurrences.size() > 0;
    }

    /**
     * Determines whether the specified occurrence's disease id belongs to the corresponding validator disease group.
     * @param diseaseOccurrenceId The id of the disease occurrence.
     * @param validatorDiseaseGroupId The id of the validator disease group.
     * @return True if the occurrence refers to a disease in the validator disease group, otherwise false.
     */
    public boolean doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(Integer diseaseOccurrenceId,
                                                          Integer validatorDiseaseGroupId) {
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(diseaseOccurrenceId);
        return validatorDiseaseGroupId.equals(occurrence.getValidatorDiseaseGroup().getId());
    }
}
