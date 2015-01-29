package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

/**
 * A DTO to represent a model run when expressing the available WMS layers for display in the atlas.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunLayer {
    private final boolean automaticRun;
    private final String date;
    private final String id;
    private final String rangeStart;
    private final String rangeEnd;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public JsonModelRunLayer(ModelRun modelRun, boolean automaticRun) {
        this.automaticRun = automaticRun;
        this.date = DATE_FORMAT.print(modelRun.getRequestDate());
        this.id = modelRun.getName();
        this.rangeStart = DATE_FORMAT.print(modelRun.getOccurrenceDataRangeStartDate());
        this.rangeEnd = DATE_FORMAT.print(modelRun.getOccurrenceDataRangeEndDate());
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getRangeEnd() {
        return rangeEnd;
    }

    public String getRangeStart() {
        return rangeStart;
    }

    public boolean isAutomatic() {
        return automaticRun;
    }
}
