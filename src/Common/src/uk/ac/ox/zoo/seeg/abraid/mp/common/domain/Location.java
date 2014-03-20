package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents a location.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getLocationsByPointAndPrecision",
                query = "from Location where geom=:point and precision=:precision"
        ),
        @NamedQuery(
                name = "getLocationByGeoNamesId",
                query = "from Location where geoNamesId=:geoNamesId"
        )
})
@Entity
public class Location {
    // The location ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The location name.
    @Column
    private String name;

    // The location point. This can be a precise location, or the centroid of an administrative unit or country.
    @Column
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geom;

    // The precision of this location.
    @Column
    @Enumerated(EnumType.STRING)
    private LocationPrecision precision;

    // The country.
    @ManyToOne
    @JoinColumn(name = "countryId")
    private Country country;

    // The GeoNames ID corresponding to this location.
    @Column
    private Integer geoNamesId;

    // The GeoNames feature code obtained for this location. This indicates location precision.
    @Column
    private String geoNamesFeatureCode;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public Location() {
    }

    public Location(Integer id) {
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

    public Point getGeom() {
        return geom;
    }

    public void setGeom(Point geom) {
        this.geom = geom;
    }

    public LocationPrecision getPrecision() {
        return precision;
    }

    public void setPrecision(LocationPrecision precision) {
        this.precision = precision;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public Integer getGeoNamesId() {
        return geoNamesId;
    }

    public void setGeoNamesId(Integer geoNamesId) {
        this.geoNamesId = geoNamesId;
    }

    public String getGeoNamesFeatureCode() {
        return geoNamesFeatureCode;
    }

    public void setGeoNamesFeatureCode(String geoNamesFeatureCode) {
        this.geoNamesFeatureCode = geoNamesFeatureCode;
    }

    @Override
    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (country != null ? !country.equals(location.country) : location.country != null) return false;
        if (createdDate != null ? !createdDate.equals(location.createdDate) : location.createdDate != null)
            return false;
        if (geoNamesId != null ? !geoNamesId.equals(location.geoNamesId) : location.geoNamesId != null) return false;
        if (geoNamesFeatureCode != null ? !geoNamesFeatureCode.equals(location.geoNamesFeatureCode) : location.geoNamesFeatureCode != null) return false;
        if (geom != null ? !geom.equals(location.geom) : location.geom != null) return false;
        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (precision != location.precision) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (geoNamesId != null ? geoNamesId.hashCode() : 0);
        result = 31 * result + (geoNamesFeatureCode != null ? geoNamesFeatureCode.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    // CHECKSTYLE.ON
}
