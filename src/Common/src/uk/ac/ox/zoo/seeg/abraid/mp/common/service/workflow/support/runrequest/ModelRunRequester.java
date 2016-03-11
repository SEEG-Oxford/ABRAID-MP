package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.function.matcher.LambdaJMatcher;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;

import java.io.File;
import java.net.URI;
import java.util.*;

import static ch.lambdaj.Lambda.*;

/**
 * Requests a model run for all relevant diseases.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunRequester {
    private ModelWrapperWebService modelWrapperWebService;
    private CovariateService covariateService;
    private final ModelRunPackageBuilder modelRunPackageBuilder;
    private DiseaseService diseaseService;
    private ModelRunService modelRunService;
    private List<String> biasModelModes;

    private static final Logger LOGGER = Logger.getLogger(ModelRunRequester.class);
    private static final String WEB_SERVICE_ERROR_MESSAGE = "Error when requesting a model run: %s";
    private static final String REQUEST_LOG_MESSAGE =
            "Requesting a model run for disease group %d (%s) with %d disease occurrence(s)";
    private static final String NO_OCCURRENCES_MESSAGE = "Cannot request a model run because there are no occurrences";
    private static final String CLEAN_UP_WARNING_MESSAGE = "Could not clean up workspace package (%s).";

    // This the max file name length (255) minus reserved space for a GUID (36), a datetime (19) and separators (2)
    private static final int MAX_DISEASE_NAME_LENGTH = 195;

    private List<URI> modelWrapperUrlCollection;

    public ModelRunRequester(ModelWrapperWebService modelWrapperWebService,
                             ModelRunPackageBuilder modelRunPackageBuilder,
                             CovariateService covariateService,
                             DiseaseService diseaseService, ModelRunService modelRunService,
                             String[] biasModelModes,
                             String[] modelWrapperUrlCollection) {
        this.modelWrapperWebService = modelWrapperWebService;
        this.modelRunPackageBuilder = modelRunPackageBuilder;
        this.diseaseService = diseaseService;
        this.covariateService = covariateService;
        this.modelRunService = modelRunService;
        this.biasModelModes = Arrays.asList(biasModelModes);
        this.modelWrapperUrlCollection = convert(modelWrapperUrlCollection, new Converter<String, URI>() {
            @Override
            public URI convert(String url) {
                return URI.create(url);
            }
        });
        if (this.modelWrapperUrlCollection.isEmpty()) {
            throw new IllegalArgumentException("At least 1 ModelWrapper URL must be provided.");
        }
    }

    /**
     * Requests a model run for the specified disease group.
     * @param diseaseGroupId The id of the disease group.
     * @param occurrencesForModelRun The disease occurrences to send to the model.
     * @param batchStartDate The start date for batching (if validator parameter batching should happen after the model
     * run is completed), otherwise null.
     * @param batchEndDate The end date for batching (if it should happen), otherwise null.
     * @throws ModelRunWorkflowException if the model run could not be requested.
     */
    public void requestModelRun(int diseaseGroupId, List<DiseaseOccurrence> occurrencesForModelRun,
                                DateTime batchStartDate, DateTime batchEndDate) throws ModelRunWorkflowException {
        if (occurrencesForModelRun != null && occurrencesForModelRun.size() > 0) {
            // Collate the data
            DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
            Collection<AdminUnitDiseaseExtentClass> diseaseExtent =
                    diseaseService.getDiseaseExtentByDiseaseGroupId(diseaseGroupId);
            Collection<CovariateFile> covariateFiles = covariateService.getCovariateFilesByDiseaseGroup(diseaseGroup);
            String covariateDirectory = covariateService.getCovariateDirectory();
            DateTime startDate = min(occurrencesForModelRun, on(DiseaseOccurrence.class).getOccurrenceDate());
            DateTime endDate = max(occurrencesForModelRun, on(DiseaseOccurrence.class).getOccurrenceDate());

            List<DiseaseOccurrence> biasOccurrences = null;
            if (biasModelModes.contains(diseaseGroup.getModelMode())) {
                if (diseaseService.getCountOfUnfilteredBespokeBiasOccurrences(diseaseGroup) != 0) {
                    biasOccurrences = (diseaseService.getCountOfUnfilteredBespokeBiasOccurrences(diseaseGroup) != 0) ?
                            diseaseService.getBespokeBiasOccurrencesForModelRun(diseaseGroup, startDate, endDate) :
                            diseaseService.getDefaultBiasOccurrencesForModelRun(diseaseGroup, startDate, endDate);
                }
            }

            // Pick a blade
            URI modelWrapperUrl = selectLeastBusyModelWrapperUrl();

            File runPackage = null;
            try {
                // Build the work package
                String name = buildRunName(diseaseGroup.getAbbreviation());
                runPackage = modelRunPackageBuilder.buildPackage(name, diseaseGroup, occurrencesForModelRun,
                        diseaseExtent, biasOccurrences, covariateFiles, covariateDirectory);

                // Submit the run
                logRequest(diseaseGroup, occurrencesForModelRun);
                ModelRun modelRun = createPreliminaryModelRun(name, diseaseGroup, modelWrapperUrl, startDate, endDate);
                setModelRunProperties(
                        modelRun, diseaseGroup, batchStartDate, batchEndDate, occurrencesForModelRun, diseaseExtent);

                JsonModelRunResponse response = modelWrapperWebService.startRun(modelWrapperUrl, runPackage);

                handleModelRunResponse(response, modelRun);
            } catch (Exception e) {
                String message = String.format(WEB_SERVICE_ERROR_MESSAGE, e.getMessage());
                LOGGER.error(message);
                throw new ModelRunWorkflowException(message, e);
            } finally {
                if (runPackage != null && runPackage.exists() && !runPackage.delete()) {
                    LOGGER.warn(String.format(CLEAN_UP_WARNING_MESSAGE, runPackage.getAbsolutePath()));
                }
            }
        } else {
            throw new ModelRunWorkflowException(NO_OCCURRENCES_MESSAGE);
        }
    }

    private ModelRun createPreliminaryModelRun(String name, DiseaseGroup diseaseGroup, URI modelWrapperUrl,
                                               DateTime startDate, DateTime endDate) {
        return new ModelRun(name, diseaseGroup, modelWrapperUrl.getHost(), DateTime.now(), startDate, endDate);
    }

    private void setModelRunProperties(final ModelRun modelRun, DiseaseGroup diseaseGroup,
                                       DateTime batchStartDate, DateTime batchEndDate,
                                       List<DiseaseOccurrence> occurrencesForModelRun,
                                       Collection<AdminUnitDiseaseExtentClass> diseaseExtent) {
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        modelRun.setInputDiseaseExtent(convert(diseaseExtent,
            new Converter<AdminUnitDiseaseExtentClass, ModelRunAdminUnitDiseaseExtentClass>() {
                @Override
                public ModelRunAdminUnitDiseaseExtentClass convert(AdminUnitDiseaseExtentClass adminUnitExtentClass) {
                    return new ModelRunAdminUnitDiseaseExtentClass(adminUnitExtentClass, modelRun);
                }
            }
        ));
        if (diseaseGroup.isAutomaticModelRunsEnabled()) {
            modelRun.setInputDiseaseOccurrences(occurrencesForModelRun);
        }
    }

    private URI selectLeastBusyModelWrapperUrl() {
        Stack<String> usedHostsWithBusiestAtTop = new Stack<>();
        usedHostsWithBusiestAtTop.addAll(modelRunService.getModelRunRequestServersByUsage());

        List<URI> availableHostsInPreferenceOrder = modelWrapperUrlCollection;

        // Until only 1 available host remains, or there are no more used hosts remaining
        while (availableHostsInPreferenceOrder.size() > 1 && usedHostsWithBusiestAtTop.size() > 0) {
            // Remove the busiest of the used hosts from the list of available hosts (assuming it's present)
            final String busiestHost = usedHostsWithBusiestAtTop.pop();
            availableHostsInPreferenceOrder = filter(new LambdaJMatcher<URI>() {
                @Override
                public boolean matches(Object host) {
                    return !((URI) host).getHost().equals(busiestHost);
                }
            }, availableHostsInPreferenceOrder);
        }

        // If only 1 host remains use that one, if multiple hosts remain just use the first (preferred) one.
        return availableHostsInPreferenceOrder.get(0);
    }

    private void logRequest(DiseaseGroup diseaseGroup, List<DiseaseOccurrence> diseaseOccurrences) {
        LOGGER.info(String.format(REQUEST_LOG_MESSAGE, diseaseGroup.getId(), diseaseGroup.getName(),
                diseaseOccurrences.size()));
    }

    private String buildRunName(String diseaseAbbreviation) {
        String safeDiseaseName = diseaseAbbreviation.replaceAll("[^A-Za-z0-9]", "-");
        if (safeDiseaseName.length() > MAX_DISEASE_NAME_LENGTH) {
            safeDiseaseName = safeDiseaseName.substring(0, MAX_DISEASE_NAME_LENGTH);
        }

        return safeDiseaseName + "_" +
                LocalDateTime.now().toString("yyyy-MM-dd-HH-mm-ss") + "_" +
                UUID.randomUUID();
    }

    private void handleModelRunResponse(JsonModelRunResponse response, ModelRun modelRun) {
        if (StringUtils.hasText(response.getErrorText())) {
            String message = String.format(WEB_SERVICE_ERROR_MESSAGE, response.getErrorText());
            LOGGER.error(message);
            throw new ModelRunWorkflowException(message);
        } else {
            modelRunService.saveModelRun(modelRun);
        }
    }
}
