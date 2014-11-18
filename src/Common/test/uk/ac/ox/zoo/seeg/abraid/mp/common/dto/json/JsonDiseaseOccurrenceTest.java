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
 * Tests for JsonDiseaseOccurrence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonDiseaseOccurrenceTest {
    @Test
    public void explicitConstructorBindsFieldsCorrectly() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String gaul = "gaul";

        // Act
        JsonDiseaseOccurrence result = new JsonDiseaseOccurrence(longitude, latitude, weight, admin, gaul);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
    }

    @Test
    public void explicitConstructorBindsFieldsCorrectlyWithNull() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;

        // Act
        JsonDiseaseOccurrence result = new JsonDiseaseOccurrence(longitude, latitude, weight, admin, null);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectly() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);

        // Act
        JsonDiseaseOccurrence result = new JsonDiseaseOccurrence(mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
    }

    @Test
    public void domainObjectConstructorBindsFieldsCorrectlyWithNullGAUL() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(null);

        // Act
        JsonDiseaseOccurrence result = new JsonDiseaseOccurrence(mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
    }

    @Test
    public void geoJsonObjectConstructorBindsFieldsCorrectly() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);

        // Act
        JsonDiseaseOccurrence result = new JsonDiseaseOccurrence(new GeoJsonDiseaseOccurrenceFeature(mock));

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
    }

    @Test
    public void geoJsonObjectConstructorBindsFieldsCorrectlyWithNullGAUL() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(null);

        // Act
        JsonDiseaseOccurrence result = new JsonDiseaseOccurrence(new GeoJsonDiseaseOccurrenceFeature(mock));

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
    }

    @Test
    public void serializesCorrectly() throws JsonProcessingException {
        // Arrange
        JsonDiseaseOccurrence target = new JsonDiseaseOccurrence(7, 6, 5, 4, "3");

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"3\"}");
    }

    @Test
    public void serializesCorrectlyWithNull() throws JsonProcessingException {
        // Arrange
        JsonDiseaseOccurrence target = new JsonDiseaseOccurrence(7, 6, 5, 4, null);

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"NA\"}");
    }
}
