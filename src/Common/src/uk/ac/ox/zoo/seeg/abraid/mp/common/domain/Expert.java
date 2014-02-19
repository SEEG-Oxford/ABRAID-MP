package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;
import java.util.Date;
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
    @Column
    private String name;

    // The expert's email address.
    @Column
    private String email;

    // The expert's password
    @Column(name = "HashedPassword")
    private String password;

    // Whether the expert has administrative control.
    @Column
    private boolean isAdministrator;

    // Whether the expert should be displayed in list on public site.
    @Column
    private boolean isPubliclyVisible;

    // List of disease groups an expert has interest in and can validate.
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ExpertDiseaseGroup",
            joinColumns = { @JoinColumn(name = "ExpertId") },
            inverseJoinColumns = { @JoinColumn(name = "DiseaseGroupId") })
    private Set<DiseaseGroup> diseaseGroups;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    private Date createdDate;

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

    public Set<DiseaseGroup> getDiseaseGroups() {
        return diseaseGroups;
    }

    public void setDiseaseGroups(Set<DiseaseGroup> diseaseGroups) {
        this.diseaseGroups = diseaseGroups;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Expert expert = (Expert) o;

        if (isAdministrator != expert.isAdministrator) return false;
        if (isPubliclyVisible != expert.isPubliclyVisible) return false;
        if (createdDate != null ? !createdDate.equals(expert.createdDate) : expert.createdDate != null) return false;
        if (diseaseGroups != null ? !diseaseGroups.equals(expert.diseaseGroups) : expert.diseaseGroups != null)
            return false;
        if (email != null ? !email.equals(expert.email) : expert.email != null) return false;
        if (id != null ? !id.equals(expert.id) : expert.id != null) return false;
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
        result = 31 * result + (isPubliclyVisible ? 1 : 0);
        result = 31 * result + (diseaseGroups != null ? diseaseGroups.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
