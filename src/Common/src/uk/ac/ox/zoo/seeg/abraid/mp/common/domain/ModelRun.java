package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

/**
 * Represents a run of the SEEG model.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
        @NamedQuery(
                name = "getModelRunByName",
                query = "from ModelRun where name=:name"
        ),
        @NamedQuery(
                name = "getLastRequestedModelRun",
                query = "from ModelRun " +
                        "where diseaseGroupId=:diseaseGroupId " +
                        "and requestDate = " +
                        "   (select max(requestDate) from ModelRun" +
                        "    where diseaseGroupId=:diseaseGroupId)"
        ),
        @NamedQuery(
                name = "getLastCompletedModelRun",
                query = "from ModelRun " +
                        "where diseaseGroupId=:diseaseGroupId " +
                        "and status = 'COMPLETED' " +
                        "and responseDate =" +
                        "   (select max(responseDate) from ModelRun" +
                        "    where diseaseGroupId=:diseaseGroupId" +
                        "    and status = 'COMPLETED')"
        ),
        @NamedQuery(
                name = "getCompletedModelRunsForDisplay",
                query = "select m from ModelRun m, DiseaseGroup d " +
                        "where m.diseaseGroupId = d.id " +
                        "and m.status = 'COMPLETED' " +
                        "and (d.automaticModelRunsStartDate is null or m.requestDate >= d.automaticModelRunsStartDate)"
        ),
        @NamedQuery(
                name = "hasBatchingEverCompleted",
                query = "select count(*) from ModelRun " +
                        "where diseaseGroupId=:diseaseGroupId " +
                        "and batchingCompletedDate is not null"
        ),
        @NamedQuery(
                name = "getModelRunRequestServersByUsage",
                query = "select requestServer from ModelRun " +
                        "group by requestServer " +
                        "order by " +
                            "sum(case(status) when 'IN_PROGRESS' then 1 else 0 end) asc, " +
                            "sum(case(status) when 'IN_PROGRESS' then 0 else 1 end) asc"
        )
})
@Entity
@Table(name = "model_run")
public class ModelRun {
    // The model run ID.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The model run name, as returned by the ModelWrapper.
    @Column
    private String name;

    // The status of the model run.
    @Column
    @Enumerated(EnumType.STRING)
    private ModelRunStatus status;

    // The ID of the disease group for the model run.
    @Column(name = "disease_group_id")
    private int diseaseGroupId;

    // Request server.
    @Column(name = "request_server")
    private String requestServer;

    // The date that the model run was requested.
    @Column(name = "request_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime requestDate;

    // The date that the outputs for this model run were received.
    @Column(name = "response_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime responseDate;

    // The output text from the model run (stdout).
    @Column(name = "output_text")
    private String outputText;

    // The error text from the model run (stderr).
    @Column(name = "error_text")
    private String errorText;

    // List of submodel statistics associated with the model run.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modelRun")
    private List<SubmodelStatistic> submodelStatistics;

    // List of covariate influences associated with the model run.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modelRun")
    private List<CovariateInfluence> covariateInfluences;

    // The start date of this batch of disease occurrences (if relevant).
    @Column(name = "batch_start_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime batchStartDate;

    // The end date of this batch of disease occurrences (if relevant).
    @Column(name = "batch_end_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime batchEndDate;

    // The number of disease occurrences in this batch (if relevant).
    @Column(name = "batch_occurrence_count")
    private Integer batchOccurrenceCount;

    // The date that batching for this model run completed (if relevant).
    @Column(name = "batching_completed_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime batchingCompletedDate;

    // List of effect curve covariate influence data points associated with this model run.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "modelRun")
    private List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences;

    public ModelRun() {
    }

    public ModelRun(int id) {
        this.id = id;
    }

    public ModelRun(String name, int diseaseGroupId, String requestServer, DateTime requestDate) {
        this.name = name;
        this.status = ModelRunStatus.IN_PROGRESS;
        this.requestServer = requestServer;
        this.requestDate = requestDate;
        this.diseaseGroupId = diseaseGroupId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelRunStatus getStatus() {
        return status;
    }

    public void setStatus(ModelRunStatus status) {
        this.status = status;
    }

    public int getDiseaseGroupId() {
        return diseaseGroupId;
    }

    public void setDiseaseGroupId(int diseaseGroupId) {
        this.diseaseGroupId = diseaseGroupId;
    }

    public String getRequestServer() {
        return requestServer;
    }

    public DateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(DateTime requestDate) {
        this.requestDate = requestDate;
    }

    public DateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(DateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public List<SubmodelStatistic> getSubmodelStatistics() {
        return submodelStatistics;
    }

    public void setSubmodelStatistics(List<SubmodelStatistic> submodelStatistics) {
        this.submodelStatistics = submodelStatistics;
    }

    public List<CovariateInfluence> getCovariateInfluences() {
        return covariateInfluences;
    }

    public void setCovariateInfluences(List<CovariateInfluence> covariateInfluences) {
        this.covariateInfluences = covariateInfluences;
    }

    public DateTime getBatchStartDate() {
        return batchStartDate;
    }

    public void setBatchStartDate(DateTime batchStartDate) {
        this.batchStartDate = batchStartDate;
    }

    public DateTime getBatchEndDate() {
        return batchEndDate;
    }

    public void setBatchEndDate(DateTime batchEndDate) {
        this.batchEndDate = batchEndDate;
    }

    public Integer getBatchOccurrenceCount() {
        return batchOccurrenceCount;
    }

    public void setBatchOccurrenceCount(Integer batchedOccurrenceCount) {
        this.batchOccurrenceCount = batchedOccurrenceCount;
    }

    public DateTime getBatchingCompletedDate() {
        return batchingCompletedDate;
    }

    public void setBatchingCompletedDate(DateTime batchingCompletedDate) {
        this.batchingCompletedDate = batchingCompletedDate;
    }

    public List<EffectCurveCovariateInfluence> getEffectCurveCovariateInfluences() {
        return effectCurveCovariateInfluences;
    }

    public void setEffectCurveCovariateInfluences(List<EffectCurveCovariateInfluence> effectCurveCovariateInfluences) {
        this.effectCurveCovariateInfluences = effectCurveCovariateInfluences;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelRun modelRun = (ModelRun) o;

        if (diseaseGroupId != modelRun.diseaseGroupId) return false;
        if (batchEndDate != null ? !batchEndDate.equals(modelRun.batchEndDate) : modelRun.batchEndDate != null)
            return false;
        if (batchOccurrenceCount != null ? !batchOccurrenceCount.equals(modelRun.batchOccurrenceCount) : modelRun.batchOccurrenceCount != null)
            return false;
        if (batchStartDate != null ? !batchStartDate.equals(modelRun.batchStartDate) : modelRun.batchStartDate != null)
            return false;
        if (batchingCompletedDate != null ? !batchingCompletedDate.equals(modelRun.batchingCompletedDate) : modelRun.batchingCompletedDate != null)
            return false;
        if (errorText != null ? !errorText.equals(modelRun.errorText) : modelRun.errorText != null) return false;
        if (id != null ? !id.equals(modelRun.id) : modelRun.id != null) return false;
        if (name != null ? !name.equals(modelRun.name) : modelRun.name != null) return false;
        if (outputText != null ? !outputText.equals(modelRun.outputText) : modelRun.outputText != null) return false;
        if (requestDate != null ? !requestDate.equals(modelRun.requestDate) : modelRun.requestDate != null)
            return false;
        if (requestServer != null ? !requestServer.equals(modelRun.requestServer) : modelRun.requestServer != null)
            return false;
        if (responseDate != null ? !responseDate.equals(modelRun.responseDate) : modelRun.responseDate != null)
            return false;
        if (status != modelRun.status) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + diseaseGroupId;
        result = 31 * result + (requestServer != null ? requestServer.hashCode() : 0);
        result = 31 * result + (requestDate != null ? requestDate.hashCode() : 0);
        result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
        result = 31 * result + (outputText != null ? outputText.hashCode() : 0);
        result = 31 * result + (errorText != null ? errorText.hashCode() : 0);
        result = 31 * result + (batchStartDate != null ? batchStartDate.hashCode() : 0);
        result = 31 * result + (batchEndDate != null ? batchEndDate.hashCode() : 0);
        result = 31 * result + (batchOccurrenceCount != null ? batchOccurrenceCount.hashCode() : 0);
        result = 31 * result + (batchingCompletedDate != null ? batchingCompletedDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
