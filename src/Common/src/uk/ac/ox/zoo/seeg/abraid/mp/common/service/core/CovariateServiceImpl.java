package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.springframework.transaction.annotation.Transactional;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.CovariateFileDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.List;

/**
 * Service for covariate file data.
 * Copyright (c) 2014 University of Oxford
 */
@Transactional(rollbackFor = Exception.class)
public class CovariateServiceImpl implements CovariateService {
    private String covariateDirectory;
    private CovariateFileDao covariateFileDao;

    public CovariateServiceImpl(String covariateDirectory, CovariateFileDao covariateFileDao) {
        this.covariateDirectory = covariateDirectory;
        this.covariateFileDao = covariateFileDao;
    }

    /**
     * Gets the directory in which covariate files are stored.
     * @return The covariate directory.
     */
    @Override
    public String getCovariateDirectory() {
        return covariateDirectory;
    }

    /**
     * Gets all covariate files.
     * @return All covariate files.
     */
    @Override
    public List<CovariateFile> getAllCovariateFiles() {
        return covariateFileDao.getAll();
    }

    /**
     * Gets all of covariate files for a given disease group.
     * @param diseaseGroup The disease group.
     * @return All covariate files.
     */
    @Override
    public List<CovariateFile> getCovariateFilesByDiseaseGroup(DiseaseGroup diseaseGroup) {
        return covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroup);
    }

    /**
     * Gets a covariate file by its ID.
     * @param id The ID.
     * @return The covariate file with the specified ID, or null if not found.
     */
    @Override
    public CovariateFile getCovariateFileById(int id) {
        return covariateFileDao.getById(id);
    }

    /**
     * Saves the specified covariate file.
     * @param covariateFile The covariate file to save.
     */
    @Override
    public void saveCovariateFile(CovariateFile covariateFile) {
        covariateFileDao.save(covariateFile);
    }
}
