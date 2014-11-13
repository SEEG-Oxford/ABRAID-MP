package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonNamedCrsProperties;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.geojson.GeoJsonObjectType;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for GeoJsonDiseaseOccurrenceFeatureCollection.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonDiseaseOccurrenceFeatureCollectionTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseOccurrenceFeatureCollectionExtractsFeaturesCorrectly() {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());

        // Act
        GeoJsonDiseaseOccurrenceFeatureCollection result = new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences);

        // Assert
        assertThat(result.getBBox()).isNull();
        assertThat(result.getCrs().getType()).isEqualTo("name");
        assertThat(result.getCrs().getProperties().getName()).isEqualTo("urn:ogc:def:crs:EPSG::4326");
        assertThat(result.getType()).isEqualTo(GeoJsonObjectType.FEATURE_COLLECTION);
        assertThat(result.getFeatures()).hasSameSizeAs(occurrences);
        assertThat(result.getFeatures().get(0)).isInstanceOf(GeoJsonDiseaseOccurrenceFeature.class);
    }
}
