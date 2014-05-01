package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.json;

import java.util.List;

/**
 * A Json DTO for covariate file configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateFile {
    private String path;
    private String name;
    private String info;
    private boolean hide;
    private List<Integer> enabled;

    public JsonCovariateFile() {
    }

    public JsonCovariateFile(String path, String name, String info, boolean hide, List<Integer> enabled) {
        this.path = path;
        this.name = name;
        this.info = info;
        this.hide = hide;
        this.enabled = enabled;
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
}
