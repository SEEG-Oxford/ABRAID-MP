package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the RasterFilePathFactory class.
 * Copyright (c) 2014 University of Oxford
 */
public class RasterFilePathFactoryTest {
    private RasterFilePathFactory builder;

    @Before
    public void setUp() {
        File root = new File(getCurrentDirectory());
        builder = new RasterFilePathFactory(root);
    }

    @Test
    public void getFullMeanPredictionRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getFullMeanPredictionRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_mean_full.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    @Test
    public void getMaskedMeanPredictionRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getMaskedMeanPredictionRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_mean.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    @Test
    public void getFullPredictionUncertaintyRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getFullPredictionUncertaintyRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_uncertainty_full.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    @Test
    public void getMaskedPredictionUncertaintyRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getMaskedPredictionUncertaintyRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_uncertainty.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    @Test
    public void getExtentInputRasterFileReturnsCorrectFile() {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getExtentInputRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_extent.tif");
        assertThat(file.getParent()).isEqualTo(getCurrentDirectory());
    }

    private String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }
}
