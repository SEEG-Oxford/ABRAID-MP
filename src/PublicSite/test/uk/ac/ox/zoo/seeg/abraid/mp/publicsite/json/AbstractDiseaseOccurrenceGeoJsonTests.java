package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class to ease the setup of mocks in disease occurrence GeoJSON tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractDiseaseOccurrenceGeoJsonTests {
    public static Alert mockAlert(String title, String summary, String feedName, String url, DateTime publication) {
        Alert alert = mock(Alert.class);
        Feed feed = mock(Feed.class);
        when(alert.getTitle()).thenReturn(title);
        when(alert.getSummary()).thenReturn(summary);
        when(alert.getUrl()).thenReturn(url);
        when(alert.getPublicationDate()).thenReturn(publication);
        when(alert.getFeed()).thenReturn(feed);
        when(feed.getName()).thenReturn(feedName);
        return alert;
    }

    public static Alert defaultAlert() {
        return mockAlert("title", "summary", "feedName", "url", new DateTime(0));
    }

    public static Location mockLocation(double longitude, double latitude, String locationName) {
        Location location = mock(Location.class);
        Point geom = mock(Point.class);
        when(location.getGeom()).thenReturn(geom);
        when(geom.getX()).thenReturn(latitude);
        when(geom.getY()).thenReturn(longitude);
        when(location.getName()).thenReturn(locationName);
        return location;
    }

    public static Location defaultLocation() {
        return mockLocation(1.0, -1.0, "locationName");
    }

    public static DiseaseOccurrence mockDiseaseOccurrence(int id, Location location, DateTime start, Alert alert) {
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getId()).thenReturn(id);
        when(occurrence.getLocation()).thenReturn(location);
        when(occurrence.getOccurrenceStartDate()).thenReturn(start);
        when(occurrence.getAlert()).thenReturn(alert);
        return occurrence;
    }

    public static DiseaseOccurrence defaultDiseaseOccurrence() {
        return mockDiseaseOccurrence(1, defaultLocation(), new DateTime(0), defaultAlert());
    }

    public static final String TWO_DISEASE_OCCURRENCE_FEATURES_AS_JSON = (
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
                    "               -1.0," +
                    "               1.0" +
                    "            ]" +
                    "         }," +
                    "         \"properties\":{" +
                    "            \"locationName\":\"locationName\"," +
                    "            \"alert\":{" +
                    "               \"title\":\"title\"," +
                    "               \"summary\":\"summary\"," +
                    "               \"url\":\"url\"," +
                    "               \"feedName\":\"feedName\"," +
                    "               \"publicationDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"" +
                    "            }," +
                    "            \"diseaseOccurrenceStartDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"" +
                    "         }" +
                    "      }," +
                    "      {" +
                    "         \"type\":\"Feature\"," +
                    "         \"id\":1," +
                    "         \"geometry\":{" +
                    "            \"type\":\"Point\"," +
                    "            \"coordinates\":[" +
                    "               -1.0," +
                    "               1.0" +
                    "            ]" +
                    "         }," +
                    "         \"properties\":{" +
                    "            \"locationName\":\"locationName\"," +
                    "            \"alert\":{" +
                    "               \"title\":\"title\"," +
                    "               \"summary\":\"summary\"," +
                    "               \"url\":\"url\"," +
                    "               \"feedName\":\"feedName\"," +
                    "               \"publicationDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"" +
                    "            }," +
                    "            \"diseaseOccurrenceStartDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"" +
                    "         }" +
                    "      }" +
                    "   ]" +
                    "}").replaceAll(" ", "");
}
