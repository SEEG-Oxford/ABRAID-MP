package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json.geojson.GeoJsonObjectType;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by zool1112 on 07/03/14.
 */
public class GeoJsonDiseaseOccurrenceFeatureTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void constructorForGeoJsonDiseaseOccurrenceFeaturePropertiesBindsParametersCorrectly() throws Exception {
        // Arrange
        int expectedId = 999;
        double expectedLongitude = 2.0;
        double expectedLatitude = 3.0;

        DiseaseOccurrence occurrence = defaultDiseaseOccurrence();
        when(occurrence.getId()).thenReturn(expectedId);
        when(occurrence.getLocation().getGeom().getX()).thenReturn(expectedLatitude);
        when(occurrence.getLocation().getGeom().getY()).thenReturn(expectedLongitude);

        // Act
        GeoJsonDiseaseOccurrenceFeature result = new GeoJsonDiseaseOccurrenceFeature(occurrence);

        // Assert
        assertThat(result.getType()).isEqualTo(GeoJsonObjectType.FEATURE);
        assertThat(result.getId()).isEqualTo(expectedId);
        assertThat(result.getGeometry().getType()).isEqualTo(GeoJsonObjectType.POINT);
        assertThat(result.getGeometry().getCoordinates()).isEqualTo(Arrays.asList(expectedLongitude, expectedLatitude));
        assertThat(result.getGeometry().getCrs()).isNull();
        assertThat(result.getGeometry().getBBox()).isNull();
        assertThat(result.getProperties()).isNotNull();
        assertThat(result.getCrs()).isNull();
        assertThat(result.getBBox()).isNull();
    }
}
