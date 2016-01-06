package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Collection;

/**
 * Represents a covariate file.
 * Copyright (c) 2015 University of Oxford
 */
@Entity
@Table(name = "covariate_file")
@NamedQueries({
        @NamedQuery(
                name = "getCovariateFilesByDiseaseGroup",
                query = "select c from CovariateFile c " +
                        "join c.enabledDiseaseGroups d " +
                        "where d.id=:diseaseGroupId and c.hide=false"
        )
})
public class CovariateFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column(nullable = false, updatable = false)
    private String file;

    @Column(nullable = false)
    private Boolean hide;

    @Column(nullable = false)
    private Boolean discrete;

    @Column
    private String info;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "disease_group_covariate_file",
            joinColumns = @JoinColumn(name = "covariate_file_id"),
            inverseJoinColumns = @JoinColumn(name = "disease_group_id"))
    @Fetch(FetchMode.SELECT)
    private Collection<DiseaseGroup> enabledDiseaseGroups;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "covariateFile")
    @Fetch(FetchMode.SELECT)
    private Collection<CovariateValueBin> covariateValueHistogramData;

    public CovariateFile() {
    }

    public CovariateFile(String name, String file, Boolean hide, Boolean discrete, String info) {
        setName(name);
        setFile(file);
        setHide(hide);
        setDiscrete(discrete);
        setInfo(info);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Boolean getHide() {
        return hide;
    }

    public void setHide(Boolean hide) {
        this.hide = hide;
    }

    public Boolean getDiscrete() {
        return discrete;
    }

    public void setDiscrete(Boolean discrete) {
        this.discrete = discrete;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Collection<DiseaseGroup> getEnabledDiseaseGroups() {
        return enabledDiseaseGroups;
    }

    public void setEnabledDiseaseGroups(Collection<DiseaseGroup> enabledDiseaseGroups) {
        this.enabledDiseaseGroups = enabledDiseaseGroups;
    }

    public Collection<CovariateValueBin> getCovariateValueHistogramData() {
        return covariateValueHistogramData;
    }

    public void setCovariateValueHistogramData(Collection<CovariateValueBin> covariateValueHistogramData) {
        this.covariateValueHistogramData = covariateValueHistogramData;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CovariateFile)) return false;

        CovariateFile that = (CovariateFile) o;

        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        if (hide != null ? !hide.equals(that.hide) : that.hide != null) return false;
        if (discrete != null ? !discrete.equals(that.discrete) : that.discrete != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (info != null ? !info.equals(that.info) : that.info != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (hide != null ? hide.hashCode() : 0);
        result = 31 * result + (discrete != null ? discrete.hashCode() : 0);
        result = 31 * result + (info != null ? info.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
