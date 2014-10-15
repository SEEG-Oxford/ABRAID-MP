package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

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

    @Column(name = "resolution_weighting")
    private Double resolutionWeighting;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    // The HealthMap country ID (if any).
    @Column(name = "healthmap_country_id")
    private Integer healthMapCountryId;

    // The GAUL code of the admin 1/2 unit assigned to the location during QC stage 1.
    @Column(name = "admin_unit_qc_gaul_code")
    private Integer adminUnitQCGaulCode;

    // The GAUL code of the admin_unit_global geometry that contains this location.
    @Column(name = "admin_unit_global_gaul_code")
    private Integer adminUnitGlobalGaulCode;

    // The GAUL code of the admin_unit_tropical geometry that contains this location.
    @Column(name = "admin_unit_tropical_gaul_code")
    private Integer adminUnitTropicalGaulCode;

    // The GAUL code of the country geometry that contains this location.
    @Column(name = "country_gaul_code")
    private Integer countryGaulCode;

    // True if this location passed all of the QC checks, false if not.
    @Column(name = "has_passed_qc")
    private boolean hasPassedQc;

    // A message returned by the QC process.
    @Column(name = "qc_message")
    private String qcMessage;

    public Location() {
    }

    public Location(Integer id) {
        this.id = id;
    }

    public Location(double x, double y) {
        setGeom(x, y);
    }

    public Location(double x, double y, LocationPrecision precision) {
        this(x, y);
        this.precision = precision;
    }

    public Location(double x, double y, LocationPrecision precision, int healthMapCountryId) {
        this(x, y, precision);
        this.healthMapCountryId = healthMapCountryId;
    }

    public Location(String name, double x, double y, LocationPrecision precision) {
        this(x, y, precision);
        this.name = name;
    }

    public Location(String name, LocationPrecision precision, int adminUnitTropicalGaulCode) {
        this.name = name;
        this.precision = precision;
        this.adminUnitTropicalGaulCode = adminUnitTropicalGaulCode;
    }

    public Location(String name, double x, double y, LocationPrecision precision, int healthMapCountryId) {
        this(name, x, y, precision);
        this.healthMapCountryId = healthMapCountryId;
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

    /**
     * Sets the point's coordinates.
     * @param x The longitude.
     * @param y The latitude.
     */
    public void setGeom(double x, double y) {
        this.geom = GeometryUtils.createPoint(x, y);
    }

    public LocationPrecision getPrecision() {
        return precision;
    }

    public void setPrecision(LocationPrecision precision) {
        this.precision = precision;
    }

    public Double getResolutionWeighting() {
        return resolutionWeighting;
    }

    public void setResolutionWeighting(Double resolutionWeighting) {
        this.resolutionWeighting = resolutionWeighting;
    }

    public Integer getHealthMapCountryId() {
        return healthMapCountryId;
    }

    public void setHealthMapCountryId(Integer healthMapCountryId) {
        this.healthMapCountryId = healthMapCountryId;
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

    public Integer getAdminUnitQCGaulCode() {
        return adminUnitQCGaulCode;
    }

    public void setAdminUnitQCGaulCode(Integer adminUnitQCGaulCode) {
        this.adminUnitQCGaulCode = adminUnitQCGaulCode;
    }

    public Integer getAdminUnitGlobalGaulCode() {
        return adminUnitGlobalGaulCode;
    }

    public void setAdminUnitGlobalGaulCode(Integer adminUnitGlobalGaulCode) {
        this.adminUnitGlobalGaulCode = adminUnitGlobalGaulCode;
    }

    public Integer getAdminUnitTropicalGaulCode() {
        return adminUnitTropicalGaulCode;
    }

    public void setAdminUnitTropicalGaulCode(Integer adminUnitTropicalGaulCode) {
        this.adminUnitTropicalGaulCode = adminUnitTropicalGaulCode;
    }

    public Integer getCountryGaulCode() {
        return countryGaulCode;
    }

    public void setCountryGaulCode(Integer countryGaulCode) {
        this.countryGaulCode = countryGaulCode;
    }

    /**
     * Returns whether the location has passed QC checks.
     * @return Whether the location has passed QC checks.
     */
    public boolean hasPassedQc() {
        return hasPassedQc;
    }

    public void setHasPassedQc(boolean hasPassedQc) {
        this.hasPassedQc = hasPassedQc;
    }

    public String getQcMessage() {
        return qcMessage;
    }

    public void setQcMessage(String qcMessage) {
        this.qcMessage = qcMessage;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (hasPassedQc != location.hasPassedQc) return false;
        if (adminUnitGlobalGaulCode != null ? !adminUnitGlobalGaulCode.equals(location.adminUnitGlobalGaulCode) : location.adminUnitGlobalGaulCode != null)
            return false;
        if (adminUnitQCGaulCode != null ? !adminUnitQCGaulCode.equals(location.adminUnitQCGaulCode) : location.adminUnitQCGaulCode != null)
            return false;
        if (adminUnitTropicalGaulCode != null ? !adminUnitTropicalGaulCode.equals(location.adminUnitTropicalGaulCode) : location.adminUnitTropicalGaulCode != null)
            return false;
        if (createdDate != null ? !createdDate.equals(location.createdDate) : location.createdDate != null)
            return false;
        if (geoNameId != null ? !geoNameId.equals(location.geoNameId) : location.geoNameId != null) return false;
        if (geom != null ? !geom.equals(location.geom) : location.geom != null) return false;
        if (healthMapCountryId != null ? !healthMapCountryId.equals(location.healthMapCountryId) : location.healthMapCountryId != null)
            return false;
        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (precision != location.precision) return false;
        if (qcMessage != null ? !qcMessage.equals(location.qcMessage) : location.qcMessage != null) return false;
        if (resolutionWeighting != null ? !resolutionWeighting.equals(location.resolutionWeighting) : location.resolutionWeighting != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        result = 31 * result + (precision != null ? precision.hashCode() : 0);
        result = 31 * result + (geoNameId != null ? geoNameId.hashCode() : 0);
        result = 31 * result + (resolutionWeighting != null ? resolutionWeighting.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (healthMapCountryId != null ? healthMapCountryId.hashCode() : 0);
        result = 31 * result + (adminUnitQCGaulCode != null ? adminUnitQCGaulCode.hashCode() : 0);
        result = 31 * result + (adminUnitGlobalGaulCode != null ? adminUnitGlobalGaulCode.hashCode() : 0);
        result = 31 * result + (adminUnitTropicalGaulCode != null ? adminUnitTropicalGaulCode.hashCode() : 0);
        result = 31 * result + (hasPassedQc ? 1 : 0);
        result = 31 * result + (qcMessage != null ? qcMessage.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
