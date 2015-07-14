package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents the count of covariate values falling within a defined range (a histogram entry).
 * Copyright (c) 2015 University of Oxford
 */
@Entity
@Table(name = "covariate_value_bin")
public class CovariateValueBin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "covariate_file_id", nullable = false)
    private CovariateFile covariateFile;

    @Column(name = "min", nullable = false)
    private Double min;

    @Column(name = "max", nullable = false)
    private Double max;

    @Column(name = "count", nullable = false)
    private Integer count;

    public CovariateValueBin() {
    }

    public CovariateValueBin(CovariateFile covariateFile, double min, double max, int count) {
        setCovariateFile(covariateFile);
        setMin(min);
        setMax(max);
        setCount(count);
    }

    public Integer getId() {
        return id;
    }

    public CovariateFile getCovariateFile() {
        return covariateFile;
    }

    public void setCovariateFile(CovariateFile covariateFile) {
        this.covariateFile = covariateFile;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CovariateValueBin that = (CovariateValueBin) o;

        if (count != null ? !count.equals(that.count) : that.count != null) return false;
        if (covariateFile != null ? !covariateFile.equals(that.covariateFile) : that.covariateFile != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (max != null ? !max.equals(that.max) : that.max != null) return false;
        if (min != null ? !min.equals(that.min) : that.min != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (covariateFile != null ? covariateFile.hashCode() : 0);
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        result = 31 * result + (count != null ? count.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
