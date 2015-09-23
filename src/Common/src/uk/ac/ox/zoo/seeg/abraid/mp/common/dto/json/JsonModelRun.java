package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang.StringUtils;

/**
 * The JSON DTO used to trigger model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRun {
    private JsonModelDisease disease;
    private String runName;

    public JsonModelRun() {
    }

    public JsonModelRun(JsonModelDisease disease,
                        String runName) {
        setDisease(disease);
        setRunName(runName);
    }

    public JsonModelDisease getDisease() {
        return disease;
    }

    public void setDisease(JsonModelDisease disease) {
        this.disease = disease;
    }

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    @JsonIgnore
    public boolean isValid() {
        return disease != null && runName != null && StringUtils.isNotBlank(runName);
    }
}
