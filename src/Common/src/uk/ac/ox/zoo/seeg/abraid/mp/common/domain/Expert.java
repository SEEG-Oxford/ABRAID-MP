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
        ),
        @NamedQuery(
                name = "getPubliclyVisibleExperts",
                query = "from Expert where visibilityApproved=TRUE"
        ),
        @NamedQuery(
            name = "countPubliclyVisibleExperts",
            query = "select count(e) from Expert e where visibilityApproved=TRUE"
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
    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    // The institution the expert works for.
    @Column(nullable = false)
    private String institution;

    // Whether the expert has administrative control.
    @Column(name = "is_administrator", nullable = false)
    private boolean isAdministrator; //default false

    @Column(name = "is_seeg_member", nullable = false)
    private boolean isSeegMember; //default false

    // Whether the expert wants to be displayed in list on public site.
    @Column(name = "visibility_requested", nullable = false)
    private boolean visibilityRequested; //default false

    // Whether the expert's request to be displayed is allowed.
    @Column(name = "visibility_approved", nullable = false)
    private boolean visibilityApproved; //default false

    // The expert's "score" determines the weighting of his response.
    @Column(nullable = false)
    private double weighting; //default 0

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    // The date on which the expert was last updated.
    @Column(name = "updated_date", insertable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedDate;

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

    public boolean isSeegMember() {
        return isSeegMember;
    }

    public void setSeegMember(boolean isSeegMember) {
        this.isSeegMember = isSeegMember;
    }

    public boolean getVisibilityRequested() {
        return visibilityRequested;
    }

    public void setVisibilityRequested(boolean visibilityRequested) {
        this.visibilityRequested = visibilityRequested;
    }

    public boolean getVisibilityApproved() {
        return visibilityApproved;
    }

    public void setVisibilityApproved(boolean visibilityApproved) {
        this.visibilityApproved = visibilityApproved;
    }

    public List<ValidatorDiseaseGroup> getValidatorDiseaseGroups() {
        return validatorDiseaseGroups;
    }

    public void setValidatorDiseaseGroups(List<ValidatorDiseaseGroup> validatorDiseaseGroups) {
        this.validatorDiseaseGroups = validatorDiseaseGroups;
    }

    public double getWeighting() {
        return weighting;
    }

    public void setWeighting(double weighting) {
        this.weighting = weighting;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public DateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(DateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expert expert = (Expert) o;

        if (isAdministrator != expert.isAdministrator) return false;
        if (isSeegMember != expert.isSeegMember) return false;
        if (visibilityApproved != expert.visibilityApproved) return false;
        if (visibilityRequested != expert.visibilityRequested) return false;
        if (Double.compare(expert.weighting, weighting) != 0) return false;
        if (createdDate != null ? !createdDate.equals(expert.createdDate) : expert.createdDate != null) return false;
        if (email != null ? !email.equals(expert.email) : expert.email != null) return false;
        if (id != null ? !id.equals(expert.id) : expert.id != null) return false;
        if (institution != null ? !institution.equals(expert.institution) : expert.institution != null) return false;
        if (jobTitle != null ? !jobTitle.equals(expert.jobTitle) : expert.jobTitle != null) return false;
        if (name != null ? !name.equals(expert.name) : expert.name != null) return false;
        if (password != null ? !password.equals(expert.password) : expert.password != null) return false;
        if (updatedDate != null ? !updatedDate.equals(expert.updatedDate) : expert.updatedDate != null) return false;
        if (validatorDiseaseGroups != null ? !validatorDiseaseGroups.equals(expert.validatorDiseaseGroups) : expert.validatorDiseaseGroups != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (institution != null ? institution.hashCode() : 0);
        result = 31 * result + (isAdministrator ? 1 : 0);
        result = 31 * result + (isSeegMember ? 1 : 0);
        result = 31 * result + (visibilityRequested ? 1 : 0);
        result = 31 * result + (visibilityApproved ? 1 : 0);
        temp = Double.doubleToLongBits(weighting);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (updatedDate != null ? updatedDate.hashCode() : 0);
        result = 31 * result + (validatorDiseaseGroups != null ? validatorDiseaseGroups.hashCode() : 0);
        return result;
    }

    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
