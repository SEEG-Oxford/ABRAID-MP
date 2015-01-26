package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.RasterUtils;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the ModelOutputRasterMaskingHelper class.
 * Copyright (c) 2015 University of Oxford
 */
public class ModelOutputRasterMaskingHelperTest {
    private static final String TEST_DATA_PATH = "ModelOutputHandler/test/uk/ac/ox/zoo/seeg/abraid/mp/modeloutputhandler/web/testdata";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void maskRasterCorrectlyAppliesExtentAndWaterBodiesMask() throws Exception {
        // Arrange
        File extentRasterFile = new File(TEST_DATA_PATH, "extent.tif");
        File waterBodiesRasterFile = new File(TEST_DATA_PATH, "waterbodies.tif");
        File expectedOutputMeanRasterFile = new File(TEST_DATA_PATH, "mean_prediction.tif");
        File inputMeanRasterFile = new File(TEST_DATA_PATH, "mean_prediction_full.tif");
        File outputMeanRasterFile = new File(testFolder.getRoot().getAbsolutePath(), "mean_prediction_out.tif");
        File inputUncertaintyRasterFile = new File(TEST_DATA_PATH, "prediction_uncertainty_full.tif");
        File expectedOutputUncertaintyRasterFile = new File(TEST_DATA_PATH, "prediction_uncertainty.tif");
        File outputUncertaintyRasterFile = new File(testFolder.getRoot().getAbsolutePath(), "prediction_uncertainty_out.tif");

        DiseaseService mockDiseaseService = mock(DiseaseService.class);
        WaterBodiesMaskRasterFileLocator mockWaterBodiesMaskRasterFileLocator = mock(WaterBodiesMaskRasterFileLocator.class);
        DiseaseExtentClass mockAbsenceDiseaseExtentClass = mock(DiseaseExtentClass.class);

        when(mockWaterBodiesMaskRasterFileLocator.getFile()).thenReturn(waterBodiesRasterFile);
        when(mockDiseaseService.getDiseaseExtentClass(DiseaseExtentClass.ABSENCE)).thenReturn(mockAbsenceDiseaseExtentClass);
        when(mockAbsenceDiseaseExtentClass.getWeighting()).thenReturn(-100);

        ModelOutputRasterMaskingHelper target =
                new ModelOutputRasterMaskingHelper(mockDiseaseService, mockWaterBodiesMaskRasterFileLocator);

        // Act
        target.maskRaster(outputMeanRasterFile, inputMeanRasterFile, extentRasterFile, 0);
        target.maskRaster(outputUncertaintyRasterFile, inputUncertaintyRasterFile, extentRasterFile, RasterUtils.UNKNOWN_VALUE);

        // Assert
        assertThat(outputMeanRasterFile).hasContentEqualTo(expectedOutputMeanRasterFile);
        assertThat(outputUncertaintyRasterFile).hasContentEqualTo(expectedOutputUncertaintyRasterFile);
    }
}
