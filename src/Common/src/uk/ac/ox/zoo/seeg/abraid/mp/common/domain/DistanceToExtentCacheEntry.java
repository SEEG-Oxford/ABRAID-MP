package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents an entry in the set of cached "distance to disease extent" values.
 * Copyright (c) 2015 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "clearDistanceToExtentCacheForDisease",
                query = "delete from DistanceToExtentCacheEntry where id.diseaseGroupId=:diseaseGroupId"
        )
})
@Entity
@Table(name = "disease_to_extent_cache")
public class DistanceToExtentCacheEntry extends ValidationParameterCacheEntry {
    @Column(name = "distance", nullable = false)
    private double distance;

    public DistanceToExtentCacheEntry() {
        super();
    }

    public DistanceToExtentCacheEntry(int diseaseGroupId, int locationId, double distance) {
        super(diseaseGroupId, locationId);
        setDistance(distance);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DistanceToExtentCacheEntry entry = (DistanceToExtentCacheEntry) o;

        if (Double.compare(entry.distance, distance) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(distance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
