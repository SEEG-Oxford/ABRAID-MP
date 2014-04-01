package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

/**
 * A DTO for uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonAlert {
    private String title;
    private String summary;
    private String url;
    private String feedName;
    private DateTime publicationDate;

    public GeoJsonAlert() {
    }

    public GeoJsonAlert(Alert alert) {
        setTitle(alert.getTitle());
        setSummary(alert.getSummary());
        setUrl(alert.getUrl());
        setFeedName(alert.getFeed().getName());
        setPublicationDate(alert.getPublicationDate());
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

    public DateTime getPublicationDate() {
        return publicationDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public void setPublicationDate(DateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    ///COVERAGE:OFF generated code
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoJsonAlert that = (GeoJsonAlert) o;

        if (feedName != null ? !feedName.equals(that.feedName) : that.feedName != null) return false;
        if (publicationDate != null ? !publicationDate.equals(that.publicationDate) : that.publicationDate != null)
            return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (feedName != null ? feedName.hashCode() : 0);
        result = 31 * result + (publicationDate != null ? publicationDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
    ///COVERAGE:ON
    ///COVERAGE:ON
}
