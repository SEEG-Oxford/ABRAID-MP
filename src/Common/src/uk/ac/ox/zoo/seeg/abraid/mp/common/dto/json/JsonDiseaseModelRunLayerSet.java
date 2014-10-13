package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * A DTO to represent collection of a model runs grouped by disease, used expressing the available WMS layers for
 * display in the atlas.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseModelRunLayerSet {
    private String disease;
    private List<JsonModelRunLayer> runs;

    public JsonDiseaseModelRunLayerSet(String disease, List<JsonModelRunLayer> runs) {
        this.disease = disease;
        this.runs = runs;
    }

    public String getDisease() {
        return disease;
    }

    public List<JsonModelRunLayer> getRuns() {
        return runs;
    }
}
