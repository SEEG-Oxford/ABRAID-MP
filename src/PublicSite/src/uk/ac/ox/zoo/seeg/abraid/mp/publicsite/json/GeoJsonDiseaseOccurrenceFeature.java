package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonCrs;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonFeature;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonGeometry;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonPointGeometry;

/**
 * Created by zool1112 on 05/03/14.
 */
public class GeoJsonDiseaseOccurrenceFeature extends GeoJsonFeature {
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

    private static GeoJsonGeometry extractGeometry(DiseaseOccurrence occurrence) {
        return new GeoJsonPointGeometry(
                occurrence.getLocation().getGeom().getY(),
                occurrence.getLocation().getGeom().getX(),
                null, null);
    }

    private static GeoJsonDiseaseOccurrenceFeatureProperties extractProperties(DiseaseOccurrence occurrence) {
        return new GeoJsonDiseaseOccurrenceFeatureProperties(occurrence);
    }


}
