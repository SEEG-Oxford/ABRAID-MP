package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;

import java.util.Map;

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
    private Map<String, String> covariateNames;

    public JsonModelOutputsMetadata() {
    }

    public JsonModelOutputsMetadata(String modelRunName, ModelRunStatus modelRunStatus,
                                    String outputText, String errorText, Map<String, String> covariateNames) {
        setModelRunName(modelRunName);
        setModelRunStatus(modelRunStatus);
        setOutputText(outputText);
        setErrorText(errorText);
        setCovariateNames(covariateNames);
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

    public Map<String, String> getCovariateNames() {
        return covariateNames;
    }

    public void setCovariateNames(Map<String, String> covariateNames) {
        this.covariateNames = covariateNames;
    }
}
