package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import com.fasterxml.jackson.databind.ObjectWriter;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ModellingConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRun;
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
     * @param covariateFiles The covariate files to use in the model.
     * @param covariateDirectory The directory where the covariates can be found.
     * @return The zip file.
     * @throws IOException Thrown if the zip workspace provisioning fails.
     */
    public File buildPackage(String name, DiseaseGroup diseaseGroup,
                             List<DiseaseOccurrence> occurrencesForModelRun,
                             Collection<AdminUnitDiseaseExtentClass> diseaseExtent,
                             Collection<CovariateFile> covariateFiles, String covariateDirectory) throws IOException {
        // Determine paths
        Path workingDirectoryPath = Paths.get(FileUtils.getTempDirectoryPath(), name);
        Path zipFilePath = Paths.get(FileUtils.getTempDirectoryPath(), name + ".zip");

        try {
            // create metadata
            JsonModelRun metadata = createJsonModelRun(diseaseGroup, name);

            // provision workspace
            provisionWorkspace(workingDirectoryPath, metadata, occurrencesForModelRun, diseaseExtent,
                    covariateFiles, covariateDirectory);

            // build zip
            zipWorkspace(workingDirectoryPath, zipFilePath);
        } catch (Exception e) {
            // clean up zip
            if (zipFilePath != null && zipFilePath.toFile().exists()) {
                Files.delete(zipFilePath);
            }
            throw new IOException(e);
        } finally {
            // clean up dir
            if (workingDirectoryPath != null && workingDirectoryPath.toFile().exists()) {
                FileUtils.deleteDirectory(workingDirectoryPath.toFile());
            }
        }
        return zipFilePath.toFile();
    }

    private JsonModelRun createJsonModelRun(DiseaseGroup diseaseGroup, String name) {
        JsonModelDisease jsonModelDisease = new JsonModelDisease(diseaseGroup);
        return new JsonModelRun(jsonModelDisease, name);
    }

    private File provisionWorkspace(
            Path workingDirectoryPath,
            JsonModelRun metadata,
            List<DiseaseOccurrence> occurrenceData,
            Collection<AdminUnitDiseaseExtentClass> extentData,
            Collection<CovariateFile> covariateFiles, String covariateStorageDirectory)
            throws IOException {
        // Create directories
        LOGGER.info(String.format(LOG_PROVISIONING_WORKSPACE, workingDirectoryPath.toString()));
        File workingDirectory = workingDirectoryPath.toFile();
        boolean workingDirectoryCreated = workingDirectory.mkdirs();

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

        // Write metadata
        Path metadataPath = Paths.get(workingDirectoryPath.toString(), "metadata.json");
        ObjectWriter writer = objectMapper.writer();
        try {
            writer.writeValue(metadataPath.toFile(), metadata);
        } catch (IOException e) {
            throw new IOException("Metadata file could not be created.");
        }

        // Copy input data
        inputDataManager.writeOccurrenceData(occurrenceData, dataDirectory);
        File baseExtentRaster = rasterFilePathFactory.getExtentGaulRaster(metadata.getDisease().isGlobal());
        inputDataManager.writeExtentData(extentData, baseExtentRaster, dataDirectory);

        // Covariate data
        for (CovariateFile file : covariateFiles) {
            FileUtils.copyFile(
                    Paths.get(covariateStorageDirectory, file.getFile()).toFile(),
                    Paths.get(covariatesDirectory.toString(), file.getFile()).toFile()
            );
        }

        //Admin units
        FileUtils.copyFile(
                    rasterFilePathFactory.getAdminRaster(0),
                    Paths.get(adminUnitsDirectory.toString(), "admin0.tif").toFile()
        );
        FileUtils.copyFile(
                rasterFilePathFactory.getAdminRaster(1),
                Paths.get(adminUnitsDirectory.toString(), "admin1.tif").toFile()
        );
        FileUtils.copyFile(
                rasterFilePathFactory.getAdminRaster(2),
                Paths.get(adminUnitsDirectory.toString(), "admin2.tif").toFile()
        );

        // Copy model
        sourceCodeManager.provision(modelDirectory);

        // Template script
        File runScript = scriptGenerator.generateScript(modellingConfiguration, workingDirectory);

        LOGGER.info(String.format(LOG_WORKSPACE_SUCCESSFULLY_PROVISIONED, workingDirectoryPath.toString()));
        return runScript;
    }

    private void zipWorkspace(Path workingDirectoryPath, Path zipFilePath) throws ZipException {
        ZipFile zip = new ZipFile(zipFilePath.toFile());
        ZipParameters parameters = new ZipParameters();
        parameters.setIncludeRootFolder(false);
        zip.createZipFileFromFolder(workingDirectoryPath.toFile(), parameters, false, 0);
    }
}
