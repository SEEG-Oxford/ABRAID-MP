package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.ModellingJsonView;
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
    public void serializingADiseaseOccurrenceCollectionGivesCorrectDisplayViewOutput() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();
        ObjectWriter writer = objectMapper.writerWithView(DisplayJsonView.class);

        // Act
        writer.writeValue(stream, new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences));

        // Assert
        assertThat(stream.toString()).isEqualTo(getTwoDiseaseOccurrenceFeaturesAsJson(DisplayJsonView.class));
    }

    @Test
    public void serializingADiseaseOccurrenceCollectionGivesCorrectModellingViewOutput() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();
        ObjectWriter writer = objectMapper.writerWithView(ModellingJsonView.class);

        // Act
        writer.writeValue(stream, new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences));

        // Assert
        assertThat(stream.toString()).isEqualTo(getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class));
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

    @Test
    public void deserializingADiseaseOccurrenceCollectionUsingAViewGivesResult() throws Exception {
        // Arrange
        GeoJsonDiseaseOccurrenceFeatureCollection occurrences = new GeoJsonDiseaseOccurrenceFeatureCollection(
                Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence()));
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();
        ObjectWriter writer = objectMapper.writerWithView(ModellingJsonView.class);
        OutputStream stream = new ByteArrayOutputStream();
        writer.writeValue(stream, occurrences);
        String input = stream.toString();

        // Clear fields that won't have been serialized
        occurrences.getFeatures().get(0).getProperties().setLocationName(null);
        occurrences.getFeatures().get(0).getProperties().setDiseaseGroupPublicName(null);
        occurrences.getFeatures().get(0).getProperties().setAlert(null);
        occurrences.getFeatures().get(0).getProperties().setOccurrenceDate(null);
        occurrences.getFeatures().get(1).getProperties().setLocationName(null);
        occurrences.getFeatures().get(1).getProperties().setDiseaseGroupPublicName(null);
        occurrences.getFeatures().get(1).getProperties().setAlert(null);
        occurrences.getFeatures().get(1).getProperties().setOccurrenceDate(null);

        // Act
        ObjectReader reader = objectMapper.reader(GeoJsonDiseaseOccurrenceFeatureCollection.class);
        GeoJsonDiseaseOccurrenceFeatureCollection result = reader.readValue(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(occurrences);
    }
}
