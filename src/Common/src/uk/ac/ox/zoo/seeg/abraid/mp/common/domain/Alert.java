package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a report of a disease occurrence or occurrences, from a feed.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getAlertByHealthMapAlertId",
                query = "from Alert where healthMapAlertId=:healthMapAlertId"
        )
})
@Entity
public class Alert {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease.
    @ManyToOne
    @JoinColumn(name = "feedId")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Feed feed;

    // The title of the alert.
    @Column
    private String title;

    // The publication date of the alert. This may be different from the actual date that the disease occurred,
    // which if known is specified in DiseaseOccurrence.OccurrenceDate.
    @Column
    private Date publicationDate;

    // The URL of the alert.
    @Column
    private String url;

    // A summary of the alert.
    @Column
    private String summary;

    // The HealthMap alert ID (if this alert has originated in HealthMap).
    @Column
    private Long healthMapAlertId;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    private Date createdDate;

    public Alert() {
    }

    public Integer getId() {
        return id;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getHealthMapAlertId() {
        return healthMapAlertId;
    }

    public void setHealthMapAlertId(Long healthMapAlertId) {
        this.healthMapAlertId = healthMapAlertId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alert alert = (Alert) o;

        if (createdDate != null ? !createdDate.equals(alert.createdDate) : alert.createdDate != null) return false;
        if (feed != null ? !feed.equals(alert.feed) : alert.feed != null) return false;
        if (healthMapAlertId != null ? !healthMapAlertId.equals(alert.healthMapAlertId) : alert.healthMapAlertId != null)
            return false;
        if (id != null ? !id.equals(alert.id) : alert.id != null) return false;
        if (publicationDate != null ? !publicationDate.equals(alert.publicationDate) : alert.publicationDate != null)
            return false;
        if (summary != null ? !summary.equals(alert.summary) : alert.summary != null) return false;
        if (title != null ? !title.equals(alert.title) : alert.title != null) return false;
        if (url != null ? !url.equals(alert.url) : alert.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (feed != null ? feed.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (publicationDate != null ? publicationDate.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (summary != null ? summary.hashCode() : 0);
        result = 31 * result + (healthMapAlertId != null ? healthMapAlertId.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
