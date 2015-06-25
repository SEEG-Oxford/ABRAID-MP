package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

/**
 * A Json DTO for covariate file configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateFile {
    private static final Logger LOGGER = Logger.getLogger(JsonCovariateFile.class);
    private static final String LOG_PATH_NOT_SPECIFIED =
            "Configuration validation failure (file): Path not specified.";
    private static final String LOG_ENABLED_DISEASE_IDS_CONTAINS_DUPLICATES =
            "Configuration validation failure (file): Enabled disease ids contains duplicates.";

    private String path;
    private String name;
    private String info;
    private boolean hide;
    private List<Integer> enabled;

    public JsonCovariateFile() {
    }

    public JsonCovariateFile(String path, String name, String info, boolean hide, List<Integer> enabled) {
        setPath(path);
        setName(name);
        setInfo(info);
        setHide(hide);
        setEnabled(enabled);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public List<Integer> getEnabled() {
        return enabled;
    }

    public void setEnabled(List<Integer> enabled) {
        this.enabled = enabled;
    }

    /**
     * Determines if the configuration object is valid.
     * @return The validity.
     */
    @JsonIgnore
    public boolean isValid() {
        return
                checkPathHasValue() &&
                checkEnabledIdsAreUnique();
    }

    private boolean checkEnabledIdsAreUnique() {
        boolean valid;
        valid = with(enabled).distinct().size() == enabled.size();
        LOGGER.assertLog(valid, LOG_ENABLED_DISEASE_IDS_CONTAINS_DUPLICATES);
        return valid;
    }

    private boolean checkPathHasValue() {
        boolean valid = StringUtils.isNotEmpty(path);
        LOGGER.assertLog(valid, LOG_PATH_NOT_SPECIFIED);
        return valid;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonCovariateFile that = (JsonCovariateFile) o;

        if (hide != that.hide) return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (info != null ? !info.equals(that.info) : that.info != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (hide ? 1 : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        return result;
    }
    ///COVERAGE:ON
    ///CHECKSTYLE:ON
}

