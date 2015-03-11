package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the AdminUnitReviewDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class AdminUnitReviewDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private AdminUnitReviewDao adminUnitReviewDao;

    @Autowired
    private DiseaseExtentClassDao diseaseExtentClassDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private ExpertDao expertDao;

    private static final Integer EXPERT_ID = 1;
    private static final Integer DISEASE_GROUP_ID = 1;

    @Test
    public void saveAndReloadGlobalAdminUnitReview() {
        // Arrange
        Expert expert = expertDao.getById(2);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        int adminUnitGlobalGaulCode = 2;
        DiseaseExtentClass response = diseaseExtentClassDao.getByName("PRESENCE");

        AdminUnitReview review = createAdminUnitReview(expert, adminUnitGlobalGaulCode, diseaseGroup, response);

        // Act
        adminUnitReviewDao.save(review);

        // Assert
        assertReviewSaved(review);

        Integer id = review.getId();
        flushAndClear();
        review = adminUnitReviewDao.getById(id);
        assertReviewParameters(review, expert, diseaseGroup, adminUnitGlobalGaulCode, response);
    }

    private void assertReviewSaved(AdminUnitReview review) {
        assertThat(review.getId()).isNotNull();
        assertThat(review.getCreatedDate()).isNotNull();
    }

    private void assertReviewParameters(AdminUnitReview review, Expert expert, DiseaseGroup diseaseGroup,
                                        int adminUnitGlobalGaulCode, DiseaseExtentClass response) {
        assertThat(review).isNotNull();
        assertThat(review.getExpert().getEmail()).isEqualTo(expert.getEmail());
        assertThat(review.getExpert().getValidatorDiseaseGroups()).containsAll(expert.getValidatorDiseaseGroups());
        assertThat(review.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(review.getAdminUnitGlobalGaulCode()).isEqualTo(adminUnitGlobalGaulCode);
        assertThat(review.getAdminUnitTropicalGaulCode()).isNull();
        assertThat(review.getResponse()).isEqualTo(response);
    }

    @Test
    public void saveTwoAdminUnitReviews() {
        // Arrange
        Expert expert = expertDao.getById(2);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(1);
        int adminUnitGlobalGaulCode = 2;
        DiseaseExtentClass response1 = diseaseExtentClassDao.getByName("PRESENCE");
        DiseaseExtentClass response2 = diseaseExtentClassDao.getByName("ABSENCE");

        AdminUnitReview review1 = createAdminUnitReview(expert, adminUnitGlobalGaulCode, diseaseGroup, response1);
        AdminUnitReview review2 = createAdminUnitReview(expert, adminUnitGlobalGaulCode, diseaseGroup, response2);

        // Act
        adminUnitReviewDao.save(review1);
        adminUnitReviewDao.save(review2);

        // Assert
        assertReviewSaved(review1);
        assertReviewSaved(review2);

        Integer id1 = review1.getId();
        Integer id2 = review2.getId();
        flushAndClear();
        review1 = adminUnitReviewDao.getById(id1);
        review2 = adminUnitReviewDao.getById(id2);
        assertReviewParameters(review1, expert, diseaseGroup, adminUnitGlobalGaulCode, response1);
        assertReviewParameters(review2, expert, diseaseGroup, adminUnitGlobalGaulCode, response2);
    }

    @Test
    public void saveAdminUnitReviewWithNullResponse() {
        // Arrange
        AdminUnitReview review = createAdminUnitReview(new Expert(1), 100, new DiseaseGroup(2), null);

        // Act
        adminUnitReviewDao.save(review);

        // Assert
        assertReviewSaved(review);
    }

    @Test
    public void getLastReviewDateByExpertIdReturnsCorrectDate() {
        // Arrange - no reviews in the database
        AdminUnitReview firstReview = new AdminUnitReview(expertDao.getById(1), 24, null, diseaseGroupDao.getById(87), diseaseExtentClassDao.getByName(DiseaseExtentClass.POSSIBLE_ABSENCE));
        adminUnitReviewDao.save(firstReview);
        flushAndClear();
        AdminUnitReview secondReview = new AdminUnitReview(expertDao.getById(1), 24, null, diseaseGroupDao.getById(87), diseaseExtentClassDao.getByName(DiseaseExtentClass.POSSIBLE_ABSENCE));
        adminUnitReviewDao.save(secondReview);
        flushAndClear();

        // Act
        DateTime actual = adminUnitReviewDao.getLastReviewDateByExpertId(1);

        // Assert
        assertThat(actual).isEqualTo(secondReview.getCreatedDate());
    }

    @Test
    public void getLastReviewDateByExpertIdReturnsNullWhenNoReviews() {
        // Arrange - no reviews in the database

        // Act
        DateTime actual = adminUnitReviewDao.getLastReviewDateByExpertId(1);

        // Assert
        assertThat(actual).isNull();
    }

    @Test
    public void getByDiseaseGroupIdReturnsExpectedListExcludingNullResponses() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(1, 1);
        createAndSaveAdminUnitReview(1, 2);
        AdminUnitReview review2 = createAndSaveAdminUnitReview(2, 1);
        createAndSaveAdminUnitReview(2, 2);
        AdminUnitReview nullReview = createAndSaveAdminUnitReview(2, 1, null);

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByDiseaseGroupId(DISEASE_GROUP_ID);

        // Assert
        assertThat(reviews.size()).isEqualTo(2);
        assertThat(reviews).contains(review);
        assertThat(reviews).contains(review2);
        assertThat(reviews).doesNotContain(nullReview);
    }

    @Test
    public void getByExpertIdAndDiseaseGroupReturnsExpectedListIncludingNullResponses() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);
        AdminUnitReview nullReview = createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID, null);

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(EXPERT_ID, DISEASE_GROUP_ID);

        // Assert
        assertThat(reviews.size()).isEqualTo(2);
        assertThat(reviews).contains(review);
        assertThat(reviews).contains(nullReview);
    }

    @Test
    public void getByExpertIdAndDiseaseGroupReturnsEmptyListForWrongDisease() {
        // Arrange
        createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(EXPERT_ID, 2);

        // Assert
        assertThat(reviews).isEmpty();
    }

    @Test
    public void getCountByExpertIdReturnsExpectedLongIncludingNullResponses() {
        // Arrange
        createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);
        createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID, null);

        // Act
        Long count = adminUnitReviewDao.getCountByExpertId(EXPERT_ID);

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void getCountByExpertIdReturnsZeroForNoReviews() {
        // Act
        Long count = adminUnitReviewDao.getCountByExpertId(1);

        // Assert
        assertThat(count).isEqualTo(0);
    }

    private AdminUnitReview createAdminUnitReview(Expert expert, int adminUnitGlobalGaulCode,
                                                  DiseaseGroup diseaseGroup, DiseaseExtentClass response) {
        AdminUnitReview review = new AdminUnitReview();
        review.setExpert(expert);
        review.setAdminUnitGlobalGaulCode(adminUnitGlobalGaulCode);
        review.setDiseaseGroup(diseaseGroup);
        review.setResponse(response);
        return review;
    }

    private AdminUnitReview createAndSaveAdminUnitReview(Integer expertId, Integer diseaseGroupId, DiseaseExtentClass response) {
        Expert expert = expertDao.getById(expertId);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        int adminUnitGlobalGaulCode = 2;
        AdminUnitReview review = new AdminUnitReview(expert, adminUnitGlobalGaulCode, null, diseaseGroup, response);
        adminUnitReviewDao.save(review);
        return review;
    }

    private AdminUnitReview createAndSaveAdminUnitReview(Integer expertId, Integer diseaseGroupId) {
        DiseaseExtentClass response = diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE);
        return createAndSaveAdminUnitReview(expertId, diseaseGroupId, response);
    }
}
