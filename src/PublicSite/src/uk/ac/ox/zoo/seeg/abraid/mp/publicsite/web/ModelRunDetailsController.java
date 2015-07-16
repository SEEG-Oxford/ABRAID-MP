package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Controller for accessing additional information about model runs.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class ModelRunDetailsController extends AbstractController {

    /** Base URL for Atlas model run details. */
    private static final String ATLAS_MODEL_RUN_DETAILS_URL = "/atlas/details/modelrun";
    private final ModelRunService modelRunService;

    @Autowired
    public ModelRunDetailsController(ModelRunService modelRunService) {
        this.modelRunService = modelRunService;
    }

    /**
     * Gets the set of summarising statistics across all submodels of a model run.
     * @param modelRunName The unique name of the model run.
     * @return The JSON of statistics.
     */
    @RequestMapping(value = ATLAS_MODEL_RUN_DETAILS_URL + "/{modelRunName}/statistics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Transactional
    public ResponseEntity<JsonModelRunStatistics> getModelRunSummaryStatistics(@PathVariable String modelRunName) {
        ModelRun modelRun = modelRunService.getModelRunByName(modelRunName);
        if (modelRun == null || modelRun.getStatus() != ModelRunStatus.COMPLETED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            List<SubmodelStatistic> submodelStatistics = modelRun.getSubmodelStatistics();
            return new ResponseEntity<>(SubmodelStatistic.summarise(submodelStatistics), HttpStatus.OK);
        }
    }


    /**
     * Gets the list of covariate influences associated with a model run.
     * @param modelRunName The unique name of the model run.
     * @return The JSON of covariate influences.
     */
    @RequestMapping(value = ATLAS_MODEL_RUN_DETAILS_URL + "/{modelRunName}/covariates",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseBody
    public ResponseEntity<List<JsonCovariateInfluence>> getCovariateInfluences(@PathVariable String modelRunName) {
        ModelRun modelRun = modelRunService.getModelRunByName(modelRunName);
        if (modelRun == null || modelRun.getStatus() != ModelRunStatus.COMPLETED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            List<CovariateInfluence> covariateInfluences = modelRun.getCovariateInfluences();
            List<EffectCurveCovariateInfluence> effectCurves = modelRun.getEffectCurveCovariateInfluences();
            return new ResponseEntity<>(convertToJson(covariateInfluences, effectCurves), HttpStatus.OK);
        }
    }

    /**
     * Gets the list of effect curve covariate influences associated with a model run.
     * @param modelRunName The unique name of the model run.
     * @return The DTO of effect curve covariate influences.
     */
    @RequestMapping(value = ATLAS_MODEL_RUN_DETAILS_URL + "/{modelRunName}/effectcurves", method = RequestMethod.GET)
    @Transactional
    @ResponseBody
    public ResponseEntity<WrappedList<JsonEffectCurveCovariateInfluence>> getEffectCurveCovariateInfluences(
            @PathVariable String modelRunName) {
        ModelRun modelRun = modelRunService.getModelRunByName(modelRunName);
        if (modelRun == null || modelRun.getStatus() != ModelRunStatus.COMPLETED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            List<EffectCurveCovariateInfluence> covariateInfluences = modelRun.getEffectCurveCovariateInfluences();
            return new ResponseEntity<>(convertToDto(covariateInfluences), HttpStatus.OK);
        }
    }

    /**
     * Gets the list of input disease occurrences associated with a model run.
     * @param modelRunName The unique name of the model run.
     * @return The DTO of input disease occurrences.
     */
    @RequestMapping(
            value = ATLAS_MODEL_RUN_DETAILS_URL + "/{modelRunName}/inputoccurrences", method = RequestMethod.GET)
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



    private List<JsonCovariateInfluence> convertToJson(List<CovariateInfluence> covariateInfluences,
                                                       List<EffectCurveCovariateInfluence> effectCurves) {
        List<JsonCovariateInfluence> json = new ArrayList<>();
        if (!covariateInfluences.isEmpty()) {
            Collections.sort(covariateInfluences, new Comparator<CovariateInfluence>() {
                @Override
                public int compare(CovariateInfluence o1, CovariateInfluence o2) {
                    return o2.getMeanInfluence().compareTo(o1.getMeanInfluence());  // desc
                }
            });
            for (CovariateInfluence covariateInfluence : covariateInfluences) {
                List<EffectCurveCovariateInfluence> curve = filter(
                        having(on(EffectCurveCovariateInfluence.class).getCovariateFile().getId(),
                        equalTo(covariateInfluence.getCovariateFile().getId())),
                        effectCurves);
                json.add(new JsonCovariateInfluence(
                        covariateInfluence, convertHistogramDTOs(covariateInfluence), convertToDto(curve).getList()));
            }
        }
        return json;
    }

    private List<JsonCovariateValueBin> convertHistogramDTOs(CovariateInfluence covariateInfluence) {
        return convert(covariateInfluence.getCovariateFile().getCovariateValueHistogramData(),
                new Converter<CovariateValueBin, JsonCovariateValueBin>() {
            @Override
            public JsonCovariateValueBin convert(CovariateValueBin domainObject) {
                return new JsonCovariateValueBin(domainObject);
            }
        });
    }

    private WrappedList<JsonEffectCurveCovariateInfluence> convertToDto(
            List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences) {
        List<JsonEffectCurveCovariateInfluence> dtos = new ArrayList<>();
        if (!effectCurveCovariateInfluences.isEmpty()) {
            for (EffectCurveCovariateInfluence covariateInfluence : effectCurveCovariateInfluences) {
                dtos.add(new JsonEffectCurveCovariateInfluence(covariateInfluence));
            }
            Collections.sort(dtos, new Comparator<JsonEffectCurveCovariateInfluence>() {
                @Override
                public int compare(JsonEffectCurveCovariateInfluence o1, JsonEffectCurveCovariateInfluence o2) {
                    return new CompareToBuilder()
                            .append(o1.getName(), o2.getName())
                            .append(o1.getCovariateValue(), o2.getCovariateValue())
                            .toComparison();
                }
            });
        }
        return new WrappedList<>(dtos);
    }

    private WrappedList<JsonDownloadDiseaseOccurrence> convertToJsonDiseaseOccurrenceDtos(
            List<DiseaseOccurrence> inputDiseaseOccurrences) {
        List<JsonDownloadDiseaseOccurrence> json = new ArrayList<>();
        if (!inputDiseaseOccurrences.isEmpty()) {
            for (DiseaseOccurrence inputDiseaseOccurrence : inputDiseaseOccurrences) {
                json.add(new JsonDownloadDiseaseOccurrence(inputDiseaseOccurrence));
            }
        }
        return new WrappedList<>(json);
    }

}
