package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractAsynchronousActionHandler;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.util.EmailFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A service for sending emails.
 * Copyright (c) 2014 University of Oxford
 */
public class EmailServiceImpl extends AbstractAsynchronousActionHandler implements EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class);
    private static final String LOG_EMAIL_SENT =
            "Email sent to '%s' with subject '%s'";
    private static final String LOG_FAILED_SEND_EMAIL =
            "Failed to send email (%s - %s)";
    private static final String LOG_FAILED_LOAD_TEMPLATE =
            "Failed to send email (%s - %s) due to failure to load template";
    private static final String LOG_FAILED_APPLY_TEMPLATE =
            "Failed to send email (%s - %s) due to failure to apply template";
    private static final int THREAD_POOL_SIZE = 3;

    private final EmailFactory emailFactory;
    private final String fromAddress;
    private final String defaultToAddress;
    private final SmtpConfiguration smtpConfig;
    private final Configuration freemarkerConfig;

    public EmailServiceImpl(EmailFactory emailFactory, String fromAddress,
                            String defaultToAddress, SmtpConfiguration smtpConfig,
                            Class[] classTemplateLookupPaths, File[] fileTemplateLookupPaths)
            throws IOException {
        super(THREAD_POOL_SIZE);
        this.emailFactory = emailFactory;
        this.fromAddress = fromAddress;
        this.defaultToAddress = defaultToAddress;
        this.smtpConfig = smtpConfig;
        this.freemarkerConfig = setupFreemarkerConfig(classTemplateLookupPaths, fileTemplateLookupPaths);
    }

    private static void setupSMTP(Email email, SmtpConfiguration smtpConfig) throws EmailException {
        email.setHostName(smtpConfig.getAddress());
        email.setSmtpPort(smtpConfig.getPort());
        email.setSslSmtpPort(Integer.toString(smtpConfig.getPort()));
        email.setAuthenticator(new DefaultAuthenticator(smtpConfig.getUsername(), smtpConfig.getPassword()));
        email.setSSLOnConnect(smtpConfig.useSSL());
    }

    private static Configuration setupFreemarkerConfig(Class[] classTemplateLookupPaths, File[] fileTemplateLookupPaths)
            throws IOException {
        Configuration config = new Configuration();

        TemplateLoader[] loaders = new TemplateLoader[classTemplateLookupPaths.length + fileTemplateLookupPaths.length];

        for (int i = 0; i < classTemplateLookupPaths.length; i++) {
            loaders[i] = new ClassTemplateLoader(classTemplateLookupPaths[i], "");
        }

        for (int i = 0; i < fileTemplateLookupPaths.length; i++) {
            loaders[classTemplateLookupPaths.length + i] = new FileTemplateLoader(fileTemplateLookupPaths[i]);
        }

        config.setTemplateLoader(new MultiTemplateLoader(loaders));

        return config;
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
    public void sendEmail(String toAddress, String subject, String templateName, Map<String, Object> templateData)
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
        LOGGER.info(String.format(LOG_EMAIL_SENT, toAddress, subject));
    }

    /**
     * Sends an email message, using a background process. Logs errors.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @return A future for the background operation.
     */
    @Override
    public Future sendEmailInBackground(final String toAddress, final String subject,
                                        final String templateName, final Map<String, Object> templateData) {
        return submitAsynchronousTask(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    sendEmail(toAddress, subject, templateName, templateData);
                } catch (EmailException e) {
                    LOGGER.error(String.format(LOG_FAILED_SEND_EMAIL, toAddress, subject), e);
                } catch (IOException e) {
                    LOGGER.error(String.format(LOG_FAILED_LOAD_TEMPLATE, toAddress, subject), e);
                } catch (TemplateException e) {
                    LOGGER.error(String.format(LOG_FAILED_APPLY_TEMPLATE, toAddress, subject), e);
                }

                return null;
            }
        });
    }

    /**
     * Sends an email message, using a background process. Logs errors.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return A future for the background operation.
     */
    @Override
    public Future sendEmailInBackground(final String toAddress, final String subject, final String body) {
        return submitAsynchronousTask(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    sendEmail(toAddress, subject, body);
                } catch (EmailException e) {
                    LOGGER.error(String.format(LOG_FAILED_SEND_EMAIL, toAddress, subject), e);
                }

                return null;
            }
        });
    }

    /**
     * Sends an email message to the default address.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @throws java.io.IOException Fired if the specified template can not be found.
     * @throws freemarker.template.TemplateException Fired if the template can not be applied to the data.
     * @throws org.apache.commons.mail.EmailException Fired if the email can not be sent.
     */
    @Override
    public void sendEmail(String subject, String templateName, Map<String, Object> templateData)
            throws IOException, TemplateException, EmailException {
        sendEmail(defaultToAddress, subject, templateName, templateData);
    }

    /**
     * Sends an email message to the default address.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @throws org.apache.commons.mail.EmailException Fired if the email can not be sent.
     */
    @Override
    public void sendEmail(String subject, String body) throws EmailException {
        sendEmail(defaultToAddress, subject, body);
    }

    /**
     * Sends an email message to the default address, using a background process. Logs errors.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @return A future for the background operation.
     */
    @Override
    public Future sendEmailInBackground(String subject, String templateName, Map<String, Object> templateData) {
        return sendEmailInBackground(defaultToAddress, subject, templateName, templateData);
    }

    /**
     * Sends an email message to the default address, using a background process. Logs errors.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return A future for the background operation.
     */
    @Override
    public Future sendEmailInBackground(String subject, String body) {
        return sendEmailInBackground(defaultToAddress, subject, body);
    }
}
