package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseModelRunLayerSet;
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
    private final ModelRunService modelRunService;
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
     * Displays the atlas home page.
     * @return The ftl template name to render.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showPage() {
        return "atlas/index";
    }

    /**
     * Displays the iframe content for the atlas home page.
     * @param model The template data to render into the template.
     * @return The ftl template name to render.
     * @throws JsonProcessingException Thrown if there is an issue generating the template data (available layers list).
     */
    @RequestMapping(value = "/atlas/content", method = RequestMethod.GET)
    public String showAtlas(Model model) throws JsonProcessingException {
        List<JsonDiseaseModelRunLayerSet> layers = prepareJsonDiseaseModelRunSets();
        model.addAttribute("layers", objectMapper.writeValueAsString(layers));
        return "atlas/content";
    }

    private List<JsonDiseaseModelRunLayerSet> prepareJsonDiseaseModelRunSets() {
        final Collection<ModelRun> modelRuns = modelRunService.getCompletedModelRuns();

        Map<Integer, List<JsonModelRunLayer>> layersByDiseaseId = new HashMap<>();
        for (ModelRun modelRun : modelRuns) {
            if (!layersByDiseaseId.containsKey(modelRun.getDiseaseGroupId())) {
                layersByDiseaseId.put(modelRun.getDiseaseGroupId(), new ArrayList<JsonModelRunLayer>());
            }
            layersByDiseaseId.get(modelRun.getDiseaseGroupId()).add(new JsonModelRunLayer(modelRun));
        }

        List<JsonDiseaseModelRunLayerSet> layers = new ArrayList<>();
        for (Map.Entry<Integer, List<JsonModelRunLayer>> diseasePair : layersByDiseaseId.entrySet()) {
            String name = diseaseService.getDiseaseGroupById(diseasePair.getKey()).getShortNameForDisplay();
            layers.add(new JsonDiseaseModelRunLayerSet(name, diseasePair.getValue()));
        }

        return layers;
    }
}
