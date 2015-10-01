package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * The DiseaseGroup entity's Data Access Object.
 *
 * Copyright (c) 2014 University of Oxford
 */
@Repository
public class DiseaseGroupDaoImpl extends AbstractDao<DiseaseGroup, Integer> implements DiseaseGroupDao {
    public DiseaseGroupDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * Gets a list of the disease groups for which there are occurrences waiting to be reviewed, by the given expert.
     * @param expertId The expert's id.
     * @return A list of disease groups.
     */
    @Override
    public List<DiseaseGroup> getDiseaseGroupsNeedingOccurrenceReviewByExpert(int expertId) {
        return listNamedQuery("getDiseaseGroupsNeedingOccurrenceReviewByExpert", "expertId", expertId);
    }

    /**
     * Gets a list of the disease groups for which there are admin units waiting to be reviewed, by the given expert.
     * @param expertId The expert's id.
     * @return A list of disease groups.
     */
    @Override
    public List<DiseaseGroup> getDiseaseGroupsNeedingExtentReviewByExpert(int expertId) {
        return listNamedQuery("getDiseaseGroupsNeedingExtentReviewByExpert", "expertId", expertId);
    }

    /**
     * Gets the IDs of disease groups that have automatic model runs enabled.
     * @return The IDs of relevant disease groups.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getIdsForAutomaticModelRuns() {
        Query query = namedQuery("getDiseaseGroupIdsForAutomaticModelRuns");
        return query.list();
    }
}
