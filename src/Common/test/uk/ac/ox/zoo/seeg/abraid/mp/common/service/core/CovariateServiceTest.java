package uk.ac.ox.zoo.seeg.abraid.mp.common.service.core;

import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.CovariateFileDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for the CovariateService class.
 * Copyright (c) 2015 University of Oxford
 */
public class CovariateServiceTest {
    @Test
    public void getCovariateDirectoryReturnsTheConstructorArgument() {
        // Arrange
        String covariateDirectory = "abc";
        CovariateService target = new CovariateServiceImpl(covariateDirectory, mock(CovariateFileDao.class));

        // Act
        String result = target.getCovariateDirectory();

        // Assert
        assertThat(result).isSameAs(covariateDirectory);
    }

    @Test
    public void getAllCovariateFilesCallsDao() {
        // Arrange
        CovariateFileDao covariateFileDao = mock(CovariateFileDao.class);
        List<CovariateFile> expectation = Arrays.asList(mock(CovariateFile.class));
        when(covariateFileDao.getAll()).thenReturn(expectation);
        CovariateService target = new CovariateServiceImpl("covariateDirectory", covariateFileDao);

        // Act
        Collection<CovariateFile> result = target.getAllCovariateFiles();

        // Assert
        assertThat(result).isSameAs(expectation);
        verify(covariateFileDao).getAll();
    }

    @Test
    public void getCovariateFilesByDiseaseGroupCallsDao() {
        // Arrange
        CovariateFileDao covariateFileDao = mock(CovariateFileDao.class);
        List<CovariateFile> expectation = Arrays.asList(mock(CovariateFile.class));
        DiseaseGroup diseaseGroup = mock(DiseaseGroup.class);
        when(covariateFileDao.getCovariateFilesByDiseaseGroup(diseaseGroup)).thenReturn(expectation);
        CovariateService target = new CovariateServiceImpl("covariateDirectory", covariateFileDao);

        // Act
        Collection<CovariateFile> result = target.getCovariateFilesByDiseaseGroup(diseaseGroup);

        // Assert
        assertThat(result).isSameAs(expectation);
        verify(covariateFileDao).getCovariateFilesByDiseaseGroup(diseaseGroup);
    }

    @Test
    public void getCovariateFileByPathCallsDao() {
        // Arrange
        CovariateFileDao covariateFileDao = mock(CovariateFileDao.class);
        CovariateFile expectation = mock(CovariateFile.class);
        String path = "path";
        when(covariateFileDao.getByFilePath(path)).thenReturn(expectation);
        CovariateService target = new CovariateServiceImpl("covariateDirectory", covariateFileDao);

        // Act
        CovariateFile result = target.getCovariateFileByPath(path);

        // Assert
        assertThat(result).isSameAs(expectation);
        verify(covariateFileDao).getByFilePath(path);
    }

    @Test
    public void saveCovariateFileCallsDao() {
        // Arrange
        CovariateFileDao covariateFileDao = mock(CovariateFileDao.class);
        CovariateFile expectation = mock(CovariateFile.class);
        CovariateService target = new CovariateServiceImpl("covariateDirectory", covariateFileDao);

        // Act
        target.saveCovariateFile(expectation);

        // Assert
        verify(covariateFileDao).save(expectation);
    }
}
