package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

/**
 * A DTO to represent a DiseaseOccurrence ready for download.
 * Used for CSV serialization of occurrences on public site.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({
        "longitude", "latitude", "weight", "admin", "gaul", "disease", "date", "provenance", "feed", "url" })
public class JsonDownloadDiseaseOccurrence extends JsonModellingDiseaseOccurrence {
    @JsonProperty("Provenance")
    private String provenance;

    @JsonProperty("Feed")
    private String feed;

    @JsonProperty("Url")
    private String url;

    public JsonDownloadDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                         double longitude, double latitude, double weight,
                                         int admin, String gaul, int disease,
                                         String date, String provenance, String feed, String url) {
        super(precisionAdjuster, longitude, latitude, weight, admin, gaul, disease, date);
        setProvenance(provenance);
        setFeed(feed);
        setUrl(url);
    }

    public JsonDownloadDiseaseOccurrence(ModellingLocationPrecisionAdjuster precisionAdjuster,
                                         DiseaseOccurrence inputDiseaseOccurrence) {
        this(precisionAdjuster,
            inputDiseaseOccurrence.getLocation().getGeom().getX(),
            inputDiseaseOccurrence.getLocation().getGeom().getY(),
            inputDiseaseOccurrence.getFinalWeighting(),
            inputDiseaseOccurrence.getLocation().getPrecision().getModelValue(),
            extractGaulString(inputDiseaseOccurrence.getLocation()),
            inputDiseaseOccurrence.getDiseaseGroup().getId(),
            extractDateString(inputDiseaseOccurrence.getOccurrenceDate()),
            inputDiseaseOccurrence.getAlert().getFeed().getProvenance().getName(),
            inputDiseaseOccurrence.getAlert().getFeed().getName(),
            inputDiseaseOccurrence.getAlert().getUrl());
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = (url != null) ? url : "-";
    }
}
