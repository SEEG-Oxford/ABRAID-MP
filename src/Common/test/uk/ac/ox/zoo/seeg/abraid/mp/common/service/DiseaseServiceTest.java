package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringUnitTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the DiseaseService class.
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseServiceTest extends AbstractSpringUnitTests {
    @Autowired
    private DiseaseService diseaseService;

    @Test
    public void saveDiseaseOccurrence() {
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        diseaseService.saveDiseaseOccurrence(occurrence);
        verify(diseaseOccurrenceDao).save(eq(occurrence));
    }

    @Test
    public void saveHealthMapDisease() {
        HealthMapDisease disease = new HealthMapDisease();
        diseaseService.saveHealthMapDisease(disease);
        verify(healthMapDiseaseDao).save(eq(disease));
    }

    @Test
    public void getAllHealthMapDiseases() {
        // Arrange
        List<HealthMapDisease> diseases = Arrays.asList(new HealthMapDisease());
        when(healthMapDiseaseDao.getAll()).thenReturn(diseases);

        // Act
        List<HealthMapDisease> testDiseases = diseaseService.getAllHealthMapDiseases();

        // Assert
        assertThat(testDiseases).isSameAs(diseases);
    }

    @Test
    public void getAllDiseaseGroups() {
        // Arrange
        List<DiseaseGroup> diseaseGroups = Arrays.asList(new DiseaseGroup());
        when(diseaseGroupDao.getAll()).thenReturn(diseaseGroups);

        // Act
        List<DiseaseGroup> testDiseaseGroups = diseaseService.getAllDiseaseGroups();

        // Assert
        assertThat(testDiseaseGroups).isSameAs(diseaseGroups);
    }

    @Test
    public void diseaseOccurrenceExists() {
        // Arrange
        Alert alert = new Alert(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Location location = new Location(1);
        DateTime occurrenceDate = DateTime.now();

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(alert);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setOccurrenceDate(occurrenceDate);

        DiseaseOccurrence returnedOccurrence = new DiseaseOccurrence();
        List<DiseaseOccurrence> occurrences = Arrays.asList(returnedOccurrence);
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(diseaseGroup, location, alert,
                occurrenceDate)).thenReturn(occurrences);

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isTrue();
    }

    @Test
    public void diseaseOccurrenceDoesNotExist() {
        // Arrange
        Alert alert = new Alert(1);
        DiseaseGroup diseaseGroup = new DiseaseGroup(1);
        Location location = new Location(1);
        DateTime occurrenceDate = DateTime.now();

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(alert);
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setOccurrenceDate(occurrenceDate);

        List<DiseaseOccurrence> occurrences = new ArrayList<>();
        when(diseaseOccurrenceDao.getDiseaseOccurrencesForExistenceCheck(diseaseGroup, location, alert,
                occurrenceDate)).thenReturn(occurrences);

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseAlertIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(null);
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseDiseaseGroupIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(null);
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseLocationIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(null);
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseAlertIdIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert());
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseDiseaseGroupIdIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(new DiseaseGroup());
        occurrence.setLocation(new Location(1));
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void diseaseOccurrenceDoesNotExistBecauseLocationIdIsNull() {
        // Arrange
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setAlert(new Alert(1));
        occurrence.setDiseaseGroup(new DiseaseGroup(1));
        occurrence.setLocation(new Location());
        occurrence.setOccurrenceDate(DateTime.now());

        // Act
        boolean doesDiseaseOccurrenceExist = diseaseService.doesDiseaseOccurrenceExist(occurrence);

        // Assert
        assertThat(doesDiseaseOccurrenceExist).isFalse();
    }

    @Test
    public void doesDiseaseOccurrenceMatchDiseaseGroupReturnsExpectedResult() {
        // Arrange
        int validatorDiseaseGroupId = 1;
        ValidatorDiseaseGroup validatorDiseaseGroup = new ValidatorDiseaseGroup(validatorDiseaseGroupId);

        int diseaseGroupId = 1;
        DiseaseGroup diseaseGroup = new DiseaseGroup(diseaseGroupId);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);

        int occurrenceId = 2;
        DiseaseOccurrence occurrence = new DiseaseOccurrence(occurrenceId);
        occurrence.setDiseaseGroup(diseaseGroup);
        when(diseaseOccurrenceDao.getById(occurrenceId)).thenReturn(occurrence);

        // Act
        boolean diseaseOccurrenceMatches =
                diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(occurrenceId,
                        validatorDiseaseGroupId);
        boolean diseaseOccurrenceDoesNotMatch =
                diseaseService.doesDiseaseOccurrenceDiseaseGroupBelongToValidatorDiseaseGroup(occurrenceId, 3);

        // Assert
        assertThat(diseaseOccurrenceMatches).isTrue();
        assertThat(diseaseOccurrenceDoesNotMatch).isFalse();
    }
}
