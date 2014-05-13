package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * The classification of disease group's presence: PRESENCE, POSSIBLE_PRESENCE, UNCERTAIN, POSSIBLE_ABSENCE, ABSENCE
 * The answer the expert has submitted as review of an administrative unit polygon.
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries(
        @NamedQuery(
                name = "getDiseaseExtentClassByName",
                query = "from DiseaseExtentClass where name=:name"
        )
)
@Entity
@Table(name = "disease_extent_class")
public class DiseaseExtentClass {
    /** The disease group is definitely present in the admin unit. */
    public static final String PRESENCE = "PRESENCE";
    /** The disease group may be present in the admin unit. */
    public static final String POSSIBLE_PRESENCE = "POSSIBLE_PRESENCE";
    /** It is unknown whether the disease group is present or absent. */
    public static final String UNCERTAIN = "UNCERTAIN";
    /** The disease group may not be present in the admin unit. */
    public static final String POSSIBLE_ABSENCE = "POSSIBLE_ABSENCE";
    /** The disease group is definitely not present in the admin unit. */
    public static final String ABSENCE = "ABSENCE";

    // The class name
    @Id
    @Column
    private String name;

    // The corresponding weighting used in the R spatial model.
    @Column(nullable = false)
    private Integer weighting;

    public DiseaseExtentClass() {
    }

    public DiseaseExtentClass(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeighting() {
        return weighting;
    }

    public void setWeighting(Integer weighting) {
        this.weighting = weighting;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiseaseExtentClass)) return false;

        DiseaseExtentClass that = (DiseaseExtentClass) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (weighting != null ? !weighting.equals(that.weighting) : that.weighting != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (weighting != null ? weighting.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
