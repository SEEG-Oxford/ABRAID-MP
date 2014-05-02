package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

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

    public boolean isHide() {
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

    public boolean isValid() {
        boolean valid = StringUtils.isNotEmpty(path);
        LOGGER.assertLog(!valid, LOG_PATH_NOT_SPECIFIED);

        valid = valid && with(enabled).distinct().size() == enabled.size();
        LOGGER.assertLog(!valid, LOG_ENABLED_DISEASE_IDS_CONTAINS_DUPLICATES);

        return valid;
    }
}
