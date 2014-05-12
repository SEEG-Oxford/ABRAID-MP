package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.geojson;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TimeZone;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests for GeoJsonObjectMapper.
 * Copyright (c) 2014 University of Oxford
 */
public class GeoJsonObjectMapperTest {
    @Test
    public void constructorForGeoJsonObjectMapperConfiguresJodaTimeSerialization() throws Exception {
        // Arrange
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        OutputStream stream = new ByteArrayOutputStream();
        DateTime jodaTime = new DateTime(0);

        // Act
        GeoJsonObjectMapper target = new GeoJsonObjectMapper();
        target.writeValue(stream, jodaTime);
        String result = stream.toString();

        // Assert
        assertThat(result).isEqualTo("\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(jodaTime) + "\"");
    }

    @Test
    public void constructorForGeoJsonObjectMapperConfiguresEmptyArraySerialization() throws Exception {
        // Arrange
        GeoJsonFeatureCollection empty = new GeoJsonDiseaseOccurrenceFeatureCollection(new ArrayList<DiseaseOccurrence>());
        OutputStream stream = new ByteArrayOutputStream();

        // Act
        GeoJsonObjectMapper target = new GeoJsonObjectMapper();
        target.writeValue(stream, empty);
        String result = stream.toString();

        // Assert
        assertThat(result).isEqualTo("{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:EPSG::4326\"}},\"features\":[]}");
    }

    @Test
    public void constructorForGeoJsonObjectMapperConfiguresSingletonArraySerialization() throws Exception {
        // Arrange
        GeoJsonFeatureCollection singleton = new GeoJsonDiseaseOccurrenceFeatureCollection(Arrays.asList(AbstractDiseaseOccurrenceGeoJsonTests.defaultDiseaseOccurrence()));
        OutputStream stream = new ByteArrayOutputStream();
        // Act
        GeoJsonObjectMapper target = new GeoJsonObjectMapper();
        target.writeValue(stream, singleton);
        String result = stream.toString();

        // Assert
        assertThat(result).isEqualTo(
                "{\"type\":\"FeatureCollection\",\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"urn:ogc:def:crs:EPSG::4326\"}},\"features\":[{\"type\":\"Feature\",\"id\":1,\"geometry\":{\"type\":\"Point\",\"coordinates\":[-1.0,1.0]},\"properties\":{\"diseaseGroupPublicName\":\"diseaseGroupPublicName\",\"locationName\":\"locationName\",\"alert\":{\"title\":\"title\",\"summary\":\"summary\",\"url\":\"url\",\"feedName\":\"feedName\",\"feedLanguage\":\"feedLanguage\"},\"occurrenceDate\":\"1970-01-01T00:00:00.000Z\",\"locationPrecision\":\"PRECISE\",\"weighting\":0.5,\"gaulCode\":102}}]}");
    }
}
