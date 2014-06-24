package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
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
        assertThat(review.getChangedDate()).isNotNull();

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
    public void updateExistingAdminUnitReviewSetsNewValues() {
        // Arrange
        createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);
        flushAndClear();
        AdminUnitReview review = adminUnitReviewDao.getAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID, 2);
        DateTime createdDate = review.getChangedDate();
        DiseaseExtentClass newResponse = diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE);

        // Act
        review.setResponse(newResponse);
        review.setChangedDate(DateTime.now());
        adminUnitReviewDao.save(review);
        flushAndClear();

        // Assert
        AdminUnitReview result = adminUnitReviewDao.getAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID, 2);
        assertThat(result.getResponse()).isEqualTo(newResponse);
        assertThat(result.getChangedDate().isAfter(createdDate)).isTrue();
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
    public void getAdminUnitReviewReturnsExpectedReview() {
        // Arrange
        AdminUnitReview review = createAndSaveAdminUnitReview(EXPERT_ID, DISEASE_GROUP_ID);
        flushAndClear();
        // Act
        AdminUnitReview result = adminUnitReviewDao.getAdminUnitReview(review.getExpert().getId(),
                review.getDiseaseGroup().getId(), review.getAdminUnitGlobalOrTropicalGaulCode());
        // Assert
        assertThatExpertIsEqual(result.getExpert(), review.getExpert());
        assertThat(result.getDiseaseGroup()).isEqualTo(review.getDiseaseGroup());
        assertThat(result.getAdminUnitGlobalOrTropicalGaulCode()).isEqualTo(review.getAdminUnitGlobalOrTropicalGaulCode());
        assertThat(result.getResponse()).isEqualTo(review.getResponse());
    }

    private void assertThatExpertIsEqual(Expert expert1, Expert expert2) {
        assertThat(expert1.getId()).isEqualTo(expert2.getId());
        assertThat(expert1.getEmail()).isEqualTo(expert2.getEmail());
        assertThat(expert1.getName()).isEqualTo(expert2.getName());
        assertThat(expert1.getWeighting()).isEqualTo(expert2.getWeighting());
        assertThat(expert1.getPassword()).isEqualTo(expert2.getPassword());
    }

    @Test
    public void getAdminUnitReviewReturnsNullIfReviewDoesNotExist() {
        // Act
        AdminUnitReview result = adminUnitReviewDao.getAdminUnitReview(0, 0, 0);
        // Assert
        assertThat(result).isNull();
    }

    private AdminUnitReview createAdminUnitReview(Expert expert, int adminUnitGlobalGaulCode,
                                                  DiseaseGroup diseaseGroup, DiseaseExtentClass response) {
        AdminUnitReview review = new AdminUnitReview();
        review.setExpert(expert);
        review.setAdminUnitGlobalGaulCode(adminUnitGlobalGaulCode);
        review.setDiseaseGroup(diseaseGroup);
        review.setResponse(response);
        review.setChangedDate(DateTime.now());
        return review;
    }

    private AdminUnitReview createAndSaveAdminUnitReview(Integer expertId, Integer diseaseGroupId) {
        Expert expert = expertDao.getById(expertId);
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        int adminUnitGlobalGaulCode = 2;
        DiseaseExtentClass response = diseaseExtentClassDao.getByName(DiseaseExtentClass.PRESENCE);
        AdminUnitReview review = new AdminUnitReview(expert, adminUnitGlobalGaulCode, null, diseaseGroup, response);
        adminUnitReviewDao.save(review);
        return review;
    }
}
