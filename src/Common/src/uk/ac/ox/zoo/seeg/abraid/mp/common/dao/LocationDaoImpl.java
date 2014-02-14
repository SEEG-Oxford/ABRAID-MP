package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;

import java.util.List;

/**
 * The Location entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class LocationDaoImpl extends AbstractDao<Location, Integer> implements LocationDao {
    /**
     * Gets a location by GeoNames ID.
     * @param geoNamesId The GeoNames ID.
     * @return The location, or null if not found.
     */
    public Location getByGeoNamesId(int geoNamesId) {
        return uniqueResultNamedQuery("getLocationByGeoNamesId", "geoNamesId", geoNamesId);
    }

    /**
     * Gets locations by point. This returns a list of locations as there may be several at the same point (e.g. a
     * precise location, a centroid of a country).
     * @param point The point.
     * @return The locations at this point. If none is found, the list is empty.
     */
    public List<Location> getByPoint(Point point) {
        // TODO
        return null;
    }
}
