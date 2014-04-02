package uk.ac.ox.zoo.seeg.abraid.mp.testutils;

import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.DisplayJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.ModellingJsonView;

import java.util.TimeZone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class to ease the setup of mocks in disease occurrence GeoJSON tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractDiseaseOccurrenceGeoJsonTests {
    public static Alert mockAlert(String title, String summary, String feedName, String feedLanguage, String url, DateTime publication) {
        Alert alert = mock(Alert.class);
        Feed feed = mock(Feed.class);
        when(alert.getTitle()).thenReturn(title);
        when(alert.getSummary()).thenReturn(summary);
        when(alert.getUrl()).thenReturn(url);
        when(alert.getPublicationDate()).thenReturn(publication);
        when(alert.getFeed()).thenReturn(feed);
        when(feed.getName()).thenReturn(feedName);
        when(feed.getLanguage()).thenReturn(feedLanguage);
        return alert;
    }

    public static Alert defaultAlert() {
        return mockAlert("title", "summary", "feedName", "feedLanguage", "url", (new DateTime(0)).withZone(DateTimeZone.UTC));
    }

    public static Location mockLocation(double longitude, double latitude, String locationName, LocationPrecision locationPrecision) {
        Location location = mock(Location.class);
        Point geom = mock(Point.class);
        when(location.getGeom()).thenReturn(geom);
        when(geom.getX()).thenReturn(latitude);
        when(geom.getY()).thenReturn(longitude);
        when(location.getName()).thenReturn(locationName);
        when(location.getPrecision()).thenReturn(locationPrecision);
        return location;
    }

    public static Location defaultLocation() {
        return mockLocation(1.0, -1.0, "locationName", LocationPrecision.PRECISE);
    }

    public static DiseaseOccurrence mockDiseaseOccurrence(int id, Location location, DateTime start, Alert alert, double weighting) {
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getId()).thenReturn(id);
        when(occurrence.getLocation()).thenReturn(location);
        when(occurrence.getOccurrenceStartDate()).thenReturn(start);
        when(occurrence.getAlert()).thenReturn(alert);
        when(occurrence.getValidationWeighting()).thenReturn(weighting);
        return occurrence;
    }

    public static DiseaseOccurrence defaultDiseaseOccurrence() {
        return mockDiseaseOccurrence(1, defaultLocation(), (new DateTime(0)).withZone(DateTimeZone.UTC), defaultAlert(), 0.5);
    }

    public static String getTwoDiseaseOccurrenceFeaturesAsJson(Class view) {

        String displayViewProperties =
           "            \"locationName\":\"locationName\"," +
           "            \"alert\":{" +
           "               \"title\":\"title\"," +
           "               \"summary\":\"summary\"," +
           "               \"url\":\"url\"," +
           "               \"feedName\":\"feedName\"," +
           "               \"feedLanguage\":\"feedLanguage\"," +
           "               \"publicationDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"" +
           "            }," +
           "            \"diseaseOccurrenceStartDate\":\"" + ISODateTimeFormat.dateTime().withZoneUTC().print(new DateTime(0)) + "\"";

        String modellingViewProperties =
           "            \"locationPrecision\":\"PRECISE\"," +
           "            \"weighting\":0.5";

        return (
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
                    ((view == DisplayJsonView.class) ? displayViewProperties : "") +
                    ((view == ModellingJsonView.class) ? modellingViewProperties : "") +
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
                    ((view == DisplayJsonView.class) ? displayViewProperties : "") +
                    ((view == ModellingJsonView.class) ? modellingViewProperties : "") +
                    "         }" +
                    "      }" +
                    "   ]" +
                    "}").replaceAll(" ", "");
    }
}
