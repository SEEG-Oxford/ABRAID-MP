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
    private DateTime startDate;

    @JsonView(ModellingJsonView.class)
    private LocationPrecision locationPrecision;

    @JsonView(ModellingJsonView.class)
    private Double weighting;

    public GeoJsonDiseaseOccurrenceFeatureProperties() {
    }

    public GeoJsonDiseaseOccurrenceFeatureProperties(DiseaseOccurrence occurrence) {
        setDiseaseGroupPublicName(occurrence.getDiseaseGroup().getPublicNameForDisplay());
        setLocationName(occurrence.getLocation().getName());
        setStartDate(occurrence.getOccurrenceStartDate());
        setAlert(new GeoJsonAlert(occurrence.getAlert()));
        setLocationPrecision(occurrence.getLocation().getPrecision());
        setWeighting(occurrence.getValidationWeighting());
    }

    public String getDiseaseGroupPublicName() {
        return diseaseGroupPublicName;
    }

    public DateTime getStartDate() {
        return startDate;
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

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
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
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (weighting != null ? !weighting.equals(that.weighting) : that.weighting != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseGroupPublicName != null ? diseaseGroupPublicName.hashCode() : 0;
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (alert != null ? alert.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (locationPrecision != null ? locationPrecision.hashCode() : 0);
        result = 31 * result + (weighting != null ? weighting.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
