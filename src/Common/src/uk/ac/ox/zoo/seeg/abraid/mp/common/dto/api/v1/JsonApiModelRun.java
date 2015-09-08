package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateInfluence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Represents a model run in the v1 JSON API.
 * Copyright (c) 2015 University of Oxford
 */
public class JsonApiModelRun {
    private static final String GEOTIFF_URL_FORMAT = "/atlas/results/%s_%s.tif";
    private static final String EXTENT = "extent";
    private static final String PREDICTION = "mean";

    private String name;
    private JsonApiDiseaseGroup diseaseGroup;
    private String predictionData;
    private String extentData;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private DateTime triggerDate;
    private DateTime completionDate;
    private JsonApiDateRange occurrenceDateRange;
    private List<JsonApiCovariateInfluence> covariateInfluences;

    public JsonApiModelRun(ModelRun modelRun) {
        this.name = modelRun.getName();
        this.diseaseGroup = new JsonApiDiseaseGroup(modelRun.getDiseaseGroup());
        this.predictionData = String.format(GEOTIFF_URL_FORMAT, this.name, PREDICTION);
        this.extentData = String.format(GEOTIFF_URL_FORMAT, this.name, EXTENT);
        this.triggerDate = modelRun.getRequestDate();
        this.completionDate = modelRun.getResponseDate();
        this.occurrenceDateRange = new JsonApiDateRange(
                modelRun.getOccurrenceDataRangeStartDate(), modelRun.getOccurrenceDataRangeEndDate());
        this.covariateInfluences = convert(modelRun.getCovariateInfluences(),
            new Converter<CovariateInfluence, JsonApiCovariateInfluence>() {
                @Override
                public JsonApiCovariateInfluence convert(CovariateInfluence covariateInfluence) {
                    return new JsonApiCovariateInfluence(covariateInfluence);
                }
            }
        );
    }

    public String getName() {
        return name;
    }

    public JsonApiDiseaseGroup getDiseaseGroup() {
        return diseaseGroup;
    }

    public String getPredictionData() {
        return predictionData;
    }

    public String getExtentData() {
        return extentData;
    }

    public DateTime getTriggerDate() {
        return triggerDate;
    }

    public DateTime getCompletionDate() {
        return completionDate;
    }

    public JsonApiDateRange getOccurrenceDateRange() {
        return occurrenceDateRange;
    }

    public List<JsonApiCovariateInfluence> getCovariateInfluences() {
        return covariateInfluences;
    }
}
