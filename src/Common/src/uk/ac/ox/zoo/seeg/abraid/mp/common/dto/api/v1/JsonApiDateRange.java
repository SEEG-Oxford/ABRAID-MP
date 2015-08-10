package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Represents a date range in the v1 JSON API.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiDateRange {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime start;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime end;

    public JsonApiDateRange(DateTime startDate, DateTime endDate) {
        this.start = startDate;
        this.end = endDate;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }
}
