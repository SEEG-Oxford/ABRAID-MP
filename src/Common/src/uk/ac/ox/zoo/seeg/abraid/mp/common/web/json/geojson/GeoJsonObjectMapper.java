package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import java.text.DateFormat;
import java.util.TimeZone;

/**
 * A custom Jackson object mapper to ensure the JSON produced is GeoJSON compliant.
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonObjectMapper extends ObjectMapper {
    private static final String UTC = "UTC";

    public GeoJsonObjectMapper() {
        super();
        this.registerModule(new JodaModule());
        DateFormat dateFormat = ISO8601DateFormat.getDateTimeInstance();
        dateFormat.setTimeZone(TimeZone.getTimeZone(UTC));
        this.setDateFormat(dateFormat);
        this.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
    }
}
