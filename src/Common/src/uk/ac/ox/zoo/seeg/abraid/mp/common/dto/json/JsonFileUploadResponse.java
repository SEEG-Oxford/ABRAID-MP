package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

import java.util.Arrays;
import java.util.Collection;

/**
 *  A DTO to express the status of file upload response, with an optional set of messages.
 *  This is for compatibility with iframe based file upload where the status code of the response is not visible to
 *  the response handlers.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class JsonFileUploadResponse {
    /**
     * Indicates success.
     */
    public static final String SUCCESS = "SUCCESS";
    /**
     * Indicates failure.
     */
    public static final String FAIL = "FAIL";

    private final String status;
    private final Collection<String> messages;

    public JsonFileUploadResponse() {
        this.status = SUCCESS;
        this.messages = null;
    }

    public JsonFileUploadResponse(boolean success, String messages) {
        this(success, Arrays.asList(messages));
    }

    public JsonFileUploadResponse(boolean success, Collection<String> messages) {
        this.status = success ? SUCCESS : FAIL;
        this.messages = messages;
    }

    public String getStatus() {
        return status;
    }

    public Collection<String> getMessages() {
        return messages;
    }
}
