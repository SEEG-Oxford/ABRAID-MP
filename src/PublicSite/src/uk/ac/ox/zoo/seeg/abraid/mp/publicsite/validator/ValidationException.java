package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.validator;

import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * foo
 * Copyright (c) 2014 University of Oxford
 */
public class ValidationException extends Exception {
    private Collection<String> validationMessages;

    public ValidationException(Collection<String> validationMessages) {
        super(StringUtils.collectionToDelimitedString(validationMessages, System.lineSeparator()));
        this.validationMessages = validationMessages;
    }

    public Collection<String> getValidationMessages() {
        return validationMessages;
    }
}
