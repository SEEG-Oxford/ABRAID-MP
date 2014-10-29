package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.SubmodelStatistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * A DTO to represent a model run when expressing the available WMS layers for display in the atlas.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunLayer {
    private String date;
    private String id;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    public JsonModelRunLayer(ModelRun modelRun) {
        this.date = DATE_FORMAT.print(modelRun.getRequestDate());
        this.id = modelRun.getName();
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }
}
