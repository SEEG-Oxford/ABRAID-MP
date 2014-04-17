package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.qc;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnit;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.LandSeaBorder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Test
    public void getLandSeaBorders() {
        // Arrange
        LocationService locationService = mock(LocationService.class);
        MultiPolygon expectedMultiPolygon = GeometryUtils.createMultiPolygon(
                new Polygon[] {getTriangle(), getSquare()});
        when(locationService.getAllLandSeaBorders()).thenReturn(getLandSeaBorderList());

        // Act
        QCLookupData lookupData = new QCLookupData(locationService);
        MultiPolygon actualMultiPolygon = lookupData.getLandSeaBorders();

        // Assert
        assertThat(actualMultiPolygon.equalsExact(expectedMultiPolygon));
    }

    private List<LandSeaBorder> getLandSeaBorderList() {
        return Arrays.asList(
                new LandSeaBorder(GeometryUtils.createMultiPolygon(new Polygon[] {getTriangle()})),
                new LandSeaBorder(GeometryUtils.createMultiPolygon(new Polygon[] {getSquare()}))
        );
    }

    private Polygon getTriangle() {
        return GeometryUtils.createPolygon(1, 1, 3, 2, 2, 3, 1, 1);
    }

    private Polygon getSquare() {
        return GeometryUtils.createPolygon(10, 10, 10, 20, 20, 20, 20, 10, 10, 10);
    }
}
