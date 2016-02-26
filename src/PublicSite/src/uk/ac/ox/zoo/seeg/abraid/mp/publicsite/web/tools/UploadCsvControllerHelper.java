package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service.DataAcquisitionService;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for UploadCsvController, which performs the actual processing of uploaded CSV.
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
    private DiseaseService diseaseService;

    @Autowired
    public UploadCsvControllerHelper(DataAcquisitionService dataAcquisitionService, DiseaseService diseaseService,
                                     EmailService emailService) {
        this.dataAcquisitionService = dataAcquisitionService;
        this.diseaseService = diseaseService;
        this.emailService = emailService;
    }

    /**
     * Acquires the supplied CSV data. Sends an e-mail when completed (either successfully or unsuccessfully).
     * @param csv The contents of the CSV file to upload.
     * @param isBias Whether or not this is a "bias" data set.
     * @param isGoldStandard Whether or not this is a "gold standard" data set (only relevant for non-bias data sets).
     * @param biasDisease The ID of the disease for which this is a bias data set (only relevant for bias data sets).
     * @param userEmailAddress The e-mail address of the user that submitted the upload.
     * @param filePath The full path to the file to upload (used for information only).
     */
    public void acquireCsvData(byte[] csv, boolean isBias, boolean isGoldStandard, DiseaseGroup biasDisease,
                               String userEmailAddress, String filePath) {
        Timestamp submissionDate = getNowAsTimestamp();

        if (isBias) {
            diseaseService.deleteBiasDiseaseOccurrencesForDisease(biasDisease);
        }

        List<String> messages = dataAcquisitionService.acquireCsvData(csv, isBias, isGoldStandard, biasDisease);
        String message = StringUtils.join(messages, System.lineSeparator());
        String detail = getEmailDetailsLine(isBias, isGoldStandard, biasDisease);

        Timestamp completionDate = getNowAsTimestamp();
        sendEmail(userEmailAddress, filePath, submissionDate, completionDate, detail, message);
    }

    private String getEmailDetailsLine(boolean isBias, boolean isGoldStandard, DiseaseGroup biasDisease) {
        if (isBias) {
            return String.format("Bias dataset for: %s", biasDisease.getName());
        } else {
            return String.format("Gold standard: %s", (isGoldStandard ? "Yes" : "No"));
        }
    }

    private void sendEmail(String userEmailAddress, String filePath, Timestamp submissionDate, Timestamp completionDate,
                           String detail, String message) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("filePath", filePath);
        templateData.put("submissionDate", submissionDate);
        templateData.put("completionDate", completionDate);
        templateData.put("detail", detail);
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
