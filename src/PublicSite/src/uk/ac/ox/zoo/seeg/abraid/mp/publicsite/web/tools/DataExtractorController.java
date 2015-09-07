package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Country;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCountry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunSimple;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.EnvironmentalSuitabilityHelper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.GeometryUtils;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Collection;

import static ch.lambdaj.Lambda.convert;

/**
 * Controller for the Data Extractor tool.
 * Copyright (c) 2015 University of Oxford
 */
@Controller
public class DataExtractorController {
    private static final int NODATA_VALUE = -9999;
    private static final Logger LOGGER = Logger.getLogger(DataExtractorController.class);
    private static final String TEMP_FILE_NOT_REMOVED =
            "Temporary cropped raster file could not be removed (%s)";

    private RasterFilePathFactory rasterFilePathFactory;
    private ModelRunService modelRunService;
    private EnvironmentalSuitabilityHelper environmentalSuitabilityHelper;
    private GeometryService geometryService;
    private AbraidJsonObjectMapper objectMapper;

    @Autowired
    public DataExtractorController(
            RasterFilePathFactory rasterFilePathFactory, ModelRunService modelRunService,
            EnvironmentalSuitabilityHelper environmentalSuitabilityHelper, GeometryService geometryService,
            AbraidJsonObjectMapper objectMapper) {
        this.rasterFilePathFactory = rasterFilePathFactory;
        this.modelRunService = modelRunService;
        this.environmentalSuitabilityHelper = environmentalSuitabilityHelper;
        this.geometryService = geometryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Load the data extract page.
     * @param model The model.
     * @return The page.
     * @throws JsonProcessingException If the page data can not be loaded.
     */
    @RequestMapping(value = "/tools/location", method = RequestMethod.GET)
    public String getPage(Model model) throws JsonProcessingException {
        Collection<ModelRun> runs = modelRunService.getFilteredModelRuns(null, null, null, null);
        Collection<Country> countries = geometryService.getAllCountries();
        Collection<JsonModelRunSimple> jsonRuns = convertToRunDtos(runs);
        Collection<JsonCountry> jsonCountries = convertToCountryDtos(countries);
        model.addAttribute("runs", objectMapper.writeValueAsString(jsonRuns));
        model.addAttribute("countries", objectMapper.writeValueAsString(jsonCountries));
        return "tools/dataextractor";
    }

    private Collection<JsonModelRunSimple> convertToRunDtos(final Collection<ModelRun> runs) {
        return convert(runs, new Converter<ModelRun, JsonModelRunSimple>() {
            @Override
            public JsonModelRunSimple convert(ModelRun modelRun) {
                return new JsonModelRunSimple(modelRun);
            }
        });
    }

    private Collection<JsonCountry> convertToCountryDtos(final Collection<Country> countries) {
        return convert(countries, new Converter<Country, JsonCountry>() {
            @Override
            public JsonCountry convert(Country country) {
                return new JsonCountry(country);
            }
        });
    }

    /**
     * Calculates a precise lat/long environmental suitability.
     * @param lat The latitude.
     * @param lng The longitude.
     * @param run The model run name.
     * @return The suitability score.
     * @throws Exception 500.
     */
    @RequestMapping(value = "/tools/location/precise", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getPreciseData(final double lat, final double lng, final String run)
            throws Exception {
        ModelRun modelRun = modelRunService.getModelRunByName(run);
        File meanRaster = rasterFilePathFactory.getMaskedMeanPredictionRasterFile(modelRun);
        Double value = environmentalSuitabilityHelper.findPointEnvironmentalSuitability(
                meanRaster, GeometryUtils.createPoint(lng, lat));

        return new ResponseEntity<>(Double.toString((value == null) ? NODATA_VALUE : value), HttpStatus.OK);
    }

    /**
     * Calculates a cropped environmental suitability raster.
     * @param gaul The gaul code of the country to crop.
     * @param run The model run name.
     * @param response The servlet response object (provided by spring).
     * @return The cropped suitability raster.
     * @throws Exception 500.
     */
    @RequestMapping(value = "/tools/location/adminUnit", method = RequestMethod.GET,
            produces = "image/tiff")
    @ResponseBody
    public ResponseEntity<byte[]> getAdminUnitData(final int gaul, final String run, HttpServletResponse response)
            throws Exception {
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s_%s.tif\"", gaul, run));
        File adminRaster = rasterFilePathFactory.getAdminRaster(0);
        ModelRun modelRun = modelRunService.getModelRunByName(run);
        File meanRaster = rasterFilePathFactory.getMaskedMeanPredictionRasterFile(modelRun);
        File croppedFile = environmentalSuitabilityHelper.
                createCroppedEnvironmentalSuitabilityRaster(gaul, adminRaster, meanRaster);
        byte[] bytes = FileUtils.readFileToByteArray(croppedFile);
        if (!croppedFile.delete()) {
            LOGGER.warn(String.format(TEMP_FILE_NOT_REMOVED, croppedFile.getAbsolutePath()));
        }
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }
}
