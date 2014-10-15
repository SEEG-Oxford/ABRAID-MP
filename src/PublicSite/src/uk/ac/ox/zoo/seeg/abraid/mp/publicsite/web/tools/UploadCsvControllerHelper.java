package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service.DataAcquisitionService;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for UploadCsvController, which performs the actual uploading of the CSV.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class UploadCsvControllerHelper {
    private static final Logger LOGGER = Logger.getLogger(UploadCsvControllerHelper.class);

    private static final String EMAIL_SUBJECT = "CSV upload results";
    private static final String EMAIL_TEMPLATE = "uploadCsvEmail.ftl";
    private static final String EMAIL_FAILED_MESSAGE = "Failed to send e-mail";

    private DataAcquisitionService dataAcquisitionService;
    private EmailService emailService;

    @Autowired
    public UploadCsvControllerHelper(DataAcquisitionService dataAcquisitionService, EmailService emailService) {
        this.dataAcquisitionService = dataAcquisitionService;
        this.emailService = emailService;
    }

    /**
     * Acquires the supplied CSV data. Sends an e-mail when completed (either successfully or unsuccessfully).
     *
     * @param csv The contents of the CSV file to upload.
     * @param isGoldStandard Whether or not this is a "gold standard" data set.
     * @param userEmailAddress The e-mail address of the user that submitted the upload.
     * @param filePath The full path to the file to upload (used for information only).
     */
    public void acquireCsvData(String csv, boolean isGoldStandard, String userEmailAddress, String filePath) {
        Timestamp submissionDate = getNowAsTimestamp();

        List<String> messages = dataAcquisitionService.acquireCsvData(csv, isGoldStandard);
        String message = StringUtils.join(messages, System.lineSeparator());

        Timestamp completionDate = getNowAsTimestamp();
        sendEmail(userEmailAddress, filePath, submissionDate, completionDate, message);
    }

    private void sendEmail(String userEmailAddress, String filePath, Timestamp submissionDate, Timestamp completionDate,
                           String message) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("filePath", filePath);
        templateData.put("submissionDate", submissionDate);
        templateData.put("completionDate", completionDate);
        templateData.put("message", message);

        try {
            emailService.sendEmail(userEmailAddress, EMAIL_SUBJECT, EMAIL_TEMPLATE, templateData);
        } catch (Exception e) {
            LOGGER.error(EMAIL_FAILED_MESSAGE, e);
        }
    }

    private Timestamp getNowAsTimestamp() {
        // Use a timestamp so that the e-mail template can format the date nicely
        return new Timestamp(DateTime.now().getMillis());
    }
}
