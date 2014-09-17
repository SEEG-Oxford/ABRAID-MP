package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.apache.commons.mail.Email;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.EmailFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.acquirers.DataAcquisitionException;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service.DataAcquisitionService;

import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Tests the UploadCsvControllerHelper class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class UploadCsvControllerHelperTest {
    private DataAcquisitionService dataAcquisitionService;
    private EmailService emailService;
    private Email email;
    private UploadCsvControllerHelper helper;

    private static final String EMAIL_TEMPLATE_PATH = "PublicSite/src/uk/ac/ox/zoo/seeg/abraid/mp/publicsite/web/tools";

    @Before
    public void setUp() throws Exception {
        dataAcquisitionService = mock(DataAcquisitionService.class);
        emailService = createEmailService();
        helper = new UploadCsvControllerHelper(dataAcquisitionService, emailService);

        DateTimeUtils.setCurrentMillisFixed(DateTime.now().getMillis());
    }

    private EmailService createEmailService() throws Exception {
        // Create a real e-mail service with a mock Email class, and specifying the path to our e-mail template
        email = mock(Email.class);
        EmailFactory factory = mock(EmailFactory.class);
        when(factory.createEmail()).thenReturn(email);
        return new EmailServiceImpl(factory, "", "", mock(SmtpConfiguration.class),
                new File[] {new File(EMAIL_TEMPLATE_PATH)});
    }

    @Test
    public void acquireCsvDataSendsCorrectEmailWhenAcquisitionSucceeds() throws Exception {
        // Arrange
        String csv = "Test csv";
        String message = "Saved 10 disease occurrence(s) in 8 location(s) (of which 7 location(s) passed QC)";
        String expectedSubject = "CSV upload succeeded";
        String expectedEmailEnd =
                "The upload succeeded with message:\r\n" +
                "\r\n" +
                "Saved 10 disease occurrence(s) in 8 location(s) (of which 7 location(s) passed QC)\r\n";

        when(dataAcquisitionService.acquireCsvData(csv)).thenReturn(message);

        // Act and assert
        acquireCsvDataSendsCorrectEmail(message, expectedEmailEnd, expectedSubject);
    }

    @Test
    public void acquireCsvDataSendsCorrectEmailWhenAcquisitionFails() throws Exception {
        // Arrange
        String csv = "Test csv";
        String message = "Some error during data acquisition";
        String expectedSubject = "CSV upload failed";
        String expectedEmailEnd =
                "The upload failed with message:\r\n" +
                "\r\n" +
                "Some error during data acquisition\r\n";

        when(dataAcquisitionService.acquireCsvData(csv)).thenThrow(new DataAcquisitionException(message));

        // Act and assert
        acquireCsvDataSendsCorrectEmail(message, expectedEmailEnd, expectedSubject);
    }

    private void acquireCsvDataSendsCorrectEmail(String message, String expectedEmailEnding, String expectedSubject)
            throws Exception {
        // Arrange
        String csv = "Test csv";
        String userEmailAddress = "user@email.com";
        String filePath = "/path/to/test.csv";

        // Act
        helper.acquireCsvData(csv, userEmailAddress, filePath);

        // Assert
        String nowString = DateTimeFormat.longDateTime().print(DateTime.now());
        verify(email).setMsg("Here are the results of the CSV upload that you submitted.\r\n" +
                "\r\n" +
                "File: /path/to/test.csv\r\n" +
                "Submitted on: " + nowString + "\r\n" +
                "Completed on: " + nowString + "\r\n" +
                "\r\n" + expectedEmailEnding);
        verify(email).setSubject(expectedSubject);
        verify(email).addTo(userEmailAddress);
    }
}
