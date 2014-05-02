package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

import static ch.lambdaj.Lambda.flatten;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.collection.LambdaCollections.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;

/**
 * A Json DTO for covariate configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateConfiguration {
    private static final Logger LOGGER = Logger.getLogger(JsonCovariateConfiguration.class);
    private static final String LOG_DISEASES_IS_NULL =
            "Configuration validation failure: 'diseases' is null.";
    private static final String LOG_FILES_IS_NULL =
            "Configuration validation failure: 'files' is null.";
    private static final String LOG_UNKNOWN_DISEASE_ID_REFERENCED_BY_FILE =
            "Configuration validation failure: One or more files specify usage for an unknown disease id.";
    private static final String LOG_DISEASES_ARE_DUPLICATED =
            "Configuration validation failure: One or more diseases are duplicated.";
    private static final String LOG_FILES_ARE_DUPLICATED = "" +
            "Configuration validation failure: One or more files are duplicated.";

    private List<JsonDisease> diseases;
    private List<JsonCovariateFile> files;

    public JsonCovariateConfiguration() {
    }

    public JsonCovariateConfiguration(List<JsonDisease> diseases, List<JsonCovariateFile> files) {
        setFiles(files);
        setDiseases(diseases);
    }

    public List<JsonDisease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<JsonDisease> diseases) {
        this.diseases = diseases;
    }

    public List<JsonCovariateFile> getFiles() {
        return files;
    }

    public void setFiles(List<JsonCovariateFile> files) {
        this.files = files;
    }

    /**
     * Determines if the configuration object is valid.
     * @return The validity.
     */
    @JsonIgnore
    public boolean isValid() {
        return
                checkDiseaseFieldForNull() &&
                checkFilesFieldForNull() &&
                checkDiseaseSubItems() &&
                checkFileSubItems() &&
                checkDiseaseReferenceIntegrity() &&
                checkDiseaseUniqueness() &&
                checkFileUniqueness();
    }

    private boolean checkFileUniqueness() {
        // Check uniqueness of files
        boolean valid = with(files).distinct(on(JsonCovariateFile.class).getPath()).size() == files.size();
        LOGGER.assertLog(valid, LOG_FILES_ARE_DUPLICATED);
        return valid;
    }

    private boolean checkDiseaseUniqueness() {
        // Check uniqueness of diseases
        boolean valid = with(diseases).distinct(on(JsonDisease.class).getId()).size() == diseases.size();
        LOGGER.assertLog(valid, LOG_DISEASES_ARE_DUPLICATED);
        return valid;
    }

    private boolean checkDiseaseReferenceIntegrity() {
        // Check integrity of disease references in file objects
        Collection<Integer> diseaseIds = with(diseases).extract(on(JsonDisease.class).getId());
        Collection<Integer> linkedDiseaseIds = flatten(with(files).extract(on(JsonCovariateFile.class).getEnabled()));
        boolean valid = with(linkedDiseaseIds).all(isIn(diseaseIds));
        LOGGER.assertLog(valid, LOG_UNKNOWN_DISEASE_ID_REFERENCED_BY_FILE);
        return valid;
    }

    private boolean checkFileSubItems() {
        // Check validity of file sub items
        // Logs printed in sub items
        return with(files).extract(on(JsonCovariateFile.class).isValid()).all(equalTo(true));
    }

    private boolean checkDiseaseSubItems() {
        // Check validity of disease sub items
        // Logs printed in sub items
        return with(diseases).extract(on(JsonDisease.class).isValid()).all(equalTo(true));
    }

    private boolean checkFilesFieldForNull() {
        // Check files field not null
        boolean valid = files != null;
        LOGGER.assertLog(valid, LOG_FILES_IS_NULL);
        return valid;
    }

    private boolean checkDiseaseFieldForNull() {
        // Check diseases field not null
        boolean valid = diseases != null;
        LOGGER.assertLog(valid, LOG_DISEASES_IS_NULL);
        return valid;
    }
}
