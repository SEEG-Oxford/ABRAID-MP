package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrs;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonPointGeometry;

/**
 * A DTO for uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence expressed as a "Feature".
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseOccurrenceFeature extends GeoJsonFeature
        <GeoJsonDiseaseOccurrenceFeatureProperties, GeoJsonPointGeometry<GeoJsonNamedCrs>, GeoJsonNamedCrs> {
    public GeoJsonDiseaseOccurrenceFeature() {
    }

    public GeoJsonDiseaseOccurrenceFeature(DiseaseOccurrence occurrence) {
        super(
                extractId(occurrence),
                extractGeometry(occurrence),
                extractProperties(occurrence),
                null, null
        );
    }

    private static int extractId(DiseaseOccurrence occurrence) {
        return occurrence.getId();
    }

    private static GeoJsonPointGeometry<GeoJsonNamedCrs> extractGeometry(DiseaseOccurrence occurrence) {
        return new GeoJsonPointGeometry<>(
                occurrence.getLocation().getGeom().getX(),
                occurrence.getLocation().getGeom().getY(),
                null, null);
    }

    private static GeoJsonDiseaseOccurrenceFeatureProperties extractProperties(DiseaseOccurrence occurrence) {
        return new GeoJsonDiseaseOccurrenceFeatureProperties(occurrence);
    }
}
