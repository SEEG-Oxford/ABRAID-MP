package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DistanceToExtentCacheEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DistanceToExtentCacheEntryId;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DistanceToExtentCacheEntryDao.
 * Copyright (c) 2015 University of Oxford
 */
public class DistanceToExtentCacheEntryDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DistanceToExtentCacheEntryDao distanceToExtentCacheEntryDao;

    @Test
    public void saveAndReload() {
        // Arrange
        int expectedDisease = 87;
        int expectedLocation = 6;
        double expectedDistance = -21.24;
        DistanceToExtentCacheEntry entry = new DistanceToExtentCacheEntry(expectedDisease, expectedLocation, expectedDistance);

        // Act
        distanceToExtentCacheEntryDao.save(entry);
        entry = null;
        entry = distanceToExtentCacheEntryDao.getById(new DistanceToExtentCacheEntryId(87, 6));

        // Assert
        assertThat(entry.getDistance()).isEqualTo(expectedDistance);
        assertThat(entry.getId().getDiseaseGroupId()).isEqualTo(expectedDisease);
        assertThat(entry.getId().getLocationId()).isEqualTo(expectedLocation);
    }

    @Test
    public void clearCacheForDisease() {
        // Arrange
        distanceToExtentCacheEntryDao.save(new DistanceToExtentCacheEntry(1, 6, 1));
        distanceToExtentCacheEntryDao.save(new DistanceToExtentCacheEntry(1, 12, 2));
        distanceToExtentCacheEntryDao.save(new DistanceToExtentCacheEntry(1, 22, 3));
        distanceToExtentCacheEntryDao.save(new DistanceToExtentCacheEntry(2, 6, 4));
        distanceToExtentCacheEntryDao.save(new DistanceToExtentCacheEntry(2, 12, 5));
        distanceToExtentCacheEntryDao.save(new DistanceToExtentCacheEntry(2, 22, 6));

        // Act
        distanceToExtentCacheEntryDao.clearCacheForDisease(2);

        // Assert
        assertThat(distanceToExtentCacheEntryDao.getById(new DistanceToExtentCacheEntryId(1, 6))).isNotNull();
        assertThat(distanceToExtentCacheEntryDao.getById(new DistanceToExtentCacheEntryId(2, 12))).isNotNull();
    }
}
