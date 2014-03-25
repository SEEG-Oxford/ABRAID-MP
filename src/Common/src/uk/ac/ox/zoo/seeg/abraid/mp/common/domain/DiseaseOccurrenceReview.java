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
        name = "getDiseaseOccurrenceReviewsByExpertId",
        query = "from DiseaseOccurrenceReview where expert.id=:expertId"
    ),
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewsByExpertIdAndDiseaseGroupId",
        query = "from DiseaseOccurrenceReview where expert.id=:expertId " +
                "and diseaseOccurrence.diseaseGroup.id=:diseaseGroupId"
    ),
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewByExpertIdAndDiseaseOccurrenceId",
        query = "select 1 from DiseaseOccurrenceReview where expert.id=:expertId " +
                "and diseaseOccurrence.id=:diseaseOccurrenceId"
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
    @JoinColumn(name = "expert_id")
    private Expert expert;

    // The id of the disease occurrence.
    @ManyToOne
    @JoinColumn(name = "disease_occurrence_id")
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

    public DiseaseOccurrenceReviewResponse getResponse() {
        return response;
    }

    public void setResponse(DiseaseOccurrenceReviewResponse response) {
        this.response = response;
    }

    public void setDiseaseOccurrence(DiseaseOccurrence diseaseOccurrence) {
        this.diseaseOccurrence = diseaseOccurrence;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }
}
