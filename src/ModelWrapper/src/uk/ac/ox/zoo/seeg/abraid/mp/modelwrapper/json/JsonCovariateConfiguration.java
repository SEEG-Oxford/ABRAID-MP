package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import java.util.List;

/**
 * A Json DTO for covariate configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateConfiguration {
    private List<JsonDisease> diseases;
    private List<JsonCovariateFile> files;

    public JsonCovariateConfiguration() {
    }

    public JsonCovariateConfiguration(List<JsonDisease> diseases, List<JsonCovariateFile> files) {
        this.files = files;
        this.diseases = diseases;
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
}
