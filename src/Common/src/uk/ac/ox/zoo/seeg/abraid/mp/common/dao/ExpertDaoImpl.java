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
     * Gets an expert by name.
     * @param name The name.
     * @return The expert, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple experts with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public Expert getByName(String name) {
        Query query = namedQuery("getExpertByName");
        query.setString("name", name);
        return uniqueResult(query);
    }

}
