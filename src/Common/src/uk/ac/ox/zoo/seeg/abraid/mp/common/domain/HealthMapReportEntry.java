package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

/**
 * Holder object for the aggregate data needed for HealthMap data rate reports.
 * Copyright (c) 2015 University of Oxford
 */
public class HealthMapReportEntry {
    private String month;
    private String qualifier;
    private final Long dataCountryCount;
    private final Long dataAdmin1Count;
    private final Long dataAdmin2Count;
    private final Long dataPreciseCount;
    private final Long locationCountryCount;
    private final Long locationAdmin1Count;
    private final Long locationAdmin2Count;
    private final Long locationPreciseCount;

    public HealthMapReportEntry(String month, String qualifier,
            Long dataCountryCount, Long dataAdmin1Count, Long dataAdmin2Count, Long dataPreciseCount,
            Long locationCountryCount, Long locationAdmin1Count, Long locationAdmin2Count, Long locationPreciseCount) {
        this.month = month;
        this.qualifier = qualifier;
        this.dataCountryCount = dataCountryCount;
        this.dataAdmin1Count = dataAdmin1Count;
        this.dataAdmin2Count = dataAdmin2Count;
        this.dataPreciseCount = dataPreciseCount;
        this.locationCountryCount = locationCountryCount;
        this.locationAdmin1Count = locationAdmin1Count;
        this.locationAdmin2Count = locationAdmin2Count;
        this.locationPreciseCount = locationPreciseCount;
    }

    public String getMonth() {
        return month;
    }

    public String getQualifier() {
        return qualifier;
    }

    public Long getDataCountryCount() {
        return dataCountryCount;
    }

    public Long getDataAdmin1Count() {
        return dataAdmin1Count;
    }

    public Long getDataAdmin2Count() {
        return dataAdmin2Count;
    }

    public Long getDataPreciseCount() {
        return dataPreciseCount;
    }

    public Long getLocationCountryCount() {
        return locationCountryCount;
    }

    public Long getLocationAdmin1Count() {
        return locationAdmin1Count;
    }

    public Long getLocationAdmin2Count() {
        return locationAdmin2Count;
    }

    public Long getLocationPreciseCount() {
        return locationPreciseCount;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HealthMapReportEntry that = (HealthMapReportEntry) o;

        if (dataAdmin1Count != null ? !dataAdmin1Count.equals(that.dataAdmin1Count) : that.dataAdmin1Count != null)
            return false;
        if (dataAdmin2Count != null ? !dataAdmin2Count.equals(that.dataAdmin2Count) : that.dataAdmin2Count != null)
            return false;
        if (dataCountryCount != null ? !dataCountryCount.equals(that.dataCountryCount) : that.dataCountryCount != null)
            return false;
        if (dataPreciseCount != null ? !dataPreciseCount.equals(that.dataPreciseCount) : that.dataPreciseCount != null)
            return false;
        if (locationAdmin1Count != null ? !locationAdmin1Count.equals(that.locationAdmin1Count) : that.locationAdmin1Count != null)
            return false;
        if (locationAdmin2Count != null ? !locationAdmin2Count.equals(that.locationAdmin2Count) : that.locationAdmin2Count != null)
            return false;
        if (locationCountryCount != null ? !locationCountryCount.equals(that.locationCountryCount) : that.locationCountryCount != null)
            return false;
        if (locationPreciseCount != null ? !locationPreciseCount.equals(that.locationPreciseCount) : that.locationPreciseCount != null)
            return false;
        if (month != null ? !month.equals(that.month) : that.month != null) return false;
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month != null ? month.hashCode() : 0;
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (dataCountryCount != null ? dataCountryCount.hashCode() : 0);
        result = 31 * result + (dataAdmin1Count != null ? dataAdmin1Count.hashCode() : 0);
        result = 31 * result + (dataAdmin2Count != null ? dataAdmin2Count.hashCode() : 0);
        result = 31 * result + (dataPreciseCount != null ? dataPreciseCount.hashCode() : 0);
        result = 31 * result + (locationCountryCount != null ? locationCountryCount.hashCode() : 0);
        result = 31 * result + (locationAdmin1Count != null ? locationAdmin1Count.hashCode() : 0);
        result = 31 * result + (locationAdmin2Count != null ? locationAdmin2Count.hashCode() : 0);
        result = 31 * result + (locationPreciseCount != null ? locationPreciseCount.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON


    @Override
    public String toString() {
        return "HealthMapReportEntry{" +
                "month='" + month + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", dataCountryCount=" + dataCountryCount +
                ", dataAdmin1Count=" + dataAdmin1Count +
                ", dataAdmin2Count=" + dataAdmin2Count +
                ", dataPreciseCount=" + dataPreciseCount +
                ", locationCountryCount=" + locationCountryCount +
                ", locationAdmin1Count=" + locationAdmin1Count +
                ", locationAdmin2Count=" + locationAdmin2Count +
                ", locationPreciseCount=" + locationPreciseCount +
                '}';
    }
}
