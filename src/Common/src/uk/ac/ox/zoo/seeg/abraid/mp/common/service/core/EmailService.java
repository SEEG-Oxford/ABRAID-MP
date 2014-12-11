package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import freemarker.template.TemplateException;
import org.apache.commons.mail.EmailException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Defines a service for sending emails.
 * Copyright (c) 2014 University of Oxford
 */
public interface EmailService {
    /**
     * Sends an email message.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @throws IOException Fired if the specified template cannot be found.
     * @throws TemplateException Fired if the template cannot be applied to the data.
     * @throws EmailException Fired if the email cannot be sent.
     */
    void sendEmail(String toAddress, String subject, String templateName, Map<String, ?> templateData)
            throws IOException, TemplateException, EmailException;

    /**
     * Sends an email message.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @throws EmailException Fired if the email cannot be sent.
     */
    void sendEmail(String toAddress, String subject, String body)
            throws EmailException;

    /**
     * Sends an email message, using a background process. Logs errors.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @return A future for the background operation.
     */
    Future sendEmailInBackground(
            String toAddress, String subject, String templateName, Map<String, ?> templateData);

    /**
     * Sends an email message, using a background process. Logs errors.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return A future for the background operation.
     */
    Future sendEmailInBackground(String toAddress, String subject, String body);

    /**
     * Sends an email message to the default address.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @throws IOException Fired if the specified template cannot be found.
     * @throws TemplateException Fired if the template cannot be applied to the data.
     * @throws EmailException Fired if the email cannot be sent.
     */
    void sendEmail(String subject, String templateName, Map<String, ?> templateData)
            throws IOException, TemplateException, EmailException;

    /**
     * Sends an email message to the default address.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @throws EmailException Fired if the email cannot be sent.
     */
    void sendEmail(String subject, String body)
            throws EmailException;

    /**
     * Sends an email message to the default address, using a background process. Logs errors.
     * @param subject The subject of the email.
     * @param templateName The name of the template to use in the body of the email.
     * @param templateData The data to use when generating the body of the email.
     * @return A future for the background operation.
     */
    Future sendEmailInBackground(String subject, String templateName, Map<String, ?> templateData);

    /**
     * Sends an email message to the default address, using a background process. Logs errors.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @return A future for the background operation.
     */
    Future sendEmailInBackground(String subject, String body);
}
