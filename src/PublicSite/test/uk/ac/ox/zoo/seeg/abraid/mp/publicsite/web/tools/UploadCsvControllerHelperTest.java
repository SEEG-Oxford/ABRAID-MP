package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.apache.commons.mail.Email;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailServiceImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.EmailFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.service.DataAcquisitionService;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
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
                new Class[0], new File[] {new File(EMAIL_TEMPLATE_PATH)});
    }

    @Test
    public void acquireCsvDataSendsCorrectEmail() throws Exception {
        // Arrange
        byte[] csv = "Test csv".getBytes();
        boolean isBias = false;
        boolean isGoldStandard = false;
        DiseaseGroup biasDisease = mock(DiseaseGroup.class);
        when(biasDisease.getName()).thenReturn("name");
        String userEmailAddress = "user@email.com";
        String filePath = "/path/to/test.csv";

        String expectedSubject = "CSV upload results";
        String expectedEmailEnd =
                "Found 10 CSV file line(s) to convert.\n" +
                "Saved 10 disease occurrence(s) in 8 location(s) (of which 7 location(s) passed QC).\n";

        List<String> messages = Arrays.asList(
                "Found 10 CSV file line(s) to convert.",
                "Saved 10 disease occurrence(s) in 8 location(s) (of which 7 location(s) passed QC)."
        );
        when(dataAcquisitionService.acquireCsvData(csv, isBias, isGoldStandard, biasDisease)).thenReturn(messages);

        // Act
        helper.acquireCsvData(csv, isBias, isGoldStandard, biasDisease, userEmailAddress, filePath);

        // Assert
        String nowString = DateTimeFormat.longDateTime().print(DateTime.now());
        verify(email).setMsg(argThat(equalToIgnoringWhiteSpace(
                "Here are the results of the CSV upload that you submitted.\n" +
                "\n" +
                "File: \"/path/to/test.csv\".\n" +
                "Gold standard: No\n" +
                "Submitted on: " + nowString + ".\n" +
                "Completed on: " + nowString + ".\n" +
                "\n" + expectedEmailEnd)));
        verify(email).setSubject(eq(expectedSubject));
        verify(email).addTo(eq(userEmailAddress));
    }

    @Test
    public void acquireCsvDataSendsCorrectBiasEmail() throws Exception {
        // Arrange
        byte[] csv = "Test csv".getBytes();
        boolean isBias = true;
        boolean isGoldStandard = false;
        DiseaseGroup biasDisease = mock(DiseaseGroup.class);
        when(biasDisease.getName()).thenReturn("name");
        String userEmailAddress = "user@email.com";
        String filePath = "/path/to/test.csv";

        String expectedSubject = "CSV upload results";
        String expectedEmailEnd =
                "Found 10 CSV file line(s) to convert.\n" +
                        "Saved 10 disease occurrence(s) in 8 location(s) (of which 7 location(s) passed QC).\n";

        List<String> messages = Arrays.asList(
                "Found 10 CSV file line(s) to convert.",
                "Saved 10 disease occurrence(s) in 8 location(s) (of which 7 location(s) passed QC)."
        );
        when(dataAcquisitionService.acquireCsvData(csv, isBias, isGoldStandard, biasDisease)).thenReturn(messages);

        // Act
        helper.acquireCsvData(csv, isBias, isGoldStandard, biasDisease, userEmailAddress, filePath);

        // Assert
        String nowString = DateTimeFormat.longDateTime().print(DateTime.now());
        verify(email).setMsg(argThat(equalToIgnoringWhiteSpace(
                "Here are the results of the CSV upload that you submitted.\n" +
                        "\n" +
                        "File: \"/path/to/test.csv\".\n" +
                        "Bias dataset for: name\n" +
                        "Submitted on: " + nowString + ".\n" +
                        "Completed on: " + nowString + ".\n" +
                        "\n" + expectedEmailEnd)));
        verify(email).setSubject(eq(expectedSubject));
        verify(email).addTo(eq(userEmailAddress));
    }
}
