package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapDataAcquisition;

/**
 * Entry point for the DataAcquisition module.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class Main {
    public static final String APPLICATION_CONTEXT_LOCATION =
            "classpath:uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/config/beans.xml";

    private HealthMapDataAcquisition healthMapDataAcquisition;

    /**
     * Entry method for the DataAcquisition module.
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_LOCATION);
        runMain(context);
    }

    /**
     * Retrieves the main class from the application context, and runs its main method.
     * @param context The application context
     */
    public static void runMain(ApplicationContext context) {
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
