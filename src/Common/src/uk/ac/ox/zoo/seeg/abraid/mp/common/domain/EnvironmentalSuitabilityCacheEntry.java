package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents an entry in the set of cached "environmental suitability" values.
 * Copyright (c) 2015 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "clearEnvironmentalSuitabilityCacheForDisease",
                query = "delete from EnvironmentalSuitabilityCacheEntry where id.diseaseGroupId=:diseaseGroupId"
        )
})
@Entity
@Table(name = "environmental_suitability_cache")
public class EnvironmentalSuitabilityCacheEntry extends ValidationParameterCacheEntry {
    @Column(name = "environmental_suitability", nullable = false)
    private double environmentalSuitability;

    public EnvironmentalSuitabilityCacheEntry() {
        super();
    }

    public EnvironmentalSuitabilityCacheEntry(int diseaseGroupId, int locationId, double environmentalSuitability) {
        super(diseaseGroupId, locationId);
        setEnvironmentalSuitability(environmentalSuitability);
    }

    public double getEnvironmentalSuitability() {
        return environmentalSuitability;
    }

    public void setEnvironmentalSuitability(double environmentalSuitability) {
        this.environmentalSuitability = environmentalSuitability;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EnvironmentalSuitabilityCacheEntry entry = (EnvironmentalSuitabilityCacheEntry) o;

        if (Double.compare(entry.environmentalSuitability, environmentalSuitability) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(environmentalSuitability);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
