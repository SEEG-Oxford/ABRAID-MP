package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a source of alerts.
 *
 * Copyright (c) 2014 University of Oxford
 */
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
    @JoinColumn(name = "provenanceId")
    private Provenance provenance;

    // The weight given to this feed.
    private double weight;

    // The feed ID used for this provenance in HealthMap.
    @Column
    private Long healthMapFeedId;

    // The database row creation date.
    @Column
    private Date createdDate;

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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Long getHealthMapFeedId() {
        return healthMapFeedId;
    }

    public void setHealthMapFeedId(Long healthMapFeedId) {
        this.healthMapFeedId = healthMapFeedId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (Double.compare(feed.weight, weight) != 0) return false;
        if (createdDate != null ? !createdDate.equals(feed.createdDate) : feed.createdDate != null) return false;
        if (healthMapFeedId != null ? !healthMapFeedId.equals(feed.healthMapFeedId) : feed.healthMapFeedId != null)
            return false;
        if (id != null ? !id.equals(feed.id) : feed.id != null) return false;
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
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (healthMapFeedId != null ? healthMapFeedId.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
}
