package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.EmailFactory;

import javax.mail.PasswordAuthentication;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for EmailServiceImpl.
 * Copyright (c) 2014 University of Oxford
 */
public class EmailServiceTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    @Test
    public void sendEmailCorrectlySetsUpSMTP() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "", "", expectation, new Class[0], new File[0]);

        // Act
        target.sendEmail("", "", "");

        // Assert
        verify(email, times(1)).setHostName(expectation.getAddress());
        verify(email, times(1)).setSmtpPort(expectation.getPort());
        verify(email, times(1)).setSslSmtpPort("" + expectation.getPort());
        verify(email, times(1)).setSSLOnConnect(expectation.useSSL());

        ArgumentCaptor<DefaultAuthenticator> captor = ArgumentCaptor.forClass(DefaultAuthenticator.class);
        verify(email, times(1)).setAuthenticator(captor.capture());
        DefaultAuthenticator authenticator = captor.getValue();
        Field protectedField = DefaultAuthenticator.class.getDeclaredField("authentication");
        protectedField.setAccessible(true);
        PasswordAuthentication authentication = (PasswordAuthentication) protectedField.get(authenticator);

        assertThat(authentication.getUserName()).isEqualTo(expectation.getUsername());
        assertThat(authentication.getPassword()).isEqualTo(expectation.getPassword());
    }

    @Test
    public void sendEmailCorrectlySetFields() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation, new Class[0], new File[0]);

        // Act
        target.sendEmail("ToAddress", "Subject", "Body");

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setFrom("FromAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
    }

    @Test
    public void sendEmailSendsMessage() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation, new Class[0], new File[0]);

        // Act
        target.sendEmail("ToAddress", "Subject", "Body");

        // Assert
        verify(email, times(1)).send();
    }

    private EmailFactory arrangeEmailFactory(Email email) {
        EmailFactory factory = mock(EmailFactory.class);
        if (email != null) {
            when(factory.createEmail()).thenReturn(email);
        } else {
            when(factory.createEmail()).thenReturn(mock(Email.class));
        }
        return factory;
    }

    private SmtpConfiguration arrangeSMTP() {
        SmtpConfiguration smtp = mock(SmtpConfiguration.class);
        when(smtp.getAddress()).thenReturn("abc");
        when(smtp.getPort()).thenReturn(54321);
        when(smtp.useSSL()).thenReturn(true);
        when(smtp.getUsername()).thenReturn("cab");
        when(smtp.getPassword()).thenReturn("cba");
        return smtp;
    }

    @Test
    public void sendEmailCanGenerateMessageFromTemplate() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);

        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "expected result");

        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation,
                new Class[0], new File[] {testFolder.getRoot()});

        // Act
        target.sendEmail("", "", "template.ftl", null);

        // Assert
        verify(email, times(1)).setMsg("expected result");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailFindsCorrectTemplate() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        File dir1 = testFolder.newFolder();
        File dir2 = testFolder.newFolder();
        FileUtils.writeStringToFile(new File(dir1.toString(), "template"), "unexpected result");
        FileUtils.writeStringToFile(new File(dir2.toString(), "templat.ftl"), "unexpected result");
        FileUtils.writeStringToFile(new File(dir2.toString(), "template.ftl"), "expected result");

        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation,
                new Class[0], new File[] {dir1, dir2});

        // Act
        target.sendEmail("", "", "template.ftl", null);

        // Assert
        verify(email, times(1)).setMsg("expected result");
    }

    @Test
    public void sendEmailGivesCorrectTemplatePrecedence() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        File dir1 = testFolder.newFolder();
        File dir2 = testFolder.newFolder();
        FileUtils.writeStringToFile(new File(dir1.toString(), "template.ftl"), "expected result");
        FileUtils.writeStringToFile(new File(dir2.toString(), "template.ftl"), "unexpected result");

        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation,
                new Class[0], new File[] {dir1, dir2});

        // Act
        target.sendEmail("", "", "template.ftl", null);

        // Assert
        verify(email, times(1)).setMsg("expected result");
    }

    @Test
    public void sendEmailAppliesTemplateToDataCorrectly() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "expected result${foo}");

        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation,
                new Class[0], new File[] {testFolder.getRoot()});

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("foo", "!");

        // Act
        target.sendEmail("", "", "template.ftl", data);

        // Assert
        verify(email, times(1)).setMsg("expected result!");
    }

    @Test
    public void sendEmailSendsCorrectMessage() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation, new Class[0],  new File[0]);

        // Act
        target.sendEmail("ToAddress", "Subject", "Body");

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailSendsCorrectTemplatedMessage() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "Body");
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation, new Class[0], new File[] {testFolder.getRoot()});

        // Act
        target.sendEmail("ToAddress", "Subject", "template.ftl", new HashMap<String, Object>());

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailInBackgroundSendsCorrectMessage() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation, new Class[0], new File[0]);

        // Act
        Future result = target.sendEmailInBackground("ToAddress", "Subject", "Body");
        result.get();

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailInBackgroundSendsCorrectTemplatedMessage() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "Body");
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "", expectation, new Class[0], new File[] {testFolder.getRoot()});

        // Act
        Future result = target.sendEmailInBackground("ToAddress", "Subject", "template.ftl", new HashMap<String, Object>());
        result.get();

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailSendsCorrectMessageWithDefaultToAddress() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "ToAddress", expectation, new Class[0], new File[0]);

        // Act
        target.sendEmail("Subject", "Body");

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailSendsCorrectTemplatedMessageWithDefaultToAddress() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "Body");
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "ToAddress", expectation, new Class[0], new File[] {testFolder.getRoot()});

        // Act
        target.sendEmail("Subject", "template.ftl", new HashMap<String, Object>());

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailInBackgroundSendsCorrectMessageWithDefaultToAddress() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "ToAddress", expectation, new Class[0], new File[0]);

        // Act
        Future result = target.sendEmailInBackground("Subject", "Body");
        result.get();

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }

    @Test
    public void sendEmailInBackgroundSendsCorrectTemplatedMessageWithDefaultToAddress() throws Exception {
        // Arrange
        SmtpConfiguration expectation = arrangeSMTP();
        Email email = mock(Email.class);
        FileUtils.writeStringToFile(new File(testFolder.getRoot().toString(), "template.ftl"), "Body");
        EmailServiceImpl target = new EmailServiceImpl(arrangeEmailFactory(email), "FromAddress", "ToAddress", expectation, new Class[0], new File[] {testFolder.getRoot()});

        // Act
        Future result = target.sendEmailInBackground("Subject", "template.ftl", new HashMap<String, Object>());
        result.get();

        // Assert
        verify(email, times(1)).addTo("ToAddress");
        verify(email, times(1)).setSubject("Subject");
        verify(email, times(1)).setMsg("Body");
        verify(email, times(1)).send();
    }
}
