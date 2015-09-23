package uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json;

/**
 * The JSON DTO used to respond to model run requests.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunResponse {
    private String errorText;

    public JsonModelRunResponse() {
    }

    public JsonModelRunResponse(String errorText) {
        this.errorText = errorText;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonModelRunResponse that = (JsonModelRunResponse) o;

        if (errorText != null ? !errorText.equals(that.errorText) : that.errorText != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return errorText != null ? errorText.hashCode() : 0;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
