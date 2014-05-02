package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

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
     * Executes an HQL query.
     * @param query The query to execute.
     * @return The number of entities updated or deleted.
     */
    protected int executeQuery(String query) {
        return getCurrentSession().createQuery(query).executeUpdate();
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}
