package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;

import java.util.List;

/**
 * The Feed entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class FeedDaoImpl extends AbstractDao<Feed, Integer> implements FeedDao {
    public FeedDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a list of feeds by provenance name.
     *
     * @param provenanceName The provenance name.
     * @return A list of feeds whose provenance has the given name.
     */
    @Override
    public List<Feed> getByProvenanceName(String provenanceName) {
        return listNamedQuery("getFeedsByProvenanceName", "provenanceName", provenanceName);
    }
}
