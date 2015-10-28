package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Interface for the DiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface DiseaseGroupDao {
    /**
     * Gets all disease groups.
     * @return All disease groups.
     */
    List<DiseaseGroup> getAll();

    /**
     * Gets a list of the disease groups for which there are occurrences waiting to be reviewed, by the given expert.
     * @param expertId The expert's id.
     * @return A list of disease groups.
     */
    List<DiseaseGroup> getDiseaseGroupsNeedingOccurrenceReviewByExpert(int expertId);

    /**
     * Gets a list of the disease groups for which there are admin units waiting to be reviewed, by the given expert.
     * @param expertId The expert's id.
     * @return A list of disease groups.
     */
    List<DiseaseGroup> getDiseaseGroupsNeedingExtentReviewByExpert(int expertId);

    /**
     * Gets a disease group by ID.
     * @param id The ID.
     * @return The disease group with the specified ID, or null if not found.
     */
    DiseaseGroup getById(Integer id);

    /**
     * Gets the names of all disease groups to be shown in the HealthMap disease report (sorted).
     * @return The disease groups names.
     */
    List<String> getDiseaseGroupNamesForHealthMapReport();

    /**
     * Saves the specified diseaseGroup.
     * @param diseaseGroup The diseaseGroup to save.
     */
    void save(DiseaseGroup diseaseGroup);

    /**
     * Gets the IDs of disease groups that have automatic model runs enabled.
     * @return The IDs of relevant disease groups.
     */
    List<Integer> getIdsForAutomaticModelRuns();
}
