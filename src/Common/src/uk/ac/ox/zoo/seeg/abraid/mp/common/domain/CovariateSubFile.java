package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;

/**
 * Represents a covariate file (can be a single file, or a time slice in a larger multi file covariate).
 * Copyright (c) 2016 University of Oxford
 */
@Entity
@Table(name = "covariate_sub_file")
public class CovariateSubFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "covariate_file_id", nullable = false)
    private CovariateFile covariateFile;

    @Column
    private String qualifier;

    @Column(nullable = false, updatable = false)
    private String file;

    public CovariateSubFile() {
    }

    public CovariateSubFile(CovariateFile parentCovariate, String qualifier, String file) {
        setCovariateFile(parentCovariate);
        setQualifier(qualifier);
        setFile(file);
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

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CovariateSubFile)) return false;

        CovariateSubFile that = (CovariateSubFile) o;

        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (covariateFile != null ? !covariateFile.equals(that.covariateFile) : that.covariateFile != null) return false;
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (covariateFile != null ? covariateFile.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
