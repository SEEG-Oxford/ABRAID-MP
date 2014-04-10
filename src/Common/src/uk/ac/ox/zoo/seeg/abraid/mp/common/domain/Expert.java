package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
    // NB: By default this is not populated.
    @ManyToMany
    @JoinTable(name = "expert_validator_disease_group",
            joinColumns = { @JoinColumn(name = "expert_id") },
            inverseJoinColumns = { @JoinColumn(name = "validator_disease_group_id") })
    private List<ValidatorDiseaseGroup> validatorDiseaseGroups;

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
        if (createdDate != null ? !createdDate.equals(expert.createdDate) : expert.createdDate != null) return false;
        if (validatorDiseaseGroups != null ? !validatorDiseaseGroups.equals(expert.validatorDiseaseGroups) : expert.validatorDiseaseGroups != null)
            return false;
        if (email != null ? !email.equals(expert.email) : expert.email != null) return false;
        if (id != null ? !id.equals(expert.id) : expert.id != null) return false;
        if (isPubliclyVisible != null ? !isPubliclyVisible.equals(expert.isPubliclyVisible) : expert.isPubliclyVisible != null)
            return false;
        if (name != null ? !name.equals(expert.name) : expert.name != null) return false;
        if (password != null ? !password.equals(expert.password) : expert.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (isAdministrator ? 1 : 0);
        result = 31 * result + (isPubliclyVisible != null ? isPubliclyVisible.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (validatorDiseaseGroups != null ? validatorDiseaseGroups.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
