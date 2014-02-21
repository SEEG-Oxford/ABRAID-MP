package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.healthmap.domain;

import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an alert from the HealthMap web service.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapAlert {
    // TODO: Ideally, move this to HealthMapWebService (custom JSON deserializer?)
    private static final Pattern ALERT_ID_REGEXP = Pattern.compile("http://healthmap.org/ln\\.php\\?(\\d+)");

    private String feed;
    private String disease;
    private String summary;
    private Date date;
    private String link;
    private String descr;
    private String original_url;

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
        this.link = link;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getOriginal_url() {
        return original_url;
    }

    public void setOriginal_url(String original_url) {
        this.original_url = original_url;
    }

    public Long getAlertId() {
        Long alertId = null;
        if (StringUtils.hasText(link)) {
            Matcher regExMatcher = ALERT_ID_REGEXP.matcher(link.trim());
            while(regExMatcher.find() && regExMatcher.groupCount() == 1) {
                alertId = Long.parseLong(regExMatcher.group(1));
            }
        }
        return alertId;
    }
}
