package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

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
        assertThat(review.getId()).isNotNull();
        assertThat(review.getCreatedDate()).isNotNull();

        Integer id = review.getId();
        flushAndClear();
        review = adminUnitReviewDao.getById(id);
        assertThat(review).isNotNull();
        assertThat(review.getExpert().getEmail()).isEqualTo(expert.getEmail());
        assertThat(review.getExpert().getValidatorDiseaseGroups()).containsAll(expert.getValidatorDiseaseGroups());
        assertThat(review.getDiseaseGroup()).isEqualTo(diseaseGroup);
        assertThat(review.getAdminUnitGlobalGaulCode()).isEqualTo(adminUnitGlobalGaulCode);
        assertThat(review.getAdminUnitTropicalGaulCode()).isNull();
        assertThat(review.getResponse()).isEqualTo(response);
    }

    @Test
    public void getByExpertIdReturnsExpectedList() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertId(1);

        // Assert
        assertThat(reviews.size()).isEqualTo(1);
        assertThat(reviews).contains(review);
    }

    @Test
    public void getByExpertIdReturnsEmptyListForNoReviews() {
        // Arrange

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertId(EXPERT_ID);

        // Assert
        assertThat(reviews.size()).isEqualTo(0);
    }

    @Test
    public void getByDiseaseGroupIdReturnsExpectedList() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(1, 1);
        createAndSaveAdminUnitReview(1, 2);
        AdminUnitReview review2 = createAndSaveAdminUnitReview(2, 1);
        createAndSaveAdminUnitReview(2, 2);

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByDiseaseGroupId(DISEASE_GROUP_ID);

        // Assert
        assertThat(reviews.size()).isEqualTo(2);
        assertThat(reviews).contains(review);
        assertThat(reviews).contains(review2);
    }

    @Test
    public void getByExpertIdAndDiseaseGroupReturnsExpectedList() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(EXPERT_ID, DISEASE_GROUP_ID);

        // Assert
        assertThat(reviews.size()).isEqualTo(1);
        assertThat(reviews).contains(review);
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
    public void getCountByExpertIdReturnsExpectedLong() {
        // Arrange
        createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);

        // Act
        Long count = adminUnitReviewDao.getCountByExpertId(1);

        // Assert
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void doesAdminUnitReviewExistReturnsTrueWhenExpected() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);
        // Act
        boolean result = adminUnitReviewDao.doesAdminUnitReviewExist(review.getExpert().getId(),
                review.getDiseaseGroup().getId(), review.getAdminUnitGlobalOrTropicalGaulCode());
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    public void doesAdminUnitReviewExistReturnsFalseWhenExpected() {
        // Act
        boolean result = adminUnitReviewDao.doesAdminUnitReviewExist(0, 0, 0);
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    public void doesAdminUnitReviewExistReturnsFalseForNullInput() {
        // Act
        boolean result = adminUnitReviewDao.doesAdminUnitReviewExist(0, null, 0);
        // Assert
        assertThat(result).isFalse();
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

    private AdminUnitReview createAndSaveAdminUnitReview(Integer expertId, Integer diseaseGroupId) {
        Expert expert = expertDao.getById(expertId);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        int adminUnitGlobalGaulCode = 2;
        DiseaseExtentClass response = new DiseaseExtentClass(DiseaseExtentClass.PRESENCE);
        AdminUnitReview review = createAdminUnitReview(expert, adminUnitGlobalGaulCode, diseaseGroup, response);
        adminUnitReviewDao.save(review);
        return review;
    }
}
