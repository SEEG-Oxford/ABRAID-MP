package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest.data;

import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.AdminUnitDiseaseExtentClass;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterTransformation;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.raster.RasterUtils;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a mechanism for writing model input extent data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class ExtentDataWriterImpl implements ExtentDataWriter {
    private static final Logger LOGGER = Logger.getLogger(ExtentDataWriterImpl.class);
    private static final Object LOG_TRANSFORMING_RASTER_DATA =
            "Transforming gaul code raster to weightings raster.";

    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param sourceRasterFile The path of the gaul code raster to reclassify.
     * @param targetFile The file to be created.
     * @throws java.io.IOException If the data could not be written.
     */
    @Override
    public void write(Collection<AdminUnitDiseaseExtentClass> extentData,
                      final File sourceRasterFile, final File targetFile)
            throws IOException {
        final Map<Integer, Integer> extentMapping = extractDiseaseExtentMap(extentData);
        RasterUtils.transformRaster(sourceRasterFile, targetFile, new File[0], new RasterTransformation() {
            @Override
            public void transform(WritableRaster raster, Raster[] referenceRasters) throws IOException {
                transformRaster(extentMapping, raster);
            }
        });
    }

    private Map<Integer, Integer> extractDiseaseExtentMap(Collection<AdminUnitDiseaseExtentClass> diseaseExtent) {
        // Create a mapping of GAUL codes to extent class weightings
        Map<Integer, Integer> extentMapping = new HashMap<>();
        for (AdminUnitDiseaseExtentClass extentClass : diseaseExtent) {
            extentMapping.put(extentClass.getAdminUnitGlobalOrTropical().getGaulCode(),
                    extentClass.getDiseaseExtentClass().getWeighting());
        }

        return extentMapping;
    }

    private void transformRaster(Map<Integer, Integer> transform, WritableRaster data) {
        LOGGER.info(LOG_TRANSFORMING_RASTER_DATA);

        for (int i = 0; i < data.getWidth(); i++) {
            for (int j = 0; j < data.getHeight(); j++) {
                int gaul = data.getSample(i, j, 0);
                if (transform.containsKey(gaul)) {
                    data.setSample(i, j, 0, transform.get(gaul));
                } else {
                    if (gaul != RasterUtils.NO_DATA_VALUE) {
                        data.setSample(i, j, 0, RasterUtils.NO_DATA_VALUE);
                    }
                }
            }
        }
    }
}
