package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Represents a location.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
public class Location {
    // The location ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The location point. This can be a precise location, or the centroid of an admin 1 or country.
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geom;

    // A descriptive place name.
    @Column
    private String placeName;

    // The first administrative unit (e.g. state, province).
    @Column
    private String admin1;

    // The country.
    @ManyToOne
    @JoinColumn(name="country")
    private Country country;

    public Location() {
    }

    public Location(Country country) {
        this.country = country;
    }

    public Integer getId() {
        return id;
    }

    public Point getGeom() {
        return geom;
    }

    public void setGeom(Point geom) {
        this.geom = geom;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAdmin1() {
        return admin1;
    }

    public void setAdmin1(String admin1) {
        this.admin1 = admin1;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
