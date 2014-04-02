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
                name = "getLocationByGeoNameId",
                query = "from Location where geoNameId=:geoNameId"
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
    @Column(nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point geom;

    // The precision of this location.
    @Column
    @Enumerated(EnumType.STRING)
    private LocationPrecision precision;

    // The GeoName corresponding to this location.
    @Column(name = "geoname_id")
    private Integer geoNameId;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    // The HealthMap country (if any).
    @ManyToOne
    @JoinColumn(name = "healthmap_country_id")
    private HealthMapCountry healthMapCountry;

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

    public HealthMapCountry getHealthMapCountry() {
        return healthMapCountry;
    }

    public void setHealthMapCountry(HealthMapCountry healthMapCountry) {
        this.healthMapCountry = healthMapCountry;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public Integer getGeoNameId() {
        return geoNameId;
    }

    public void setGeoNameId(Integer geoNameId) {
        this.geoNameId = geoNameId;
    }

    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (createdDate != null ? !createdDate.equals(location.createdDate) : location.createdDate != null)
            return false;
        if (geoNameId != null ? !geoNameId.equals(location.geoNameId) : location.geoNameId != null) return false;
        if (geom != null ? !geom.equals(location.geom) : location.geom != null) return false;
        if (healthMapCountry != null ? !healthMapCountry.equals(location.healthMapCountry) : location.healthMapCountry != null)
            return false;
        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (precision != location.precision) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (geoNameId != null ? geoNameId.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (healthMapCountry != null ? healthMapCountry.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
}
