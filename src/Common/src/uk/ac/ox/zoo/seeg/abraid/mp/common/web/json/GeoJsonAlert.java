package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

/**
 * A DTO for uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Copyright (c) 2014 University of Oxford
 */
public final class GeoJsonAlert {
    private final String title;
    private final String summary;
    private final String url;
    private final String feedName;
    private final String feedLanguage;
    private final DateTime publicationDate;

    public GeoJsonAlert(Alert alert) {
        this.title = alert.getTitle();
        this.summary = alert.getSummary();
        this.url = alert.getUrl();
        this.feedName = alert.getFeed().getName();
        this.feedLanguage = alert.getFeed().getLanguage();
        this.publicationDate = alert.getPublicationDate();
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getUrl() {
        return url;
    }

    public String getFeedName() {
        return feedName;
    }

    public String getFeedLanguage() {
        return feedLanguage;
    }

    public DateTime getPublicationDate() {
        return publicationDate;
    }
}
