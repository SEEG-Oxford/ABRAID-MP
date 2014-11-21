package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import static org.assertj.core.api.Assertions.assertThat;
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
        String provenance = "provenance";
        String feed = "feed";

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(longitude, latitude, weight, admin, gaul, provenance, feed);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
    }

    @Test
    public void explicitConstructorBindsFieldsCorrectlyWithNull() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String provenance = "provenance";
        String feed = "feed";

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(longitude, latitude, weight, admin, null, provenance, feed);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectly() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";
        String provenance = "provenance";
        String feed = "feed";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
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
        when(mock.getAlert().getFeed().getProvenance()).thenReturn(mock(Provenance.class));
        when(mock.getAlert().getFeed().getProvenance().getName()).thenReturn(provenance);

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectlyWithNullGAUL() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String provenance = "provenance";
        String feed = "feed";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
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
        when(mock.getAlert().getFeed().getProvenance()).thenReturn(mock(Provenance.class));
        when(mock.getAlert().getFeed().getProvenance().getName()).thenReturn(provenance);

        // Act
        JsonDownloadDiseaseOccurrence result = new JsonDownloadDiseaseOccurrence(mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
        assertThat(result.getProvenance()).isEqualTo(provenance);
        assertThat(result.getFeed()).isEqualTo(feed);
    }

    @Test
    public void serializesCorrectly() throws JsonProcessingException {
        // Arrange
        JsonDownloadDiseaseOccurrence target = new JsonDownloadDiseaseOccurrence(7, 6, 5, 4, "3", "2", "1");

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"3\",\"Provenance\":\"2\",\"Feed\":\"1\"}");
    }

    @Test
    public void serializesCorrectlyWithNull() throws JsonProcessingException {
        // Arrange
        JsonDownloadDiseaseOccurrence target = new JsonDownloadDiseaseOccurrence(7, 6, 5, 4, null, "2", "1");

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"NA\",\"Provenance\":\"2\",\"Feed\":\"1\"}");
    }
}