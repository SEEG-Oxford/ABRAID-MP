package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain;

import org.joda.time.DateTime;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.extract;

/**
 * JSON DTO for Experts (used in administering experts).
 * Copyright (c) 2014 University of Oxford
 */
public class JsonExpertFull extends JsonExpertDetails {
    private Integer id;
    private String email;
    private Double weighting;
    private Boolean visibilityApproved;
    private DateTime createdDate;
    private DateTime updatedDate;
    private Boolean isAdministrator;
    private Boolean isSEEGMember;
    private DateTime lastReviewDate;
    private long diseaseOccurrenceReviewCount;
    private long adminUnitReviewCount;
    private List<String> diseaseInterestNames;

    public JsonExpertFull() {
        super();
    }

    public JsonExpertFull(
            Expert expert, long diseaseOccurrenceReviewCount, long adminUnitReviewCount, DateTime lastReviewDate) {
        super(expert);
        setId(expert.getId());
        setEmail(expert.getEmail());
        setWeighting(expert.getWeighting());
        setVisibilityApproved(expert.getVisibilityApproved());
        setCreatedDate(expert.getCreatedDate());
        setUpdatedDate(expert.getUpdatedDate());
        setAdministrator(expert.isAdministrator());
        setSEEGMember(expert.isSeegMember());
        setLastReviewDate(lastReviewDate);
        setDiseaseOccurrenceReviewCount(diseaseOccurrenceReviewCount);
        setAdminUnitReviewCount(adminUnitReviewCount);
        setDiseaseInterestNames(extract(expert.getValidatorDiseaseGroups(), on(ValidatorDiseaseGroup.class).getName()));

        setDiseaseInterests(null); // Don't serialize
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getWeighting() {
        return weighting;
    }

    public void setWeighting(Double weighting) {
        this.weighting = weighting;
    }

    public Boolean getVisibilityApproved() {
        return visibilityApproved;
    }

    public void setVisibilityApproved(Boolean visibilityApproved) {
        this.visibilityApproved = visibilityApproved;
    }

    public Boolean isAdministrator() {
        return isAdministrator;
    }

    public void setAdministrator(Boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public Boolean isSEEGMember() {
        return isSEEGMember;
    }

    public void setSEEGMember(Boolean isSEEGMember) {
        this.isSEEGMember = isSEEGMember;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public DateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(DateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public DateTime getLastReviewDate() {
        return lastReviewDate;
    }

    public void setLastReviewDate(DateTime lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
    }

    public long getDiseaseOccurrenceReviewCount() {
        return diseaseOccurrenceReviewCount;
    }

    public void setDiseaseOccurrenceReviewCount(long diseaseOccurrenceReviewCount) {
        this.diseaseOccurrenceReviewCount = diseaseOccurrenceReviewCount;
    }

    public long getAdminUnitReviewCount() {
        return adminUnitReviewCount;
    }

    public void setAdminUnitReviewCount(long adminUnitReviewCount) {
        this.adminUnitReviewCount = adminUnitReviewCount;
    }

    public List<String> getDiseaseInterestNames() {
        return diseaseInterestNames;
    }

    public void setDiseaseInterestNames(List<String> diseaseInterestNames) {
        this.diseaseInterestNames = diseaseInterestNames;
    }
}
