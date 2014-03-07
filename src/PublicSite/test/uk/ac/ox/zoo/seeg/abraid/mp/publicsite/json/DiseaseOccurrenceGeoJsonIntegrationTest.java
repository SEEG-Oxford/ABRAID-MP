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
 * Created by zool1112 on 07/03/14.
 */
public class DiseaseOccurrenceGeoJsonIntegrationTest extends AbstractDiseaseOccurrenceGeoJsonTests {
    @Test
    public void serializingADiseaseOccurrenceCollectionGivesCorrectOutput() throws Exception {
        // Arrange
        List<DiseaseOccurrence> occurrences = Arrays.asList(defaultDiseaseOccurrence(), defaultDiseaseOccurrence());
        GeoJsonObjectMapper objectMapper = new GeoJsonObjectMapper();
        OutputStream stream = new ByteArrayOutputStream();
        String expectedJson =
            "{" +
            "   \"type\":\"FeatureCollection\"," +
            "   \"crs\":{" +
            "      \"type\":\"name\"," +
            "      \"properties\":{" +
            "         \"name\":\"urn:ogc:def:crs:EPSG::4326\"" +
            "      }" +
            "   }," +
            "   \"features\":[" +
            "      {" +
            "         \"type\":\"Feature\"," +
            "         \"id\":1," +
            "         \"geometry\":{" +
            "            \"type\":\"Point\"," +
            "            \"coordinates\":[" +
            "               1.0," +
            "               -1.0" +
            "            ]" +
            "         }," +
            "         \"properties\":{" +
            "            \"locationName\":\"locationName\"," +
            "            \"countryName\":\"countryName\"," +
            "            \"alert\":{" +
            "               \"title\":\"title\"," +
            "               \"summary\":\"summary\"," +
            "               \"url\":\"url\"," +
            "               \"feedName\":\"feedName\"," +
            "               \"publicationDate\":\"1970-01-01T01:00:00.000+01:00\"" +
            "            }," +
            "            \"diseaseOccurrenceStartDate\":\"1970-01-01T01:00:00.000+01:00\"" +
            "         }" +
            "      }," +
            "      {" +
            "         \"type\":\"Feature\"," +
            "         \"id\":1," +
            "         \"geometry\":{" +
            "            \"type\":\"Point\"," +
            "            \"coordinates\":[" +
            "               1.0," +
            "               -1.0" +
            "            ]" +
            "         }," +
            "         \"properties\":{" +
            "            \"locationName\":\"locationName\"," +
            "            \"countryName\":\"countryName\"," +
            "            \"alert\":{" +
            "               \"title\":\"title\"," +
            "               \"summary\":\"summary\"," +
            "               \"url\":\"url\"," +
            "               \"feedName\":\"feedName\"," +
            "               \"publicationDate\":\"1970-01-01T01:00:00.000+01:00\"" +
            "            }," +
            "            \"diseaseOccurrenceStartDate\":\"1970-01-01T01:00:00.000+01:00\"" +
            "         }" +
            "      }" +
            "   ]" +
            "}";

        // Act
        objectMapper.writeValue(stream, new GeoJsonDiseaseOccurrenceFeatureCollection(occurrences));

        // Assert
        assertThat(stream.toString()).isEqualTo(expectedJson.replaceAll(" ", ""));

    }
}
