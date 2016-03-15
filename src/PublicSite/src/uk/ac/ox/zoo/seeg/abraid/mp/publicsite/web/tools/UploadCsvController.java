package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import ch.lambdaj.function.convert.Converter;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.AbraidJsonObjectMapper;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ExpertService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.JsonParentDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.io.IOException;
import java.util.List;

import static ch.lambdaj.Lambda.convert;

/**
 * Controller for uploading a CSV file for data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class UploadCsvController extends AbstractController {
    private static final String FILE_EMPTY_MESSAGE = "CSV file not supplied.";
    private static final String BAD_DISEASE_MESSAGE = "Invalid background disease group specified.";

    private CurrentUserService currentUserService;
    private ExpertService expertService;
    private DiseaseService diseaseService;
    private AbraidJsonObjectMapper objectMapper;
    private UploadCsvControllerHelperAsyncWrapper uploadCsvControllerHelperAsyncWrapper;

    @Autowired
    public UploadCsvController(CurrentUserService currentUserService, ExpertService expertService,
                               DiseaseService diseaseService, AbraidJsonObjectMapper objectMapper,
                               UploadCsvControllerHelperAsyncWrapper uploadCsvControllerHelperAsyncWrapper) {
        this.currentUserService = currentUserService;
        this.expertService = expertService;
        this.diseaseService = diseaseService;
        this.objectMapper = objectMapper;
        this.uploadCsvControllerHelperAsyncWrapper = uploadCsvControllerHelperAsyncWrapper;
    }

    /**
     * Returns the Upload CSV page.
     * @param model The model.
     * @return The page.
     * @throws JsonProcessingException If the page data can not be loaded.
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/tools/uploadcsv", method = RequestMethod.GET)
    public String showCSVPage(Model model) throws JsonProcessingException {
        List<JsonParentDiseaseGroup> diseaseGroups = convert(
            diseaseService.getAllDiseaseGroups(),
            new Converter<DiseaseGroup, JsonParentDiseaseGroup>() {
                @Override
                public JsonParentDiseaseGroup convert(DiseaseGroup diseaseGroup) {
                    // We can use the simpler "JsonParentDiseaseGroup" because we only need name/id
                    return new JsonParentDiseaseGroup(diseaseGroup);
                }
            }
        );
        model.addAttribute("diseaseGroups", objectMapper.writeValueAsString(diseaseGroups));
        return "tools/uploadcsv";
    }

    /**
     * Uploads a CSV file to the database, by sending it to data acquisition.
     * @param file The CSV file to be uploaded.
     * @param isBias Whether or not this is a "bias" data set.
     * @param isGoldStandard Whether or not this is a "gold standard" data set (only relevant for non-bias data sets).
     * @param diseaseGroup The ID of the disease for which this is a bias data set (only relevant for bias data sets).
     * @return A response entity with JsonFileUploadResponse for compatibility with iframe based upload.
     * @throws Exception if upload could not be performed
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/tools/uploadcsv/upload", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<JsonFileUploadResponse> uploadCsvFile(
            MultipartFile file, boolean isBias, boolean isGoldStandard, int diseaseGroup) throws Exception {
        DiseaseGroup biasDisease = null;
        if (isBias) {
            biasDisease = diseaseService.getDiseaseGroupById(diseaseGroup);
            isGoldStandard = false;
        }

        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new JsonFileUploadResponse(false, FILE_EMPTY_MESSAGE), HttpStatus.BAD_REQUEST);
        } else if (isBias && biasDisease == null) {
            return new ResponseEntity<>(new JsonFileUploadResponse(false, BAD_DISEASE_MESSAGE), HttpStatus.BAD_REQUEST);
        } else {
            acquireCsvData(file, isBias, isGoldStandard, biasDisease);
            return new ResponseEntity<>(new JsonFileUploadResponse(), HttpStatus.OK);
        }
    }

    /**
     * Purges any uploaded sample bias data for a specified disease.
     * @param diseaseGroup The ID of the disease for which bias data should be removed.
     * @return HTTP Status code: 204 for success, 400 if any inputs are invalid.
     * @throws Exception if upload could not be performed
     */
    @Secured({ "ROLE_ADMIN" })
    @RequestMapping(value = "/tools/uploadcsv/purgeBiasData", method = RequestMethod.POST)
    public ResponseEntity purgeBiasData(int diseaseGroup) throws Exception {
        DiseaseGroup biasDisease = diseaseService.getDiseaseGroupById(diseaseGroup);
        if (biasDisease == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        diseaseService.deleteBiasDiseaseOccurrencesForDisease(biasDisease);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void acquireCsvData(MultipartFile file, boolean isBias, boolean isGoldStandard, DiseaseGroup biasDisease)
            throws IOException {
        byte[] csvFile = file.getBytes();
        String filePath = file.getOriginalFilename();

        String userEmailAddress = expertService.getExpertById(currentUserService.getCurrentUserId()).getEmail();
        uploadCsvControllerHelperAsyncWrapper.acquireCsvData(
                csvFile, isBias, isGoldStandard, biasDisease, userEmailAddress, filePath);
    }
}
