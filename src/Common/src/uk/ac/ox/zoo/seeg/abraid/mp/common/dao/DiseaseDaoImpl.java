package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

/**
 * The DiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseDaoImpl extends AbstractDao<DiseaseGroup, Integer> implements DiseaseDao {
    /**
     * Gets a disease group by name.
     * @param name The name.
     * @return The disease group, or null if not found.
     * @throws org.springframework.dao.DataAccessException if multiple disease groups with this name are found (should not
     * occur as names are unique)
     */
    @Override
    public DiseaseGroup getByName(String name) {
        Query query = namedQuery("getDiseaseByName");
        query.setString("name", name);
        return uniqueResult(query);
    }
}
