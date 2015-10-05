package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for GeoJSON serialization of DiseaseOccurrence collections.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceGeoJsonIntegrationTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void serializingADiseaseOccurrenceCollectionGivesCorrectOutput() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();
        ObjectWriter writer = objectMapper.writer();

        // Act
        writer.writeValue(stream, new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences));

        // Assert
        assertThat(stream.toString()).isEqualTo(getTwoDiseaseOccurrenceFeaturesAsJson());
    }

    @Test
    public void deserializingADiseaseOccurrenceCollectionGivesCorrectResult() throws Exception {
        // Arrange
        GeoJsonDiseaseOccurrenceFeatureCollection occurrences = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();
        objectMapper.writeValue(stream, occurrences);
        String input = stream.toString();

        // Act
        ObjectReader reader = objectMapper.reader(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        GeoJsonDiseaseOccurrenceFeatureCollection result = reader.readValue(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(occurrences);
    }
}
