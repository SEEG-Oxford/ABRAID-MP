package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;

/**
 * The Location entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class LocationDaoImpl extends AbstractDao<Location, Integer> implements LocationDao {
}
