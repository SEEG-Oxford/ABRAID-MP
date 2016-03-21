package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.CovariateSubFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateSubFile;
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
        jsonCovariateFiles.add(createMockJsonCovariateFile(1, "1", false, new ArrayList<>(Arrays.asList(22)), new ArrayList<>(Arrays.asList(
                createMockJsonCovariateSubFile(1, "access.tif", "foo"),
                createMockJsonCovariateSubFile(2, "2.tif", null)))));
        jsonCovariateFiles.add(createMockJsonCovariateFile(2, "2", false, new ArrayList<Integer>(), new ArrayList<JsonCovariateSubFile>()));
        jsonCovariateFiles.add(createMockJsonCovariateFile(3, "3", false, new ArrayList<Integer>(), new ArrayList<JsonCovariateSubFile>()));
        jsonCovariateFiles.add(createMockJsonCovariateFile(4, "4", false, new ArrayList<Integer>(), new ArrayList<JsonCovariateSubFile>()));
        when(mock.getFiles()).thenReturn(jsonCovariateFiles);
        return mock;
    }

    protected CovariateService createMockCovariateService(File covariatesDir) {
        CovariateService mock = mock(CovariateService.class);
        List<CovariateFile> covariateFiles = new ArrayList<>();
        covariateFiles.add(createMockCovariateFile(1, "1", false, new ArrayList<>(Arrays.asList(createMockDiseaseGroup(22))), new ArrayList<>(Arrays.asList(
                createMockCovariateSubFile(1, "access.tif", "foo"),
                createMockCovariateSubFile(2, "2.tif", null)))));
        covariateFiles.add(createMockCovariateFile(2, "2", false, new ArrayList<DiseaseGroup>(), new ArrayList<CovariateSubFile>()));
        covariateFiles.add(createMockCovariateFile(3, "3", false, new ArrayList<DiseaseGroup>(), new ArrayList<CovariateSubFile>()));
        covariateFiles.add(createMockCovariateFile(4, "4", false, new ArrayList<DiseaseGroup>(), new ArrayList<CovariateSubFile>()));
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

    protected JsonCovariateFile createMockJsonCovariateFile(int id, String name, boolean hide, List<Integer> enabled, List<JsonCovariateSubFile> subFiles) {
        JsonCovariateFile mock = mock(JsonCovariateFile.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        when(mock.getHide()).thenReturn(hide);
        when(mock.getEnabled()).thenReturn(enabled);
        when(mock.getSubFiles()).thenReturn(subFiles);
        return mock;
    }

    protected JsonCovariateSubFile createMockJsonCovariateSubFile(int id, String path, String qualifier) {
        JsonCovariateSubFile mock = mock(JsonCovariateSubFile.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getPath()).thenReturn(path);
        when(mock.getQualifier()).thenReturn(qualifier);
        return mock;
    }

    protected CovariateFile createMockCovariateFile(int id, String name, boolean hide, List<DiseaseGroup> enabled,
                                                    List<CovariateSubFile> subFiles) {
        CovariateFile mock = mock(CovariateFile.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getName()).thenReturn(name);
        when(mock.getHide()).thenReturn(hide);
        when(mock.getEnabledDiseaseGroups()).thenReturn(enabled);
        when(mock.getFiles()).thenReturn(subFiles);
        return mock;
    }

    protected CovariateSubFile createMockCovariateSubFile(int id, String path, String qualifier) {
        CovariateSubFile mock = mock(CovariateSubFile.class);
        when(mock.getId()).thenReturn(id);
        when(mock.getFile()).thenReturn(path);
        when(mock.getQualifier()).thenReturn(qualifier);
        return mock;
    }

    private DiseaseGroup createMockDiseaseGroup(Integer id) {
        DiseaseGroup mock = mock(DiseaseGroup.class);
        when(mock.getId()).thenReturn(id);
        return mock;
    }
}
