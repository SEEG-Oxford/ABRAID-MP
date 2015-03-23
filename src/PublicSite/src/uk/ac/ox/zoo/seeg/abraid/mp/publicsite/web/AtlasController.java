package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDiseaseModelRunLayerSet;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonModelRunLayer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.util.*;

/**
 * Controller for the Atlas Home page.
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class AtlasController extends AbstractController {
    private final ModelRunService modelRunService;
    private final DiseaseService diseaseService;
    private final CurrentUserService currentUserService;
    private final ExpertService expertService;
    private final AbraidJsonObjectMapper objectMapper;

    @Autowired
    public AtlasController(ModelRunService modelRunService, DiseaseService diseaseService,
                           CurrentUserService currentUserService, ExpertService expertService,
                           AbraidJsonObjectMapper objectMapper) {
        this.modelRunService = modelRunService;
        this.diseaseService = diseaseService;
        this.currentUserService = currentUserService;
        this.expertService = expertService;
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
        boolean seegMember = userIsSeegMember();
        List<JsonDiseaseModelRunLayerSet> layers = prepareJsonDiseaseModelRunSets(seegMember);
        model.addAttribute("layers", objectMapper.writeValueAsString(layers));
        model.addAttribute("seegMember", seegMember);
        return "atlas/content";
    }

    private List<JsonDiseaseModelRunLayerSet> prepareJsonDiseaseModelRunSets(boolean seegMember) {
        final List<Integer> diseaseGroupsInAutomaticModelRuns =
                diseaseService.getDiseaseGroupIdsForAutomaticModelRuns();
        final Collection<ModelRun> modelRuns = modelRunService.getCompletedModelRunsForDisplay();

        Map<Integer, List<JsonModelRunLayer>> layersByDiseaseId = new HashMap<>();
        for (ModelRun modelRun : modelRuns) {
            int diseaseGroupId = modelRun.getDiseaseGroupId();
            boolean automaticRun = diseaseGroupsInAutomaticModelRuns.contains(diseaseGroupId);
            if (seegMember || automaticRun) {
                if (!layersByDiseaseId.containsKey(diseaseGroupId)) {
                    layersByDiseaseId.put(diseaseGroupId, new ArrayList<JsonModelRunLayer>());
                }
                layersByDiseaseId.get(diseaseGroupId).add(new JsonModelRunLayer(modelRun, automaticRun));
            }
        }

        List<JsonDiseaseModelRunLayerSet> layers = new ArrayList<>();
        for (Map.Entry<Integer, List<JsonModelRunLayer>> diseasePair : layersByDiseaseId.entrySet()) {
            String name = diseaseService.getDiseaseGroupById(diseasePair.getKey()).getShortNameForDisplay();
            layers.add(new JsonDiseaseModelRunLayerSet(name, diseasePair.getValue()));
        }

        return layers;
    }

    private boolean userIsSeegMember() {
        Integer expertId = currentUserService.getCurrentUserId();
        if (expertId == null) {
            return false;
        } else {
            Expert expert = expertService.getExpertById(expertId);
            if (expert == null) {
                throw new IllegalArgumentException("Logged in user does not have an associated expert.");
            } else {
                return expert.isSeegMember();
            }
        }
    }
}
