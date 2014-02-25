package uk.ac.ox.zoo.seeg.abraid.mp.common.domain;

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
        query = "from DiseaseOccurrenceReview where expert.Id=:expertId"
    ),
    @NamedQuery(
        name = "getDiseaseOccurrenceReviewsByExpertIdAndDiseaseOccurrenceId",
        query = "from DiseaseOccurrenceReview where expert.Id=:expertId"
              + "and diseaseOccurrence.diseaseGroup.Id=:diseaseGroupId"
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
}
