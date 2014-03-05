package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;

import java.util.List;

/**
 * Created by zool1112 on 05/03/14.
 */
public class GeoJsonDiseaseOccurrenceFeatureCollection extends GeoJsonFeatureCollection {
    public GeoJsonDiseaseOccurrenceFeatureCollection(List<DiseaseOccurrence> occurrences) {
        super(extractOccurrenceFeatures(occurrences), GeoJsonCrs.createEPSG4326());
    }

    private static List<GeoJsonFeature> extractOccurrenceFeatures(List<DiseaseOccurrence> occurrences) {
        return Lambda.convert(occurrences, new Converter<DiseaseOccurrence, GeoJsonFeature>() {
            public GeoJsonFeature convert(DiseaseOccurrence occurrence) {
                return new GeoJsonDiseaseOccurrenceFeature(occurrence);
            }
        });
    }
}
