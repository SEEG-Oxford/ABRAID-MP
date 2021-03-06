package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.WebServiceClient;
import uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.geoserver.GeoserverRestService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the Spring Bean wiring for the module.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:ModelOutputHandler/web/WEB-INF/applicationContext.xml")
public class BeanTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Test
    public void canObtainWebServiceClientBean() throws Exception {
        // Arrange
        // Act
        Object result = applicationContext.getBean("webServiceClient");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(WebServiceClient.class);
    }

    @Test
    public void canObtainGeoserverRestServiceBean() throws Exception {
        // Arrange
        // Act
        Object result = applicationContext.getBean("geoserverRestService");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(GeoserverRestService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
