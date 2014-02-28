package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.ParseUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an alert from the HealthMap web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlert {
    // Regular expression for extracting the alert ID from a link.
    // For example, in the link "http://healthmap.org/ln.php?2154965", the alert ID is 2154965.
    private static final Pattern ALERT_ID_REGEXP = Pattern.compile("http://healthmap.org/ln\\.php\\?(\\d+)");

    private String feed;
    private String disease;
    private String summary;
    private Date date;
    private String link;
    @JsonProperty("descr")
    private String description;
    @JsonProperty("original_url")
    private String originalUrl;

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = StringUtils.trimWhitespace(feed);
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = StringUtils.trimWhitespace(disease);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = StringUtils.trimWhitespace(summary);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = StringUtils.trimWhitespace(link);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimWhitespace(description);
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = StringUtils.trimWhitespace(originalUrl);
    }

    /**
     * Extracts the alert ID from the link.
     * @return The alert ID, or null if it could not be extracted from the link.
     */
    public Long getAlertId() {
        Long alertId = null;
        if (StringUtils.hasText(link)) {
            Matcher regExMatcher = ALERT_ID_REGEXP.matcher(link.trim());
            while (regExMatcher.find() && regExMatcher.groupCount() == 1) {
                alertId = ParseUtils.parseLong(regExMatcher.group(1));
            }
        }
        return alertId;
    }
}
