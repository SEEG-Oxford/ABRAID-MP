package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents a source of alerts.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getFeedsByProvenanceName",
                query = "from Feed where provenance.name=:provenanceName"
        )
})
@Entity
public class Feed {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The name of this feed.
    @Column
    private String name;

    // The provenance of this feed.
    @ManyToOne
    @JoinColumn(name = "provenance_id")
    private Provenance provenance;

    // The weighting given to this feed.
    private double weighting;

    // The language of this feed.
    // For HealthMap data, this is a code supplied by HealthMap in their feed_lang web service field.
    private String language;

    // The feed ID used for this provenance in HealthMap.
    @Column(name = "healthmap_feed_id")
    private Long healthMapFeedId;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public Feed() {
    }

    public Feed(String name, Provenance provenance, double weighting, String language, Long healthMapFeedId) {
        this.name = name;
        this.provenance = provenance;
        this.weighting = weighting;
        this.language = language;
        this.healthMapFeedId = healthMapFeedId;
    }

    public Feed(Integer id, String name, Provenance provenance, String language, double weighting,
                Long healthMapFeedId) {
        this.id = id;
        this.name = name;
        this.provenance = provenance;
        this.weighting = weighting;
        this.language = language;
        this.healthMapFeedId = healthMapFeedId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
    }

    public double getWeighting() {
        return weighting;
    }

    public void setWeighting(double weighting) {
        this.weighting = weighting;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getHealthMapFeedId() {
        return healthMapFeedId;
    }

    public void setHealthMapFeedId(Long healthMapFeedId) {
        this.healthMapFeedId = healthMapFeedId;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (Double.compare(feed.weighting, weighting) != 0) return false;
        if (createdDate != null ? !createdDate.equals(feed.createdDate) : feed.createdDate != null) return false;
        if (healthMapFeedId != null ? !healthMapFeedId.equals(feed.healthMapFeedId) : feed.healthMapFeedId != null)
            return false;
        if (id != null ? !id.equals(feed.id) : feed.id != null) return false;
        if (language != null ? !language.equals(feed.language) : feed.language != null) return false;
        if (name != null ? !name.equals(feed.name) : feed.name != null) return false;
        if (provenance != null ? !provenance.equals(feed.provenance) : feed.provenance != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (provenance != null ? provenance.hashCode() : 0);
        temp = Double.doubleToLongBits(weighting);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (healthMapFeedId != null ? healthMapFeedId.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
