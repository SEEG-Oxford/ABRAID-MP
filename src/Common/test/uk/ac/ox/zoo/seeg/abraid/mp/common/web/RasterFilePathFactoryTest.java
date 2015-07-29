package uk.ac.ox.zoo.seeg.abraid.mp.common.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the RasterFilePathFactory class.
 * Copyright (c) 2014 University of Oxford
 */
public class RasterFilePathFactoryTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private RasterFilePathFactory builder;
    private String adminDirectory;
    private String resultsDirectory;

    @Before
    public void setUp() throws IOException {
        resultsDirectory = getResultsDirectory();
        this.adminDirectory = getAdminDirectory();
        builder = new RasterFilePathFactory(new File(resultsDirectory), new File(adminDirectory));
    }

    @Test
    public void getFullMeanPredictionRasterFileReturnsCorrectFile() throws IOException {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getFullMeanPredictionRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_mean_full.tif");
        assertThat(file.getParent()).isEqualTo(resultsDirectory);
    }

    @Test
    public void getMaskedMeanPredictionRasterFileReturnsCorrectFile() throws IOException {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getMaskedMeanPredictionRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_mean.tif");
        assertThat(file.getParent()).isEqualTo(resultsDirectory);
    }

    @Test
    public void getFullPredictionUncertaintyRasterFileReturnsCorrectFile() throws IOException {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getFullPredictionUncertaintyRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_uncertainty_full.tif");
        assertThat(file.getParent()).isEqualTo(resultsDirectory);
    }

    @Test
    public void getMaskedPredictionUncertaintyRasterFileReturnsCorrectFile() throws IOException {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getMaskedPredictionUncertaintyRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_uncertainty.tif");
        assertThat(file.getParent()).isEqualTo(resultsDirectory);
    }

    @Test
    public void getExtentInputRasterFileReturnsCorrectFile() throws IOException {
        ModelRun modelRun = new ModelRun("testname", 87, "host", DateTime.now(), DateTime.now(), DateTime.now());
        File file = builder.getExtentInputRasterFile(modelRun);
        assertThat(file.getName()).isEqualTo("testname_extent.tif");
        assertThat(file.getParent()).isEqualTo(resultsDirectory);
    }

    @Test
    public void getExtentAdminRasterFileReturnsCorrectFile() throws IOException {
        File file = builder.getAdminRaster(0);
        assertThat(file.getName()).isEqualTo("admin0qc.tif");
        assertThat(file.getParent()).isEqualTo(adminDirectory);
        file = builder.getAdminRaster(1);
        assertThat(file.getName()).isEqualTo("admin1qc.tif");
        assertThat(file.getParent()).isEqualTo(adminDirectory);
        file = builder.getAdminRaster(2);
        assertThat(file.getName()).isEqualTo("admin2qc.tif");
        assertThat(file.getParent()).isEqualTo(adminDirectory);
    }

    private String getResultsDirectory() throws IOException {
        return testFolder.newFolder().getAbsolutePath();
    }

    private String getAdminDirectory() throws IOException {
        return testFolder.newFolder().getAbsolutePath();
    }
}
