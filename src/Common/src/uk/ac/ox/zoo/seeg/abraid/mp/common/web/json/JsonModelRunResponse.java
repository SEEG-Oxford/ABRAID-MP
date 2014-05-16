package uk.ac.ox.zoo.seeg.abraid.mp.common.web.json;

/**
 * The JSON DTO used to respond to model run requests.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunResponse {
    private String modelRunName;
    private String errorText;

    public JsonModelRunResponse() {
    }

    public JsonModelRunResponse(String modelRunName, String errorText) {
        this.modelRunName = modelRunName;
        this.errorText = errorText;
    }

    public String getModelRunName() {
        return modelRunName;
    }

    public void setModelRunName(String modelRunName) {
        this.modelRunName = modelRunName;
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
        if (modelRunName != null ? !modelRunName.equals(that.modelRunName) : that.modelRunName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelRunName != null ? modelRunName.hashCode() : 0;
        result = 31 * result + (errorText != null ? errorText.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
