package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseModelRunSet;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunLayer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.util.*;

/**
 * Controller for the Atlas Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AtlasController extends AbstractController {
    private ModelRunService modelRunService;
    private final DiseaseService diseaseService;
    private final AbraidJsonObjectMapper objectMapper;

    @Autowired
    public AtlasController(ModelRunService modelRunService, DiseaseService diseaseService,
                           AbraidJsonObjectMapper objectMapper) {
        this.modelRunService = modelRunService;
        this.diseaseService = diseaseService;
        this.objectMapper = objectMapper;
    }

    /**
     * Return the view to display.
     * @return The ftl page name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showPage() {
        return "atlas/index";
    }

    @RequestMapping(value = "/atlas/content", method = RequestMethod.GET)
    public String showAtlas(Model model) throws JsonProcessingException {
        List<JsonDiseaseModelRunSet> layers = prepareJsonDiseaseModelRunSets();
        model.addAttribute("layers", objectMapper.writeValueAsString(layers));
        return "atlas/content";
    }

    private List<JsonDiseaseModelRunSet> prepareJsonDiseaseModelRunSets() {
        final Collection<ModelRun> modelRuns = modelRunService.getCompletedModelRuns();

        Map<Integer, List<JsonModelRunLayer>> layersByDiseaseId = new HashMap<>();
        for (ModelRun modelRun : modelRuns) {
            if (layersByDiseaseId.containsKey(modelRun.getDiseaseGroupId())) {
                layersByDiseaseId.get(modelRun.getDiseaseGroupId()).add(new JsonModelRunLayer(modelRun));
            } else {
                layersByDiseaseId.put(modelRun.getDiseaseGroupId(), Arrays.asList(new JsonModelRunLayer(modelRun)));
            }
        }

        List<JsonDiseaseModelRunSet> layers = new ArrayList<>();
        for (Integer diseaseId: layersByDiseaseId.keySet()) {
            String name = diseaseService.getDiseaseGroupById(diseaseId).getPublicName();
            layers.add(new JsonDiseaseModelRunSet(name, layersByDiseaseId.get(diseaseId)));
        }

        return layers;
    }
}
