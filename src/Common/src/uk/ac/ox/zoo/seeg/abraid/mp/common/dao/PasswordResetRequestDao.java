package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.HibernateException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.PasswordResetRequest;

import java.util.List;

/**
 * Interface for the PasswordResetRequest entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
public interface PasswordResetRequestDao {
    /**
     * Gets all password reset requests.
     * @return All password reset requests.
     */
    List<PasswordResetRequest> getAll();

    /**
     * Gets password reset request identified by its ID.
     * @param id The ID.
     * @return The password reset request, or null if it does not exist.
     */
    PasswordResetRequest getById(Integer id);

    /**
     * Remove the password reset request(s) issued for an expert.
     * @param expert The expert
     */
    void removeRequestsIssuedForExpert(Expert expert);

    /**
     * Deletes the given instance.
     * @param entity a transient or detached instance to be deleted
     * @throws org.hibernate.HibernateException Indicates a problem executing the SQL or processing the SQL results.
     * @see org.hibernate.Session#delete(Object)
     */
    void delete(PasswordResetRequest entity) throws HibernateException;

    /**
     * Saves the specified password reset request.
     * @param passwordResetRequest The password reset request to save.
     */
    void save(PasswordResetRequest passwordResetRequest);

    /**
     * Remove the password reset request(s) that have expired due to age.
     */
    void removeOldRequests();
}
