package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

//import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Copyright (c) 2014 University of Oxford
 */
public class Location {
    @Id
    @GeneratedValue
    private Long id;

//    @Column(columnDefinition="Geometry")
//    @Type(type = "org.hibernate.spatial.GeometryType")
//    private Point geom;

    private String placeName;

    private String admin1;

    private String country;

    public Location(String placeName, String admin1, String country) {
        this.placeName = placeName;
        this.admin1 = admin1;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
