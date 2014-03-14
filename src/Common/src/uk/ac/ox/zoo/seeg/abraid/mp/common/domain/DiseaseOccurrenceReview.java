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
@Entity
@NamedQueries({
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewsByExpertId",
        query = "from DiseaseOccurrenceReview where expert.id=:expertId"
    ),
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewsByExpertIdAndDiseaseGroupId",
        query = "from DiseaseOccurrenceReview where expert.id=:expertId " +
                "and diseaseOccurrence.diseaseGroup.id=:diseaseGroupId"
    )
})
public class DiseaseOccurrenceReview {
    // The id of the review.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // The expert.
    @ManyToOne
    @JoinColumn(name = "ExpertId")
    private Expert expert;

    // The id of the disease occurrence.
    @ManyToOne
    @JoinColumn(name = "DiseaseOccurrenceId")
    private DiseaseOccurrence diseaseOccurrence;

    // The expert's response.
    @Column
    @Enumerated(EnumType.STRING)
    private DiseaseOccurrenceReviewResponse response;

    // The database row creation date.
    @Column(insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdDate;

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
