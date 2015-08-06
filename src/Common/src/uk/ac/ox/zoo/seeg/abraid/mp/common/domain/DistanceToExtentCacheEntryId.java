package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * A composite primary key used on DistanceToExtentCacheEntry objects.
 * Copyright (c) 2015 University of Oxford
 */
@Embeddable
public class DistanceToExtentCacheEntryId implements Serializable {
    @Column(name = "disease_group_id", nullable = false)
    private int diseaseGroupId;

    @Column(name = "location_id", nullable = false)
    private int locationId;

    public DistanceToExtentCacheEntryId() {
    }

    public DistanceToExtentCacheEntryId(int diseaseGroupId, int locationId) {
        setDiseaseGroupId(diseaseGroupId);
        setLocationId(locationId);
    }

    public int getDiseaseGroupId() {
        return diseaseGroupId;
    }

    public void setDiseaseGroupId(int diseaseGroupId) {
        this.diseaseGroupId = diseaseGroupId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DistanceToExtentCacheEntryId that = (DistanceToExtentCacheEntryId) o;

        if (diseaseGroupId != that.diseaseGroupId) return false;
        if (locationId != that.locationId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseGroupId;
        result = 31 * result + locationId;
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
