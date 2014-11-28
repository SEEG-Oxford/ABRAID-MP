package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents the extent class (e.g. presence, absence) of an administrative unit, used in a specific model run.
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "model_run_admin_unit_disease_extent_class")
public class ModelRunAdminUnitDiseaseExtentClass extends AbstractAdminUnitDiseaseExtentClass {
    @ManyToOne
    @JoinColumn(name = "model_run_id")
    private ModelRun modelRun;

    public ModelRunAdminUnitDiseaseExtentClass() {
    }

    public ModelRunAdminUnitDiseaseExtentClass(
            AbstractAdminUnitDiseaseExtentClass adminUnitExtentClass, ModelRun modelRun) {
        super(adminUnitExtentClass);
        this.modelRun = modelRun;
    }

    public ModelRun getModelRun() {
        return modelRun;
    }

    public void setModelRun(ModelRun modelRun) {
        this.modelRun = modelRun;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ModelRunAdminUnitDiseaseExtentClass that = (ModelRunAdminUnitDiseaseExtentClass) o;

        if (modelRun != null ? !modelRun.equals(that.modelRun) : that.modelRun != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (modelRun != null ? modelRun.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
