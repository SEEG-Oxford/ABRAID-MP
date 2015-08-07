package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;

/**
 * Base class for cached validation parameter values.
 * Copyright (c) 2015 University of Oxford
 */
@MappedSuperclass
public class ValidationParameterCacheEntry {
    @EmbeddedId
    private ValidationParameterCacheEntryId id;

    public ValidationParameterCacheEntry() {
    }

    public ValidationParameterCacheEntry(int diseaseGroupId, int locationId) {
        this.id = new ValidationParameterCacheEntryId(diseaseGroupId, locationId);
    }

    public ValidationParameterCacheEntryId getId() {
        return id;
    }

    public void setId(ValidationParameterCacheEntryId id) {
        this.id = id;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationParameterCacheEntry that = (ValidationParameterCacheEntry) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
