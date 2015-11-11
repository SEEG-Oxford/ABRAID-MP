package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.LocalDate;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

/**
 * A simplified model run DTO for use in the data extraction tool.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonModelRunSimple {
    private String name;
    private String disease;
    private LocalDate date;

    public JsonModelRunSimple(ModelRun modelRun) {
        this.name = modelRun.getName();
        this.disease = modelRun.getDiseaseGroup().getShortNameForDisplay();
        this.date = modelRun.getRequestDate().toLocalDate();
    }

    public String getName() {
        return name;
    }

    public String getDisease() {
        return disease;
    }

    public LocalDate getDate() {
        return date;
    }
}
