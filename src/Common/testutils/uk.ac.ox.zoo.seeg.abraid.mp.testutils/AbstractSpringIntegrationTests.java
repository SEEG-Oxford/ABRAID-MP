package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests.
 *
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class AbstractSpringIntegrationTests {
    @Autowired
    protected SessionFactory sessionFactory;

    /**
     * Flushes the session, causing it to send any queued commands to the database. Also clears
     * the first-level cache of entities that have been previously read or written.
     */
    protected void flushAndClear() {
        getCurrentSession().flush();
        getCurrentSession().clear();
    }

    /**
     * Executes a SQL select query and returns a single result.
     *
     * @param queryString The query to execute.
     * @param parameterNamesAndValues The names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     * @return The query result.
     */
    protected Object uniqueSQLResult(String queryString, Object... parameterNamesAndValues) {
        return getParameterisedSQLQuery(queryString, parameterNamesAndValues).uniqueResult();
    }

    /**
     * Executes a SQL update query.
     *
     * @param queryString The query to execute.
     * @param parameterNamesAndValues The names and values of the parameters. These must be in the format
     * name1, value1, name2, value2, ...
     */
    protected void executeSQLUpdate(String queryString, Object... parameterNamesAndValues) {
        getParameterisedSQLQuery(queryString, parameterNamesAndValues).executeUpdate();
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    private SQLQuery getParameterisedSQLQuery(String queryString, Object... parameterNamesAndValues) {
        SQLQuery query = getCurrentSession().createSQLQuery(queryString);
        for (int i = 0; i < parameterNamesAndValues.length; i += 2) {
            query.setParameter((String) parameterNamesAndValues[i], parameterNamesAndValues[i + 1]);
        }
        return query;
    }
}
