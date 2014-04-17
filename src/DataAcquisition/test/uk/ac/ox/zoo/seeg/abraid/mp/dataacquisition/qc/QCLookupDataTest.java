package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the QCLookupData class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class QCLookupDataTest {
    @Test
    public void getAdminUnits() {
        // Arrange
        LocationService locationService = mock(LocationService.class);
        List<AdminUnit> expectedAdminUnits = new ArrayList<>();
        when(locationService.getAllAdminUnits()).thenReturn(expectedAdminUnits);

        // Act
        QCLookupData lookupData = new QCLookupData(locationService);
        List<AdminUnit> actualAdminUnits = lookupData.getAdminUnits();

        // Assert
        assertThat(actualAdminUnits).isEqualTo(expectedAdminUnits);
    }
}
