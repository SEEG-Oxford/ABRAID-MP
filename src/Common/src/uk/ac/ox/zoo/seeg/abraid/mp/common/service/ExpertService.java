package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;
import java.util.Set;

/**
 * Service interface for experts.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface ExpertService {
    /**
     * Gets a list of all experts.
     * @return A list of all experts.
     */
    List<Expert> getAllExperts();

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    Expert getExpertByEmail(String email);

    /**
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param diseaseGroupId The id of the diseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     * @throws java.lang.IllegalArgumentException if the expertId or diseaseGroupId cannot be found in the database.
     */
    List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId, Integer diseaseGroupId)
            throws IllegalArgumentException;

    /**
     * Gets a list of the specified expert's disease interests.
     *
     * @param expertId The id of the specified expert.
     * @return The list of disease groups the expert can validate.
     */
    Set<DiseaseGroup> getDiseaseInterests(Integer expertId);

    /**
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void saveExpert(Expert expert);
}
