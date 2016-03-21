package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * A Json DTO for covariate configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateConfiguration {
    private List<JsonModelDisease> diseases;
    private List<JsonCovariateFile> files;

    public JsonCovariateConfiguration() {
    }

    public JsonCovariateConfiguration(List<JsonModelDisease> diseases, List<JsonCovariateFile> files) {
        setFiles(files);
        setDiseases(diseases);
    }

    public List<JsonModelDisease> getDiseases() {
        return diseases;
    }

    public void setDiseases(List<JsonModelDisease> diseases) {
        this.diseases = diseases;
    }

    public List<JsonCovariateFile> getFiles() {
        return files;
    }

    public void setFiles(List<JsonCovariateFile> files) {
        this.files = files;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonCovariateConfiguration that = (JsonCovariateConfiguration) o;

        if (diseases != null ? !diseases.equals(that.diseases) : that.diseases != null) return false;
        if (files != null ? !files.equals(that.files) : that.files != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseases != null ? diseases.hashCode() : 0;
        result = 31 * result + (files != null ? files.hashCode() : 0);
        return result;
    }
    ///COVERAGE:ON
    ///CHECKSTYLE:ON
}
