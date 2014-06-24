package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

/**
 * Contains metadata associated with the outputs of a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelOutputsMetadata {
    private String modelRunName;
    private ModelRunStatus modelRunStatus;
    private String outputText;
    private String errorText;

    public JsonModelOutputsMetadata() {
    }

    public JsonModelOutputsMetadata(String modelRunName, ModelRunStatus modelRunStatus,
                                    String outputText, String errorText) {
        setModelRunName(modelRunName);
        setModelRunStatus(modelRunStatus);
        setOutputText(outputText);
        setErrorText(errorText);
    }

    public String getModelRunName() {
        return modelRunName;
    }

    public void setModelRunName(String modelRunName) {
        this.modelRunName = modelRunName;
    }

    public ModelRunStatus getModelRunStatus() {
        return modelRunStatus;
    }

    public void setModelRunStatus(ModelRunStatus modelRunStatus) {
        this.modelRunStatus = modelRunStatus;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
