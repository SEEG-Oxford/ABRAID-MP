package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.springframework.ui.Model;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.EmailFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A service for sending emails.
 * Copyright (c) 2014 University of Oxford
 */
public class EmailServiceImpl implements EmailService {
    private final EmailFactory emailFactory;
    private final String fromAddress;
    private final SmtpConfiguration smtpConfig;
    private final Configuration freemarkerConfig;

    public EmailServiceImpl(EmailFactory emailFactory, String fromAddress, SmtpConfiguration smtpConfig,
                            String[] emailTemplateLookupPaths)
            throws IOException {
        this.emailFactory = emailFactory;
        this.fromAddress = fromAddress;
        this.smtpConfig = smtpConfig;
        this.freemarkerConfig = setupFreemarkerConfig(emailTemplateLookupPaths);
    }

    /**
     * Sends an email message.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @throws IOException Fired if the specified template can not be found.
     * @throws TemplateException Fired if the template can not be applied to the data.
     * @throws EmailException Fired if the email can not be sent.
     */
    @Override
    public void sendEmail(String toAddress, String subject, String templateName, Model templateData)
            throws IOException, TemplateException, EmailException {
        Template template = freemarkerConfig.getTemplate(templateName);
        Writer bodyWriter = new StringWriter();
        template.process(templateData, bodyWriter);
        String body = bodyWriter.toString();
        sendEmail(toAddress, subject, body);
    }

    /**
     * Sends an email message.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @throws EmailException Fired if the email can not be sent.
     */
    @Override
    public void sendEmail(String toAddress, String subject, String body)
            throws EmailException {
        Email email = emailFactory.createEmail();
        setupSMTP(email, smtpConfig);
        email.setMsg(body);
        email.setSubject(subject);
        email.addTo(toAddress);
        email.setFrom(fromAddress);
        email.send();
    }

    private static void setupSMTP(Email email, SmtpConfiguration smtpConfig) throws EmailException {
        email.setHostName(smtpConfig.getAddress());
        email.setSmtpPort(smtpConfig.getPort());
        email.setAuthenticator(new DefaultAuthenticator(smtpConfig.getUsername(), smtpConfig.getPassword()));
        email.setSSLOnConnect(smtpConfig.useSSL());
    }

    private static Configuration setupFreemarkerConfig(String[] templateLookupPaths) throws IOException {
        Configuration config = new Configuration();

        TemplateLoader[] loaders = new TemplateLoader[templateLookupPaths.length];
        for (int i = 0; i < templateLookupPaths.length; i++) {
            loaders[i] = new FileTemplateLoader(new File(templateLookupPaths[i]));
        }
        config.setTemplateLoader(new MultiTemplateLoader(loaders));

        return config;
    }
}
