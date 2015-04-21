package uk.ac.ox.zoo.seeg.abraid.mp.datamanager;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process.DataAcquisitionManager;
import uk.ac.ox.zoo.seeg.abraid.mp.datamanager.process.DiseaseProcessManager;

import java.util.ArrayList;
import java.util.List;

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
    private static final String FINISHED_MESSAGE = "Data Manager finished";

    private final DiseaseService diseaseService;
    private final DataAcquisitionManager dataAcquisitionManager;
    private final DiseaseProcessManager diseaseProcessManager;
    private final String applicationVersion;

    public Main(DiseaseService diseaseService,
                DataAcquisitionManager dataAcquisitionManager,
                DiseaseProcessManager diseaseProcessManager,
                String applicationVersion) {
        this.diseaseService = diseaseService;
        this.dataAcquisitionManager = dataAcquisitionManager;
        this.diseaseProcessManager = diseaseProcessManager;
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
        main.runMain(args);
    }

    /**
     * Performs the main DataManager process.
     * @param dataAcquisitionArgs Command line arguments. If specified, these are interpreted as a list of file
     *                            names containing HealthMap JSON data to acquire.
     */
    public void runMain(String[] dataAcquisitionArgs) {
        logStarted();
        List<Integer> diseaseGroupIdsForAutomaticModelRuns = getDiseaseGroupIdsForAutomaticModelRuns();
        updateExpertsWeightings();
        processOccurrencesOnDataValidator(diseaseGroupIdsForAutomaticModelRuns);
        runDataAcquisition(dataAcquisitionArgs);
        updateDiseaseExtents(diseaseGroupIdsForAutomaticModelRuns);
        requestModelRuns(diseaseGroupIdsForAutomaticModelRuns);
        logFinished();
    }

    private void logStarted() {
        String message = String.format(STARTED_MESSAGE, applicationVersion);
        LOGGER.info(message);
        System.out.println(message);
    }

    private List<Integer> getDiseaseGroupIdsForAutomaticModelRuns() {
        List<Integer> diseaseGroupIdsForAutomaticModelRuns = diseaseService.getDiseaseGroupIdsForAutomaticModelRuns();
        // Return a mutable version of the list.
        return new ArrayList<>(diseaseGroupIdsForAutomaticModelRuns);
    }

    /**
     * Calculates and saves the new weighting for each active expert, with their reviews across all disease groups.
     */
    private void updateExpertsWeightings() {
        try {
            diseaseProcessManager.updateExpertsWeightings();
        } catch (Exception e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
            // Ignore the exception, because it is thrown to roll back the transaction if the process step fails.
            // Logging has already been done by this point.
        }

    }

    /**
     * Process any occurrences currently on the validator, for each disease group that has automatic model runs enabled.
     * @param diseaseGroupIdsForAutomaticModelRuns The id of all disease groups to act on.
     */
    private void processOccurrencesOnDataValidator(List<Integer> diseaseGroupIdsForAutomaticModelRuns) {
        for (Integer diseaseGroupId : diseaseGroupIdsForAutomaticModelRuns) {
            try {
                diseaseProcessManager.processOccurrencesOnDataValidator(diseaseGroupId);
            } catch (Exception e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
                // Ignore the exception, because it is thrown to roll back the transaction per disease group if the
                // process step fails. Logging has already been done by this point.
            }
        }
    }

    /**
     * Acquires data from all sources.
     * @param fileNames A list of file names containing data to acquire. If no file names are specified
     * (or if null), the HealthMap web service will be called instead.
     */
    private void runDataAcquisition(String[] fileNames) {
        try {
            dataAcquisitionManager.runDataAcquisition(fileNames);
        } catch (Exception e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
            // Ignore the exception, because it is thrown to roll back the transaction per disease group if the
            // process step fails. Logging has already been done by this point.
        }
    }

    /**
     * Updates the disease extents if required, for each disease group that has automatic model runs enabled.
     * @param diseaseGroupIdsForAutomaticModelRuns The id of all disease groups to act on.
     */
    private void updateDiseaseExtents(List<Integer> diseaseGroupIdsForAutomaticModelRuns) {
        List<Integer> diseaseGroupIdsForAutomaticModelRunsClone = new ArrayList<>(diseaseGroupIdsForAutomaticModelRuns);
        for (Integer diseaseGroupId : diseaseGroupIdsForAutomaticModelRunsClone) {
            try {
                diseaseProcessManager.updateDiseaseExtents(diseaseGroupId);
            } catch (Exception e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
                // Catch the exception, because it is thrown to roll back the transaction per disease group if the
                // process step fails. Logging has already been done by this point.

                // If extent generation fails for a disease, we should not attempt a model run today for that disease
                diseaseGroupIdsForAutomaticModelRuns.remove(diseaseGroupId);
            }
        }
    }

    /**
     * Requests a model run if required, for each disease group that has automatic model runs enabled.
     * @param diseaseGroupIdsForAutomaticModelRuns The id of all disease groups to act on.
     */
    private void requestModelRuns(List<Integer> diseaseGroupIdsForAutomaticModelRuns) {
        for (Integer diseaseGroupId : diseaseGroupIdsForAutomaticModelRuns) {
            try {
                diseaseProcessManager.requestModelRun(diseaseGroupId);
            } catch (Exception e) { ///CHECKSTYLE:SUPPRESS EmptyBlock
                // Ignore the exception, because it is thrown to roll back the transaction per disease group if the
                // process step fails. Logging has already been done by this point.
            }
        }
    }

    private void logFinished() {
        LOGGER.info(FINISHED_MESSAGE);
    }
}
