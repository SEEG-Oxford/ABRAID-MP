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
    private AdminUnitGlobalDao adminUnitGlobalDao;

    @Autowired
    private AdminUnitReviewDao adminUnitReviewDao;

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
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(2);
        DiseaseExtentClass response = DiseaseExtentClass.PRESENCE;

        AdminUnitReview review = createAdminUnitReview(expert, adminUnitGlobal, diseaseGroup, response);

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
        assertThat(review.getAdminUnitGlobal()).isEqualTo(adminUnitGlobal);
        assertThat(review.getAdminUnitTropical()).isNull();
        assertThat(review.getResponse()).isEqualTo(response);
    }

    @Test
    public void getByExpertIdReturnsExpectedList() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview();

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
    public void getByExpertIdAndDiseaseGroupReturnsExpectedList() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview();

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(EXPERT_ID, DISEASE_GROUP_ID);

        // Assert
        assertThat(reviews.size()).isEqualTo(1);
        assertThat(reviews).contains(review);
    }

    @Test
    public void getByExpertIdAndDiseaseGroupReturnsEmptyListForWrongDisease() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview();

        // Act
        List<AdminUnitReview> reviews = adminUnitReviewDao.getByExpertIdAndDiseaseGroupId(EXPERT_ID, 2);

        // Assert
        assertThat(reviews).isEmpty();
    }

    @Test
    public void getCountByExpertIdReturnsExpectedLong() {
        // Arrange
        createAndSaveAdminUnitReview();

        // Act
        Long count = adminUnitReviewDao.getCountByExpertId(1);

        // Assert
        assertThat(count).isEqualTo(1);
    }

    private AdminUnitReview createAdminUnitReview(Expert expert, AdminUnitGlobal adminUnitGlobal,
                                                  DiseaseGroup diseaseGroup, DiseaseExtentClass response) {
        AdminUnitReview review = new AdminUnitReview();
        review.setExpert(expert);
        review.setAdminUnitGlobal(adminUnitGlobal);
        review.setDiseaseGroup(diseaseGroup);
        review.setResponse(response);
        return review;
    }

    private AdminUnitReview createAndSaveAdminUnitReview() {
        Expert expert = expertDao.getById(EXPERT_ID);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(DISEASE_GROUP_ID);
        AdminUnitGlobal adminUnitGlobal = adminUnitGlobalDao.getByGaulCode(2);
        DiseaseExtentClass response = DiseaseExtentClass.PRESENCE;
        AdminUnitReview review = createAdminUnitReview(expert, adminUnitGlobal, diseaseGroup, response);
        adminUnitReviewDao.save(review);
        return review;
    }
}
