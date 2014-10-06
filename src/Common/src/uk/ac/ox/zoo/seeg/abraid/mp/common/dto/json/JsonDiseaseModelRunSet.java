package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.List;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseModelRunSet {
    private String disease;
    private List<JsonModelRunLayer> runs;

    public JsonDiseaseModelRunSet(String disease, List<JsonModelRunLayer> runs) {
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
