package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.geotools.coverage.grid.GridCoverage2D;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the EnvironmentalSuitabilityHelper class.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class,
        locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
public class EnvironmentalSuitabilityHelperIntegrationTest extends AbstractSpringIntegrationTests {
    // Parameters taken from the test raster files
    private static final String LARGE_RASTER_FILENAME =
            "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/testdata/test_raster_large_double.tif";
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

    @Autowired
    @ReplaceWithMock
    private RasterFilePathFactory rasterFilePathFactory;

    @Before
    public void setUp() {
        diseaseGroup = diseaseService.getDiseaseGroupById(87);
    }

    @Test
    public void getLatestMeanPredictionRasterReturnsNullIfNoRelevantModelRunsForThisDiseaseGroup() throws Exception {
        // Arrange - 2 irrelevant model runs
        ModelRun modelRun1 = createAndSaveModelRun("failed", diseaseGroup.getId(), ModelRunStatus.FAILED);
        mockGetRasterFileForModelRun(modelRun1);
        ModelRun modelRun2 = createAndSaveModelRun("different disease group", 1, ModelRunStatus.COMPLETED);
        mockGetRasterFileForModelRun(modelRun2);

        // Act
        GridCoverage2D meanPredictionRaster = helper.getLatestMeanPredictionRaster(diseaseGroup);

        // Assert
        assertThat(meanPredictionRaster).isNull();
    }

    @Test
    public void findEnvironmentalSuitabilityLowerLeftCorner() throws Exception {
        findEnvironmentalSuitability(LARGE_RASTER_XLLCORNER, LARGE_RASTER_YLLCORNER, 0.89);
    }

    @Test
    public void findEnvironmentalSuitabilityUpperRightCorner() throws Exception {
        double upperRightCornerX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_COLUMNS - 1) * LARGE_RASTER_CELLSIZE;
        double upperRightCornerY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 1) * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitability(upperRightCornerX, upperRightCornerY, 0.79);
    }

    @Test
    public void findEnvironmentalSuitabilityInterpolated() throws Exception {
        double lowerLeftCornerSlightlyShiftedX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        double lowerLeftCornerSlightlyShiftedY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        findEnvironmentalSuitability(lowerLeftCornerSlightlyShiftedX, lowerLeftCornerSlightlyShiftedY, 0.89);
    }

    @Test
    public void findEnvironmentalSuitabilityOutOfRasterRange() throws Exception {
        double oneCellBeyondUpperRightCornerX = LARGE_RASTER_XLLCORNER + LARGE_RASTER_COLUMNS * LARGE_RASTER_CELLSIZE;
        double oneCellBeyondUpperRightCornerY = LARGE_RASTER_YLLCORNER + LARGE_RASTER_ROWS * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitability(oneCellBeyondUpperRightCornerX, oneCellBeyondUpperRightCornerY, null);
    }

    @Test
    public void findEnvironmentalSuitabilityNoDataValueWithinRasterRange() throws Exception {
        // The NODATA value in the raster is in column 6 row 12 (from the top left)
        double noDataValueX = LARGE_RASTER_XLLCORNER + 5 * LARGE_RASTER_CELLSIZE;
        double noDataValueY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 12) * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitability(noDataValueX, noDataValueY, null);
    }

    private ModelRun createAndSaveModelRun(String name, int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, "host", DateTime.now());
        modelRun.setStatus(status);
        modelRun.setResponseDate(DateTime.now());
        modelRunService.saveModelRun(modelRun);
        return modelRun;
    }

    private void mockGetRasterFileForModelRun(ModelRun modelRun) throws Exception {
        when(rasterFilePathFactory.getMeanPredictionRasterFile(same(modelRun)))
                .thenReturn(new File(LARGE_RASTER_FILENAME));
    }

    private void findEnvironmentalSuitability(double x, double y,
                                              Double expectedEnvironmentalSuitability) throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = createOccurrence(x, y);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroup.getId(), ModelRunStatus.COMPLETED);
        mockGetRasterFileForModelRun(modelRun);
        GridCoverage2D raster = helper.getLatestMeanPredictionRaster(diseaseGroup);

        // Act
        Double suitability = helper.findEnvironmentalSuitability(occurrence, raster);

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
