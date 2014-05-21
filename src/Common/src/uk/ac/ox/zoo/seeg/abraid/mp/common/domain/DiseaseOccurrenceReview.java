package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Represents an expert's response to the validity of a disease occurrence point.
 *
 * Copyright (c) 2014 University of Oxford
 */
@NamedQueries({
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewCountByExpertId",
        query = "select count(*) from DiseaseOccurrenceReview where expert.id=:expertId"
    ),
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewByExpertIdAndDiseaseOccurrenceId",
        query = "select 1 from DiseaseOccurrenceReview where expert.id=:expertId " +
                "and diseaseOccurrence.id=:diseaseOccurrenceId"
    ),
    @NamedQuery(
        name = "getAllDiseaseOccurrenceReviewsByDiseaseGroupId",
        query = "from DiseaseOccurrenceReview where diseaseOccurrence.diseaseGroup.id=:diseaseGroupId"
    ),
    @NamedQuery(
        name = "getAllDiseaseOccurrenceReviewsForDiseaseGroupOccurrencesWithNewReviewsSinceLastModelRunPrep",
        query = "from DiseaseOccurrenceReview where diseaseOccurrence.diseaseGroup.id=:diseaseGroupId " +
                "and diseaseOccurrence in " +
                "(select diseaseOccurrence from DiseaseOccurrenceReview where createdDate > :lastModelRunPrepDate)"
    )
})
@Entity
@Table(name = "disease_occurrence_review")
public class DiseaseOccurrenceReview {
    // The id of the review.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The expert.
    @ManyToOne
    @JoinColumn(name = "expert_id", nullable = false)
    private Expert expert;

    // The id of the disease occurrence.
    @ManyToOne
    @JoinColumn(name = "disease_occurrence_id", nullable = false)
    private DiseaseOccurrence diseaseOccurrence;

    // The expert's response.
    @Column
    @Enumerated(EnumType.STRING)
    private DiseaseOccurrenceReviewResponse response;

    // The database row creation date.
    @Column(name = "created_date", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

    public DiseaseOccurrenceReview() {
    }

    public DiseaseOccurrenceReview(Expert expert, DiseaseOccurrence diseaseOccurrence,
                                   DiseaseOccurrenceReviewResponse response) {
        this.expert = expert;
        this.diseaseOccurrence = diseaseOccurrence;
        this.response = response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public DiseaseOccurrence getDiseaseOccurrence() {
        return diseaseOccurrence;
    }

    public void setDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence) {
        this.diseaseOccurrence = diseaseOccurrence;
    }

    public DiseaseOccurrenceReviewResponse getResponse() {
        return response;
    }

    public void setResponse(DiseaseOccurrenceReviewResponse response) {
        this.response = response;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    ///COVERAGE:OFF - generated code
    ///CHECKSTYLE:OFF AvoidInlineConditionalsCheck|LineLengthCheck|MagicNumberCheck|NeedBracesCheck - generated code
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiseaseOccurrenceReview that = (DiseaseOccurrenceReview) o;

        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (diseaseOccurrence != null ? !diseaseOccurrence.equals(that.diseaseOccurrence) : that.diseaseOccurrence != null)
            return false;
        if (expert != null ? !expert.equals(that.expert) : that.expert != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (response != that.response) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (expert != null ? expert.hashCode() : 0);
        result = 31 * result + (diseaseOccurrence != null ? diseaseOccurrence.hashCode() : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        return result;
    }
    ///CHECKSTYLE:ON
    ///COVERAGE:ON
}
