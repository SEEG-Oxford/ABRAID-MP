package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonFileUploadResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.AbstractController;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;

import java.io.IOException;

/**
 * Controller for uploading a CSV file for data acquisition.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Controller
public class UploadCsvController extends AbstractController {
    private static final String FILE_EMPTY_MESSAGE = "CSV file not supplied.";

    private CurrentUserService currentUserService;
    private UploadCsvControllerHelperAsyncWrapper uploadCsvControllerHelperAsyncWrapper;

    @Autowired
    public UploadCsvController(CurrentUserService currentUserService,
                               UploadCsvControllerHelperAsyncWrapper uploadCsvControllerHelperAsyncWrapper) {
        this.currentUserService = currentUserService;
        this.uploadCsvControllerHelperAsyncWrapper = uploadCsvControllerHelperAsyncWrapper;
    }

    /**
     * Returns the Upload CSV page.
     * @return The Upload CSV page name.
     */
    @Secured({ "ROLE_SEEG" })
    @RequestMapping(value = "/tools/uploadcsv", method = RequestMethod.GET)
    public String showCovariatesPage() {
        return "tools/uploadcsv";
    }

    /**
     * Uploads a CSV file to the database, by sending it to data acquisition.
     * @param file The CSV file to be uploaded.
     * @return A response entity with JsonFileUploadResponse for compatibility with iframe based upload.
     */
    @Secured({ "ROLE_SEEG" })
    @RequestMapping(value = "/tools/uploadcsv/upload", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<JsonFileUploadResponse> uploadCsvFile(MultipartFile file)
            throws Exception {
        if (file == null || file.isEmpty()) {
            return new ResponseEntity<>(new JsonFileUploadResponse(false, FILE_EMPTY_MESSAGE), HttpStatus.BAD_REQUEST);
        } else {
            acquireCsvData(file);
            return new ResponseEntity<>(new JsonFileUploadResponse(), HttpStatus.OK);
        }
    }

    private void acquireCsvData(MultipartFile file) throws IOException {
        String csvFile = new String(file.getBytes());
        String filePath = file.getOriginalFilename();
        String userEmailAddress = currentUserService.getCurrentUser().getUsername();
        uploadCsvControllerHelperAsyncWrapper.acquireCsvData(csvFile, userEmailAddress, filePath);
    }
}
