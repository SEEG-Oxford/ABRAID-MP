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
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ValidationParameterCacheService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the EnvironmentalSuitabilityHelper class.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = {
    "classpath:uk/ac/ox/zoo/seeg/abraid/mp/testutils/test-context.xml",
    "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml"
})
public class EnvironmentalSuitabilityHelperIntegrationTest extends AbstractSpringIntegrationTests {
    // Parameters taken from the test raster files
    private static final String LARGE_RASTER_FILENAME =
            "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/testdata/test_raster_large_double.tif";
    private static final String ADMIN_RASTER_FILENAME =
            "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/testdata/admin_raster_large_double.tif";
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

    @Autowired
    @ReplaceWithMock
    private ValidationParameterCacheService cacheService;

    @Before
    public void setUp() {
        diseaseGroup = diseaseService.getDiseaseGroupById(87);
        reset(cacheService);
        when(cacheService.getEnvironmentalSuitabilityFromCache(anyInt(), anyInt())).thenReturn(null);
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
        findEnvironmentalSuitabilityPrecise(LARGE_RASTER_XLLCORNER, LARGE_RASTER_YLLCORNER, 0.89);
    }

    @Test
    public void findEnvironmentalSuitabilityUpperRightCorner() throws Exception {
        double upperRightCornerX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_COLUMNS - 1) * LARGE_RASTER_CELLSIZE;
        double upperRightCornerY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 1) * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitabilityPrecise(upperRightCornerX, upperRightCornerY, 0.79);
    }

    @Test
    public void findEnvironmentalSuitabilityInterpolated() throws Exception {
        double lowerLeftCornerSlightlyShiftedX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        double lowerLeftCornerSlightlyShiftedY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_CELLSIZE * 0.5);
        findEnvironmentalSuitabilityPrecise(lowerLeftCornerSlightlyShiftedX, lowerLeftCornerSlightlyShiftedY, 0.89);
    }

    @Test
    public void findEnvironmentalSuitabilityOutOfRasterRange() throws Exception {
        double oneCellBeyondUpperRightCornerX = LARGE_RASTER_XLLCORNER + LARGE_RASTER_COLUMNS * LARGE_RASTER_CELLSIZE;
        double oneCellBeyondUpperRightCornerY = LARGE_RASTER_YLLCORNER + LARGE_RASTER_ROWS * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitabilityPrecise(oneCellBeyondUpperRightCornerX, oneCellBeyondUpperRightCornerY, null);
    }

    @Test
    public void findEnvironmentalSuitabilityNoDataValueWithinRasterRange() throws Exception {
        // The NODATA value in the raster is in column 6 row 12 (from the top left)
        double noDataValueX = LARGE_RASTER_XLLCORNER + 5 * LARGE_RASTER_CELLSIZE;
        double noDataValueY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 12) * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitabilityPrecise(noDataValueX, noDataValueY, null);
    }

    @Test
    public void findEnvironmentalSuitabilityNoDataInShape() throws Exception {
        // Falls back to precise
        double upperRightCornerX = LARGE_RASTER_XLLCORNER + (LARGE_RASTER_COLUMNS - 1) * LARGE_RASTER_CELLSIZE;
        double upperRightCornerY = LARGE_RASTER_YLLCORNER + (LARGE_RASTER_ROWS - 1) * LARGE_RASTER_CELLSIZE;
        findEnvironmentalSuitabilityShape(upperRightCornerX, upperRightCornerY, 987, LocationPrecision.COUNTRY, 0.79);
    }

    @Test
    public void findEnvironmentalSuitabilityShapeHalfNoData() throws Exception {
        findEnvironmentalSuitabilityShape(0, 0, 654, LocationPrecision.ADMIN1, 0.504699);
    }

    @Test
    public void findEnvironmentalSuitabilityShapeFullPopulated() throws Exception {
        findEnvironmentalSuitabilityShape(0, 0, 321, LocationPrecision.ADMIN2, 0.491874);
    }

    @Test
    public void findEnvironmentalSuitabilityUsesCachedValue() throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = createOccurrence(1, 1, 1, LocationPrecision.PRECISE);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroup.getId(), ModelRunStatus.COMPLETED);
        mockGetRasterFileForModelRun(modelRun);
        GridCoverage2D suitabilityRaster = helper.getLatestMeanPredictionRaster(diseaseGroup);
        GridCoverage2D[] adminRasters = helper.getSingleAdminRaster(LocationPrecision.PRECISE);

        when(cacheService.getEnvironmentalSuitabilityFromCache(occurrence.getDiseaseGroup().getId(), occurrence.getLocation().getId())).thenReturn(12345d);

        // Act
        Double suitability = helper.findEnvironmentalSuitability(occurrence, suitabilityRaster, adminRasters);

        // Assert
        assertThat(suitability).isEqualTo(12345d);
    }

    private ModelRun createAndSaveModelRun(String name, int diseaseGroupId, ModelRunStatus status) {
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, "host", DateTime.now(), DateTime.now(), DateTime.now());
        modelRun.setStatus(status);
        modelRun.setResponseDate(DateTime.now());
        modelRunService.saveModelRun(modelRun);
        return modelRun;
    }

    private void mockGetRasterFileForModelRun(ModelRun modelRun) {
        when(rasterFilePathFactory.getFullMeanPredictionRasterFile(same(modelRun)))
                .thenReturn(new File(LARGE_RASTER_FILENAME));
    }

    private void mockGetAdminRasterFileForLevel(int level) {
        when(rasterFilePathFactory.getAdminRaster(level)).thenReturn(new File(ADMIN_RASTER_FILENAME));
    }

    private void findEnvironmentalSuitabilityPrecise(double x, double y,
                                                     Double expectedEnvironmentalSuitability) throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = createOccurrence(x, y, 1, LocationPrecision.PRECISE);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroup.getId(), ModelRunStatus.COMPLETED);
        mockGetRasterFileForModelRun(modelRun);
        GridCoverage2D suitabilityRaster = helper.getLatestMeanPredictionRaster(diseaseGroup);
        GridCoverage2D[] adminRasters = helper.getSingleAdminRaster(LocationPrecision.PRECISE);

        // Act
        Double suitability = helper.findEnvironmentalSuitability(occurrence, suitabilityRaster, adminRasters);

        // Assert
        assertThat(suitabilityRaster).isNotNull();
        assertThat(adminRasters).isNotNull();
        assertThat(adminRasters[0]).isNull();
        assertThat(adminRasters[1]).isNull();
        assertThat(adminRasters[2]).isNull();
        if (expectedEnvironmentalSuitability != null) {
            assertThat(suitability).isEqualTo(expectedEnvironmentalSuitability, offset(0.0000005));
            verify(cacheService).saveEnvironmentalSuitabilityCacheEntry(occurrence.getDiseaseGroup().getId(), occurrence.getLocation().getId(), suitability);
        } else {
            assertThat(suitability).isNull();
        }
    }

    private void findEnvironmentalSuitabilityShape(double x, double y, int gaul, LocationPrecision precision,
                                                     Double expectedEnvironmentalSuitability) throws Exception {
        // Arrange
        DiseaseOccurrence occurrence = createOccurrence(x, y, gaul, precision);
        ModelRun modelRun = createAndSaveModelRun("test name", diseaseGroup.getId(), ModelRunStatus.COMPLETED);
        mockGetRasterFileForModelRun(modelRun);
        mockGetAdminRasterFileForLevel(precision.getModelValue());
        GridCoverage2D suitabilityRaster = helper.getLatestMeanPredictionRaster(diseaseGroup);
        GridCoverage2D[] adminRasters = helper.getSingleAdminRaster(precision);

        // Act
        Double suitability = helper.findEnvironmentalSuitability(occurrence, suitabilityRaster, adminRasters);

        // Assert
        assertThat(suitabilityRaster).isNotNull();
        assertThat(adminRasters).isNotNull();
        assertThat(adminRasters[precision.getModelValue()]).isNotNull();
        if (expectedEnvironmentalSuitability != null) {
            assertThat(suitability).isEqualTo(expectedEnvironmentalSuitability, offset(0.0000005));
            verify(cacheService).saveEnvironmentalSuitabilityCacheEntry(occurrence.getDiseaseGroup().getId(), occurrence.getLocation().getId(), suitability);
        } else {
            assertThat(suitability).isNull();
        }
    }

    private DiseaseOccurrence createOccurrence(double x, double y, int gaul, LocationPrecision precision) {
        double offsetForRounding = 0.00005;
        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        Location location = mock(Location.class);
        when(location.getGeom()).thenReturn(GeometryUtils.createPoint(x + offsetForRounding,  y + offsetForRounding));
        when(location.getPrecision()).thenReturn(precision);
        if (precision == LocationPrecision.COUNTRY) {
            when(location.getCountryGaulCode()).thenReturn(gaul);
        } else {
            when(location.getAdminUnitQCGaulCode()).thenReturn(gaul);
        }
        occurrence.setLocation(location);
        occurrence.setDiseaseGroup(diseaseGroup);
        return occurrence;
    }
}
