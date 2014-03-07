package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.json;

import com.vividsolutions.jts.geom.Point;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class to ease the setup of mocks in disease occurrence GeoJSON tests.
 * Copyright (c) 2014 University of Oxford
 */
public abstract class AbstractDiseaseOccurrenceGeoJsonTests {
    protected static Alert mockAlert(String title, String summary, String feedName, String url, DateTime publication) {
        Alert alert = mock(Alert.class);
        Feed feed = mock(Feed.class);
        when(alert.getTitle()).thenReturn(title);
        when(alert.getSummary()).thenReturn(summary);
        when(alert.getUrl()).thenReturn(url);
        when(alert.getPublicationDate()).thenReturn(publication.toDate());
        when(alert.getFeed()).thenReturn(feed);
        when(feed.getName()).thenReturn(feedName);
        return alert;
    }

    protected static Alert defaultAlert() {
        return mockAlert("title", "summary", "feedName", "url", new DateTime(0, DateTimeZone.UTC));
    }

    protected static Location mockLocation(double longitude, double latitude, String locationName, String countryName) {
        Location location = mock(Location.class);
        Point geom = mock(Point.class);
        when(location.getGeom()).thenReturn(geom);
        when(geom.getX()).thenReturn(latitude);
        when(geom.getY()).thenReturn(longitude);
        when(location.getName()).thenReturn(locationName);
        Country country = mock(Country.class);
        when(location.getCountry()).thenReturn(country);
        when(country.getName()).thenReturn(countryName);
        return location;
    }

    protected static Location defaultLocation() {
        return mockLocation(1.0, -1.0, "locationName", "countryName");
    }

    protected static DiseaseOccurrence mockDiseaseOccurrence(int id, Location location, DateTime start, Alert alert) {
        DiseaseOccurrence occurrence = mock(DiseaseOccurrence.class);
        when(occurrence.getId()).thenReturn(id);
        when(occurrence.getLocation()).thenReturn(location);
        when(occurrence.getOccurrenceStartDate()).thenReturn(start.toDate());
        when(occurrence.getAlert()).thenReturn(alert);
        return occurrence;
    }

    protected static DiseaseOccurrence defaultDiseaseOccurrence() {
        return mockDiseaseOccurrence(1, defaultLocation(), new DateTime(0, DateTimeZone.UTC), defaultAlert());
    }

}
