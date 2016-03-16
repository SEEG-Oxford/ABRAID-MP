package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.runrequest;

import com.vividsolutions.jts.geom.Point;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ConfigurationServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.GitSourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModelRunWorkflowException;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
* Tests the ModelRunPackageBuilder class.
* Copyright (c) 2015 University of Oxford
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoContextLoader.class, locations = {
        "classpath:uk/ac/ox/zoo/seeg/abraid/mp/testutils/test-context.xml",
        "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelRunPackageBuilderIntegrationTest {
    @Autowired
    private ConfigurationServiceImpl configurationServiceImpl;

    @Autowired
    private GitSourceCodeManager gitSourceCodeManager;

    @Autowired
    @ReplaceWithMock
    private RasterFilePathFactory rasterFilePathFactory;

    @Autowired
    private ModelRunPackageBuilder modelRunPackageBuilder;

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private static final String DATA_DIR = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/runrequest/data/testdata/";

    @Test
    public void buildPackageShouldThrowForBadMode() throws Exception {
        // Arrange/Act
        catchException(this).buildTestPackage("bad_mode");

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        assertThat(caughtException()).hasMessage("Disease group (1234) is configured for a model mode (bad_mode) that is not supported by the current model version.");
    }

    @Test
    public void buildPackageShouldCreateAZipContainingTheCorrectContent() throws Exception {
        // Arrange/Act
        Path directory = buildTestPackage("Bhatt2013");

        // Assert
        buildPackageShouldCreateAZipContainingTheCorrectMetadata(directory);
        buildPackageShouldCreateAZipContainingTheGeneratedRunScript(directory);
        buildPackageShouldCreateAZipContainingModelCode(directory);
        buildPackageShouldCreateAZipContainingGaulLevelAdminRasters(directory);
        buildPackageShouldCreateAZipContainingCovariateRasters(directory);
        buildPackageShouldCreateAZipContainingExtentInput(directory);
        buildPackageShouldCreateAZipContainingOccurrenceInput(directory);
        buildPackageShouldCreateAZipContainingBiasOccurrenceInput(directory);
        buildPackageDeletesWorkspace();

        // Clean up
        FileUtils.deleteDirectory(directory.toFile());
    }

    public Path buildTestPackage(String mode) throws Exception {
          // Arrange
        when(rasterFilePathFactory.getAdminRaster(0)).thenReturn(new File(DATA_DIR + "admin/a0.tif"));
        when(rasterFilePathFactory.getAdminRaster(1)).thenReturn(new File(DATA_DIR + "admin/a1.tif"));
        when(rasterFilePathFactory.getAdminRaster(2)).thenReturn(new File(DATA_DIR + "admin/a2.tif"));
        when(rasterFilePathFactory.getExtentGaulRaster(true)).thenReturn(new File(DATA_DIR + "SmallRaster.tif"));
        when(rasterFilePathFactory.getExtentGaulRaster(false)).thenReturn(new File("doesnt exist"));
        configurationServiceImpl.setModelRepositoryUrl("https://github.com/SEEG-Oxford/seegSDM.git");
        configurationServiceImpl.setModelRepositoryVersion("0.1-9");
        gitSourceCodeManager.updateRepository();

        String runName = "runName12356";
        DiseaseGroup diseaseGroup = createDiseaseGroup(1234, "disease name", "disease abbr", true, mode);
        List<DiseaseOccurrence> occurrences = Arrays.asList(
                createOccurrence(diseaseGroup, createGeom(1, 2), 3, LocationPrecision.PRECISE, 4, 5, new DateTime("2014-01-02")),
                createOccurrence(diseaseGroup, createGeom(6, 7), 8, LocationPrecision.COUNTRY, 9, 10, new DateTime("2015-01-02")),
                createOccurrence(diseaseGroup, createGeom(11, 12), 13, LocationPrecision.ADMIN1, 14, 15, new DateTime("2014-02-01")),
                createOccurrence(diseaseGroup, createGeom(16, 17), 18, LocationPrecision.ADMIN2, 19, 20, new DateTime("2014-01-21"))
        );
        Collection<AdminUnitDiseaseExtentClass> extent = Arrays.asList(
                createAdminUnitDiseaseExtentClass(1, -100),
                createAdminUnitDiseaseExtentClass(2, -50),
                createAdminUnitDiseaseExtentClass(3, 0),
                createAdminUnitDiseaseExtentClass(4, 50),
                createAdminUnitDiseaseExtentClass(5, 100)
        );
        DiseaseGroup diseaseGroupA = createDiseaseGroup(23141, "disease name A", "disease abbr A", true, "meh");
        DiseaseGroup diseaseGroupB = createDiseaseGroup(1111, "disease name B", "disease abbr B", true, "meh");
        List<DiseaseOccurrence> biasOccurrences = Arrays.asList(
                createOccurrence(diseaseGroupA, createGeom(2, 4), 12, LocationPrecision.PRECISE, 13, 43, new DateTime("2014-01-02")),
                createOccurrence(diseaseGroupA, createGeom(4, 2), 3, LocationPrecision.COUNTRY, 19, 11, new DateTime("2016-01-02")),
                createOccurrence(diseaseGroupA, createGeom(6, 13), 5, LocationPrecision.ADMIN1, 14, 12, new DateTime("2014-01-12")),
                createOccurrence(diseaseGroupB, createGeom(1, 35), 11, LocationPrecision.ADMIN2, 11, 22, new DateTime("2014-09-02"))
        );
        Collection<CovariateFile> covariateFiles = Arrays.asList(
                createCovariateFile(1, "c1.tif", true, 1),
                createCovariateFile(2, "c2.tif", false, 2),
                createCovariateFile(3, "sub/c3.tif", true, 1)
        );
        String covariateDirectory = DATA_DIR + "covariates/";

        //Act
        File zip = modelRunPackageBuilder.buildPackage(runName, diseaseGroup, occurrences, extent, biasOccurrences, covariateFiles, covariateDirectory);
        ZipFile zipFile = new ZipFile(zip);
        Path directory = testDir.getRoot().toPath();
        zipFile.extractAll(directory.toAbsolutePath().toString());
        Files.delete(zip.toPath());
        return directory;
    }

    private DiseaseGroup createDiseaseGroup(int id, String name, String abbreviation, boolean isGlobal, String modelMode) {
        DiseaseGroup obj = mock(DiseaseGroup.class);
        when(obj.getId()).thenReturn(id);
        when(obj.getName()).thenReturn(name);
        when(obj.getAbbreviation()).thenReturn(abbreviation);
        when(obj.isGlobal()).thenReturn(isGlobal);
        when(obj.getModelMode()).thenReturn(modelMode);
        return obj;
    }

    private Point createGeom(double x, double y) {
        Point point = mock(Point.class);
        when(point.getX()).thenReturn(x);
        when(point.getY()).thenReturn(y);
        return point;
    }
    private DiseaseOccurrence createOccurrence(DiseaseGroup diseaseGroup, Point geom, double weight, LocationPrecision precision, int countryGaul, int qcGaul, DateTime occurrenceDate) {
        DiseaseOccurrence obj = mock(DiseaseOccurrence.class);
        when(obj.getLocation()).thenReturn(mock(Location.class));
        when(obj.getLocation().getPrecision()).thenReturn(precision);
        when(obj.getLocation().getGeom()).thenReturn(geom);
        when(obj.getDiseaseGroup()).thenReturn(diseaseGroup);
        when(obj.getFinalWeighting()).thenReturn(weight);
        when(obj.getLocation().getAdminUnitQCGaulCode()).thenReturn(qcGaul);
        when(obj.getLocation().getCountryGaulCode()).thenReturn(countryGaul);
        when(obj.getOccurrenceDate()).thenReturn(occurrenceDate);
        return obj;
    }

    private AdminUnitDiseaseExtentClass createAdminUnitDiseaseExtentClass(int gaul, int weighting) {
        AdminUnitDiseaseExtentClass obj = mock(AdminUnitDiseaseExtentClass.class);
        when(obj.getDiseaseExtentClass()).thenReturn(mock(DiseaseExtentClass.class));
        when(obj.getDiseaseExtentClass().getWeighting()).thenReturn(weighting);
        when(obj.getAdminUnitGlobalOrTropical()).thenReturn(mock(AdminUnitGlobalOrTropical.class));
        when(obj.getAdminUnitGlobalOrTropical().getGaulCode()).thenReturn(gaul);
        return obj;
    }

    private CovariateFile createCovariateFile(int id, String file, boolean discrete, int subCount) {
        CovariateFile obj = mock(CovariateFile.class);
        List<CovariateSubFile> covariateSubFiles = new ArrayList<>();
        for (int i = 1; i <= subCount; i++) {
            CovariateSubFile subObj = mock(CovariateSubFile.class);
            when(subObj.getQualifier()).thenReturn(subCount == 1 ? null : "2015-0" + i);
            when(subObj.getFile()).thenReturn(subCount == 1 ? file : file + "_" + i);
            covariateSubFiles.add(subObj);
        }
        when(obj.getFiles()).thenReturn(covariateSubFiles);
        when(obj.getId()).thenReturn(id);
        when(obj.getDiscrete()).thenReturn(discrete);
        return obj;
    }

    private void buildPackageShouldCreateAZipContainingTheCorrectMetadata(Path directory) throws Exception {
        // Assert
        File metadata = Paths.get(directory.toString(), "metadata.json").toFile();
        assertThat(metadata).exists();

        String content = FileUtils.readFileToString(metadata);
        assertThat(content).contains("\"disease\":{\"id\":1234,\"name\":\"disease name\",\"abbreviation\":\"disease abbr\",\"global\":true}");
        assertThat(content).contains("\"runName\":\"runName12356\"");
        assertThat(content).startsWith("{");
        assertThat(content).endsWith("}");
    }

    private void buildPackageShouldCreateAZipContainingTheGeneratedRunScript(Path directory) throws Exception {
        // Assert
        File script = Paths.get(directory.toString(), "modelRun.R").toFile();
        assertThat(script).exists();

        String content = FileUtils.readFileToString(script);
        assertThat(content).startsWith("# A launch script for the ABRAID-MP disease risk model");
        assertThat(content).contains("verbose <- TRUE");
        assertThat(content).contains("max_cpus <- 64");
        assertThat(content).contains("do_dry_run()");
        assertThat(script).hasContentEqualTo(Paths.get(DATA_DIR, "correct.R").toFile());
    }

    private void buildPackageShouldCreateAZipContainingModelCode(Path directory) throws Exception {
        // Assert
        File dir = Paths.get(directory.toString(), "model").toFile();
        assertThat(dir).exists();
        assertThat(dir).isDirectory();
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        assertThat(files).contains(Paths.get(dir.toString(), "R/seegSDM.R").toFile());
        assertThat(files).contains(Paths.get(dir.toString(), "DESCRIPTION").toFile());
        String description = FileUtils.readFileToString(Paths.get(dir.toString(), "DESCRIPTION").toFile());
        assertThat(description).contains("Version: 0.1-9");
    }

    private void buildPackageShouldCreateAZipContainingGaulLevelAdminRasters(Path directory) throws Exception {
        // Assert
        File dir = Paths.get(directory.toString(), "admins").toFile();
        assertThat(dir).exists();
        assertThat(dir).isDirectory();
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        assertThat(files).contains(Paths.get(dir.toString(), "admin0.tif").toFile());
        assertThat(Paths.get(dir.toString(), "admin0.tif").toFile()).hasContentEqualTo(rasterFilePathFactory.getAdminRaster(0));
        assertThat(files).contains(Paths.get(dir.toString(), "admin1.tif").toFile());
        assertThat(Paths.get(dir.toString(), "admin1.tif").toFile()).hasContentEqualTo(rasterFilePathFactory.getAdminRaster(1));
        assertThat(files).contains(Paths.get(dir.toString(), "admin2.tif").toFile());
        assertThat(Paths.get(dir.toString(), "admin2.tif").toFile()).hasContentEqualTo(rasterFilePathFactory.getAdminRaster(2));
    }

    private void buildPackageShouldCreateAZipContainingCovariateRasters(Path directory) throws Exception {
        // Assert
        File dir = Paths.get(directory.toString(), "covariates").toFile();
        assertThat(dir).exists();
        assertThat(dir).isDirectory();
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        assertThat(files).contains(Paths.get(dir.toString(), "c1.tif").toFile());
        assertThat(Paths.get(dir.toString(), "c1.tif").toFile()).hasContentEqualTo(Paths.get(DATA_DIR, "covariates/c1.tif").toFile());
        assertThat(files).contains(Paths.get(dir.toString(), "c2.tif_1").toFile());
        assertThat(Paths.get(dir.toString(), "c2.tif_1").toFile()).hasContentEqualTo(Paths.get(DATA_DIR, "covariates/c2.tif_1").toFile());
        assertThat(files).contains(Paths.get(dir.toString(), "c2.tif_2").toFile());
        assertThat(Paths.get(dir.toString(), "c2.tif_2").toFile()).hasContentEqualTo(Paths.get(DATA_DIR, "covariates/c2.tif_2").toFile());
        assertThat(files).contains(Paths.get(dir.toString(), "sub/c3.tif").toFile());
        assertThat(Paths.get(dir.toString(), "sub/c3.tif").toFile()).hasContentEqualTo(Paths.get(DATA_DIR, "covariates/sub/c3.tif").toFile());
    }

    private void buildPackageShouldCreateAZipContainingExtentInput(Path directory) throws Exception {
        // Assert
        File dir = Paths.get(directory.toString(), "data").toFile();
        assertThat(dir).exists();
        assertThat(dir).isDirectory();
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        assertThat(files).contains(Paths.get(dir.toString(), "extent.tif").toFile());
        assertThat(Paths.get(dir.toString(), "extent.tif").toFile()).hasContentEqualTo(Paths.get(DATA_DIR, "SmallRaster_transformed.tif").toFile());
    }

    private void buildPackageShouldCreateAZipContainingOccurrenceInput(Path directory) throws Exception {
        // Assert
        File dir = Paths.get(directory.toString(), "data").toFile();
        assertThat(dir).exists();
        assertThat(dir).isDirectory();
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        assertThat(files).contains(Paths.get(dir.toString(), "occurrences.csv").toFile());
        String content = FileUtils.readFileToString(Paths.get(dir.toString(), "occurrences.csv").toFile());
        assertThat(content).isEqualTo(
                "Longitude,Latitude,Weight,Admin,GAUL,Disease,Date\n" +
                "1.0,2.0,3.0,-999,NA,1234,2014-01-02\n" +
                "6.0,7.0,8.0,0,9,1234,2015-01-02\n" +
                "11.0,12.0,13.0,1,15,1234,2014-02-01\n" +
                "16.0,17.0,18.0,2,20,1234,2014-01-21\n");
    }

    private void buildPackageShouldCreateAZipContainingBiasOccurrenceInput(Path directory) throws Exception {
        // Assert
        File dir = Paths.get(directory.toString(), "data").toFile();
        assertThat(dir).exists();
        assertThat(dir).isDirectory();
        Collection<File> files = FileUtils.listFiles(dir, null, true);
        assertThat(files).contains(Paths.get(dir.toString(), "sample_bias.csv").toFile());
        String content = FileUtils.readFileToString(Paths.get(dir.toString(), "sample_bias.csv").toFile());
        assertThat(content).isEqualTo("Longitude,Latitude,Admin,GAUL,Disease,Date\n" +
                        "2.0,4.0,-999,NA,23141,2014-01-02\n" +
                        "4.0,2.0,0,19,23141,2016-01-02\n" +
                        "6.0,13.0,1,12,23141,2014-01-12\n" +
                        "1.0,35.0,2,22,1111,2014-09-02\n");
    }

    private void buildPackageDeletesWorkspace() throws Exception {
        // Assert
        assertThat(Paths.get(FileUtils.getTempDirectory().toString(), "runName12356").toFile()).doesNotExist();
    }
}
