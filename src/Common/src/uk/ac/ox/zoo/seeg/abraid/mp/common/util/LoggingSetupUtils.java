package uk.ac.ox.zoo.seeg.abraid.mp.common.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SMTPAppender;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.SmtpConfiguration;

/**
 * Logging configuration utilities.
 * Copyright (c) 2014 University of Oxford
 */
public class LoggingSetupUtils {
    private static final PatternLayout LAYOUT = new PatternLayout("%d{ISO8601} %5p [%t] %c - %m%n");
    private static final int EMAIL_BUFFER_SIZE = 10;
    private final String loggingContextName;
    private final boolean useEmailLogging;
    private final String emailFromAddress;
    private final String emailToAddress;
    private final SmtpConfiguration emailSmtpConfig;

    public LoggingSetupUtils(String loggingContextName, boolean useEmailLogging,
                             String emailFromAddress, String emailToAddress, SmtpConfiguration emailSmtpConfig) {
        this.loggingContextName = loggingContextName;
        this.useEmailLogging = useEmailLogging;
        this.emailFromAddress = emailFromAddress;
        this.emailToAddress = emailToAddress;
        this.emailSmtpConfig = emailSmtpConfig;
    }

    /**
     * Set up additional log4j appenders.
     */
    public void setupLogging() {
        if (useEmailLogging) {
            setupEmailLogging();
        }
    }

    private void setupEmailLogging() {
        SMTPAppender emailAppender = new SMTPAppender();
        emailAppender.setSMTPHost(emailSmtpConfig.getAddress());
        emailAppender.setSMTPPort(emailSmtpConfig.getPort());
        emailAppender.setSMTPUsername(emailSmtpConfig.getUsername());
        emailAppender.setSMTPPassword(emailSmtpConfig.getPassword());
        emailAppender.setFrom(emailFromAddress);
        emailAppender.setTo(emailToAddress);
        emailAppender.setLayout(LAYOUT);
        emailAppender.setBufferSize(EMAIL_BUFFER_SIZE);
        emailAppender.setThreshold(Level.ERROR);
        emailAppender.setSubject(String.format("%s Error Alert", loggingContextName));
        emailAppender.activateOptions();
        Logger.getRootLogger().addAppender(emailAppender);
    }
}
