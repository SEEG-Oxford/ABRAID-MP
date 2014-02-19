package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Represents a provenance, i.e. the source of a group of feeds.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getProvenanceByName",
                query = "from Provenance where name=:name"
        )
})
@Entity
public class Provenance {
    // The provenance ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The name of the provenance.
    @Column
    private String name;

    // The default weight of feeds of this provenance. Used when creating a new feed.
    @Column
    private Double defaultFeedWeight;

    // The date of the last online retrieval of this provenance (if relevant).
    @Column
    private Date lastRetrievedDate;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    private Date createdDate;

    public Provenance() {
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

    public Double getDefaultFeedWeight() {
        return defaultFeedWeight;
    }

    public void setDefaultFeedWeight(Double defaultFeedWeight) {
        this.defaultFeedWeight = defaultFeedWeight;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provenance that = (Provenance) o;

        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (defaultFeedWeight != null ? !defaultFeedWeight.equals(that.defaultFeedWeight) : that.defaultFeedWeight != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (defaultFeedWeight != null ? defaultFeedWeight.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
