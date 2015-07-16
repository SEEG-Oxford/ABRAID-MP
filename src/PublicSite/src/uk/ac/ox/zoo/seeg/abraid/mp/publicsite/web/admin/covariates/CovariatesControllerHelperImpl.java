package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

import static ch.lambdaj.Lambda.*;

/**
 * Helper for the AccountController, separated out into a class to isolate the transaction/exception rollback.
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class CovariatesControllerHelperImpl implements CovariatesControllerHelper {
    private final CovariateService covariateService;
    private final DiseaseService diseaseService;
    private static final String ERROR_CREATE_SUBDIRECTORY = "Could not create subdirectory for new covariate file";

    @Autowired
    public CovariatesControllerHelperImpl(CovariateService covariateService, DiseaseService diseaseService) {
        this.covariateService = covariateService;
        this.diseaseService = diseaseService;
    }

    /**
     * Builds a covariate file storage location.
     * @param subdirectory The subdirectory of the covariate directory in which to store the file.
     * @param file The covariate file.
     * @return The covariate file storage location
     */
    @Override
    public String extractTargetPath(String subdirectory, MultipartFile file) {
        String covariateDirectory = covariateService.getCovariateDirectory();
        Path path = Paths.get(covariateDirectory, subdirectory, file.getOriginalFilename()).normalize();
        return FilenameUtils.separatorsToUnix(path.toAbsolutePath().toString());
    }

    /**
     * Gets the JSON version of the covariate configuration.
     * @return The covariate configuration
     * @throws java.io.IOException Thrown if the covariate directory can not be checked for new files.
     */
    @Override
    public JsonCovariateConfiguration getCovariateConfiguration() throws IOException {
        checkForNewCovariateFilesOnDisk();
        return new JsonCovariateConfiguration(
                convert(diseaseService.getAllDiseaseGroups(), new Converter<DiseaseGroup, JsonModelDisease>() {
                    @Override
                    public JsonModelDisease convert(DiseaseGroup diseaseGroup) {
                        return new JsonModelDisease(diseaseGroup.getId(), diseaseGroup.getName());
                    }
                }),
                convert(covariateService.getAllCovariateFiles(), new Converter<CovariateFile, JsonCovariateFile>() {
                    @Override
                    public JsonCovariateFile convert(CovariateFile covariateFile) {
                        return new JsonCovariateFile(
                                covariateFile.getFile(),
                                covariateFile.getName(),
                                covariateFile.getInfo(),
                                covariateFile.getHide(),
                                extract(covariateFile.getEnabledDiseaseGroups(), on(DiseaseGroup.class).getId())
                        );
                    }
                })
        );
    }

    /**
     * Persist the JSON version of the covariate configuration into the database.
     * @param config The covariate configuration
     */
    @Override
    public  void setCovariateConfiguration(JsonCovariateConfiguration config) {
        Map<String, CovariateFile> allCovariateFiles =
                index(covariateService.getAllCovariateFiles(), on(CovariateFile.class).getFile());
        final Map<Integer, DiseaseGroup> allDiseaseGroups =
                index(diseaseService.getAllDiseaseGroups(), on(DiseaseGroup.class).getId());

        for (JsonCovariateFile jsonFile : config.getFiles()) {
            boolean changed = false;
            CovariateFile dbFile = allCovariateFiles.get(jsonFile.getPath());
            if (dbFile.getName() == null || !dbFile.getName().equals(jsonFile.getName())) {
                dbFile.setName(jsonFile.getName());
                changed = true;
            }

            if (dbFile.getInfo() == null || !dbFile.getInfo().equals(jsonFile.getInfo())) {
                dbFile.setInfo(jsonFile.getInfo());
                changed = true;
            }

            if (dbFile.getHide() == null || !dbFile.getHide().equals(jsonFile.getHide())) {
                dbFile.setHide(jsonFile.getHide());
                changed = true;
            }

            Collection<DiseaseGroup> enabledDiseases =
                    convert(jsonFile.getEnabled(), new Converter<Integer, DiseaseGroup>() {
                        @Override
                        public DiseaseGroup convert(Integer diseaseGroupId) {
                            return allDiseaseGroups.get(diseaseGroupId);
                        }
                    });

            if (dbFile.getEnabledDiseaseGroups() == null || !dbFile.getEnabledDiseaseGroups().equals(enabledDiseases)) {
                dbFile.setEnabledDiseaseGroups(enabledDiseases);
                changed = true;
            }

            if (changed) {
                covariateService.saveCovariateFile(dbFile);
            }
        }
    }


    /**
     * Persist a single new covariate file to the filesystem and database.
     * @param name The display name for the covariate.
     * @param path The location to store the covariate.
     * @param file The covariate.
     * @throws IOException Thrown if the covariate director can not be writen to.
     */
    @Override
    public void saveNewCovariateFile(String name, String path, MultipartFile file) throws IOException {
        writeCovariateFileToDisk(file, path);
        addCovariateToDatabase(name, extractRelativePath(path));
    }

    private void checkForNewCovariateFilesOnDisk() throws IOException {
        final Path covariateDirectoryPath = Paths.get(covariateService.getCovariateDirectory());
        File covariateDirectory = covariateDirectoryPath.toFile();

        if (covariateDirectory.exists()) {
            Collection<File> files = FileUtils.listFiles(covariateDirectory, null, true);
            Collection<String> paths = convert(files, new Converter<File, String>() {
                public String convert(File file) {
                    Path subPath = covariateDirectoryPath.relativize(file.toPath());
                    return FilenameUtils.separatorsToUnix(subPath.toString());
                }
            });

            Collection<CovariateFile> knownFiles = covariateService.getAllCovariateFiles();
            Collection<String> knownPaths = extract(knownFiles, on(CovariateFile.class).getFile());

            paths.removeAll(knownPaths);

            for (String path : paths) {
                addCovariateToDatabase("", path);
            }
        }
    }

    private void addCovariateToDatabase(String name, String path) throws IOException {
        covariateService.saveCovariateFile(new CovariateFile(
                name,
                path,
                false,
                ""
        ));
    }

    private void writeCovariateFileToDisk(MultipartFile file, String path) throws IOException {
        // Create directory
        createDirectoryForCovariate(path);

        File serverFile = Paths.get(path).toFile();
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(file.getBytes());
        stream.close();
    }

    private void createDirectoryForCovariate(String path) throws IOException {
        File dir = Paths.get(path).getParent().toFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException(ERROR_CREATE_SUBDIRECTORY);
            }
        }
    }

    private String extractRelativePath(String path) {
        Path parent = Paths.get(covariateService.getCovariateDirectory()).toAbsolutePath();
        Path child = Paths.get(path).toAbsolutePath();
        Path relativePath = parent.relativize(child).normalize();
        return FilenameUtils.separatorsToUnix(relativePath.toString());
    }
}
