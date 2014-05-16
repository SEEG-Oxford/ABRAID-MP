package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import com.vividsolutions.jts.geom.Point;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;

/**
 * Tests the NativeSQL class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class NativeSQLTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ModelRunDao modelRunDao;

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

    @Test
    public void updateAndReloadMeanPredictionRasterForModelRun() throws IOException {
        // Arrange - create a model run
        ModelRun modelRun = new ModelRun("test name", DateTime.now());
        modelRunDao.save(modelRun);

        // Arrange - load raster file
        String filename = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster.asc";
        byte[] actualGDALRaster = FileUtils.readFileToByteArray(new File(filename));

        // Act - update model run with mean prediction raster
        nativeSQL.updateMeanPredictionRasterForModelRun(modelRun.getId(), actualGDALRaster);

        // Assert - load mean prediction raster from model run and compare for equality (ignoring whitespace)
        byte[] expectedGDALRaster = nativeSQL.loadMeanPredictionRasterForModelRun(modelRun.getId());
        Assert.assertThat(new String(actualGDALRaster), equalToIgnoringWhiteSpace(new String(expectedGDALRaster)));
    }
}
