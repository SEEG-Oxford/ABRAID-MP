package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the RasterFileBuilder class.
 * Copyright (c) 2014 University of Oxford
 */
public class RasterFileBuilderTest {
    private RasterFileBuilder builder;

    @Before
    public void setUp() {
        File root = new File(getCurrentDirectory());
        builder = new RasterFileBuilder(root);
    }

    @Test
    public void getMeanPredictionRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, DateTime.now());
        File file = builder.getMeanPredictionRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_mean.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    @Test
    public void getPredictionUncertaintyRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, DateTime.now());
        File file = builder.getPredictionUncertaintyRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_uncertainty.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    private String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }
}
