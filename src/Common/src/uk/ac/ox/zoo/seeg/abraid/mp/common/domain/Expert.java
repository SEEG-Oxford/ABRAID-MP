package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.List;

/**
 * Represents a user of the PublicSite.
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getExpertByEmail",
                query = "from Expert where email=:email"
        )
})
@Entity
public class Expert {
    // The primary key.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The expert's name.
    @Column(nullable = false)
    private String name;

    // The expert's email address.
    @Column(nullable = false)
    private String email;

    // The expert's password
    @Column(name = "hashed_password", nullable = false)
    private String password;

    // The expert's job title.
    @Column(name = "job_title")
    private String jobTitle;

    // The institution the expert works for.
    @Column
    private String institution;

    // Whether the expert has administrative control.
    @Column(name = "is_administrator", nullable = false)
    private boolean isAdministrator;

    // Whether the expert should be displayed in list on public site.
    // For now this is Boolean to allow null values in test data, but should really be boolean with NOT NULL in DB
    @Column(name = "is_publicly_visible")
    private Boolean isPubliclyVisible;

    // The expert's "score" determines the weighting of his response.
    @Column
    private Double weighting;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    // List of disease groups an expert has interest in and can validate.
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "expert_validator_disease_group",
            joinColumns = { @JoinColumn(name = "expert_id") },
            inverseJoinColumns = { @JoinColumn(name = "validator_disease_group_id") })
    @Fetch(FetchMode.SELECT)
    private List<ValidatorDiseaseGroup> validatorDiseaseGroups;

    public Expert() {
    }

    public Expert(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public boolean isAdministrator() {
        return isAdministrator;
    }

    public void setAdministrator(boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public boolean isPubliclyVisible() {
        return isPubliclyVisible;
    }

    public void setPubliclyVisible(boolean isPubliclyVisible) {
        this.isPubliclyVisible = isPubliclyVisible;
    }

    public List<ValidatorDiseaseGroup> getValidatorDiseaseGroups() {
        return validatorDiseaseGroups;
    }

    public void setValidatorDiseaseGroups(List<ValidatorDiseaseGroup> validatorDiseaseGroups) {
        this.validatorDiseaseGroups = validatorDiseaseGroups;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expert expert = (Expert) o;

        if (isAdministrator != expert.isAdministrator) return false;
        if (!createdDate.equals(expert.createdDate)) return false;
        if (!email.equals(expert.email)) return false;
        if (!id.equals(expert.id)) return false;
        if (!institution.equals(expert.institution)) return false;
        if (!isPubliclyVisible.equals(expert.isPubliclyVisible)) return false;
        if (!jobTitle.equals(expert.jobTitle)) return false;
        if (!name.equals(expert.name)) return false;
        if (!password.equals(expert.password)) return false;
        if (!validatorDiseaseGroups.equals(expert.validatorDiseaseGroups)) return false;
        if (!weighting.equals(expert.weighting)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + jobTitle.hashCode();
        result = 31 * result + institution.hashCode();
        result = 31 * result + (isAdministrator ? 1 : 0);
        result = 31 * result + isPubliclyVisible.hashCode();
        result = 31 * result + weighting.hashCode();
        result = 31 * result + createdDate.hashCode();
        result = 31 * result + validatorDiseaseGroups.hashCode();
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
