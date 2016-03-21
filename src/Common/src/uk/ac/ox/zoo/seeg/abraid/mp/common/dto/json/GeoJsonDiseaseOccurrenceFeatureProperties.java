package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * A DTO for the properties on a uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence object.
 * Structured to reflect the fields that should be serialized in GeoJSON server response.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseOccurrenceFeatureProperties {
    private String diseaseGroupPublicName;

    private String locationName;

    private GeoJsonAlert alert;

    private DateTime occurrenceDate;

    public GeoJsonDiseaseOccurrenceFeatureProperties() {
    }

    public GeoJsonDiseaseOccurrenceFeatureProperties(DiseaseOccurrence occurrence) {
        setDiseaseGroupPublicName(occurrence.getDiseaseGroup().getPublicNameForDisplay());
        setLocationName(occurrence.getLocation().getName());
        setOccurrenceDate(occurrence.getOccurrenceDate());
        setAlert(new GeoJsonAlert(occurrence.getAlert()));
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
        if (occurrenceDate != null ? !occurrenceDate.equals(that.occurrenceDate) : that.occurrenceDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = diseaseGroupPublicName != null ? diseaseGroupPublicName.hashCode() : 0;
        result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
        result = 31 * result + (alert != null ? alert.hashCode() : 0);
        result = 31 * result + (occurrenceDate != null ? occurrenceDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
