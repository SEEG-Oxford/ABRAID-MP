package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringUnitTests;

import java.util.*;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the ExpertService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertServiceTest extends AbstractSpringUnitTests {
    @Autowired
    private ExpertService expertService;

    @Test
    public void getDiseaseInterestsReturnsExpectedList() {
        // Arrange
        int expertId = 1;
        Expert expert = new Expert();
        ValidatorDiseaseGroup group1 = new ValidatorDiseaseGroup();
        ValidatorDiseaseGroup group2 = new ValidatorDiseaseGroup();

        List<ValidatorDiseaseGroup> testList = new ArrayList<>();
        testList.add(group2);
        testList.add(group1);

        expert.setValidatorDiseaseGroups(testList);
        when(expertDao.getById(expertId)).thenReturn(expert);

        // Act
        List<ValidatorDiseaseGroup> list = expertService.getDiseaseInterests(expertId);

        // Assert
        assertThat(list).isEqualTo(testList);
    }

    @Test
    public void getDiseaseOccurrenceReviewCountReturnsExpectedInteger() {
        // Arrange
        int n = 2;
        List<DiseaseOccurrenceReview> reviews = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            reviews.add(new DiseaseOccurrenceReview());
        }
        when(diseaseOccurrenceReviewDao.getByExpertId(anyInt())).thenReturn(reviews);

        // Act
        Integer reviewCount = expertService.getDiseaseOccurrenceReviewCount(1);

        // Assert
        assertThat(reviewCount).isEqualTo(n);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustReturnExpectedList() {
        // Arrange
        List<DiseaseOccurrence> testList = new ArrayList<>();
        when(expertDao.getById(anyInt())).thenReturn(new Expert());
        when(validatorDiseaseGroupDao.getById(anyInt())).thenReturn(new ValidatorDiseaseGroup());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewed(anyInt(), anyInt())).thenReturn(testList);

        // Act
        List<DiseaseOccurrence> list = expertService.getDiseaseOccurrencesYetToBeReviewed(1, 1);

        // Assert
        assertThat(list).isSameAs(testList);
    }


    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustThrowExceptionIfExpertDoesNotExist() {
        // Arrange
        when(expertDao.getById(anyInt())).thenReturn(null); // For any expertId, act as if the expert does not exist

        // Act
        catchException(expertService).getDiseaseOccurrencesYetToBeReviewed(0, 0);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedMustThrowExceptionIfValidatorDiseaseGroupDoesNotExist() {
        // Arrange - For any validatorDiseaseGroupId, act as if the group does not exist
        when(validatorDiseaseGroupDao.getById(anyInt())).thenReturn(null);

        // Act
        catchException(expertService).getDiseaseOccurrencesYetToBeReviewed(0, 0);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getDiseaseOccurrenceReviewCountPerValidatorDiseaseGroupWithNoReviewsMustReturnInitialMap() {
        // Arrange
        int expertId = 1;
        Expert expert = new Expert();
        setDiseaseInterests(expert);
        when(expertDao.getById(expertId)).thenReturn(expert);

        List<ValidatorDiseaseGroup> diseaseInterests = expert.getValidatorDiseaseGroups();
        Set<String> diseaseInterestsNames = new HashSet<>();
        for (ValidatorDiseaseGroup diseaseGroup : diseaseInterests) {
            diseaseInterestsNames.add(diseaseGroup.getName());
        }

        // Act
        Map<String, Integer> map = expertService.getDiseaseOccurrenceReviewCountPerValidatorDiseaseGroup(expertId,
                diseaseInterests);

        // Assert
        assertThat(map.keySet()).isEqualTo(diseaseInterestsNames);
        assertThat(map.values()).containsOnly(0);
    }

    @Test
    public void getDiseaseOccurrenceCountPerValidatorDiseaseGroupWithNoOccurrencesMustReturnInitialMap() {
        // Arrange
        ValidatorDiseaseGroup group1 = new ValidatorDiseaseGroup();
        group1.setName("Group1");
        ValidatorDiseaseGroup group2 = new ValidatorDiseaseGroup();
        group2.setName("Group2");
        List<ValidatorDiseaseGroup> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);
        Set<String> names = new HashSet<>();
        for (ValidatorDiseaseGroup diseaseGroup : groups) {
            names.add(diseaseGroup.getName());
        }

        // Act
        Map<String, Integer> map = expertService.getDiseaseOccurrenceCountPerValidatorDiseaseGroup(groups);

        // Assert
        assertThat(map.keySet()).isEqualTo(names);
        assertThat(map.values()).containsOnly(0);
    }

    private void setDiseaseInterests(Expert expert) {
        ValidatorDiseaseGroup group1 = new ValidatorDiseaseGroup();
        group1.setName("Group1");
        ValidatorDiseaseGroup group2 = new ValidatorDiseaseGroup();
        group2.setName("Group2");
        List<ValidatorDiseaseGroup> groups = new ArrayList<>();
        groups.add(group1);
        groups.add(group2);
        expert.setValidatorDiseaseGroups(groups);
    }

    @Test
    public void getAllExperts() {
        // Arrange
        List<Expert> experts = Arrays.asList(new Expert());
        when(expertDao.getAll()).thenReturn(experts);

        // Act
        List<Expert> testExperts = expertService.getAllExperts();

        // Assert
        assertThat(testExperts).isSameAs(experts);
    }

    @Test
    public void getExpertByEmail() {
        // Arrange
        String email = "test@test.com";
        Expert expert = new Expert();
        when(expertDao.getByEmail(email)).thenReturn(expert);

        // Act
        Expert testExpert = expertService.getExpertByEmail(email);

        // Assert
        assertThat(testExpert).isSameAs(expert);
    }

    @Test
    public void doesDiseaseOccurrenceReviewExistReturnsExpectedResult() {
        // Arrange
        when(diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(1, 1)).thenReturn(true);
        when(diseaseOccurrenceReviewDao.doesDiseaseOccurrenceReviewExist(2, 2)).thenReturn(false);

        // Act
        boolean result1 = expertService.doesDiseaseOccurrenceReviewExist(1, 1);
        boolean result2 = expertService.doesDiseaseOccurrenceReviewExist(2, 2);

        // Assert
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    public void saveDiseaseOccurrenceReview() {
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview();
        review.setExpert(new Expert());
        review.setDiseaseOccurrence(new DiseaseOccurrence());
        review.setResponse(DiseaseOccurrenceReviewResponse.YES);
        diseaseOccurrenceReviewDao.save(review);
        verify(diseaseOccurrenceReviewDao).save(eq(review));
    }

    @Test
    public void saveExpert() {
        Expert expert = new Expert();
        expertService.saveExpert(expert);
        verify(expertDao).save(eq(expert));
    }
}
