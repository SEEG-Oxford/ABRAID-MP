package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LocationPrecision;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.ModellingJsonView;

/**
 * A DTO for the properties on a uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence object.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseOccurrenceFeatureProperties {
    @JsonView(DisplayJsonView.class)
    private String diseaseGroupPublicName;

    @JsonView(DisplayJsonView.class)
    private String locationName;

    @JsonView(DisplayJsonView.class)
    private GeoJsonAlert alert;

    @JsonView(DisplayJsonView.class)
    private DateTime occurrenceDate;

    @JsonView(ModellingJsonView.class)
    private LocationPrecision locationPrecision;

    @JsonView(ModellingJsonView.class)
    private Double weighting;

    @JsonView(ModellingJsonView.class)
    private Integer gaulCode;

    public GeoJsonDiseaseOccurrenceFeatureProperties() {
    }

    public GeoJsonDiseaseOccurrenceFeatureProperties(DiseaseOccurrence occurrence) {
        setDiseaseGroupPublicName(occurrence.getDiseaseGroup().getPublicNameForDisplay());
        setLocationName(occurrence.getLocation().getName());
        setOccurrenceDate(occurrence.getOccurrenceDate());
        setAlert(new GeoJsonAlert(occurrence.getAlert()));
        setLocationPrecision(occurrence.getLocation().getPrecision());
        setWeighting(occurrence.getValidationWeighting());
        setGaulCode(getAdminUnitGlobalOrTropicalGaulCode(occurrence));
    }

    public String getDiseaseGroupPublicName() {
        return diseaseGroupPublicName;
    }

    public DateTime getOccurrenceDate() {
        return occurrenceDate;
    }

    public GeoJsonAlert getAlert() {
        return alert;
    }

    public String getLocationName() {
        return locationName;
    }

    public LocationPrecision getLocationPrecision() {
        return locationPrecision;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setDiseaseGroupPublicName(String diseaseGroupPublicName) {
        this.diseaseGroupPublicName = diseaseGroupPublicName;
    }

    public void setOccurrenceDate(DateTime occurrenceDate) {
        this.occurrenceDate = occurrenceDate;
    }

    public void setAlert(GeoJsonAlert alert) {
        this.alert = alert;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationPrecision(LocationPrecision locationPrecision) {
        this.locationPrecision = locationPrecision;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public Integer getGaulCode() {
        return gaulCode;
    }

    public void setGaulCode(Integer gaulCode) {
        this.gaulCode = gaulCode;
    }

    /**
     * Return the Location's global or tropical GAUL code, depending on whether the DiseaseGroup is global or tropical.
     * @param occurrence The disease occurrence.
     * @return The GAUL code.
     */
    private Integer getAdminUnitGlobalOrTropicalGaulCode(DiseaseOccurrence occurrence) {
        if (occurrence.getDiseaseGroup().isGlobal()) {
            return occurrence.getLocation().getAdminUnitGlobalGaulCode();
        } else {
            return occurrence.getLocation().getAdminUnitTropicalGaulCode();
        }
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoJsonDiseaseOccurrenceFeatureProperties)) return false;

        GeoJsonDiseaseOccurrenceFeatureProperties that = (GeoJsonDiseaseOccurrenceFeatureProperties) o;

        if (alert != null ? !alert.equals(that.alert) : that.alert != null) return false;
        if (diseaseGroupPublicName != null ? !diseaseGroupPublicName.equals(that.diseaseGroupPublicName) : that.diseaseGroupPublicName != null)
            return false;
        if (locationName != null ? !locationName.equals(that.locationName) : that.locationName != null) return false;
        if (locationPrecision != that.locationPrecision) return false;
        if (occurrenceDate != null ? !occurrenceDate.equals(that.occurrenceDate) : that.occurrenceDate != null) return false;
        if (weighting != null ? !weighting.equals(that.weighting) : that.weighting != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseGroupPublicName != null ? diseaseGroupPublicName.hashCode() : 0;
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (alert != null ? alert.hashCode() : 0);
        result = 31 * result + (occurrenceDate != null ? occurrenceDate.hashCode() : 0);
        result = 31 * result + (locationPrecision != null ? locationPrecision.hashCode() : 0);
        result = 31 * result + (weighting != null ? weighting.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
