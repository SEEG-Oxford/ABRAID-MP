package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the DiseaseOccurrenceDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private CountryDao countryDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private DiseaseOccurrenceReviewDao diseaseOccurrenceReviewDao;

    @Autowired
    private ExpertDao expertDao;

    @Autowired
    private FeedDao feedDao;


    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustNotReturnAReviewedPoint() {
        // Arrange
        Expert expert = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(1);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(expertId, diseaseGroupId);

        // Assert
        assertThat(list).doesNotContain(occurrence);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustOnlyReturnSpecifiedDiseaseGroup() {
        // Arrange
        Expert expert = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(1);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(expertId, diseaseGroupId);

        // Assert
        for (DiseaseOccurrence item : list) {
            assertThat(item.getDiseaseGroup().getId()).isSameAs(diseaseGroupId);
        }
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustHaveNoIntersectionWithExpertsReviewedList() {
        // Arrange
        Expert expert = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence = diseaseOccurrenceDao.getById(1);
        DiseaseOccurrenceReviewResponse response = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert, occurrence, response);

        // Act
        Integer expertId = expert.getId();
        Integer diseaseGroupId = occurrence.getDiseaseGroup().getId();
        List<DiseaseOccurrence> occurrencesYetToBeReviewed = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(expertId, diseaseGroupId);

        //Assert
        List<DiseaseOccurrenceReview> reviewedOccurrences = diseaseOccurrenceReviewDao.getByExpertIdAndDiseaseGroupId(expertId, diseaseGroupId);
        occurrencesYetToBeReviewed.retainAll(reviewedOccurrences);
        assertThat(occurrencesYetToBeReviewed).hasSize(0);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustReturnOccurrencesForCorrectExpert() {
        // Arrange
        // Two experts save reviews for different disease occurrences
        Expert expert0 = expertDao.getByEmail("zool1250@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence0 = diseaseOccurrenceDao.getById(1);
        DiseaseOccurrenceReviewResponse response0 = DiseaseOccurrenceReviewResponse.YES;
        createAndSaveDiseaseOccurrenceReview(expert0, occurrence0, response0);

        Expert expert1 = expertDao.getByEmail("zool1251@zoo.ox.ac.uk");
        DiseaseOccurrence occurrence1 = diseaseOccurrenceDao.getById(2);
        DiseaseOccurrenceReviewResponse response1 = DiseaseOccurrenceReviewResponse.NO;
        createAndSaveDiseaseOccurrenceReview(expert1, occurrence1, response1);

        // Act
        Integer expertId = expert0.getId();
        Integer diseaseGroupId = occurrence0.getDiseaseGroup().getId();
        List<DiseaseOccurrence> list = diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(expertId, diseaseGroupId);

        // Assert
        assertThat(list).contains(occurrence1);
        assertThat(list).doesNotContain(occurrence0);
    }

    @Test
    public void saveThenReloadDiseaseOccurrence() {
        // Arrange
        Alert alert = createAlert();
        Location location = createLocation();
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        Calendar occurrenceStartCalendar = Calendar.getInstance();
        occurrenceStartCalendar.add(Calendar.DAY_OF_YEAR, -5);
        Date occurrenceStartDate = occurrenceStartCalendar.getTime();
        double validationWeighting = 0.5;

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(alert);
        occurrence.setLocation(location);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setOccurrenceStartDate(occurrenceStartDate);
        occurrence.setValidationWeighting(validationWeighting);

        // Act
        diseaseOccurrenceDao.save(occurrence);

        // Assert
        assertThat(occurrence.getCreatedDate()).isNotNull();
        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getId()).isNotNull();
        assertThat(occurrence.getAlert().getCreatedDate()).isNotNull();
        assertThat(occurrence.getLocation()).isNotNull();
        assertThat(occurrence.getLocation().getId()).isNotNull();
        assertThat(occurrence.getLocation().getCreatedDate()).isNotNull();

        Integer id = occurrence.getId();
        flushAndClear();
        occurrence = diseaseOccurrenceDao.getById(id);

        assertThat(occurrence.getAlert()).isNotNull();
        assertThat(occurrence.getAlert().getId()).isNotNull();
        assertThat(occurrence.getCreatedDate()).isNotNull();
        assertThat(occurrence.getLocation()).isNotNull();
        assertThat(occurrence.getLocation().getId()).isNotNull();
        assertThat(occurrence.getValidationWeighting()).isEqualTo(validationWeighting);
        assertThat(occurrence.getDiseaseGroup()).isNotNull();
        assertThat(occurrence.getDiseaseGroup().getId()).isNotNull();
        assertThat(occurrence.getOccurrenceStartDate()).isEqualTo(occurrenceStartDate);
    }

    private Alert createAlert() {
        Feed feed = feedDao.getById(1);
        Calendar publicationCalendar = Calendar.getInstance();
        publicationCalendar.add(Calendar.DAY_OF_YEAR, -5);
        Date publicationDate = publicationCalendar.getTime();
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
        return alert;
    }

    private Location createLocation() {
        String countryName = "Pakistan";
        String placeName = "Karachi";
        Country country = countryDao.getByName(countryName);
        Point point = GeometryUtils.createPoint(25.0111455, 67.0647043);

        Location location = new Location();
        location.setGeom(point);
        location.setCountry(country);
        location.setName(placeName);
        location.setPrecision(LocationPrecision.PRECISE);
        return location;
    }

    public DiseaseOccurrenceReview createAndSaveDiseaseOccurrenceReview(Expert expert, DiseaseOccurrence occurrence, DiseaseOccurrenceReviewResponse response) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview();
        review.setExpert(expert);
        review.setDiseaseOccurrence(occurrence);
        review.setResponse(response);
        diseaseOccurrenceReviewDao.save(review);
        return review;
    }
}
