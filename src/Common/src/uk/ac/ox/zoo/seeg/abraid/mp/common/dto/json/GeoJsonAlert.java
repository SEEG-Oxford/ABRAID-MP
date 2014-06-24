package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;

/**
 * A DTO for uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Copyright (c) 2014 University of Oxford
 */

public final class GeoJsonAlert {
    private String title;
    private String summary;
    private String url;
    private String feedName;
    private String feedLanguage;

    public GeoJsonAlert() { }

    public GeoJsonAlert(Alert alert) {
        this.title = alert.getTitle();
        this.summary = alert.getSummary();
        this.url = alert.getUrl();
        this.feedName = alert.getFeed().getName();
        this.feedLanguage = alert.getFeed().getLanguage();
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

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeoJsonAlert that = (GeoJsonAlert) o;

        if (feedName != null ? !feedName.equals(that.feedName) : that.feedName != null) return false;
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
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
