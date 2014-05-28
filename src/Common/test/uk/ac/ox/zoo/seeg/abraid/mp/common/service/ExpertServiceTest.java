package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class ExpertServiceTest extends AbstractCommonSpringUnitTests {
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
    public void getDiseaseOccurrenceReviewCountReturnsExpectedLong() {
        // Arrange
        List<DiseaseOccurrenceReview> reviews = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            reviews.add(new DiseaseOccurrenceReview());
        }
        Long n = (long) reviews.size();
        when(diseaseOccurrenceReviewDao.getCountByExpertId(anyInt())).thenReturn(n);

        // Act
        Long reviewCount = expertService.getDiseaseOccurrenceReviewCount(1);

        // Assert
        assertThat(reviewCount).isEqualTo(n);
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnExpectedList() {
        // Arrange
        List<DiseaseOccurrence> testList = new ArrayList<>();
        when(expertDao.getById(anyInt())).thenReturn(new Expert());
        when(validatorDiseaseGroupDao.getById(anyInt())).thenReturn(new ValidatorDiseaseGroup());
        when(diseaseOccurrenceDao.getDiseaseOccurrencesYetToBeReviewedByExpert(anyInt(), anyInt())).thenReturn(testList);

        // Act
        List<DiseaseOccurrence> list = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(1, 1);

        // Assert
        assertThat(list).isSameAs(testList);
    }


    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnEmptyListIfExpertDoesNotExist() {
        // Arrange
        when(expertDao.getById(anyInt())).thenReturn(null); // For any expertId, act as if the expert does not exist

        // Act
        List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(0, 0);

        // Assert
        assertThat(occurrences.size()).isEqualTo(0);
        assertThat(occurrences.isEmpty());
    }

    @Test
    public void getDiseaseOccurrencesYetToBeReviewedByExpertMustReturnEmptyListIfDiseaseGroupDoesNotExist() {
        // Arrange
        when(diseaseGroupDao.getById(anyInt())).thenReturn(null); // For any diseaseGroupId, act as if the group does not exist

        // Act
        List<DiseaseOccurrence> occurrences = expertService.getDiseaseOccurrencesYetToBeReviewedByExpert(0, 0);

        // Assert
        assertThat(occurrences.isEmpty());
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
