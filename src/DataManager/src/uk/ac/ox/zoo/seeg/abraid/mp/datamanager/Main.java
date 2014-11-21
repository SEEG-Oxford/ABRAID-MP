package uk.ac.ox.zoo.seeg.abraid.mp.datamanager;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process.DataAcquisitionManager;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process.ModelRunManager;

import java.util.Map;

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
            "classpath:uk/ac/ox/zoo/seeg/abraid/mp/datamanager/config/beans.xml";
    private static final Logger LOGGER = Logger.getLogger(Main.class);
    private static final String STARTED_MESSAGE = "Data Manager version %s: started";

    private DataAcquisitionManager dataAcquisitionManager;
    private ModelRunManager modelRunManager;
    private String applicationVersion;

    public Main(DataAcquisitionManager dataAcquisitionManager, ModelRunManager modelRunManager,
                String applicationVersion) {
        this.dataAcquisitionManager = dataAcquisitionManager;
        this.modelRunManager = modelRunManager;
        this.applicationVersion = applicationVersion;
    }

    /**
     * Entry method for the DataAcquisition module.
     * @param args Command line arguments. If specified, these are interpreted as a list of file names containing
     *             HealthMap JSON data to acquire.
     */
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = null;
        try {
            context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_LOCATION);
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
        } finally {
            if (context != null) {
                context.close();
            }
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
        logStarted(main.applicationVersion);
        main.runDataAcquisition(args);
        main.prepareForAndRequestModelRuns();
    }

    /**
     * Acquires data from all sources.
     * @param fileNames A list of file names containing data to acquire. If no file names are specified
     * (or if null), the HealthMap web service will be called instead.
     */
    public void runDataAcquisition(String[] fileNames) {
        dataAcquisitionManager.runDataAcquisition(fileNames);
    }

    /**
     * Requests a model run (after preparation and if relevant), for each disease group that has automatic model
     * runs enabled.
     */
    public void prepareForAndRequestModelRuns() {
        Map<Integer, Double> newExpertWeightings = modelRunManager.prepareExpertsWeightings();
        for (int diseaseGroupId : modelRunManager.getDiseaseGroupIdsForAutomaticModelRuns()) {
            prepareForAndRequestModelRun(diseaseGroupId);
        }
        modelRunManager.saveExpertsWeightings(newExpertWeightings);
    }

    private void prepareForAndRequestModelRun(int diseaseGroupId) {
        try {
            modelRunManager.prepareForAndRequestModelRun(diseaseGroupId);
        } catch (ModelRunWorkflowException e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
            // Ignore the exception, because it is thrown to roll back the transaction per disease group if the model
            // run request fails. Logging has already been done by this point.
        }
    }

    private static void logStarted(String applicationVersion) {
        String message = String.format(STARTED_MESSAGE, applicationVersion);
        LOGGER.info(message);
        System.out.println(message);
    }
}
