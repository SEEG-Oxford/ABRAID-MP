package uk.ac.ox.zoo.seeg.abraid.mp.common;

import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for integration tests.
 *
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
@Transactional
public abstract class AbstractSpringIntegrationTests {
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Flushes the session, causing it to send any queued commands to the database. Also clears
     * the first-level cache of entities that have been previously read or written.
     */
    protected void flushAndClear() {
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
    }
}
