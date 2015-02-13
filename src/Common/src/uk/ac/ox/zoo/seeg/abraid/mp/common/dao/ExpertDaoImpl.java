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

    /**
     * Gets a page worth of publicly visible experts.
     * @param pageNumber The page number to return.
     * @param pageSize The size of the pages to split the visible experts into.
     * @return A page worth of publicly visible experts
     */
    @Override
    public List<Expert> getPageOfPubliclyVisible(int pageNumber, int pageSize) {
        return listPageOfNamedQuery("getPubliclyVisibleExperts", pageNumber, pageSize);
    }

    /**
     * Gets a count of the publicly visible experts.
     * @return The count.
     */
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
        String lowerEmail = (email == null) ? null : email.toLowerCase();
        return uniqueResultNamedQuery("getExpertByEmail", "email", lowerEmail);
    }
}
