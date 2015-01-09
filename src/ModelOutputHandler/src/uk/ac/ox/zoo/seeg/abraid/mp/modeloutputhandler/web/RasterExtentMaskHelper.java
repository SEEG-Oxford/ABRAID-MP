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
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class RasterExtentMaskHelper {
    private static final Logger LOGGER = Logger.getLogger(RasterExtentMaskHelper.class);
    private static final Object LOG_TRANSFORMING_RASTER_DATA =
            "Masking out known absence regions in model output raster.";

    private final int extentAbsenceValue;

    public RasterExtentMaskHelper(DiseaseService diseaseService) {
        this.extentAbsenceValue = diseaseService.getDiseaseExtentClass(DiseaseExtentClass.ABSENCE).getWeighting();
    }

    public void maskRaster(final File targetFile, final File sourceRasterFile,
                           final File extentRasterFile, final int maskValue) throws IOException {
        File[] referenceRasterFiles = new File[] { extentRasterFile };
        RasterUtils.transformRaster(sourceRasterFile, targetFile, referenceRasterFiles, new RasterTransformation() {
            @Override
            public void transform(WritableRaster raster, Raster[] referenceRasters) {
                transformRaster(raster, referenceRasters[0], maskValue);
            }
        });
    }

    private void transformRaster(WritableRaster raster, Raster extentRaster, int maskValue) {
        LOGGER.info(LOG_TRANSFORMING_RASTER_DATA);

        for (int i = 0; i < raster.getWidth(); i++) {
            for (int j = 0; j < raster.getHeight(); j++) {
                int extentValue = extentRaster.getSample(i, j, 0);

                if (extentValue == extentAbsenceValue) {
                    int value = raster.getSample(i, j, 0);
                    if (value != maskValue && value != RasterUtils.NO_DATA_VALUE) {
                        raster.setSample(i, j, 0, maskValue);
                    }
                }
            }
        }
    }
}
