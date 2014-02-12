package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import javax.persistence.*;
import java.util.Set;

/**
 * Represents an expert.
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
    // The expert's serial ID.
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

    // Whether the expert has administrative control
    @Column
    private boolean isAdministrator;

    // Whether the expert should be displayed in list on public site
    @Column
    private boolean isPubliclyVisible;

    // List of diseases an expert has interest in and can validate
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "ExpertDisease",
            joinColumns = { @JoinColumn(name = "ExpertId") },
            inverseJoinColumns = { @JoinColumn(name = "DiseaseId") })
    private Set<Disease> diseases;

    public Expert() {
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

}
