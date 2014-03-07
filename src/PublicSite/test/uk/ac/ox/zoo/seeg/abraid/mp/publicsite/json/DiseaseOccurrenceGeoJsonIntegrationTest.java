package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Integration tests for GeoJSON serialization of DiseaseOccurrence collections.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceGeoJsonIntegrationTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void serializingADiseaseOccurrenceCollectionGivesCorrectOutput() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();

        // Act
        objectMapper.writeValue(stream, new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences));

        // Assert
        assertThat(stream.toString()).isEqualTo(TWO_DISEASE_OCCURRENCE_FEATURES_AS_JSON);

    }
}
