package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.ParseUtils;

import java.util.*;
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
    @JsonProperty("feed_id")
    private Integer feedId;
    private String disease;
    private List<String> diseases;
    @JsonProperty("disease_id")
    private Integer diseaseId;
    @JsonProperty("disease_ids")
    private List<Integer> diseaseIds;
    private String summary;
    private DateTime date;
    private DateTime reviewed;
    private String link;
    @JsonProperty("descr")
    private String description;
    @JsonProperty("original_url")
    private String originalUrl;
    @JsonProperty("feed_lang")
    private String feedLanguage;
    private String comment;

    public HealthMapAlert() {
    }

    public HealthMapAlert(String feed, Integer feedId, String disease, Integer diseaseId, String summary, DateTime date,
                          String link, String description, String originalUrl, String feedLanguage) {
        this.feed = feed;
        this.feedId = feedId;
        this.disease = disease;
        this.diseaseId = diseaseId;
        this.summary = summary;
        this.date = date;
        this.link = link;
        this.description = description;
        this.originalUrl = originalUrl;
        this.feedLanguage = feedLanguage;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = ParseUtils.convertString(feed);
    }

    public Integer getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = ParseUtils.parseInteger(feedId);
    }

    public void setDisease(String disease) {
        this.disease = ParseUtils.convertString(disease);
    }

    public List<String> getDiseases() {
        return retrieveListOrSingleItem(diseases, disease);
    }

    public void setDiseases(List<String> diseases) {
        this.diseases = ParseUtils.convertStrings(diseases);
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = ParseUtils.parseInteger(diseaseId);
    }

    public List<Integer> getDiseaseIds() {
        return retrieveListOrSingleItem(diseaseIds, diseaseId);
    }

    /**
     * Sets the disease IDs. Only those that are successfully parsed to integers are added.
     * @param diseaseIds The disease IDs.
     */
    public void setDiseaseIds(List<String> diseaseIds) {
        this.diseaseIds = ParseUtils.parseIntegers(diseaseIds);
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = ParseUtils.convertString(summary);
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public DateTime getReviewed() {
        return reviewed;
    }

    public void setReviewed(DateTime reviewed) {
        this.reviewed = reviewed;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = ParseUtils.convertString(link);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = ParseUtils.convertString(description);
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = ParseUtils.convertString(originalUrl);
    }

    public String getFeedLanguage() {
        return feedLanguage;
    }

    public void setFeedLanguage(String feedLanguage) {
        this.feedLanguage = ParseUtils.convertString(feedLanguage);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = ParseUtils.convertString(comment);
    }

    /**
     * Extracts the alert ID from the link.
     * @return The alert ID, or null if it could not be extracted from the link.
     */
    public Integer getAlertId() {
        Integer alertId = null;
        if (StringUtils.hasText(link)) {
            Matcher regExMatcher = ALERT_ID_REGEXP.matcher(link.trim());
            while (regExMatcher.find() && regExMatcher.groupCount() == 1) {
                alertId = ParseUtils.parseInteger(regExMatcher.group(1));
            }
        }
        return alertId;
    }

    /**
     * Gets the comment, split into constituent parts and sanitised.
     * @return The split comment, or an empty list if no comment.
     */
    public Set<String> getSplitComment() {
        Set<String> splitComment = new HashSet<>();
        for (String part : splitCommaDelimitedString(this.comment)) {
            // For each comment part, remove whitespace and make lowercase
            splitComment.add(part.replaceAll("\\s", "").toLowerCase());
        }
        return splitComment;
    }

    private <T> List<T> retrieveListOrSingleItem(List<T> list, T item) {
        // Returns a non-null list of items, either from the input list or the input item
        if (list != null && list.size() > 0) {
            return list;
        } else if (item == null) {
            return new ArrayList<>();
        } else {
            return Arrays.asList(item);
        }
    }

    private List<String> splitCommaDelimitedString(String text) {
        List<String> splitList = new ArrayList<>();

        if (StringUtils.hasText(text)) {
            // Note: all tokens are trimmed and empty tokens are ignored
            String[] splitArray = StringUtils.tokenizeToStringArray(text, ",", true, true);
            Collections.addAll(splitList, splitArray);
        }

        return splitList;
    }
}
