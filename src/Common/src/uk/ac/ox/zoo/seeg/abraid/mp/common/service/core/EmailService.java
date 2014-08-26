package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import freemarker.template.TemplateException;
import org.apache.commons.mail.EmailException;
import org.springframework.ui.Model;

import java.io.IOException;

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
     * @throws IOException Fired if the specified template can not be found.
     * @throws TemplateException Fired if the template can not be applied to the data.
     * @throws EmailException Fired if the email can not be sent.
     */
    void sendEmail(String toAddress, String subject, String templateName, Model templateData)
            throws IOException, TemplateException, EmailException;

    /**
     * Sends an email message.
     * @param toAddress The target address to send the email to.
     * @param subject The subject of the email.
     * @param body The body of the email.
     * @throws EmailException Fired if the email can not be sent.
     */
    void sendEmail(String toAddress, String subject, String body)
            throws EmailException;
}
