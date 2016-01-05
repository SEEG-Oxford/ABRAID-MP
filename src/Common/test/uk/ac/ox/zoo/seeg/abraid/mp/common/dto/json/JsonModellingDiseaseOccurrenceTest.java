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
 * Tests for JsonModellingDiseaseOccurrence.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModellingDiseaseOccurrenceTest {
    @Test
    public void explicitConstructorBindsFieldsCorrectly() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String gaul = "gaul";
        int disease = 123;
        String date = "2014-02-01";

        // Act
        JsonModellingDiseaseOccurrence result = new JsonModellingDiseaseOccurrence(createNoopAdjuster(), longitude, latitude, weight, admin, gaul, disease, date);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @Test
    public void explicitConstructorBindsFieldsCorrectlyWithNull() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        int disease = 123;
        String date = "2014-02-01";

        // Act
        JsonModellingDiseaseOccurrence result = new JsonModellingDiseaseOccurrence(createNoopAdjuster(), longitude, latitude, weight, admin, null, disease, date);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @Test
    public void explicitConstructorBindsFieldsCorrectlyWithAdjustedPrecision() {
        // Arrange
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = 10;
        String gaul = "gaul";
        int disease = 123;
        String date = "2014-02-01";

        ModellingLocationPrecisionAdjuster adjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(admin, gaul)).thenReturn(admin - 1);

        // Act
        JsonModellingDiseaseOccurrence result = new JsonModellingDiseaseOccurrence(adjuster, longitude, latitude, weight, admin, gaul, disease, date);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin - 1);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @Test
    public void geoJsonObjectConstructorBindsFieldsCorrectly() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";
        int disease = 123;
        String date = "2014-02-01";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getDiseaseGroup().getId()).thenReturn(disease);
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);
        when(mock.getOccurrenceDate()).thenReturn(new DateTime(date));

        // Act
        JsonModellingDiseaseOccurrence result = new JsonModellingDiseaseOccurrence(createNoopAdjuster(), mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @Test
    public void geoJsonObjectConstructorBindsFieldsCorrectlyWithNullGAUL() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        int disease = 123;
        String date = "2014-02-01";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getDiseaseGroup().getId()).thenReturn(disease);
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(null);
        when(mock.getOccurrenceDate()).thenReturn(new DateTime(date));

        // Act
        JsonModellingDiseaseOccurrence result = new JsonModellingDiseaseOccurrence(createNoopAdjuster(), mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin);
        assertThat(result.getGaul()).isEqualTo("NA");
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @Test
    public void geoJsonObjectConstructorBindsFieldsCorrectlyWithAdjustedPrecision() {
        double longitude = 7;
        double latitude = 8;
        double weight = 9;
        int admin = LocationPrecision.ADMIN1.getModelValue();
        String gaul = "1234";
        int disease = 123;
        String date = "2014-02-01";

        DiseaseOccurrence mock = mock(DiseaseOccurrence.class);
        when(mock.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(mock.getDiseaseGroup().getId()).thenReturn(disease);
        when(mock.getAlert()).thenReturn(mock(Alert.class));
        when(mock.getAlert().getFeed()).thenReturn(mock(Feed.class));
        when(mock.getLocation()).thenReturn(mock(Location.class));
        when(mock.getLocation().getGeom()).thenReturn(mock(Point.class));
        when(mock.getLocation().getGeom().getX()).thenReturn(longitude);
        when(mock.getLocation().getGeom().getY()).thenReturn(latitude);
        when(mock.getFinalWeighting()).thenReturn(weight);
        when(mock.getLocation().getPrecision()).thenReturn(LocationPrecision.ADMIN1);
        when(mock.getLocation().getAdminUnitQCGaulCode()).thenReturn(1234);
        when(mock.getOccurrenceDate()).thenReturn(new DateTime(date));

        ModellingLocationPrecisionAdjuster adjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(adjuster.adjust(admin, gaul)).thenReturn(admin - 1);

        // Act
        JsonModellingDiseaseOccurrence result = new JsonModellingDiseaseOccurrence(adjuster, mock);

        // Assert
        assertThat(result.getLongitude()).isEqualTo(longitude);
        assertThat(result.getLatitude()).isEqualTo(latitude);
        assertThat(result.getWeight()).isEqualTo(weight);
        assertThat(result.getAdmin()).isEqualTo(admin - 1);
        assertThat(result.getGaul()).isEqualTo(gaul);
        assertThat(result.getDisease()).isEqualTo(disease);
        assertThat(result.getDate()).isEqualTo(date);
    }

    @Test
    public void serializesCorrectly() throws JsonProcessingException {
        // Arrange
        JsonModellingDiseaseOccurrence target = new JsonModellingDiseaseOccurrence(createNoopAdjuster(), 7, 6, 5, 4, "3", 123, "2008-01-21");

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"3\",\"Disease\":123,\"Date\":\"2008-01-21\"}");
    }

    @Test
    public void serializesCorrectlyWithNull() throws JsonProcessingException {
        // Arrange
        JsonModellingDiseaseOccurrence target = new JsonModellingDiseaseOccurrence(createNoopAdjuster(), 7, 6, 5, 4, null, 123, "2008-01-21");

        // Act
        String result = new ObjectMapper().writeValueAsString(target);

        // Assert
        assertThat(result).isEqualTo("{\"Longitude\":7.0,\"Latitude\":6.0,\"Weight\":5.0,\"Admin\":4,\"GAUL\":\"NA\",\"Disease\":123,\"Date\":\"2008-01-21\"}");
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
