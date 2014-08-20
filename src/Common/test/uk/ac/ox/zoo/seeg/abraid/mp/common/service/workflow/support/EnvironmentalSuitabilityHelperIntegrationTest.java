package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.apache.commons.io.FileUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

/**
 * Integration tests for the EnvironmentalSuitabilityHelper class.
 * Copyright (c) 2014 University of Oxford
 */
public class EnvironmentalSuitabilityHelperIntegrationTest extends AbstractCommonSpringIntegrationTests {
    // Parameters taken from the test raster files
    private static final String LARGE_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_large_double.tif";
    private static final double LARGE_RASTER_COLUMNS = 720;
    private static final double LARGE_RASTER_ROWS = 240;
    private static final double LARGE_RASTER_XLLCORNER = -180;
    private static final double LARGE_RASTER_YLLCORNER = -60;
    private static final double LARGE_RASTER_CELLSIZE = 0.5;

    private DiseaseGroup diseaseGroup;

    @Autowired
    private EnvironmentalSuitabilityHelper helper;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ModelRunService modelRunService;

    @Before
    public void setUp() {
        diseaseGroup = diseaseService.getDiseaseGroupById(87);
    }

    @Test
    public void getLatestMeanPredictionRasterReturnsNullIfNoRelevantModelRunsForThisDiseaseGroup() throws Exception {
        // Arrange - 3 irrelevant model runs
        ModelRun modelRun1 = createAndSaveModelRun("failed with a raster", diseaseGroup.getId(), ModelRunStatus.FAILED);
        updateRasterForModelRun(modelRun1);
        createAndSaveModelRun("completed without a raster (for some reason)", diseaseGroup.getId(), ModelRunStatus.COMPLETED);
        ModelRun modelRun2 = createAndSaveModelRun("different disease group", 1, ModelRunStatus.COMPLETED);
        updateRasterForModelRun(modelRun2);

        // Act
        GridCoverage2D meanPredictionRaster = helper.getLatestMeanPredictionRaster(diseaseGroup);

        // Assert
        assertThat(meanPredictionRaster).isNull();
    }

    @Test
    public void findEnvironmentalSuitabilityLowerLeftCornerUsingGeoTools() throws Exception {
        findEnvironmentalSuitabilityLowerLeftCorner(true);
    }

    @Test
    public void findEnvironmentalSuitabilityLowerLeftCornerUsingPostGIS() throws Exception {
        findEnvironmentalSuitabilityLowerLeftCorner(false);
    }

    private void findEnvironmentalSuitabilityLowerLeftCorner(boolean useRaster) throws Exception {
        findEnvironmentalSuitability(useRaster, LARGE_RASTER_XLLCORNER, LARGE_RASTER_YLLCORNER, 0.89);
    }

    @Test
    public void findEnvironmentalSuitabilityUpperRightCornerUsingGeoTools() throws Exception {
        findEnvironmentalSuitabilityUpperRightCorner(true);
    }

    @Test
    public void findEnvironmentalSuitabilityUpperRightCornerUsingPostGIS() throws Exception {
        findEnvironmentalSuitabilityUpperRightCorner(false);
    }

    private void findEnvironmentalSuitabilityUpperRightCorner(boolean useRaster) throws Exception {
        double upperRightCornerX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_COLUMNS - 1) * LARGE_RASTER_CELLSIZE;
        double upperRightCornerY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 1) * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitability(useRaster, upperRightCornerX, upperRightCornerY, 0.79);
    }

    @Test
    public void findEnvironmentalSuitabilityInterpolatedUsingGeoTools() throws Exception {
        findEnvironmentalSuitabilityInterpolated(true);
    }

    @Test
    public void findEnvironmentalSuitabilityInterpolatedUsingPostGIS() throws Exception {
        findEnvironmentalSuitabilityInterpolated(false);
    }

    private void findEnvironmentalSuitabilityInterpolated(boolean useRaster) throws Exception {
        double lowerLeftCornerSlightlyShiftedX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        double lowerLeftCornerSlightlyShiftedY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        findEnvironmentalSuitability(useRaster, lowerLeftCornerSlightlyShiftedX, lowerLeftCornerSlightlyShiftedY, 0.89);
    }

    @Test
    public void findEnvironmentalSuitabilityOutOfRasterRangeUsingGeoTools() throws Exception {
        findEnvironmentalSuitabilityOutOfRasterRange(true);
    }

    @Test
    public void findEnvironmentalSuitabilityOutOfRasterRangeUsingPostGIS() throws Exception {
        findEnvironmentalSuitabilityOutOfRasterRange(false);
    }

    private void findEnvironmentalSuitabilityOutOfRasterRange(boolean useRaster) throws Exception {
        double oneCellBeyondUpperRightCornerX = LARGE_RASTER_XLLCORNER + LARGE_RASTER_COLUMNS * LARGE_RASTER_CELLSIZE;
        double oneCellBeyondUpperRightCornerY = LARGE_RASTER_YLLCORNER + LARGE_RASTER_ROWS * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitability(useRaster, oneCellBeyondUpperRightCornerX, oneCellBeyondUpperRightCornerY, null);
    }

    private ModelRun createAndSaveModelRun(String name, int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, DateTime.now());
        modelRun.setStatus(status);
        modelRun.setResponseDate(DateTime.now());
        modelRunService.saveModelRun(modelRun);
        return modelRun;
    }

    private void updateRasterForModelRun(ModelRun modelRun) throws Exception {
        byte[] gdalRaster = FileUtils.readFileToByteArray(new File(LARGE_RASTER_FILENAME));
        modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), gdalRaster);
    }

    private void findEnvironmentalSuitability(boolean useRaster, double x, double y,
                                              Double expectedEnvironmentalSuitability) throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = createOccurrence(x, y);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroup.getId(), ModelRunStatus.COMPLETED);
        updateRasterForModelRun(modelRun);
        GridCoverage2D raster = helper.getLatestMeanPredictionRaster(diseaseGroup);

        // Act
        Double suitability = helper.findEnvironmentalSuitability(occurrence, useRaster ? raster : null);

        // Assert
        assertThat(raster).isNotNull();
        if (expectedEnvironmentalSuitability != null) {
            assertThat(suitability).isEqualTo(expectedEnvironmentalSuitability, offset(0.0000005));
        } else {
            assertThat(suitability).isNull();
        }
    }

    private DiseaseOccurrence createOccurrence(double x, double y) {
        double offsetForRounding = 0.00005;
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setLocation(new Location(x + offsetForRounding, y + offsetForRounding));
        occurrence.setDiseaseGroup(diseaseGroup);
        return occurrence;
    }
}
