package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Feed;

import java.util.Calendar;
import java.util.Date;

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
    public void saveThenGetByHealthMapAlertId() throws Exception {
        // Arrange
        Feed feed = feedDao.getById(1);
        Calendar publicationCalendar = Calendar.getInstance();
        publicationCalendar.add(Calendar.DAY_OF_YEAR, -5);
        Date publicationDate = publicationCalendar.getTime();
        Date createdDate = Calendar.getInstance().getTime();
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
        flushAndClear();
        alert = alertDao.getByHealthMapAlertId(healthMapAlertId);

        // Assert
        assertThat(alert.getFeed()).isEqualTo(feed);
        assertThat(alert.getCreatedDate()).isNotNull();
        assertThat(alert.getHealthMapAlertId()).isEqualTo(healthMapAlertId);
        assertThat(alert.getPublicationDate()).isEqualTo(publicationDate);
        assertThat(alert.getTitle()).isEqualTo(title);
        assertThat(alert.getSummary()).isEqualTo(summary);
        assertThat(alert.getUrl()).isEqualTo(url);
    }
}
