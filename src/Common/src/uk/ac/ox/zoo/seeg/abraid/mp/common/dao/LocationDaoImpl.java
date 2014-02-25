package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;

import java.util.List;

/**
 * The Location entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class LocationDaoImpl extends AbstractDao<Location, Integer> implements LocationDao {
    public LocationDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

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
     * @param precision The precision.
     * @return The locations at this point. If none is found, the list is empty.
     */
    public List<Location> getByPointAndPrecision(Point point, LocationPrecision precision) {
        return listNamedQuery("getLocationsByPointAndPrecision", "point", point, "precision", precision);
    }
}
