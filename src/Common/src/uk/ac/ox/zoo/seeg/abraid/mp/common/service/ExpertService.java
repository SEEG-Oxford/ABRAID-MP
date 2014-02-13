package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

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
     * Saves the specified expert.
     * @param expert The expert to save.
     */
    void saveExpert(Expert expert);
}
