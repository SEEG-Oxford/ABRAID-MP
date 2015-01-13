package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.RasterTransformation;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.RasterUtils;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/**
 * Helper class to perform the masking transformation on model output rasters.
 * Copyright (c) 2014 University of Oxford
 */
public class ModelOutputRasterMaskingHelper {
    private static final Logger LOGGER = Logger.getLogger(ModelOutputRasterMaskingHelper.class);
    private static final Object LOG_TRANSFORMING_RASTER_DATA =
            "Masking out known absence regions in model output raster.";

    private final int extentAbsenceValue;
    private final File waterBodiesMaskRasterFile;

    public ModelOutputRasterMaskingHelper(DiseaseService diseaseService, File waterBodiesMaskRasterFile) {
        this.waterBodiesMaskRasterFile = waterBodiesMaskRasterFile;
        this.extentAbsenceValue = diseaseService.getDiseaseExtentClass(DiseaseExtentClass.ABSENCE).getWeighting();
    }

    /**
     * Creates a new file at the targetFile location based on the sourceRasterFile but where pixels aligned with
     * DiseaseExtentClass.ABSENCE pixels in the extentRasterFile have been replaced with extentMaskValue. Pixels aligned
     * with known water bodies will also get set to RasterUtils.NO_DATA_VALUE.
     * @param targetFile The location at which to write the updated file.
     * @param sourceRasterFile The raster to be transformed
     * @param extentRasterFile The extent raster to compare against.
     * @param extentMaskValue The value to write at DiseaseExtentClass.ABSENCE locations.
     * @throws IOException thrown if unable to complete the masking operation.
     */
    public void maskRaster(final File targetFile, final File sourceRasterFile,
                           final File extentRasterFile, final int extentMaskValue) throws IOException {
        File[] referenceRasterFiles = new File[] {extentRasterFile, waterBodiesMaskRasterFile};
        RasterUtils.transformRaster(sourceRasterFile, targetFile, referenceRasterFiles, new RasterTransformation() {
            @Override
            public void transform(WritableRaster raster, Raster[] referenceRasters) {
                transformRaster(raster, referenceRasters[0], extentMaskValue, referenceRasters[1]);
            }
        });
    }

    private void transformRaster(WritableRaster raster, Raster extentRaster, int extentMaskValue,
                                 Raster waterBodiesMaskRaster) {
        LOGGER.info(LOG_TRANSFORMING_RASTER_DATA);

        for (int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                int rasterValue = raster.getSample(i, j, 0);
                if (rasterValue != RasterUtils.NO_DATA_VALUE) {
                    int waterBodiesValue = waterBodiesMaskRaster.getSample(i, j, 0);
                    if (waterBodiesValue != RasterUtils.NO_DATA_VALUE) {
                        raster.setSample(i, j, 0, RasterUtils.NO_DATA_VALUE);
                    } else {
                        int extentValue = extentRaster.getSample(i, j, 0);
                        if (extentValue == extentAbsenceValue) {
                            raster.setSample(i, j, 0, extentMaskValue);
                        }
                    }

                }
            }
        }
    }
}
