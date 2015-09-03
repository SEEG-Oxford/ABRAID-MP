package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.api;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.api.v1.JsonApiModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.WrappedList;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Controller for the v1 JSON API.
 * Copyright (c) 2015 University of Oxford
 */
@Controller
public class ApiController extends AbstractController {
    private ModelRunService modelRunService;

    @Autowired
    public ApiController(ModelRunService modelRunService) {
        this.modelRunService = modelRunService;
    }

    /**
     * Gets a filtered subset of the model runs (completed - automatic).
     * @param name The name to filter on, or null.
     * @param diseaseGroupId The disease to filter on, or null.
     * @param minCompletionDate The min completion date to filter on, or null.
     * @param maxCompletionDate The max completion date to filter on, or null.
     * @return A filtered list of model runs.
     */
    @RequestMapping(value = "/api/v1/modelrun",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WrappedList<JsonApiModelRun>> getModelRuns(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer diseaseGroupId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date minCompletionDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date maxCompletionDate
    ) {
        List<ModelRun> runs = modelRunService.getFilteredModelRuns(
                name, diseaseGroupId, convertDate(minCompletionDate), convertDate(maxCompletionDate));

        List<JsonApiModelRun> converted = convert(
                runs,
                new Converter<ModelRun, JsonApiModelRun>() {
                    @Override
                    public JsonApiModelRun convert(ModelRun modelRun) {
                        return new JsonApiModelRun(modelRun);
                    }
                }
        );

        return new ResponseEntity<>(new WrappedList<>(converted), HttpStatus.OK);
    }

    private LocalDate convertDate(Date date) {
        return date == null ?  null : LocalDate.fromDateFields(date);
    }
}
