package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import com.fasterxml.jackson.annotation.JsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.GlobalAdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;

/**
 * A DTO for the properties of an AdminUnit, with reference to a DiseaseGroup.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseExtentFeatureProperties {
    @JsonView(DisplayJsonView.class)
    private String adminUnitPublicName;

    @JsonView(DisplayJsonView.class)
    private DiseaseExtentClass diseaseExtentClass;

//    @JsonView(DisplayJsonView.class)
//    private int diseaseOccurrenceCount;

    public GeoJsonDiseaseExtentFeatureProperties(GlobalAdminUnit globalAdminUnit, DiseaseExtentClass diseaseExtentClass)
    {
        setAdminUnitPublicName(globalAdminUnit.getPublicName());
        setDiseaseExtentClass(diseaseExtentClass);
    }

    public String getAdminUnitPublicName() {
        return adminUnitPublicName;
    }

    public void setAdminUnitPublicName(String adminUnitPublicName) {
        this.adminUnitPublicName = adminUnitPublicName;
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

        if (adminUnitPublicName != null ? !adminUnitPublicName.equals(that.adminUnitPublicName) : that.adminUnitPublicName != null)
            return false;
        if (diseaseExtentClass != that.diseaseExtentClass) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = adminUnitPublicName != null ? adminUnitPublicName.hashCode() : 0;
        result = 31 * result + (diseaseExtentClass != null ? diseaseExtentClass.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
