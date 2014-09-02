package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.EmailFactoryImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Integration test for EmailServiceImpl.
 * Copyright (c) 2014 University of Oxford
 */
@Ignore
public class EmailServiceIntegrationTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void sendEmailNow() throws Exception {
        EmailService emailService = new EmailServiceImpl(
                new EmailFactoryImpl(),
                "EmailServiceIntegrationTestFrom@uk.ac.ox.zoo.seeg.abraid.mp.common.service.core",
                "EmailServiceIntegrationTestTo@uk.ac.ox.zoo.seeg.abraid.mp.common.service.core",
                new SmtpConfiguration(
                    "mailtrap.io",
                    2525,
                    false,
                    "235398130c23105ca",
                    "b69aea70436f30"
                ),
                new File[] {testFolder.getRoot()});

        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "${foo} ${date}");
        Map<String, Object> data = new HashMap<>();
        data.put("foo", "bar");
        data.put("date", DateTime.now());

        emailService.sendEmail(
                "sendEmailNow@EmailServiceIntegrationTest",
                "Test " + DateTime.now().toString(),
                "template.ftl",
                data);
    }
}

