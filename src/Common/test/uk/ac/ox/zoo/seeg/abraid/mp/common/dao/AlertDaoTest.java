package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the AlertDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class AlertDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private AlertDao alertDao;

    @Autowired
    private FeedDao feedDao;

    @Test
    public void saveThenGetByHealthMapAlertId() {
        // Arrange
        Feed feed = feedDao.getById(1);
        DateTime publicationDate = new DateTime("2014-01-03T01:00:00-05:00");
        long healthMapAlertId = 100L;
        String title = "Dengue/DHF update (15): Asia, Indian Ocean, Pacific";
        String summary = "This is a summary of the alert";
        String url = "http://www.promedmail.org/direct.php?id=20140217.2283261";

        Alert alert = new Alert();
        alert.setFeed(feed);
        alert.setHealthMapAlertId(healthMapAlertId);
        alert.setPublicationDate(publicationDate);
        alert.setTitle(title);
        alert.setSummary(summary);
        alert.setUrl(url);

        // Act
        alertDao.save(alert);

        // Assert
        assertThat(alert.getCreatedDate()).isNotNull();
        flushAndClear();
        alert = alertDao.getByHealthMapAlertId(healthMapAlertId);
        assertThat(alert.getFeed()).isEqualTo(feed);
        assertThat(alert.getCreatedDate()).isNotNull();
        assertThat(alert.getHealthMapAlertId()).isEqualTo(healthMapAlertId);
        // Test that the time zone has been converted correctly
        DateTime expectedPublicationDate = new DateTime("2014-01-03T06:00:00");
        assertThatDatesAreEqual(alert.getPublicationDate(), expectedPublicationDate);
        assertThat(alert.getTitle()).isEqualTo(title);
        assertThat(alert.getSummary()).isEqualTo(summary);
        assertThat(alert.getUrl()).isEqualTo(url);
    }

    @Test
    public void getByAlertIdSuccessful() {
        Alert alert = alertDao.getById(212855);
        assertThat(alert).isNotNull();
    }

    @Test
    public void getByAlertIdDoesNotExist() {
        Alert alert = alertDao.getById(-1);
        assertThat(alert).isNull();
    }

    private void assertThatDatesAreEqual(DateTime date1, DateTime date2) {
        assertThat(date1.toDateTimeISO()).isEqualTo(date2.toDateTimeISO());
    }
}
