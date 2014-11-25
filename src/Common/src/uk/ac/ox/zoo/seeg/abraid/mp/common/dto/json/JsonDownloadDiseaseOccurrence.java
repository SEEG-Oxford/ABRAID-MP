package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * A DTO to represent a DiseaseOccurrence ready for download.
 * Used for CSV serialization of occurrences on public site.
 * Copyright (c) 2014 University of Oxford
 */
@JsonPropertyOrder({ "longitude", "latitude", "weight", "admin", "gaul", "provenance", "feed" })
public class JsonDownloadDiseaseOccurrence extends JsonModellingDiseaseOccurrence {
    @JsonProperty("Provenance")
    private String provenance;

    @JsonProperty("Feed")
    private String feed;

    public JsonDownloadDiseaseOccurrence(double longitude, double latitude, double weight, int admin, String gaul,
                                         String provenance, String feed) {
        super(longitude, latitude, weight, admin, gaul);
        setProvenance(provenance);
        setFeed(feed);
    }

    public JsonDownloadDiseaseOccurrence(DiseaseOccurrence inputDiseaseOccurrence) {
        this(inputDiseaseOccurrence.getLocation().getGeom().getX(),
            inputDiseaseOccurrence.getLocation().getGeom().getY(),
            inputDiseaseOccurrence.getFinalWeighting(),
            inputDiseaseOccurrence.getLocation().getPrecision().getModelValue(),
            extractGaulString(inputDiseaseOccurrence.getLocation().getAdminUnitQCGaulCode()),
            inputDiseaseOccurrence.getAlert().getFeed().getProvenance().getName(),
            inputDiseaseOccurrence.getAlert().getFeed().getName());
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
}
