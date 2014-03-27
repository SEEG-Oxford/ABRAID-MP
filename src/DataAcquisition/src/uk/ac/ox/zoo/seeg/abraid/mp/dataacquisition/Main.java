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
     * @param args Command line arguments. If specified, these are interpreted as a list of file names containing
     *             HealthMap JSON data to acquire.
     */
    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_LOCATION);
            runMain(context, args);
        } catch (Throwable e) {
            try {
                // Ensure that top-level exceptions are logged
                LOGGER.fatal(e.getMessage(), e);
            } catch (Throwable e2) {
                // But if the logging fails, throw the original exception
                throw e;
            }
            throw e;
        }
    }

    /**
     * Retrieves the main class from the application context, and runs its main method.
     * @param context The application context
     * @param args Command line arguments. If specified, these are interpreted as a list of file names containing
     *             HealthMap JSON data to acquire.
     */
    public static void runMain(ApplicationContext context, String[] args) {
        Main main = (Main) context.getBean("main");
        main.acquireData(args);
    }

    public Main(HealthMapDataAcquisition healthMapDataAcquisition) {
        this.healthMapDataAcquisition = healthMapDataAcquisition;
    }

    /**
     * Acquires data from all sources.
     * @param fileNames A list of file names containing HealthMap JSON data to acquire. If no file names are specified
     * (or if null), the HealthMap web service will be called instead.
     */
    public void acquireData(String[] fileNames) {
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                healthMapDataAcquisition.acquireDataFromFile(fileName);
            }
        } else {
            healthMapDataAcquisition.acquireDataFromWebService();
        }
    }
}
