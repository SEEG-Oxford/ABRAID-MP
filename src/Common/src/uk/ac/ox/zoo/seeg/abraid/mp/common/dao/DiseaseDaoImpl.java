package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

/**
 * The Disease entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseDaoImpl extends AbstractDao<Disease, Integer> implements DiseaseDao {
    /**
     * Gets a disease by name.
     * @param name The name.
     * @return The disease, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple diseases with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public Disease getByName(String name) {
        Query query = namedQuery("getDiseaseByName");
        query.setString("name", name);
        return uniqueResult(query);
    }
}
