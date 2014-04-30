package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;

/**
 * Contains routines that interact with the PostGIS database using native SQL.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLImpl implements NativeSQL {
    // We use ST_INTERSECTS rather than ST_CONTAINS because the former returns true if the point is on the
    // geometry border
    private static final String ADMIN_UNIT_CONTAINS_POINT_QUERY =
            "select min(gaul_code) from %s where st_intersects(geom, :point)";
    private static final String ADMIN_UNIT_GLOBAL_TABLE_NAME = "admin_unit_global";
    private static final String ADMIN_UNIT_TROPICAL_TABLE_NAME = "admin_unit_tropical";

    private SessionFactory sessionFactory;

    public NativeSQLImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Finds the first admin unit for global diseases that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the first global admin unit that contains the specified point, or null if no
     * admin units found.
     */
    @Override
    public Integer findAdminUnitGlobalThatContainsPoint(Point point) {
        return findAdminUnitThatContainsPoint(point, ADMIN_UNIT_GLOBAL_TABLE_NAME);
    }

    /**
     * Finds the first admin unit for tropical diseases that contains the specified point.
     * @param point The point.
     * @return The GAUL code of the first tropical admin unit that contains the specified point, or null if no
     * admin units found.
     */
    @Override
    public Integer findAdminUnitTropicalThatContainsPoint(Point point) {
        return findAdminUnitThatContainsPoint(point, ADMIN_UNIT_TROPICAL_TABLE_NAME);
    }

    private Integer findAdminUnitThatContainsPoint(Point point, String tableName) {
        return (Integer) createSQLQuery(String.format(ADMIN_UNIT_CONTAINS_POINT_QUERY, tableName))
                            .setParameter("point", point)
                            .uniqueResult();
    }

    private SQLQuery createSQLQuery(String query) {
        return sessionFactory.getCurrentSession().createSQLQuery(query);
    }
}
