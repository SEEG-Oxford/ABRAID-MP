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

    @Override
    public List<CovariateFile> getAllCovariateFiles() {
        return covariateFileDao.getAll();
    }

    @Override
    public List<CovariateFile> getCovariateFilesByDiseaseGroup(DiseaseGroup diseaseGroup) {
        return covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroup);
    }

    @Override
    public CovariateFile getCovariateFileByPath(String path) {
        return covariateFileDao.getByFilePath(path);
    }

    @Override
    public void saveCovariateFile(CovariateFile covariateFile) {
        covariateFileDao.save(covariateFile);
    }

    @Override
    public String getCovariateDirectory() {
        return covariateDirectory;
    }
}
