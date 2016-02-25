package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import com.fasterxml.jackson.databind.ObjectWriter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.SourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data.InputDataManager;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.on;

/**
 * Builds a zip, representing a model run from a set of inputs.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelRunPackageBuilder {
    private static final Logger LOGGER = Logger.getLogger(ModelRunPackageBuilder.class);
    private static final String LOG_DIRECTORY_ERROR =
            "Model run package directory structure could not be created at %s";

    private static final String MODEL_CODE_DIRECTORY_NAME = "model";
    private static final String MODEL_DATA_DIRECTORY_NAME = "data";
    private static final String COVARIATES_DATA_DIRECTORY_NAME = "covariates";
    private static final String ADMIN_UNIT_DATA_DIRECTORY_NAME = "admins";
    private static final String LOG_PROVISIONING_WORKSPACE = "Provisioning workspace at %s";
    private static final String LOG_WORKSPACE_SUCCESSFULLY_PROVISIONED = "Workspace successfully provisioned at %s";
    private static final String UNSUPPORTED_MODEL_MODE =
           "Disease group (%s) is configured for a model mode (%s) that is not supported by the current model version.";

    private final SourceCodeManager sourceCodeManager;
    private final ScriptGenerator scriptGenerator;
    private final InputDataManager inputDataManager;
    private final AbraidJsonObjectMapper objectMapper;
    private RasterFilePathFactory rasterFilePathFactory;
    private final ModellingConfiguration modellingConfiguration;

    public ModelRunPackageBuilder(SourceCodeManager sourceCodeManager,
                                  ScriptGenerator scriptGenerator,
                                  InputDataManager inputDataManager,
                                  AbraidJsonObjectMapper objectMapper,
                                  RasterFilePathFactory rasterFilePathFactory,
                                  ModellingConfiguration modellingConfiguration) {
        this.sourceCodeManager = sourceCodeManager;
        this.scriptGenerator = scriptGenerator;
        this.inputDataManager = inputDataManager;
        this.objectMapper = objectMapper;
        this.rasterFilePathFactory = rasterFilePathFactory;
        this.modellingConfiguration = modellingConfiguration;
    }

    /**
     * Build a zip, representing a model run from a set of input data.
     * @param name The name of the model run.
     * @param diseaseGroup The disease group being modelled
     * @param occurrencesForModelRun The occurrences to be modelled.
     * @param diseaseExtent The extent to be modelled.
     * @param biasOccurrences The bias occurrences to be used by the model.
     * @param covariateFiles The covariate files to use in the model.
     * @param covariateDirectory The directory where the covariates can be found.
     * @return The zip file.
     * @throws IOException Thrown if the zip workspace provisioning fails.
     */
    public File buildPackage(String name, DiseaseGroup diseaseGroup,
                             List<DiseaseOccurrence> occurrencesForModelRun,
                             Collection<AdminUnitDiseaseExtentClass> diseaseExtent,
                             List<DiseaseOccurrence> biasOccurrences,
                             Collection<CovariateFile> covariateFiles,
                             String covariateDirectory) throws IOException {
        // Check the mode
        if (!sourceCodeManager.getSupportedModesForCurrentVersion().contains(diseaseGroup.getModelMode())) {
            throw new ModelRunWorkflowException(String.format(
                    UNSUPPORTED_MODEL_MODE, diseaseGroup.getId(), diseaseGroup.getModelMode()));
        }

        // Determine paths
        Path workingDirectory = Paths.get(FileUtils.getTempDirectoryPath(), name);
        Path zipFile = Paths.get(FileUtils.getTempDirectoryPath(), name + ".zip");

        try {
            // create metadata
            JsonModelRun metadata = createJsonModelRun(diseaseGroup, name);

            // provision workspace
            LOGGER.info(String.format(LOG_PROVISIONING_WORKSPACE, workingDirectory.toString()));
            buildDirectories(workingDirectory);
            addMetadata(workingDirectory, metadata);
            addData(workingDirectory, diseaseGroup, occurrencesForModelRun, diseaseExtent, biasOccurrences);
            addCovariates(workingDirectory, covariateFiles, covariateDirectory);
            addGaulLayers(workingDirectory);
            addRModelCode(workingDirectory, diseaseGroup, covariateFiles);

            LOGGER.info(String.format(LOG_WORKSPACE_SUCCESSFULLY_PROVISIONED, workingDirectory.toString()));

            // build zip
            zipWorkspace(workingDirectory, zipFile);
        } catch (Exception e) {
            // clean up zip
            if (zipFile != null && zipFile.toFile().exists()) {
                Files.delete(zipFile);
            }
            throw new IOException(e);
        } finally {
            // clean up dir
            if (workingDirectory != null && workingDirectory.toFile().exists()) {
                FileUtils.deleteDirectory(workingDirectory.toFile());
            }
        }
        return zipFile.toFile();
    }

    private JsonModelRun createJsonModelRun(DiseaseGroup diseaseGroup, String name) {
        JsonModelDisease jsonModelDisease = new JsonModelDisease(diseaseGroup);
        return new JsonModelRun(jsonModelDisease, name);
    }

    private void buildDirectories(Path workingDirectoryPath) throws IOException {
        // Create directories
        boolean workingDirectoryCreated = workingDirectoryPath.toFile().mkdirs();

        File modelDirectory = null;
        if (workingDirectoryCreated) {
            Path modelDirectoryPath = Paths.get(workingDirectoryPath.toString(), MODEL_CODE_DIRECTORY_NAME);
            modelDirectory = modelDirectoryPath.toFile();
            workingDirectoryCreated = modelDirectory.mkdir();
        }

        File dataDirectory = null;
        if (workingDirectoryCreated) {
            Path dataDirectoryPath = Paths.get(workingDirectoryPath.toString(), MODEL_DATA_DIRECTORY_NAME);
            dataDirectory = dataDirectoryPath.toFile();
            workingDirectoryCreated = dataDirectory.mkdir();
        }

        File covariatesDirectory = null;
        if (workingDirectoryCreated) {
            Path covariatesDirectoryPath = Paths.get(workingDirectoryPath.toString(), COVARIATES_DATA_DIRECTORY_NAME);
            covariatesDirectory = covariatesDirectoryPath.toFile();
            workingDirectoryCreated = covariatesDirectory.mkdir();
        }

        File adminUnitsDirectory = null;
        if (workingDirectoryCreated) {
            Path adminUnitsDirectoryPath = Paths.get(workingDirectoryPath.toString(), ADMIN_UNIT_DATA_DIRECTORY_NAME);
            adminUnitsDirectory = adminUnitsDirectoryPath.toFile();
            workingDirectoryCreated = adminUnitsDirectory.mkdir();
        }

        if (!workingDirectoryCreated) {
            LOGGER.warn(String.format(LOG_DIRECTORY_ERROR, workingDirectoryPath.toString()));
            throw new IOException("Directory structure could not be created.");
        }
    }

    private void addMetadata(Path workingDirectory, JsonModelRun metadata) throws IOException {
        // Write metadata
        Path metadataPath = Paths.get(workingDirectory.toString(), "metadata.json");
        ObjectWriter writer = objectMapper.writer();
        try {
            writer.writeValue(metadataPath.toFile(), metadata);
        } catch (IOException e) {
            throw new IOException("Metadata file could not be created.");
        }
    }

    private void addData(Path workingDirectory,
                         DiseaseGroup disease,
                         List<DiseaseOccurrence> occurrenceData,
                         Collection<AdminUnitDiseaseExtentClass> extentData,
                         List<DiseaseOccurrence> biasOccurrenceData) throws IOException {
        File dataDirectory = Paths.get(workingDirectory.toString(), MODEL_DATA_DIRECTORY_NAME).toFile();
        // Copy input data
        inputDataManager.writeOccurrenceData(occurrenceData, dataDirectory, false);
        inputDataManager.writeOccurrenceData(biasOccurrenceData, dataDirectory, true);
        File baseExtentRaster = rasterFilePathFactory.getExtentGaulRaster(disease.isGlobal());
        inputDataManager.writeExtentData(extentData, baseExtentRaster, dataDirectory);
    }

    private void addCovariates(Path workingDirectory, Collection<CovariateFile> covariateFiles,
                               String covariateStorageDirectory) throws IOException {
        File covariatesDirectory = Paths.get(workingDirectory.toString(), COVARIATES_DATA_DIRECTORY_NAME).toFile();
        // Covariate data
        List<CovariateSubFile> files = flatten(extract(covariateFiles, on(CovariateFile.class).getFiles()));
        for (CovariateSubFile file : files) {
            FileUtils.copyFile(
                    Paths.get(covariateStorageDirectory, file.getFile()).toFile(),
                    Paths.get(covariatesDirectory.toString(), file.getFile()).toFile()
            );
        }
    }

    private void addGaulLayers(Path workingDirectory) throws IOException {
        File adminDirectory = Paths.get(workingDirectory.toString(), ADMIN_UNIT_DATA_DIRECTORY_NAME).toFile();
        //Admin units
        FileUtils.copyFile(
                rasterFilePathFactory.getAdminRaster(0),
                Paths.get(adminDirectory.toString(), "admin0.tif").toFile()
        );
        FileUtils.copyFile(
                rasterFilePathFactory.getAdminRaster(1),
                Paths.get(adminDirectory.toString(), "admin1.tif").toFile()
        );
        FileUtils.copyFile(
                rasterFilePathFactory.getAdminRaster(2),
                Paths.get(adminDirectory.toString(), "admin2.tif").toFile()
        );
    }

    private void addRModelCode(Path workingDirectory, DiseaseGroup diseaseGroup, Collection<CovariateFile> covariates)
            throws IOException {
        File modelDirectory = Paths.get(workingDirectory.toString(), MODEL_CODE_DIRECTORY_NAME).toFile();
        // Copy model
        sourceCodeManager.provision(modelDirectory);

        // Template script
        scriptGenerator.generateScript(modellingConfiguration, workingDirectory.toFile(), diseaseGroup, covariates);
    }

    private void zipWorkspace(Path workingDirectoryPath, Path zipFilePath) throws ZipException {
        ZipFile zip = new ZipFile(zipFilePath.toFile());
        ZipParameters parameters = new ZipParameters();
        parameters.setIncludeRootFolder(false);
        zip.createZipFileFromFolder(workingDirectoryPath.toFile(), parameters, false, 0);
    }
}
