package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseTest {
    @Test
    public void saveDisease() {
        // Create an instance of ClassPathXmlApplicationContext
        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans-common.xml"});

        Configuration configuration = new Configuration().configure("uk/ac/ox/zoo/seeg/abraid/mp/common/config/hibernate.cfg.xml");
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save( new Disease("My test disease", null));
        session.getTransaction().commit();
        session.close();
    }
}
