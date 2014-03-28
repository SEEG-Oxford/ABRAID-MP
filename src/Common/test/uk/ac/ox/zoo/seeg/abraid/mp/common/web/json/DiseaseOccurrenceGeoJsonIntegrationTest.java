package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson.GeoJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.ModellingJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

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
    public void serializingADiseaseOccurrenceCollectionGivesCorrectDisplayViewOutput() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();
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
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();
        ObjectWriter writer = objectMapper.writerWithView(ModellingJsonView.class);

        // Act
        writer.writeValue(stream, new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences));

        // Assert
        assertThat(stream.toString()).isEqualTo(getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class));
    }
}
