package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapDataAcquisition;

/**
 * Entry point for the DataAcquisition module.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class Main {
    private HealthMapDataAcquisition healthMapDataAcquisition;

    /**
     * Entry method for the DataAcquisition module.
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "classpath:uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/config/beans.xml");
        Main main = (Main) context.getBean("main");
        main.acquireData();
    }

    public Main(HealthMapDataAcquisition healthMapDataAcquisition) {
        this.healthMapDataAcquisition = healthMapDataAcquisition;
    }

    /**
     * Acquires data from all sources.
     */
    public void acquireData() {
        healthMapDataAcquisition.acquireData();
    }
}
