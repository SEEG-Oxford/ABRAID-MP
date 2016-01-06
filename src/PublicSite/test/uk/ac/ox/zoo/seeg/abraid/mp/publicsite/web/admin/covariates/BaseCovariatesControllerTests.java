package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test utils for covariate controller tests.
 * Copyright (c) 2014 University of Oxford
 */
public class BaseCovariatesControllerTests {
    protected JsonCovariateConfiguration createValidMockConfig() {
        JsonCovariateConfiguration mock = mock(JsonCovariateConfiguration.class);
        List<JsonCovariateFile> jsonCovariateFiles = new ArrayList<>();
        jsonCovariateFiles.add(createMockJsonCovariateFile("access.tif", "1", false, new ArrayList<>(Arrays.asList(22))));
        jsonCovariateFiles.add(createMockJsonCovariateFile("duffy_neg.tif", "2", false, new ArrayList<Integer>()));
        jsonCovariateFiles.add(createMockJsonCovariateFile("gecon.tif", "3", false, new ArrayList<Integer>()));
        jsonCovariateFiles.add(createMockJsonCovariateFile("mod_dem.tif", "4", false, new ArrayList<Integer>()));
        when(mock.getFiles()).thenReturn(jsonCovariateFiles);
        return mock;
    }

    protected CovariateService createMockCovariateService(File covariatesDir) {
        CovariateService mock = mock(CovariateService.class);
        List<CovariateFile> covariateFiles = new ArrayList<>();
        covariateFiles.add(createMockCovariateFile("access.tif", "1", false, new ArrayList<>(Arrays.asList(createMockDiseaseGroup(22)))));
        covariateFiles.add(createMockCovariateFile("duffy_neg.tif", "2", false, new ArrayList<DiseaseGroup>()));
        covariateFiles.add(createMockCovariateFile("gecon.tif", "3", false, new ArrayList<DiseaseGroup>()));
        covariateFiles.add(createMockCovariateFile("mod_dem.tif", "4", false, new ArrayList<DiseaseGroup>()));
        when(mock.getAllCovariateFiles()).thenReturn(covariateFiles);
        when(mock.getCovariateDirectory()).thenReturn(covariatesDir.getAbsolutePath());
        return mock;
    }

    protected DiseaseService createMockDiseaseService() {
        DiseaseService mock = mock(DiseaseService.class);
        List<DiseaseGroup> diseases = new ArrayList<>();
        diseases.add(createMockDiseaseGroup(22));
        diseases.add(createMockDiseaseGroup(60));
        when(mock.getAllDiseaseGroups()).thenReturn(diseases);
        return mock;
    }

    protected JsonCovariateFile createMockJsonCovariateFile(String path, String name, boolean hide, List<Integer> enabled) {
        JsonCovariateFile mock = mock(JsonCovariateFile.class);
        when(mock.getPath()).thenReturn(path);
        when(mock.getName()).thenReturn(name);
        when(mock.getHide()).thenReturn(hide);
        when(mock.getEnabled()).thenReturn(enabled);
        return mock;
    }

    protected CovariateFile createMockCovariateFile(String path, String name, boolean hide, List<DiseaseGroup> enabled) {
        CovariateFile mock = mock(CovariateFile.class);
        // TEMP = USE FIRST SUBFILE
        CovariateSubFile submock = mock(CovariateSubFile.class);
        when(submock.getFile()).thenReturn(path);
        when(mock.getFiles()).thenReturn(Arrays.asList(submock));
        when(mock.getName()).thenReturn(name);
        when(mock.getHide()).thenReturn(hide);
        when(mock.getEnabledDiseaseGroups()).thenReturn(enabled);
        return mock;
    }

    private DiseaseGroup createMockDiseaseGroup(Integer id) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.getId()).thenReturn(id);
        return mock;
    }
}
