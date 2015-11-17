package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for HealthMapService.
 * Copyright (c) 2015 University of Oxford
 */
public class HealthMapServiceTest {
    private HealthMapDiseaseDao healthMapDiseaseDao;
    private HealthMapSubDiseaseDao healthMapSubDiseaseDao;
    private HealthMapService healthMapService;

    @Before
    public void setUp() {
        healthMapDiseaseDao = mock(HealthMapDiseaseDao.class);
        healthMapSubDiseaseDao = mock(HealthMapSubDiseaseDao.class);
        healthMapService = new HealthMapServiceImpl(healthMapDiseaseDao, healthMapSubDiseaseDao);
    }

    @Test
    public void getHealthMapDiseaseById() {
        // Arrange
        HealthMapDisease disease = new HealthMapDisease();
        when(healthMapDiseaseDao.getById(123)).thenReturn(disease);

        // Act
        HealthMapDisease testDisease = healthMapService.getHealthMapDiseasesById(123);

        // Assert
        assertThat(testDisease).isSameAs(disease);
    }

    @Test
    public void getHealthMapSubDiseaseById() {
        // Arrange
        HealthMapSubDisease disease = new HealthMapSubDisease();
        when(healthMapSubDiseaseDao.getById(123)).thenReturn(disease);

        // Act
        HealthMapSubDisease testDisease = healthMapService.getHealthMapSubDiseasesById(123);

        // Assert
        assertThat(testDisease).isSameAs(disease);
    }

    @Test
    public void getAllHealthMapDiseases() {
        // Arrange
        List<HealthMapDisease> diseases = Arrays.asList(new HealthMapDisease());
        when(healthMapDiseaseDao.getAll()).thenReturn(diseases);

        // Act
        List<HealthMapDisease> testDiseases = healthMapService.getAllHealthMapDiseases();

        // Assert
        assertThat(testDiseases).isSameAs(diseases);
    }

    @Test
    public void getAllHealthMapSubDiseases() {
        // Arrange
        List<HealthMapSubDisease> subDiseases = Arrays.asList(new HealthMapSubDisease());
        when(healthMapSubDiseaseDao.getAll()).thenReturn(subDiseases);

        // Act
        List<HealthMapSubDisease> testSubDiseases = healthMapService.getAllHealthMapSubDiseases();

        // Assert
        assertThat(testSubDiseases).isSameAs(subDiseases);
    }

    @Test
    public void saveHealthMapDisease() {
        HealthMapDisease disease = new HealthMapDisease();
        healthMapService.saveHealthMapDisease(disease);
        verify(healthMapDiseaseDao).save(eq(disease));
    }

    @Test
    public void saveHealthMapSubDisease() {
        HealthMapSubDisease disease = new HealthMapSubDisease();
        healthMapService.saveHealthMapSubDisease(disease);
        verify(healthMapSubDiseaseDao).save(eq(disease));
    }
}
