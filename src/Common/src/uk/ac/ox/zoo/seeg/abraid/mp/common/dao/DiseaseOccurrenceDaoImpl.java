package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;

import java.util.List;

/**
 * The DiseaseOccurrence entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseOccurrenceDaoImpl extends AbstractDao<DiseaseOccurrence, Integer> implements DiseaseOccurrenceDao {
    public DiseaseOccurrenceDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a list of occurrence points, for the specified disease group.
     * @param diseaseGroupId The id of the disease group of interest.
     * @return The list of disease occurrence points for one disease group.
     */
    public List<DiseaseOccurrence> getDiseaseOccurrencesByDiseaseGroupId(Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesByDiseaseGroupId", "diseaseGroupId", diseaseGroupId);
    }

    /**
     * Gets a list of occurrence points, for the specified disease group, for which the specified expert has not yet
     * submitted a review.
     * @param expertId The id of the specified expert.
     * @param diseaseGroupId The id of the diseaseGroup of interest.
     * @return The list of disease occurrence points to be displayed to the expert on the map.
     */
    public List<DiseaseOccurrence> getDiseaseOccurrencesYetToBeReviewed(Integer expertId, Integer diseaseGroupId) {
        return listNamedQuery("getDiseaseOccurrencesYetToBeReviewed",
                "expertId", expertId, "diseaseGroupId", diseaseGroupId);
    }


    /**
     * Get disease occurrences that match the specified disease group, location, alert and occurrence start date.
     * Used to check for the existence of a disease occurrence.
     * @param diseaseGroup The disease group.
     * @param location The location.
     * @param alert The alert.
     * @param occurrenceStartDate The occurrence start date.
     * @return A list of matching disease occurrences.
     */
    public List<DiseaseOccurrence> getDiseaseOccurrencesForExistenceCheck(DiseaseGroup diseaseGroup,
                                                                          Location location, Alert alert,
                                                                          DateTime occurrenceStartDate) {
        return listNamedQuery("getDiseaseOccurrencesForExistenceCheck",
                "diseaseGroup", diseaseGroup, "location", location, "alert", alert,
                "occurrenceStartDate", occurrenceStartDate);
    }
}
