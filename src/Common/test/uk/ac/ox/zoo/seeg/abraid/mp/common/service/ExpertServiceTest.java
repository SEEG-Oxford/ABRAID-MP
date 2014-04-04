package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
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
    public void getDiseaseInterestsReturnsExpectedSet() {
        // Arrange
        Set<DiseaseGroup> testSet = new HashSet<>();
        Expert expert = new Expert();
        expert.setDiseaseGroups(testSet);
        when(expertDao.getById(anyInt())).thenReturn(expert);

        // Act
        Set<DiseaseGroup> set = expertService.getDiseaseInterests(1);

        // Assert
        assertThat(set).isEqualTo(testSet);
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
        when(diseaseGroupDao.getById(anyInt())).thenReturn(new DiseaseGroup());
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
    public void getDiseaseOccurrencesYetToBeReviewedMustThrowExceptionIfDiseaseGroupDoesNotExist() {
        // Arrange
        when(diseaseGroupDao.getById(anyInt())).thenReturn(null); // For any diseaseGroupId, act as if the group does not exist

        // Act
        catchException(expertService).getDiseaseOccurrencesYetToBeReviewed(0, 0);

        // Assert
        assertThat(caughtException()).isInstanceOf(IllegalArgumentException.class);
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
    public void saveExpert() {
        Expert expert = new Expert();
        expertService.saveExpert(expert);
        verify(expertDao).save(eq(expert));
    }
}
