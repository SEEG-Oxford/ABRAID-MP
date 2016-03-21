package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import com.vividsolutions.jts.geom.Polygon;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tests for DataExtractorController.
 * Copyright (c) 2015 University of Oxford
 */
public class DataExtractorControllerTest {
    @Test
    public void getPageReturnsCorrectTemplateAndData() throws Exception {
        // Arrange
        Model model = mock(Model.class);

        ModelRunService modelRunService = mock(ModelRunService.class);
        RasterFilePathFactory rasterFilePathFactory = mock(RasterFilePathFactory.class);
        EnvironmentalSuitabilityHelper environmentalSuitabilityHelper = mock(EnvironmentalSuitabilityHelper.class);
        GeometryService geometryService = mock(GeometryService.class);
        AbraidJsonObjectMapper objectMapper = new AbraidJsonObjectMapper();
        List<ModelRun> runs = Arrays.asList(
                mockModelRun("NAME1", "DISEASE1", new DateTime("2015-02-01")),
                mockModelRun("NAME2", "DISEASE2", new DateTime("2015-03-02")),
                mockModelRun("NAME3", "DISEASE2", new DateTime("2015-04-03")));
        List<Country> countries = Arrays.asList(
                mockCountry("NAME", 54321, GeometryUtils.createPolygon(0, 0, 1, 2, 2, 3, 0, 0)),
                mockCountry("BAD", 9876, GeometryUtils.createPolygon(0, 0, 1, 2, 2, 3, 0, 0)),
                mockCountry("NAME1", 432, GeometryUtils.createPolygon(-1, -2, 1, 2, 5, 3, -1, -2)),
                mockCountry("NAME2", 123, GeometryUtils.createPolygon(0, 0, 1, 2, 4, 4, 0, 0)));
        ModellingLocationPrecisionAdjuster precisionAdjuster = mock(ModellingLocationPrecisionAdjuster.class);
        when(precisionAdjuster.checkGaul(anyString())).thenReturn(false);
        when(precisionAdjuster.checkGaul("9876")).thenReturn(true);
        DataExtractorController target = new DataExtractorController(rasterFilePathFactory, modelRunService, environmentalSuitabilityHelper, geometryService, objectMapper, precisionAdjuster);
        when(modelRunService.getFilteredModelRuns(null, null, null, null)).thenReturn(runs);
        when(geometryService.getAllCountries()).thenReturn(countries);

        // Act
        String result = target.getPage(model);

        // Assert
        assertThat(result).isEqualTo("tools/dataextractor");
        verify(model).addAttribute("runs", "[{\"name\":\"NAME1\",\"disease\":\"DISEASE1\",\"date\":\"2015-02-01\"},{\"name\":\"NAME2\",\"disease\":\"DISEASE2\",\"date\":\"2015-03-02\"},{\"name\":\"NAME3\",\"disease\":\"DISEASE2\",\"date\":\"2015-04-03\"}]");
        verify(model).addAttribute("countries", "[{\"name\":\"NAME\",\"gaulCode\":54321,\"minX\":0.0,\"maxX\":2.0,\"minY\":0.0,\"maxY\":3.0},{\"name\":\"NAME1\",\"gaulCode\":432,\"minX\":-1.0,\"maxX\":5.0,\"minY\":-2.0,\"maxY\":3.0},{\"name\":\"NAME2\",\"gaulCode\":123,\"minX\":0.0,\"maxX\":4.0,\"minY\":0.0,\"maxY\":4.0}]");
    }

    private Country mockCountry(String name, int gaul, Polygon polygon) {
        Country country = mock(Country.class);
        when(country.getName()).thenReturn(name);
        when(country.getGaulCode()).thenReturn(gaul);
        when(country.getGeom()).thenReturn(GeometryUtils.createMultiPolygon(polygon));
        return country;
    }

    private ModelRun mockModelRun(String name, String disease, DateTime dateTime) {
        ModelRun run = mock(ModelRun.class);
        when(run.getName()).thenReturn(name);
        when(run.getRequestDate()).thenReturn(dateTime);
        when(run.getDiseaseGroup()).thenReturn(mock(DiseaseGroup.class));
        when(run.getDiseaseGroup().getShortNameForDisplay()).thenReturn(disease);
        return run;
    }

    @Test
    public void getPreciseDataForwardsCorrectResult() throws Exception {
        // Arrange
        String run = "name";
        double lat = 123;
        double lng = 321;
        Double expected = 666.666;

        ModelRun modelRun = mock(ModelRun.class);
        File raster = mock(File.class);
        ModelRunService modelRunService = mock(ModelRunService.class);
        RasterFilePathFactory rasterFilePathFactory = mock(RasterFilePathFactory.class);
        EnvironmentalSuitabilityHelper environmentalSuitabilityHelper = mock(EnvironmentalSuitabilityHelper.class);
        when(modelRunService.getModelRunByName(eq(run))).thenReturn(modelRun);
        when(rasterFilePathFactory.getMaskedMeanPredictionRasterFile(same(modelRun))).thenReturn(raster);
        when(environmentalSuitabilityHelper.findPointEnvironmentalSuitability(same(raster), eq(GeometryUtils.createPoint(lng, lat)))).thenReturn(expected);

        DataExtractorController target = new DataExtractorController(rasterFilePathFactory, modelRunService, environmentalSuitabilityHelper, mock(GeometryService.class), mock(AbraidJsonObjectMapper.class), mock(ModellingLocationPrecisionAdjuster.class));

        // Act
        ResponseEntity<String> result = target.getPreciseData(lat, lng, run);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(Double.toString(expected));
    }

    @Test
    public void getPreciseDataForwardsCorrectResultWhenNull() throws Exception {
        // Arrange
        String run = "name";
        double lat = 123;
        double lng = 321;
        Double expected = null;

        ModelRun modelRun = mock(ModelRun.class);
        File raster = mock(File.class);
        ModelRunService modelRunService = mock(ModelRunService.class);
        RasterFilePathFactory rasterFilePathFactory = mock(RasterFilePathFactory.class);
        EnvironmentalSuitabilityHelper environmentalSuitabilityHelper = mock(EnvironmentalSuitabilityHelper.class);
        when(modelRunService.getModelRunByName(eq(run))).thenReturn(modelRun);
        when(rasterFilePathFactory.getMaskedMeanPredictionRasterFile(same(modelRun))).thenReturn(raster);
        when(environmentalSuitabilityHelper.findPointEnvironmentalSuitability(same(raster), eq(GeometryUtils.createPoint(lng, lat)))).thenReturn(expected);

        DataExtractorController target = new DataExtractorController(rasterFilePathFactory, modelRunService, environmentalSuitabilityHelper, mock(GeometryService.class), mock(AbraidJsonObjectMapper.class), mock(ModellingLocationPrecisionAdjuster.class));

        // Act
        ResponseEntity<String> result = target.getPreciseData(lat, lng, run);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(Double.toString(-9999));
    }

    @Test
    public void getAdminUnitDataForwardsCorrectResult() throws Exception {
        // Arrange
        String run = "name";
        int gaul = 123;
        File cropped = File.createTempFile("test" , "expectation");
        FileUtils.writeStringToFile(cropped, "Expected Result");

        ModelRun modelRun = mock(ModelRun.class);
        File meanRaster = mock(File.class);
        File adminRaster = mock(File.class);
        ModelRunService modelRunService = mock(ModelRunService.class);
        RasterFilePathFactory rasterFilePathFactory = mock(RasterFilePathFactory.class);
        EnvironmentalSuitabilityHelper environmentalSuitabilityHelper = mock(EnvironmentalSuitabilityHelper.class);
        when(modelRunService.getModelRunByName(eq(run))).thenReturn(modelRun);
        when(rasterFilePathFactory.getMaskedMeanPredictionRasterFile(same(modelRun))).thenReturn(meanRaster);
        when(rasterFilePathFactory.getAdminRaster(eq(0))).thenReturn(adminRaster);
        when(environmentalSuitabilityHelper.createCroppedEnvironmentalSuitabilityRaster(eq(gaul), same(adminRaster), same(meanRaster))).thenReturn(cropped);

        DataExtractorController target = new DataExtractorController(rasterFilePathFactory, modelRunService, environmentalSuitabilityHelper, mock(GeometryService.class), mock(AbraidJsonObjectMapper.class), mock(ModellingLocationPrecisionAdjuster.class));

        // Act
        ResponseEntity<byte[]> result = target.getAdminUnitData(gaul, run, mock(HttpServletResponse.class));

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(new String(result.getBody())).isEqualTo("Expected Result");
        assertThat(cropped).doesNotExist();
    }
}
