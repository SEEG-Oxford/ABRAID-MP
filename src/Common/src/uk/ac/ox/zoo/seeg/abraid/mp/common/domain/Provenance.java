package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

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

    // The default weighting of feeds of this provenance. Used when creating a new feed.
    @Column
    private double defaultFeedWeighting;

    // The end date of the last online retrieval of this provenance (if relevant).
    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastRetrievalEndDate;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public Provenance() {
    }

    public Provenance(String name) {
        this.name = name;
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

    public double getDefaultFeedWeighting() {
        return defaultFeedWeighting;
    }

    public void setDefaultFeedWeighting(double defaultFeedWeighting) {
        this.defaultFeedWeighting = defaultFeedWeighting;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public DateTime getLastRetrievalEndDate() {
        return lastRetrievalEndDate;
    }

    public void setLastRetrievalEndDate(DateTime lastRetrievedDate) {
        this.lastRetrievalEndDate = lastRetrievedDate;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Provenance that = (Provenance) o;

        if (Double.compare(that.defaultFeedWeighting, defaultFeedWeighting) != 0) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (lastRetrievalEndDate != null ? !lastRetrievalEndDate.equals(that.lastRetrievalEndDate) : that.lastRetrievalEndDate != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(defaultFeedWeighting);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (lastRetrievalEndDate != null ? lastRetrievalEndDate.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
