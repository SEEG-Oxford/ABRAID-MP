package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.data;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.geometry.Envelope2D;

import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Provides a mechanism for writing model input extent data into the working directory.
 * Copyright (c) 2014 University of Oxford
 */
public class ExtentDataWriterImpl implements ExtentDataWriter {
    /**
     * Write the extent data to a raster file ready to run the model.
     * @param extentData The data to be written.
     * @param sourceRasterFile The path of the gaul code raster to reclassify.
     * @param targetFile The file to be created.
     * @throws java.io.IOException If the data could not be written.
     */
    @Override
    public void write(Map<Integer, Integer> extentData, File sourceRasterFile, File targetFile) throws IOException {
        GridCoverage2D sourceRaster = loadRaster(sourceRasterFile);

        Envelope2D rasterExtent = sourceRaster.getGridGeometry().getEnvelope2D();
        WritableRaster rasterData = (WritableRaster) sourceRaster.getRenderedImage().getData();

        transformRaster(extentData, rasterData);

        saveRaster(targetFile, rasterData, rasterExtent);
    }

    private GridCoverage2D loadRaster(File location) throws IOException {
        ArcGridReader reader = new ArcGridReader(location.toURI().toURL());
        return reader.read(null);
    }

    private void transformRaster(Map<Integer, Integer> transform, WritableRaster data) {
        for (int i = 0; i < data.getWidth(); i++) {
            for (int j = 0; j < data.getHeight(); j++) {
                int gaul = data.getSample(i, j, 0);
                if (transform.containsKey(gaul)) {
                    data.setSample(i, j, 0, transform.get(gaul));
                } else {
                    data.setSample(i, j, 0, Double.NaN);
                }
            }
        }
    }

    private void saveRaster(File location, WritableRaster raster, Envelope2D extents) throws IOException {
        GridCoverageFactory factory = new GridCoverageFactory();
        GridCoverage2D targetRaster = factory.create(location.getName(), raster, extents);
        ArcGridWriter writer = new ArcGridWriter(location.toURI().toURL());
        writer.write(targetRaster, null);
    }
}
