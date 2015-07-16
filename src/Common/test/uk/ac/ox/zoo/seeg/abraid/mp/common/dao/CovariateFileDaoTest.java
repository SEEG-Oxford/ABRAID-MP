package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateValueBin;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the CovariateFileDao class.
 * Copyright (c) 2014 University of Oxford
 */
public class CovariateFileDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private CovariateFileDao covariateFileDao;

    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void getAllReturnsAll() {
        assertThat(covariateFileDao.getAll()).hasSize(22);
    }

    @Test
    public void getByPathReturnsNullIfNonExistent() {
        assertThat(covariateFileDao.getByFilePath("non-existent name")).isNull();
    }

    @Test
    public void getByPathReturnsCorrectInstance() {
        assertThat(covariateFileDao.getByFilePath("access.tif").getName()).isEqualTo("EC JRC Urban Accessability");
    }

    @Test
    public void getCovariateFilesByDiseaseGroupReturnsCorrectSet() {
        assertThat(covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroupDao.getById(87))).hasSize(8);
    }

    @Test
    public void getCovariateFilesByDiseaseGroupExcludesHidden() {
        CovariateFile file = covariateFileDao.getByFilePath("access.tif");
        file.setHide(true);
        covariateFileDao.save(file);
        assertThat(covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroupDao.getById(87))).hasSize(7);
    }

    @Test
    public void saveAndReload() {
        // Arrange
        CovariateFile covariateFile = new CovariateFile("NAME", "FILE", true, true, "INFO");
        Collection<DiseaseGroup> enabledDiseaseGroups = Arrays.asList(diseaseGroupDao.getById(87), diseaseGroupDao.getById(60));
        Collection<CovariateValueBin> bins = Arrays.asList(new CovariateValueBin(covariateFile, 0, 5, 10), new CovariateValueBin(covariateFile, 5, 10, 1), new CovariateValueBin(covariateFile, 10, 10, 10));
        covariateFile.setEnabledDiseaseGroups(enabledDiseaseGroups);
        covariateFile.setCovariateValueHistogramData(bins);

        // Act
        covariateFileDao.save(covariateFile);

        // Assert
        Integer id = covariateFile.getId();
        flushAndClear();
        covariateFile = covariateFileDao.getById(id);
        assertThat(covariateFile.getName()).isEqualTo("NAME");
        assertThat(covariateFile.getInfo()).isEqualTo("INFO");
        assertThat(covariateFile.getFile()).isEqualTo("FILE");
        assertThat(covariateFile.getHide()).isEqualTo(true);
        assertThat(covariateFile.getEnabledDiseaseGroups()).containsAll(enabledDiseaseGroups);
        assertThat(covariateFile.getEnabledDiseaseGroups()).hasSameSizeAs(enabledDiseaseGroups);
        assertThat(covariateFile.getCovariateValueHistogramData()).hasSameSizeAs(bins);
    }
}
