package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web.covariates;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json.JsonCovariateFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * A validator for checking the user input for the actions associated with the CovariatesController.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariatesControllerValidator {
    private static final String FAIL_FILE_MISSING = "File missing.";
    private static final String FAIL_NAME_MISSING = "Name missing.";
    private static final String FAIL_SUBDIRECTORY_MISSING = "Subdirectory missing.";
    private static final String FAIL_SUBDIRECTORY_NOT_VALID = "Subdirectory not valid.";
    private static final String FAIL_NAME_NOT_UNIQUE = "Name not unique.";
    private static final String FAIL_FILE_ALREADY_EXISTS = "File already exists.";
    private static final String FAIL_TARGET_PATH_NOT_VALID = "Target path not valid.";

    /**
     * Validate the user input from a covariate upload.
     * @param name Name of the covariate file.
     * @param subdirectory The directory to add the file to.
     * @param file The covariate file.
     * @param targetPath The absolute path at which the file should be added.
     * @param covariateDirectory The absolute path of the covariate directory.
     * @param covariateConfiguration The current covariate config.
     * @return A set of validation failures.
     */
    public Collection<String> validateCovariateUpload(String name, String subdirectory, MultipartFile file,
                                                      String targetPath, String covariateDirectory,
                                                      JsonCovariateConfiguration covariateConfiguration) {
        List<String> messages = new ArrayList<>();

        if (file == null || file.isEmpty()) {
            messages.add(FAIL_FILE_MISSING);
        }

        if (StringUtils.isEmpty(name)) {
            messages.add(FAIL_NAME_MISSING);
        }

        if (StringUtils.isEmpty(subdirectory)) {
            messages.add(FAIL_SUBDIRECTORY_MISSING);
        }

        if (!StringUtils.isEmpty(subdirectory) && checkForNonNormalPath(subdirectory)) {
            messages.add(FAIL_SUBDIRECTORY_NOT_VALID);
        }

        if (!checkCovariateNameUniqueness(name, covariateConfiguration)) {
            messages.add(FAIL_NAME_NOT_UNIQUE);
        }

        if (messages.isEmpty()) {
            if (Paths.get(targetPath).toFile().exists()) {
                messages.add(FAIL_FILE_ALREADY_EXISTS);
            }

            if (!checkPathUnderCovariateDir(covariateDirectory, targetPath)) {
                messages.add(FAIL_TARGET_PATH_NOT_VALID);
            }
        }

        return messages;
    }

    private boolean checkForNonNormalPath(String subdirectory) {
        return (subdirectory.contains("/./") || subdirectory.contains("/../") || subdirectory.contains("\\") || subdirectory.contains("//"));
    }

    private boolean checkCovariateNameUniqueness(String name, JsonCovariateConfiguration covariateConfiguration) {
        return !extract(covariateConfiguration.getFiles(), on(JsonCovariateFile.class).getName()).contains(name);
    }

    private boolean checkPathUnderCovariateDir(String covariateDirectory, String path) {
        Path parent = Paths.get(covariateDirectory).toAbsolutePath();
        Path child = Paths.get(path).toAbsolutePath();
        while (child != null && !child.equals(parent)) {
            child = child.getParent();
        }
        return child != null;
    }
}
