package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;

/**
 * The Expert entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class ExpertDaoImpl extends AbstractDao<Expert, Integer> implements ExpertDao {

    /**
     * Gets an expert by email address.
     * @param email The email address.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this email address are found
     * (should not occur as emails are unique)
     */
    @Override
    public Expert getByEmail(String email) {
        Query query = namedQuery("getExpertByEmail");
        query.setString("email", email);
        return uniqueResult(query);
    }

}
