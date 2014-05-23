package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

/**
 * Contains metadata associated with the outputs of a model run.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelOutputsMetadata {
    private String modelRunName;

    public JsonModelOutputsMetadata() {
    }

    public JsonModelOutputsMetadata(String modelRunName) {
        this.modelRunName = modelRunName;
    }

    public String getModelRunName() {
        return modelRunName;
    }

    public void setModelRunName(String modelRunName) {
        this.modelRunName = modelRunName;
    }
}
