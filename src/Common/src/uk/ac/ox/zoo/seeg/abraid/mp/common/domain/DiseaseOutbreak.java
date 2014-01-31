package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Represents a disease outbreak.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class DiseaseOutbreak {
    // The disease outbreak ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The disease.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "diseaseId")
    private Disease disease;

    // The location of this outbreak.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "locationId")
    private Location location;

    // The provenance of this outbreak.
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "provenanceId")
    private Provenance provenance;

    // The title of the outbreak alert.
    @Column
    private String title;

    // The publication date of the outbreak alert.
    @Column
    private Date publicationDate;

    // The start date of the outbreak.
    @Column
    private Date outbreakStartDate;

    public DiseaseOutbreak() {
    }

    public Integer getId() {
        return id;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Provenance getProvenance() {
        return provenance;
    }

    public void setProvenance(Provenance provenance) {
        this.provenance = provenance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Date getOutbreakStartDate() {
        return outbreakStartDate;
    }

    public void setOutbreakStartDate(Date outbreakStartDate) {
        this.outbreakStartDate = outbreakStartDate;
    }
}
