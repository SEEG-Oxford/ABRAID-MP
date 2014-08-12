package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

import java.util.List;

/**
 * The Expert entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class ExpertDaoImpl extends AbstractDao<Expert, Integer> implements ExpertDao {
    public ExpertDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<Expert> getPageOfPubliclyVisible(int pageNumber, int pageSize) {
        return listPageOfNamedQuery("getPubliclyVisibleExperts", pageNumber, pageSize);
    }

    @Override
    public long getCountOfPubliclyVisible() {
        Query query = namedQuery("countPubliclyVisibleExperts");
        return (long) query.uniqueResult();
    }

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    @Override
    public Expert getByEmail(String email) {
        return uniqueResultNamedQuery("getExpertByEmail", "email", email);
    }
}
