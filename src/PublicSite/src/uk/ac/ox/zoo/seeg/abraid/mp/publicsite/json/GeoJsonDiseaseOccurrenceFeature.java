package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

/**
 * Created by zool1112 on 05/03/14.
 */
public class GeoJsonDiseaseOccurrenceFeature extends GeoJsonFeature {
    public GeoJsonDiseaseOccurrenceFeature(DiseaseOccurrence occurrence) {
        super(
                extractId(occurrence),
                extractGeometry(occurrence),
                extractProperties(occurrence)
        );
    }

    private static int extractId(DiseaseOccurrence occurrence) {
        return occurrence.getId();
    }

    private static GeoJsonGeometry extractGeometry(DiseaseOccurrence occurrence) {
        return new GeoJsonPointGeometry(
                occurrence.getLocation().getGeom().getX(),
                occurrence.getLocation().getGeom().getY());
    }

    private static GeoJsonDiseaseOccurrenceFeatureProperties extractProperties(DiseaseOccurrence occurrence) {
        return new GeoJsonDiseaseOccurrenceFeatureProperties(occurrence);
    }


}
