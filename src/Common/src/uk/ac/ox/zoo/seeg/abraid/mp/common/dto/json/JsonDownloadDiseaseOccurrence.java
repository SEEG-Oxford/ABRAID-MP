package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * A DTO to represent a DiseaseOccurrence ready for download.
 * Used for CSV serialization of occurrences on public site.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({ "longitude", "latitude", "weight", "admin", "gaul", "date", "provenance", "feed", "url" })
public class JsonDownloadDiseaseOccurrence extends JsonModellingDiseaseOccurrence {
    @JsonProperty("Date")
    private DateTime date;

    @JsonProperty("Provenance")
    private String provenance;

    @JsonProperty("Feed")
    private String feed;

    @JsonProperty("Url")
    private String url;

    public JsonDownloadDiseaseOccurrence(double longitude, double latitude, double weight, int admin, String gaul,
                                         DateTime date, String provenance, String feed, String url) {
        super(longitude, latitude, weight, admin, gaul);
        setDate(date);
        setProvenance(provenance);
        setFeed(feed);
        setUrl(url);
    }

    public JsonDownloadDiseaseOccurrence(DiseaseOccurrence inputDiseaseOccurrence) {
        this(inputDiseaseOccurrence.getLocation().getGeom().getX(),
            inputDiseaseOccurrence.getLocation().getGeom().getY(),
            inputDiseaseOccurrence.getFinalWeighting(),
            inputDiseaseOccurrence.getLocation().getPrecision().getModelValue(),
            extractGaulString(inputDiseaseOccurrence.getLocation().getAdminUnitQCGaulCode()),
            inputDiseaseOccurrence.getOccurrenceDate(),
            inputDiseaseOccurrence.getAlert().getFeed().getProvenance().getName(),
            inputDiseaseOccurrence.getAlert().getFeed().getName(),
            inputDiseaseOccurrence.getAlert().getUrl());
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
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
        this.url = url;
    }
}
