package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import com.fasterxml.jackson.annotation.JsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitGlobal;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;

/**
 * A DTO for the properties of an AdminUnit, with reference to a DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureProperties {
    @JsonView(DisplayJsonView.class)
    private String name;

    @JsonView(DisplayJsonView.class)
    private DiseaseExtentClass diseaseExtentClass;

//    @JsonView(DisplayJsonView.class)
//    private int diseaseOccurrenceCount;

    public GeoJsonDiseaseExtentFeatureProperties(AdminUnitGlobal adminUnitGlobal, DiseaseExtentClass diseaseExtentClass)
    {
        setName(adminUnitGlobal.getPublicName());
        setDiseaseExtentClass(diseaseExtentClass);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiseaseExtentClass getDiseaseExtentClass() {
        return diseaseExtentClass;
    }

    public void setDiseaseExtentClass(DiseaseExtentClass diseaseExtentClass) {
        this.diseaseExtentClass = diseaseExtentClass;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoJsonDiseaseExtentFeatureProperties)) return false;

        GeoJsonDiseaseExtentFeatureProperties that = (GeoJsonDiseaseExtentFeatureProperties) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (diseaseExtentClass != that.diseaseExtentClass) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (diseaseExtentClass != null ? diseaseExtentClass.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
