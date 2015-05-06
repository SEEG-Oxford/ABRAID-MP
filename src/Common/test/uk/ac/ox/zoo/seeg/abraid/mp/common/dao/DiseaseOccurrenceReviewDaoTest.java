package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseOccurrenceReviewDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseOccurrenceReviewDaoTest extends AbstractCommonSpringIntegrationTests {
    private static final int DISEASE_GROUP_ID_1 = 87;
    private static final int DISEASE_GROUP_ID_2 = 22;

    @Autowired
    private AlertDao alertDao;

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

    @Autowired
    private LocationDao locationDao;

    @Test
    public void getLastReviewDateByExpertIdReturnsCorrectDate() {
        // Arrange
        DiseaseOccurrenceReview firstReview = new DiseaseOccurrenceReview(expertDao.getById(1), diseaseOccurrenceDao.getById(272407), DiseaseOccurrenceReviewResponse.NO);
        diseaseOccurrenceReviewDao.save(firstReview);
        flushAndClear();
        DiseaseOccurrenceReview secondReview = new DiseaseOccurrenceReview(expertDao.getById(1), diseaseOccurrenceDao.getById(272829), DiseaseOccurrenceReviewResponse.NO);
        diseaseOccurrenceReviewDao.save(secondReview);
        flushAndClear();

        // Act
        DateTime actual = diseaseOccurrenceReviewDao.getLastReviewDateByExpertId(1);

        // Assert
        assertThat(actual).isEqualTo(secondReview.getCreatedDate());
    }

    @Test
    public void getLastReviewDateByExpertIdReturnsNullWhenNoReviews() {
        // Arrange - no reviews in the database

        // Act
        DateTime actual = diseaseOccurrenceReviewDao.getLastReviewDateByExpertId(1);

        // Assert
        assertThat(actual).isNull();
    }

    @Test
    public void getCountByExpertId() {
        // Arrange - no reviews in the database

        // Act
        Long count = diseaseOccurrenceReviewDao.getCountByExpertId(1);

        // Assert
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void doesDiseaseOccurrenceReviewExistReturnsTrueWhenExpected() {
        // Arrange
        Expert expert = createExpert("expert@test.com");
        DiseaseOccurrence occurrence = createDiseaseOccurrence(DiseaseOccurrenceStatus.READY, createDiseaseGroup());
        createDiseaseOccurrenceReview(expert, occurrence);

        // Act
        boolean result = diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(expert.getId(), occurrence.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void doesDiseaseOccurrenceReviewExistReturnsFalseWhenReviewDoesNotExist() {
        // Arrange
        Expert expert = createExpert("expert@test.com");
        DiseaseOccurrence occurrence = createDiseaseOccurrence(DiseaseOccurrenceStatus.READY, createDiseaseGroup());

        // Act
        boolean result = diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(expert.getId(), occurrence.getId());

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void getAllDiseaseOccurrenceReviewsForOccurrencesInValidationReturnsExpectedResult() {
        // Arrange
        List<DiseaseOccurrenceReview> testReviews = createTestReviews();

        // Act
        List<DiseaseOccurrenceReview> reviews =
                diseaseOccurrenceReviewDao.getAllDiseaseOccurrenceReviewsForOccurrencesInValidation(DISEASE_GROUP_ID_1);

        // Assert
        assertThat(reviews).hasSize(2);
        assertThat(containsById(reviews, testReviews.get(2))).isTrue();
        assertThat(containsById(reviews, testReviews.get(3))).isTrue();
    }

    @Test
    public void getDiseaseOccurrenceReviewsForUpdatingWeightingReturnsReviewsForOccurrencesInReviewByExpertsOverWeightingThreshold() {
        // Arrange
        List<DiseaseOccurrenceReview> testReviews = createTestReviews();
        double expertWeightingThreshold = 0.6;

        // Act
        List<DiseaseOccurrenceReview> reviews =
                diseaseOccurrenceReviewDao.getDiseaseOccurrenceReviewsForUpdatingWeightings(
                        DISEASE_GROUP_ID_1, expertWeightingThreshold);

        // Assert
        assertThat(reviews).hasSize(1);
        assertThat(containsById(reviews, testReviews.get(2))).isTrue();     // Submitted by expert1 with weighting 0.6
        assertThat(containsById(reviews, testReviews.get(3))).isFalse();    // Submitted by expert2 with weighting 0.5
    }

    @Test
    public void getDiseaseOccurrenceReviewsForUpdatingWeightingReturnsEmptyListIfNoExpertsOverWeightingThreshold() {
        // Arrange
        createTestReviews();

        // Act
        List<DiseaseOccurrenceReview> reviews =
                diseaseOccurrenceReviewDao.getDiseaseOccurrenceReviewsForUpdatingWeightings(DISEASE_GROUP_ID_1, 0.7);

        // Assert
        assertThat(reviews).isEmpty();
    }

    @Test
    public void canSaveReviewWithNullResponse() {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(DISEASE_GROUP_ID_1);
        Expert expert = createExpert("expert1@test.com", 0.6);
        DiseaseOccurrence occurrence = createDiseaseOccurrence(DiseaseOccurrenceStatus.READY, diseaseGroup);

        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, occurrence, null);

        // Act
        diseaseOccurrenceReviewDao.save(review);
        flushAndClear();
        List<DiseaseOccurrenceReview> all = diseaseOccurrenceReviewDao.getAll();

        // Assert
        assertThat(all).hasSize(1);
    }

    private List<DiseaseOccurrenceReview> createTestReviews() {
        Expert expert1 = createExpert("expert1@test.com", 0.6);
        Expert expert2 = createExpert("expert2@test.com", 0.5);

        DiseaseGroup diseaseGroup1 = diseaseGroupDao.getById(DISEASE_GROUP_ID_1);
        DiseaseGroup diseaseGroup2 = diseaseGroupDao.getById(DISEASE_GROUP_ID_2);

        DiseaseOccurrence occurrence1 = createDiseaseOccurrence(DiseaseOccurrenceStatus.READY, diseaseGroup1);
        DiseaseOccurrence occurrence2 = createDiseaseOccurrence(DiseaseOccurrenceStatus.IN_REVIEW, diseaseGroup1);
        DiseaseOccurrence occurrence3 = createDiseaseOccurrence(DiseaseOccurrenceStatus.IN_REVIEW, diseaseGroup2);

        DiseaseOccurrenceReview occurrence1Expert1Review = createDiseaseOccurrenceReview(expert1, occurrence1);
        DiseaseOccurrenceReview occurrence1Expert2Review = createDiseaseOccurrenceReview(expert2, occurrence1);
        DiseaseOccurrenceReview occurrence2Expert1Review = createDiseaseOccurrenceReview(expert1, occurrence2);
        DiseaseOccurrenceReview occurrence2Expert2Review = createDiseaseOccurrenceReview(expert2, occurrence2);
        DiseaseOccurrenceReview occurrence3Expert1Review = createDiseaseOccurrenceReview(expert1, occurrence3);
        DiseaseOccurrenceReview occurrence3Expert2Review = createDiseaseOccurrenceReview(expert2, occurrence3);

        return Arrays.asList(occurrence1Expert1Review, occurrence1Expert2Review, occurrence2Expert1Review,
                occurrence2Expert2Review, occurrence3Expert1Review, occurrence3Expert2Review);
    }

    private boolean containsById(List<DiseaseOccurrenceReview> reviews, DiseaseOccurrenceReview expectedReview) {
        for (DiseaseOccurrenceReview review : reviews) {
            if (review.getId().equals(expectedReview.getId())) {
                return true;
            }
        }
        return false;
    }

    private DiseaseOccurrenceReview createDiseaseOccurrenceReview(Expert expert, DiseaseOccurrence occurrence) {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview();
        review.setExpert(expert);
        review.setDiseaseOccurrence(occurrence);
        review.setResponse(DiseaseOccurrenceReviewResponse.YES);
        diseaseOccurrenceReviewDao.save(review);
        return review;
    }

    private Expert createExpert(String email) {
        return createExpert(email, 1.0);
    }

    private Expert createExpert(String email, double weighting) {
        String name = "Test Expert";
        String password = "pa55word";
        String jobTitle = "job";
        String institution = "institution";
        boolean visibilityRequested = true;

        Expert expert = new Expert();
        expert.setName(name);
        expert.setEmail(email);
        expert.setPassword(password);
        expert.setJobTitle(jobTitle);
        expert.setInstitution(institution);
        //noinspection ConstantConditions
        expert.setVisibilityRequested(visibilityRequested);
        expert.setWeighting(weighting);
        expertDao.save(expert);

        return expert;
    }

    private DiseaseOccurrence createDiseaseOccurrence(DiseaseOccurrenceStatus status, DiseaseGroup diseaseGroup) {
        DiseaseOccurrence diseaseOccurrence = new DiseaseOccurrence();
        diseaseOccurrence.setAlert(createAlert());
        diseaseOccurrence.setDiseaseGroup(diseaseGroup);
        diseaseOccurrence.setLocation(locationDao.getById(6));
        diseaseOccurrence.setOccurrenceDate(DateTime.now());
        diseaseOccurrence.setStatus(status);
        diseaseOccurrenceDao.save(diseaseOccurrence);
        return diseaseOccurrence;
    }

    private Alert createAlert() {
        Feed feed = feedDao.getById(2);
        DateTime publicationDate = DateTime.now().minusDays(5);
        String title = "Dengue/DHF update (15): Asia, Indian Ocean, Pacific";
        String summary = "This is a summary of the alert";
        String url = "http://www.promedmail.org/direct.php?id=20140217.2283261";

        Alert alert = new Alert();
        alert.setFeed(feed);
        alert.setPublicationDate(publicationDate);
        alert.setTitle(title);
        alert.setSummary(summary);
        alert.setUrl(url);
        alertDao.save(alert);

        return alert;
    }

    private DiseaseGroup createDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName("Test cluster");
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);
        diseaseGroupDao.save(diseaseGroup);
        return diseaseGroup;
    }
}
