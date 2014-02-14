package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the DiseaseService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupServiceTest extends AbstractSpringUnitTests {
    @Autowired
    private DiseaseService diseaseService;

    @Test
    public void getAllDiseases() {
        List<DiseaseGroup> allDiseaseGroups = Arrays.asList(new DiseaseGroup("A"), new DiseaseGroup("B"));
        when(diseaseDao.getAll()).thenReturn(allDiseaseGroups);
        List<DiseaseGroup> testAllDiseaseGroups = diseaseService.getAllDiseases();
        assertThat(testAllDiseaseGroups).isSameAs(allDiseaseGroups);
    }

    @Test
    public void getDiseaseByName() {
        String name = "Test DiseaseGroup Name";
        DiseaseGroup diseaseGroup = new DiseaseGroup(name);
        when(diseaseDao.getByName(name)).thenReturn(diseaseGroup);
        DiseaseGroup testDiseaseGroup = diseaseService.getDiseaseByName(name);
        assertThat(testDiseaseGroup).isSameAs(diseaseGroup);
    }

    @Test
    public void saveDisease() {
        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseService.saveDisease(diseaseGroup);
        verify(diseaseDao).save(eq(diseaseGroup));
    }
}
