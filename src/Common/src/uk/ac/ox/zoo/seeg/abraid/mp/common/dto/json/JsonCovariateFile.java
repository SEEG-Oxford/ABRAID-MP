package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * A Json DTO for covariate file configuration.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonCovariateFile {
    private int id;
    private List<JsonCovariateSubFile> subFiles;
    private String name;
    private String info;
    private boolean hide;
    private boolean discrete;
    private List<Integer> enabled;

    public JsonCovariateFile() {
    }

    public JsonCovariateFile(int id, List<JsonCovariateSubFile> subFiles, String name, String info, boolean hide,
                             boolean discrete, List<Integer> enabled) {
        setId(id);
        setSubFiles(subFiles);
        setName(name);
        setInfo(info);
        setHide(hide);
        setDiscrete(discrete);
        setEnabled(enabled);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<JsonCovariateSubFile> getSubFiles() {
        return subFiles;
    }

    public void setSubFiles(List<JsonCovariateSubFile> subFiles) {
        this.subFiles = subFiles;
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

    public boolean getDiscrete() {
        return discrete;
    }

    public void setDiscrete(boolean discrete) {
        this.discrete = discrete;
    }

    public List<Integer> getEnabled() {
        return enabled;
    }

    public void setEnabled(List<Integer> enabled) {
        this.enabled = enabled;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonCovariateFile that = (JsonCovariateFile) o;

        if (discrete != that.discrete) return false;
        if (hide != that.hide) return false;
        if (id != that.id) return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (info != null ? !info.equals(that.info) : that.info != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (subFiles != null ? !subFiles.equals(that.subFiles) : that.subFiles != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (subFiles != null ? subFiles.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        result = 31 * result + (hide ? 1 : 0);
        result = 31 * result + (discrete ? 1 : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        return result;
    }
    ///COVERAGE:ON
    ///CHECKSTYLE:ON
}

