package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.PasswordResetRequest;

/**
 * The PasswordResetRequest entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class PasswordResetRequestDaoImpl extends AbstractDao<PasswordResetRequest, Integer>
        implements PasswordResetRequestDao {
    private static final int PASSWORD_RESET_EXPIRY_CUTOFF_IN_HOURS = 24;

    public PasswordResetRequestDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    /**
     * Remove the password reset request(s) issued for an expert.
     * @param expert The expert
     */
    @Override
    public void removeRequestsIssuedForExpert(Expert expert) {
        noResultNamedQuery("removeRequestsIssuedForExpert", "expert", expert);
    }

    /**
     * Remove the password reset request(s) issued prior to 24 hours ago.
     */
    @Override
    public void removeOldRequests() {
        noResultNamedQuery("removeRequestsIssuedBeforeDate",
                "cutOffDate", DateTime.now().minusHours(PASSWORD_RESET_EXPIRY_CUTOFF_IN_HOURS));
    }

    /**
     * Deletes the given instance.
     * @param entity a transient or detached instance to be deleted
     * @throws org.hibernate.HibernateException Indicates a problem executing the SQL or processing the SQL results.
     * @see org.hibernate.Session#delete(Object)
     */
    @Override
    public void delete(PasswordResetRequest entity) throws HibernateException {
        currentSession().delete(entity);
    }
}
