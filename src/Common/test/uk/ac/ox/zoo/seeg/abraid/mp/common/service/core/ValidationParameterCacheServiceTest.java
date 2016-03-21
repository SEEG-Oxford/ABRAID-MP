package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DistanceToExtentCacheEntryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.EnvironmentalSuitabilityCacheEntryDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DistanceToExtentCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.EnvironmentalSuitabilityCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidationParameterCacheEntryId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ValidationParameterCacheService.
 * Copyright (c) 2015 University of Oxford
 */
public class ValidationParameterCacheServiceTest {
    private DistanceToExtentCacheEntryDao distanceToExtentCacheEntryDao;
    private EnvironmentalSuitabilityCacheEntryDao environmentalSuitabilityCacheEntryDao;
    private ValidationParameterCacheService validationParameterCacheService;

    @Before
    public void setUp() {
        environmentalSuitabilityCacheEntryDao = mock(EnvironmentalSuitabilityCacheEntryDao.class);
        distanceToExtentCacheEntryDao = mock(DistanceToExtentCacheEntryDao.class);
        validationParameterCacheService = new ValidationParameterCacheServiceImpl(distanceToExtentCacheEntryDao, environmentalSuitabilityCacheEntryDao);
    }

    @Test
    public void getDistanceToExtentFromCacheReturnsCorrectValue() {
        // Arrange
        DistanceToExtentCacheEntry mock = mock(DistanceToExtentCacheEntry.class);
        when(mock.getDistance()).thenReturn(3d);
        when(distanceToExtentCacheEntryDao.getById(eq(new ValidationParameterCacheEntryId(1, 2)))).thenReturn(mock);

        // Act
        Double result = validationParameterCacheService.getDistanceToExtentFromCache(1, 2);

        // Assert
        assertThat(result).isEqualTo(3d);
    }

    @Test
    public void getDistanceToExtentFromCacheReturnsCorrectValueIfNull() {
        // Arrange
        when(distanceToExtentCacheEntryDao.getById(eq(new ValidationParameterCacheEntryId(1, 2)))).thenReturn(null);

        // Act
        Double result = validationParameterCacheService.getDistanceToExtentFromCache(1, 2);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    public void clearDistanceToExtentCacheForDiseaseCallsDao() {
        // Act
        validationParameterCacheService.clearDistanceToExtentCacheForDisease(1);

        // Assert
        verify(distanceToExtentCacheEntryDao).clearCacheForDisease(1);
    }

    @Test
    public void saveDistanceToExtentCacheEntryCallsDao() {
        // Act
        validationParameterCacheService.saveDistanceToExtentCacheEntry(1, 2, 3d);

        // Assert
        verify(distanceToExtentCacheEntryDao).save(eq(new DistanceToExtentCacheEntry(1, 2, 3d)));
    }

    @Test
    public void getEnvironmentalSuitabilityFromCacheReturnsCorrectValue() {
        // Arrange
        EnvironmentalSuitabilityCacheEntry mock = mock(EnvironmentalSuitabilityCacheEntry.class);
        when(mock.getEnvironmentalSuitability()).thenReturn(3d);
        when(environmentalSuitabilityCacheEntryDao.getById(eq(new ValidationParameterCacheEntryId(1, 2)))).thenReturn(mock);

        // Act
        Double result = validationParameterCacheService.getEnvironmentalSuitabilityFromCache(1, 2);

        // Assert
        assertThat(result).isEqualTo(3d);
    }

    @Test
    public void getEnvironmentalSuitabilityFromCacheReturnsCorrectValueIfNull() {
        // Arrange
        when(environmentalSuitabilityCacheEntryDao.getById(eq(new ValidationParameterCacheEntryId(1, 2)))).thenReturn(null);

        // Act
        Double result = validationParameterCacheService.getEnvironmentalSuitabilityFromCache(1, 2);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    public void clearEnvironmentalSuitabilityCacheForDiseaseCallsDao() {
        // Act
        validationParameterCacheService.clearEnvironmentalSuitabilityCacheForDisease(1);

        // Assert
        verify(environmentalSuitabilityCacheEntryDao).clearCacheForDisease(1);
    }

    @Test
    public void saveEnvironmentalSuitabilityCacheEntryCallsDao() {
        // Act
        validationParameterCacheService.saveEnvironmentalSuitabilityCacheEntry(1, 2, 3d);

        // Assert
        verify(environmentalSuitabilityCacheEntryDao).save(eq(new EnvironmentalSuitabilityCacheEntry(1, 2, 3d)));
    }
}
