package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * The CovariateFile entity's Data Access Object.
 * Copyright (c) 2015 University of Oxford
 */
@Repository
public class CovariateFileDaoImpl extends AbstractDao<CovariateFile, Integer> implements CovariateFileDao {
    public CovariateFileDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<CovariateFile> getCovariateFilesByDiseaseGroup(DiseaseGroup diseaseGroup) {
        return listNamedQuery("getCovariateFilesByDiseaseGroup", "diseaseGroupId", diseaseGroup.getId());
    }
}
