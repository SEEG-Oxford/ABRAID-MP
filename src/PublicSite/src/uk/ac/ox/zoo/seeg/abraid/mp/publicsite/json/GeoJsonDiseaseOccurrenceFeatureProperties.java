package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Created by zool1112 on 05/03/14.
 */
public class GeoJsonDiseaseOccurrenceFeatureProperties {
    private final String locationName;
    private final String countryName;
    private final GeoJsonAlert alert;
    //@JsonSerialize
    private final DateTime diseaseOccurrenceStartDate;

    public GeoJsonDiseaseOccurrenceFeatureProperties(DiseaseOccurrence occurrence) {
        this.locationName = occurrence.getLocation().getName();
        this.countryName = occurrence.getLocation().getCountry().getName();
        this.diseaseOccurrenceStartDate = new DateTime(occurrence.getOccurrenceStartDate());
        this.alert = new GeoJsonAlert(occurrence.getAlert());
    }

    public DateTime getDiseaseOccurrenceStartDate() {
        return diseaseOccurrenceStartDate;
    }

    public GeoJsonAlert getAlert() {
        return alert;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getCountryName() {
        return countryName;
    }
}
