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
public final class GeoJsonDiseaseOccurrenceFeatureProperties {
    @JsonView(DisplayJsonView.class)
    private final String locationName;

    @JsonView(DisplayJsonView.class)
    private final GeoJsonAlert alert;

    @JsonView(DisplayJsonView.class)
    private final DateTime diseaseOccurrenceStartDate;

    @JsonView(ModellingJsonView.class)
    private final LocationPrecision locationPrecision;

    @JsonView(ModellingJsonView.class)
    private final double weighting;

    public GeoJsonDiseaseOccurrenceFeatureProperties(DiseaseOccurrence occurrence) {
        this.locationName = occurrence.getLocation().getName();
        this.diseaseOccurrenceStartDate = occurrence.getOccurrenceStartDate();
        this.alert = new GeoJsonAlert(occurrence.getAlert());
        this.locationPrecision = occurrence.getLocation().getPrecision();
        this.weighting = occurrence.getValidationWeighting();
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

    public LocationPrecision getLocationPrecision() {
        return locationPrecision;
    }

    public double getWeighting() {
        return weighting;
    }
}
