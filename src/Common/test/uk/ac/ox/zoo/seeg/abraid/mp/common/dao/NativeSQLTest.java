package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the NativeSQL class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private NativeSQLImpl nativeSQL;

    @Test
    public void findAdminUnitGlobalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(826);
    }

    @Test
    public void findAdminUnitGlobalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, null);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitGlobalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitGlobalWherePointIsNotInTheRequestedAdminLevel() {
        // This point is within British Columbia, which is an admin1
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitGlobalThatContainsPoint(point, '0');
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitTropicalThatContainsPoint() {
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(825);
    }

    @Test
    public void findAdminUnitTropicalWherePointIsOnBorder() {
        Point point = GeometryUtils.createPoint(172, -42);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, null);
        assertThat(gaulCode).isEqualTo(179);
    }

    @Test
    public void findAdminUnitTropicalThatContainsPointReturnsNullIfNoGaulCodesContainThePoint() {
        Point point = GeometryUtils.createPoint(0, 0);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, null);
        assertThat(gaulCode).isNull();
    }

    @Test
    public void findAdminUnitTropicalWherePointIsNotInTheRequestedAdminLevel() {
        // This point is within British Columbia, which is an admin1
        Point point = GeometryUtils.createPoint(-124.2, 54.1);
        Integer gaulCode = nativeSQL.findAdminUnitTropicalThatContainsPoint(point, '0');
        assertThat(gaulCode).isNull();
    }
}
