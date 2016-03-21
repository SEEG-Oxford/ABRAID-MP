package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import ch.lambdaj.collection.LambdaList;
import ch.lambdaj.collection.LambdaMap;
import ch.lambdaj.collection.LambdaSet;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static ch.lambdaj.collection.LambdaCollections.with;
import static org.hamcrest.Matchers.isIn;

/**
 * A validator for checking the user input for the actions associated with the CovariatesController.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerValidator {
    private static final String FAIL_FILE_MISSING = "File missing.";
    private static final String FAIL_NAME_MISSING = "Name missing.";
    private static final String FAIL_INVALID_PARENT = "Parent ID not valid.";
    private static final String FAIL_QUALIFIER_MISSING = "Qualifier missing.";
    private static final String FAIL_SUBDIRECTORY_MISSING = "Subdirectory missing.";
    private static final String FAIL_SUBDIRECTORY_NOT_VALID = "Subdirectory not valid.";
    private static final String FAIL_NAME_NOT_UNIQUE = "Name not unique.";
    private static final String FAIL_FILE_ALREADY_EXISTS = "File already exists.";
    private static final String FAIL_TARGET_PATH_NOT_VALID = "Target path not valid.";
    private static final String FAIL_FILES_IS_NULL = "'files' is null.";
    private static final String FAIL_FILES_PATH_DO_NOT_MATCH = "Unexpected file or subfile listed or missing.";
    private static final String FAIL_ENABLED_DISEASE_IDS_CONTAINS_DUPLICATES =
            "Enabled disease ids contains duplicates.";
    private static final String FAIL_UNKNOWN_DISEASE_ID_REFERENCED_BY_FILE =
            "One or more files specify usage for an unknown disease id.";

    private final CovariateService covariateService;
    private final DiseaseService diseaseService;

    @Autowired
    public CovariatesControllerValidator(CovariateService covariateService, DiseaseService diseaseService) {
        this.covariateService = covariateService;
        this.diseaseService = diseaseService;
    }

    /**
     * Validate the user input from a covariate upload.
     * @param name The display name for the covariate (null if a sub file).
     * @param qualifier The qualifier name for the covariate sub file (ie the year/month).
     * @param parentId The ID of the parent covariate for this file (or null if this is the first file).
     * @param subdirectory The directory to add the file to.
     * @param file The covariate file.
     * @param targetPath The location to store the file.
     * @return A set of validation failures.
     */
    public Collection<String> validateCovariateUpload(String name, String qualifier, Integer parentId,
                                                      String subdirectory, MultipartFile file, String targetPath) {
        List<String> messages = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            messages.add(FAIL_FILE_MISSING);
        }

        if (parentId != null && covariateService.getCovariateFileById(parentId) == null) {
            messages.add(FAIL_INVALID_PARENT);
        }

        if (parentId == null && StringUtils.isEmpty(name)) {
            messages.add(FAIL_NAME_MISSING);
        }

        if (StringUtils.isEmpty(qualifier)) {
            messages.add(FAIL_QUALIFIER_MISSING);
        }

        if (StringUtils.isEmpty(subdirectory)) {
            messages.add(FAIL_SUBDIRECTORY_MISSING);
        }

        if (!StringUtils.isEmpty(subdirectory) && checkForNonNormalPath(subdirectory)) {
            messages.add(FAIL_SUBDIRECTORY_NOT_VALID);
        }

        if (!checkCovariateNameUniqueness(name)) {
            messages.add(FAIL_NAME_NOT_UNIQUE);
        }

        if (messages.isEmpty()) {
            if (Paths.get(targetPath).toFile().exists()) {
                messages.add(FAIL_FILE_ALREADY_EXISTS);
            }

            if (!checkPathUnderCovariateDir(covariateService.getCovariateDirectory(), targetPath)) {
                messages.add(FAIL_TARGET_PATH_NOT_VALID);
            }
        }

        return messages;
    }

    private boolean checkForNonNormalPath(String subdirectory) {
        return
                subdirectory.contains("/./") ||
                        subdirectory.contains("/../") ||
                        subdirectory.contains("\\") ||
                        subdirectory.contains("//");
    }

    private boolean checkCovariateNameUniqueness(String name) {
        return !extract(covariateService.getAllCovariateFiles(), on(CovariateFile.class).getName()).contains(name);
    }

    private boolean checkPathUnderCovariateDir(String covariateDirectory, String path) {
        Path parent = Paths.get(covariateDirectory).toAbsolutePath();
        Path child = Paths.get(path).toAbsolutePath();
        while (child != null && !child.equals(parent)) {
            child = child.getParent();
        }
        return child != null;
    }

    /**
     * Validate the user input from a covariate configuration change.
     * @param config The new configuration.
     * @return A set of validation failures.
     */
    public Collection<String> validateCovariateConfiguration(JsonCovariateConfiguration config) {
        List<String> messages = new ArrayList<>();
        if (!checkFilesFieldForNull(config)) {
            messages.add(FAIL_FILES_IS_NULL);
        } else {
            if (!checkFileListAndSubFiles(config)) {
                messages.add(FAIL_FILES_PATH_DO_NOT_MATCH);
            }

            if (!checkEnabledDiseaseUniqueness(config)) {
                messages.add(FAIL_ENABLED_DISEASE_IDS_CONTAINS_DUPLICATES);
            }

            if (!checkDiseaseReferenceIntegrity(config)) {
                messages.add(FAIL_UNKNOWN_DISEASE_ID_REFERENCED_BY_FILE);
            }
        }

        return messages;
    }

    private boolean checkFilesFieldForNull(JsonCovariateConfiguration config) {
        return (config != null && config.getFiles() != null);
    }

    private boolean checkFileListAndSubFiles(JsonCovariateConfiguration config) {
        List<CovariateFile> knownCovariates = covariateService.getAllCovariateFiles();
        List<JsonCovariateFile> configCovariates = config.getFiles();

        LambdaMap<Integer, CovariateFile> knownCovariatesById = with(knownCovariates)
                .index(on(CovariateFile.class).getId());

        LambdaMap<Integer, JsonCovariateFile> configCovariatesById = with(configCovariates)
                .index(on(JsonCovariateFile.class).getId());

        // The IDs of the covariates must match the ones in the db
        LambdaSet<Integer> knowIds = knownCovariatesById.keySet();
        LambdaSet<Integer> configIds = configCovariatesById.keySet();

        if (!knowIds.equals(configIds)) {
            return false;
        }

        // Check Subfiles
        for (Integer id : knowIds) {
            CovariateFile knownCovariate = knownCovariatesById.get(id);
            JsonCovariateFile configCovariate = configCovariatesById.get(id);

            if (!checkSubFile(knownCovariate, configCovariate)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkSubFile(CovariateFile knownCovariate, JsonCovariateFile configCovariate) {
        List<CovariateSubFile> knownCovariateSubFiles = knownCovariate.getFiles();
        List<JsonCovariateSubFile> configCovariateSubFiles = configCovariate.getSubFiles();

        // The IDs of the sub files for this covariate must match the ones in the db
        LambdaList<Integer> knownSubFileIds = with(knownCovariateSubFiles)
                .extract(on(CovariateSubFile.class).getId());
        LambdaList<Integer> configSubFileIds = with(configCovariateSubFiles)
                .extract(on(JsonCovariateSubFile.class).getId());

        if (!knownSubFileIds.equals(configSubFileIds)) {
            return false;
        }

        // The file paths of the sub files for this covariate must match the ones in the db
        LambdaList<String> knownSubFilePaths = with(knownCovariateSubFiles)
                .extract(on(CovariateSubFile.class).getFile());
        LambdaList<String> configSubFilePaths = with(configCovariateSubFiles)
                .extract(on(JsonCovariateSubFile.class).getPath());

        if (!knownSubFilePaths.equals(configSubFilePaths)) {
            return false;
        }

        return true;
    }


    private boolean checkEnabledDiseaseUniqueness(JsonCovariateConfiguration config) {
        for (JsonCovariateFile file : config.getFiles()) {
            if (with(file.getEnabled()).distinct().size() != file.getEnabled().size()) {
                return false;
            }
        }
        return true;
    }

    private boolean checkDiseaseReferenceIntegrity(JsonCovariateConfiguration config) {
        Collection<Integer> knownDiseaseIds =
                with(diseaseService.getAllDiseaseGroups()).extract(on(DiseaseGroup.class).getId());
        Collection<Integer> linkedDiseaseIds =
                flatten(with(config.getFiles()).extract(on(JsonCovariateFile.class).getEnabled()));
        return with(linkedDiseaseIds).all(isIn(knownDiseaseIds));
    }

}
