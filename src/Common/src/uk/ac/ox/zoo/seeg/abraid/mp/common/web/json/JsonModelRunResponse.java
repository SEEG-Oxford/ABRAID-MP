package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

/**
 * The JSON DTO used to respond to model run requests.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunResponse {
    private String modelRunName;
    private String errorText;

    public JsonModelRunResponse() {
    }

    public JsonModelRunResponse(String modelRunName, String errorText) {
        this.modelRunName = modelRunName;
        this.errorText = errorText;
    }

    public String getModelRunName() {
        return modelRunName;
    }

    public void setModelRunName(String modelRunName) {
        this.modelRunName = modelRunName;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
