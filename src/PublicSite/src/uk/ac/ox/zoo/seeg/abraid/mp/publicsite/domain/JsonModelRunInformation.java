package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

/**
 * JSON DTO to hold information about a disease group's model runs.
 * Copyright (c) 2014 University of Oxford
 */
public class JsonModelRunInformation {
    private String lastModelRunText;
    private String diseaseOccurrencesText;
    private boolean hasModelBeenSuccessfullyRun;
    private boolean canRunModel;
    private String cannotRunModelReason;
    private String batchDateMinimum;
    private String batchDateMaximum;
    private String batchEndDateDefault;
    private boolean hasGoldStandardOccurrences;

    public String getLastModelRunText() {
        return lastModelRunText;
    }

    public void setLastModelRunText(String lastModelRunText) {
        this.lastModelRunText = lastModelRunText;
    }

    public String getDiseaseOccurrencesText() {
        return diseaseOccurrencesText;
    }

    public void setDiseaseOccurrencesText(String diseaseOccurrencesText) {
        this.diseaseOccurrencesText = diseaseOccurrencesText;
    }

    // The strange method name is necessary for JSON serialization to include this field
    public boolean isHasModelBeenSuccessfullyRun() {
        return hasModelBeenSuccessfullyRun;
    }

    public void setHasModelBeenSuccessfullyRun(boolean hasModelBeenSuccessfullyRun) {
        this.hasModelBeenSuccessfullyRun = hasModelBeenSuccessfullyRun;
    }

    // The strange method name is necessary for JSON serialization to include this field
    public boolean isCanRunModel() {
        return canRunModel;
    }

    public void setCanRunModel(boolean canRunModel) {
        this.canRunModel = canRunModel;
    }

    public String getCannotRunModelReason() {
        return cannotRunModelReason;
    }

    public void setCannotRunModelReason(String cannotRunModelReason) {
        this.cannotRunModelReason = cannotRunModelReason;
    }

    public String getBatchDateMinimum() {
        return batchDateMinimum;
    }

    public void setBatchDateMinimum(String batchDateMinimum) {
        this.batchDateMinimum = batchDateMinimum;
    }

    public String getBatchDateMaximum() {
        return batchDateMaximum;
    }

    public void setBatchDateMaximum(String batchDateMaximum) {
        this.batchDateMaximum = batchDateMaximum;
    }

    public String getBatchEndDateDefault() {
        return batchEndDateDefault;
    }

    public void setBatchEndDateDefault(String batchEndDateDefault) {
        this.batchEndDateDefault = batchEndDateDefault;
    }

    // The strange method name is necessary for JSON serialization to include this field
    public boolean isHasGoldStandardOccurrences() {
        return hasGoldStandardOccurrences;
    }

    public void setHasGoldStandardOccurrences(boolean hasGoldStandardOccurrences) {
        this.hasGoldStandardOccurrences = hasGoldStandardOccurrences;
    }
}
