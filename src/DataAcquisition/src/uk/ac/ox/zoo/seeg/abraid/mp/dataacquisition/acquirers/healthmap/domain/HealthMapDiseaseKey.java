package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.healthmap.domain;

/**
 * Represents a natural key for the healthmap_disease table.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class HealthMapDiseaseKey {
    private int diseaseId;
    private String subName;

    public HealthMapDiseaseKey(int diseaseId, String subName) {
        this.diseaseId = diseaseId;
        this.subName = subName;
    }

    public int getDiseaseId() {
        return diseaseId;
    }

    public String getSubName() {
        return subName;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapDiseaseKey that = (HealthMapDiseaseKey) o;

        if (diseaseId != that.diseaseId) return false;
        if (subName != null ? !subName.equals(that.subName) : that.subName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseId;
        result = 31 * result + (subName != null ? subName.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
