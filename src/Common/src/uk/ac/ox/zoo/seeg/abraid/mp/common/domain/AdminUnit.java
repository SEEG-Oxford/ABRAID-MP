package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents an admin 1/2 area. Imported from the standard SEEG/GAUL admin 1 and admin 2 shapefiles, with smaller
 * islands removed.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Entity
@Table(name = "admin_unit")
@Immutable
public class AdminUnit {
    // The admin unit's GAUL (Global Administrative Unit Layers) code. This is used in the corresponding SEEG shapefile.
    @Id
    @Column(name = "gaul_code")
    private Integer gaulCode;

    // The admin unit's level (1 or 2).
    @Column(name = "admin_level")
    private char adminLevel;

    // The admin unit's name.
    @Column
    private String name;

    // The latitude component of the admin unit's centroid.
    @Column(name = "centroid_latitude")
    private double centroidLatitude;

    // The longitude component of the admin unit's centroid.
    @Column(name = "centroid_longitude")
    private double centroidLongitude;

    // The admin unit's area.
    @Column
    private double area;

    public Integer getGaulCode() {
        return gaulCode;
    }

    public char getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(char adminLevel) {
        this.adminLevel = adminLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCentroidLatitude() {
        return centroidLatitude;
    }

    public void setCentroidLatitude(double centroidLatitude) {
        this.centroidLatitude = centroidLatitude;
    }

    public double getCentroidLongitude() {
        return centroidLongitude;
    }

    public void setCentroidLongitude(double centroidLongitude) {
        this.centroidLongitude = centroidLongitude;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    // CHECKSTYLE.OFF: AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminUnit adminUnit = (AdminUnit) o;

        if (adminLevel != adminUnit.adminLevel) return false;
        if (Double.compare(adminUnit.area, area) != 0) return false;
        if (Double.compare(adminUnit.centroidLatitude, centroidLatitude) != 0) return false;
        if (Double.compare(adminUnit.centroidLongitude, centroidLongitude) != 0) return false;
        if (gaulCode != null ? !gaulCode.equals(adminUnit.gaulCode) : adminUnit.gaulCode != null) return false;
        if (name != null ? !name.equals(adminUnit.name) : adminUnit.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = gaulCode != null ? gaulCode.hashCode() : 0;
        result = 31 * result + (int) adminLevel;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        temp = Double.doubleToLongBits(centroidLatitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(centroidLongitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(area);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
    // CHECKSTYLE.ON
}
