package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for JsonDownloadDiseaseOccurrence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDownloadDiseaseOccurrenceTest {
    @Test
    public void explicitConstructorBindsFieldsCorrectly() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String gaul = "gaul";
        String date = "date";
        String provenance = "provenance";
        String feed = "feed";
        String url = "url";
        int disease = 123;

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(createNoopAdjuster(), longitude, latitude, weight, admin, gaul, disease, date, provenance, feed, url);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
        assertThat(result.getUrl()).isEqualTo(url);
    }

    @Test
    public void explicitConstructorBindsFieldsCorrectlyWithNull() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String date = "date";
        String provenance = "provenance";
        String feed = "feed";
        String url = null;
        int disease = 123;

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(createNoopAdjuster(), longitude, latitude, weight, admin, null, disease, date, provenance, feed, url);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
        assertThat(result.getUrl()).isEqualTo("-");
    }

    @Test
    public void explicitConstructorBindsFieldsCorrectlyWithAdjustedPrecision() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String date = "date";
        String provenance = "provenance";
        String feed = "feed";
        String url = null;
        String gaul = "gaul";
        int disease = 123;

        ModellingLocationPrecisionAdjuster adjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(admin, gaul)).thenReturn(admin - 1);

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(adjuster, longitude, latitude, weight, admin, gaul, disease, date, provenance, feed, url);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin - 1);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
        assertThat(result.getUrl()).isEqualTo("-");
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectly() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";
        DateTime date = new DateTime("2015-03-07T01:02:03.004Z");
        String provenance = "provenance";
        String feed = "feed";
        String url = "url";
        int disease = 123;

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getDiseaseGroup().getId()).thenReturn(disease);
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getAlert().getFeed().getName()).thenReturn(feed);
        when(mock.getOccurrenceDate()).thenReturn(date);
        when(mock.getAlert().getFeed().getProvenance()).thenReturn(mock(Provenance.class));
        when(mock.getAlert().getFeed().getProvenance().getName()).thenReturn(provenance);
        when(mock.getAlert().getUrl()).thenReturn(url);

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(createNoopAdjuster(), mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDate()).isEqualTo("2015-03-07");
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
        assertThat(result.getUrl()).isEqualTo(url);
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectlyWithNullGAUL() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        DateTime date = new DateTime("2015-03-07T01:02:03.004Z");
        String provenance = "provenance";
        String feed = "feed";
        String url = "url";
        int disease = 123;

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getDiseaseGroup().getId()).thenReturn(disease);
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(null);
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getAlert().getFeed().getName()).thenReturn(feed);
        when(mock.getOccurrenceDate()).thenReturn(date);
        when(mock.getAlert().getFeed().getProvenance()).thenReturn(mock(Provenance.class));
        when(mock.getAlert().getFeed().getProvenance().getName()).thenReturn(provenance);
        when(mock.getAlert().getUrl()).thenReturn(url);

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(createNoopAdjuster(), mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
        assertThat(result.getDate()).isEqualTo("2015-03-07");
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
        assertThat(result.getUrl()).isEqualTo(url);
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectlyWithAdjustedPrecision() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";
        DateTime date = new DateTime("2015-03-07T01:02:03.004Z");
        String provenance = "provenance";
        String feed = "feed";
        String url = "url";
        int disease = 123;

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getDiseaseGroup().getId()).thenReturn(disease);
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getAlert().getFeed().getName()).thenReturn(feed);
        when(mock.getOccurrenceDate()).thenReturn(date);
        when(mock.getAlert().getFeed().getProvenance()).thenReturn(mock(Provenance.class));
        when(mock.getAlert().getFeed().getProvenance().getName()).thenReturn(provenance);
        when(mock.getAlert().getUrl()).thenReturn(url);

        ModellingLocationPrecisionAdjuster adjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(admin, gaul)).thenReturn(admin - 1);
        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(adjuster, mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin - 1);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDate()).isEqualTo("2015-03-07");
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
        assertThat(result.getUrl()).isEqualTo(url);
    }

    @Test
    public void serializesCorrectly() throws JsonProcessingException {
        // Arrange
        JsonDownloadDiseaseOccurrence target = new JsonDownloadDiseaseOccurrence(createNoopAdjuster(), 7, 6, 5, 4, "3", 123, "2015-03-07", "2", "1", "0");

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"3\",\"Disease\":123,\"Date\":\"2015-03-07\",\"Provenance\":\"2\",\"Feed\":\"1\",\"Url\":\"0\"}");
    }

    @Test
    public void serializesCorrectlyWithNull() throws JsonProcessingException {
        // Arrange
        JsonDownloadDiseaseOccurrence target = new JsonDownloadDiseaseOccurrence(createNoopAdjuster(), 7, 6, 5, 4, null, 123, "2015-03-07", "2", "1", null);

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"NA\",\"Disease\":123,\"Date\":\"2015-03-07\",\"Provenance\":\"2\",\"Feed\":\"1\",\"Url\":\"-\"}");
    }

    private ModellingLocationPrecisionAdjuster createNoopAdjuster() {
        ModellingLocationPrecisionAdjuster adjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(anyInt(), anyString())).thenAnswer(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                return (Integer) invocationOnMock.getArguments()[0];
            }
        });
        return adjuster;
    }
}
