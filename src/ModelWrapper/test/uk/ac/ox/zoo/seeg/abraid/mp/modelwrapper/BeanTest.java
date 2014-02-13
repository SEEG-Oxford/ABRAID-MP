package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the Spring Bean wiring for the module.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/modelwrapper//beans.xml")
public class BeanTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Test
    public void canObtainModelRunnerBean() {
        // Arrange
        // Act
        Object result = applicationContext.getBean("modelRunner");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(ModelRunner.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
