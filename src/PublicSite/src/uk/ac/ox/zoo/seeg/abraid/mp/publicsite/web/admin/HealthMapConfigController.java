package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.HealthMapSubDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonHealthMapDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonHealthMapSubDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonNamedEntry;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.HealthMapService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;

import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Controller for the HealthMap configuration page.
 * Copyright (c) 2015 University of Oxford
 */
@Controller
public class HealthMapConfigController extends AbstractController {
    private static final String ADMIN_HEALTHMAP_BASE_URL = "/admin/healthmap";

    private final HealthMapService healthMapService;
    private final DiseaseService diseaseService;
    private final ObjectWriter jsonWriter;

    @Autowired
    public HealthMapConfigController(HealthMapService healthMapService, DiseaseService diseaseService,
                                     AbraidJsonObjectMapper objectMapper) {
        this.healthMapService = healthMapService;
        this.diseaseService = diseaseService;
        this.jsonWriter = objectMapper.writer();
    }

    /**
     * Gets the HealthMap config page.
     * @param model The model.
     * @return The ftl page name.
     * @throws JsonProcessingException Thrown if there is an error during JSON serialization.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = ADMIN_HEALTHMAP_BASE_URL, method = RequestMethod.GET)
    public String getHealthMapConfigPage(Model model) throws JsonProcessingException {
        model.addAttribute("healthMapDiseases", jsonWriter.writeValueAsString(listHealthMapDiseases()));
        model.addAttribute("healthMapSubDiseases", jsonWriter.writeValueAsString(listHealthMapSubDiseases()));
        model.addAttribute("abraidDiseases", jsonWriter.writeValueAsString(listAbraidDiseases()));
        return "admin/healthMapConfig";
    }

    private List<JsonHealthMapDisease> listHealthMapDiseases() {
        return convertHealthMapDiseasesToJson(healthMapService.getAllHealthMapDiseases());
    }

    private List<JsonHealthMapSubDisease> listHealthMapSubDiseases() {
        return convertHealthMapSubDiseasesToJson(healthMapService.getAllHealthMapSubDiseases());
    }

    private List<JsonNamedEntry> listAbraidDiseases() {
        return convertAbraidDiseasesToJson(diseaseService.getAllDiseaseGroups());
    }

    /**
     * Saves the changes to a HealthMap disease.
     * @param json The JSON containing the new values to save.
     * @return HTTP Status code: 204 for success, 400 if any inputs are invalid.
     */
    @Secured({ "ROLE_ADMIN" })
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = ADMIN_HEALTHMAP_BASE_URL + "/updateDisease", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateHealthMapDisease(@RequestBody JsonHealthMapDisease json) {
        if (!validateJsonHealthMapDisease(json)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        boolean hasAbraidDisease = (json.getAbraidDisease() != null);
        DiseaseGroup abraidDisease =
                hasAbraidDisease ? diseaseService.getDiseaseGroupById(json.getAbraidDisease().getId()) : null;
        HealthMapDisease disease = healthMapService.getHealthMapDiseasesById(json.getId());
        if (disease == null || (hasAbraidDisease && abraidDisease == null)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        disease.setDiseaseGroup(abraidDisease);
        healthMapService.saveHealthMapDisease(disease);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Saves the changes to a HealthMap subdisease.
     * @param json The JSON containing the new values to save.
     * @return HTTP Status code: 204 for success, 400 if any inputs are invalid.
     */
    @Secured({ "ROLE_ADMIN" })
    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = ADMIN_HEALTHMAP_BASE_URL + "/updateSubDisease", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateHealthMapSubDisease(@RequestBody JsonHealthMapSubDisease json) {
        if (!validateJsonHealthMapSubDisease(json)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        boolean hasAbraidDisease = (json.getAbraidDisease() != null);
        DiseaseGroup abraidDisease =
                hasAbraidDisease ? diseaseService.getDiseaseGroupById(json.getAbraidDisease().getId()) : null;
        boolean hasParentDisease = (json.getParent() != null);
        HealthMapDisease parentDisease =
                hasParentDisease ? healthMapService.getHealthMapDiseasesById(json.getParent().getId()) : null;
        HealthMapSubDisease disease = healthMapService.getHealthMapSubDiseasesById(json.getId());
        if (disease == null || (hasAbraidDisease && abraidDisease == null) ||
                               (hasParentDisease && parentDisease == null)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        disease.setDiseaseGroup(abraidDisease);
        disease.setHealthMapDisease(parentDisease);
        healthMapService.saveHealthMapSubDisease(disease);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private List<JsonHealthMapDisease> convertHealthMapDiseasesToJson(List<HealthMapDisease> diseases) {
        return convert(diseases, new Converter<HealthMapDisease, JsonHealthMapDisease>() {
            @Override
            public JsonHealthMapDisease convert(HealthMapDisease disease) {
                return convertHealthMapDiseaseToJson(disease);
            }
        });
    }

    private List<JsonHealthMapSubDisease> convertHealthMapSubDiseasesToJson(List<HealthMapSubDisease> subDiseases) {
        return convert(subDiseases, new Converter<HealthMapSubDisease, JsonHealthMapSubDisease>() {
            @Override
            public JsonHealthMapSubDisease convert(HealthMapSubDisease healthMapDisease) {
                return convertHealthMapSubDiseaseToJson(healthMapDisease);
            }
        });
    }

    private JsonHealthMapDisease convertHealthMapDiseaseToJson(HealthMapDisease disease) {
        return new JsonHealthMapDisease(
                disease.getId(),
                disease.getName(),
                convertAbraidDiseaseToJson(disease.getDiseaseGroup())
        );
    }

    private JsonHealthMapSubDisease convertHealthMapSubDiseaseToJson(HealthMapSubDisease subDisease) {
        return new JsonHealthMapSubDisease(
                subDisease.getId(),
                subDisease.getName(),
                convertAbraidDiseaseToJson(subDisease.getDiseaseGroup()),
                convertParentHealthMapDisease(subDisease.getHealthMapDisease())
        );
    }

    private JsonNamedEntry convertParentHealthMapDisease(HealthMapDisease parent) {
        return parent == null ? null : new JsonNamedEntry(parent.getId(), parent.getName());
    }

    private List<JsonNamedEntry> convertAbraidDiseasesToJson(List<DiseaseGroup> diseaseGroups) {
        return convert(diseaseGroups, new Converter<DiseaseGroup, JsonNamedEntry>() {
            @Override
            public JsonNamedEntry convert(DiseaseGroup diseaseGroup) {
                return convertAbraidDiseaseToJson(diseaseGroup);
            }
        });
    }

    private JsonNamedEntry convertAbraidDiseaseToJson(DiseaseGroup diseaseGroup) {
        return diseaseGroup == null ? null : new JsonNamedEntry(diseaseGroup.getId(), diseaseGroup.getName());
    }

    private boolean validateJsonHealthMapDisease(JsonHealthMapDisease disease) {
        return validateJsonNamedEntry(disease, false) && validateJsonNamedEntry(disease.getAbraidDisease(), true);
    }

    private boolean validateJsonHealthMapSubDisease(JsonHealthMapSubDisease disease) {
        return validateJsonHealthMapDisease(disease) && validateJsonNamedEntry(disease.getParent(), true);
    }

    private boolean validateJsonNamedEntry(JsonNamedEntry entry, boolean allowNull) {
        if (allowNull) {
            return entry == null || (entry.getId() != null && StringUtils.isNotBlank(entry.getName()));
        } else {
            return entry != null && (entry.getId() != null && StringUtils.isNotBlank(entry.getName()));
        }

    }
}
