package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

/**
 * A Json DTO for covariate sub file configuration.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonCovariateSubFile {
    private int id;
    private String path;
    private String qualifier;

    public JsonCovariateSubFile() {
    }

    public JsonCovariateSubFile(int id, String path, String qualifier) {
        setId(id);
        setPath(path);
        setQualifier(qualifier);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonCovariateSubFile that = (JsonCovariateSubFile) o;

        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        return result;
    }
    ///COVERAGE:ON
    ///CHECKSTYLE:ON
}

