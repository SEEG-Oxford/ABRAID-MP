package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.GeoJsonDiseaseOccurrenceFeatureCollection;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDownloadDiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.ModellingLocationPrecisionAdjuster;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for accessing data used in model runs.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunDataController {
    private static final String ATLAS_MODEL_RUN_DATA_URL = "/atlas/data/modelrun";
    private final ModelRunService modelRunService;
    private ModellingLocationPrecisionAdjuster modellingLocationPrecisionAdjuster;

    @Autowired
    public ModelRunDataController(ModelRunService modelRunService,
                                  ModellingLocationPrecisionAdjuster modellingLocationPrecisionAdjuster) {
        this.modelRunService = modelRunService;
        this.modellingLocationPrecisionAdjuster = modellingLocationPrecisionAdjuster;
    }

    /**
     * Gets the list of input disease occurrences associated with a model run, as geojson.
     * @param modelRunName The unique name of the model run.
     * @return The DTO of input disease occurrences.
     */
    @RequestMapping(
            value = ATLAS_MODEL_RUN_DATA_URL + "/{modelRunName}/geojson", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public ResponseEntity<GeoJsonDiseaseOccurrenceFeatureCollection> getInputDiseaseOccurrencesGeoJson(
            @PathVariable String modelRunName) {
        ModelRun modelRun = modelRunService.getModelRunByName(modelRunName);
        if (modelRun == null || modelRun.getStatus() != ModelRunStatus.COMPLETED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            List<DiseaseOccurrence> inputDiseaseOccurrences = modelRun.getInputDiseaseOccurrences();
            return new ResponseEntity<>(
                    new GeoJsonDiseaseOccurrenceFeatureCollection(inputDiseaseOccurrences), HttpStatus.OK);
        }
    }

    /**
     * Gets the list of input disease occurrences associated with a model run, as csv.
     * @param modelRunName The unique name of the model run.
     * @return The DTO of input disease occurrences.
     */
    @RequestMapping(value = ATLAS_MODEL_RUN_DATA_URL + "/{modelRunName}/inputoccurrences", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public ResponseEntity<WrappedList<JsonDownloadDiseaseOccurrence>> getInputDiseaseOccurrences(
            @PathVariable String modelRunName) {
        ModelRun modelRun = modelRunService.getModelRunByName(modelRunName);
        if (modelRun == null || modelRun.getStatus() != ModelRunStatus.COMPLETED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            List<DiseaseOccurrence> inputDiseaseOccurrences = modelRun.getInputDiseaseOccurrences();
            return new ResponseEntity<>(convertToJsonDiseaseOccurrenceDtos(inputDiseaseOccurrences), HttpStatus.OK);
        }
    }

    private WrappedList<JsonDownloadDiseaseOccurrence> convertToJsonDiseaseOccurrenceDtos(
            List<DiseaseOccurrence> inputDiseaseOccurrences) {
        List<JsonDownloadDiseaseOccurrence> json = new ArrayList<>();
        if (!inputDiseaseOccurrences.isEmpty()) {
            for (DiseaseOccurrence inputDiseaseOccurrence : inputDiseaseOccurrences) {
                json.add(new JsonDownloadDiseaseOccurrence(modellingLocationPrecisionAdjuster, inputDiseaseOccurrence));
            }
        }
        return new WrappedList<>(json);
    }
}
