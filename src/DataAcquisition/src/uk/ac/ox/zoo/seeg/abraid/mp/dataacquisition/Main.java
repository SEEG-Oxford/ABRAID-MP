package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.HealthMapDataAcquisition;

/**
 * Entry point for the DataAcquisition module.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class Main {
    /**
     * The location of the application context.
     */
    public static final String APPLICATION_CONTEXT_LOCATION =
            "classpath:uk/ac/ox/zoo/seeg/abraid/mp/dataacquisition/config/beans.xml";
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    private HealthMapDataAcquisition healthMapDataAcquisition;

    /**
     * Entry method for the DataAcquisition module.
     * @param args Command line arguments (unused).
     */
    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_LOCATION);
            runMain(context);
        } catch (Throwable e) {
            try {
                // Ensure that top-level exceptions are logged
                LOGGER.fatal(e);
            } catch (Throwable e2) {
                // But if the logger fails, throw the original exception
                throw e;
            }
            throw e;
        }
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
