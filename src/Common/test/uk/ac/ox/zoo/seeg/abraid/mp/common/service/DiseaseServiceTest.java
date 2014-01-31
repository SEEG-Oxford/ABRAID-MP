package uk.ac.ox.zoo.seeg.abraid.mp.common.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringUnitTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Disease;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the DiseaseService class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseServiceTest extends AbstractSpringUnitTests {
    @Autowired
    private DiseaseService diseaseService;

    @Test
    public void getDiseaseByName() {
        String name = "Test Disease Name";
        Disease disease = new Disease(name);
        when(diseaseDao.getByName(name)).thenReturn(disease);
        Disease testDisease = diseaseService.getDiseaseByName(name);
        assertThat(testDisease).isSameAs(disease);
    }

    @Test
    public void saveDisease() {
        Disease disease = new Disease();
        diseaseService.saveDisease(disease);
        verify(diseaseDao).save(eq(disease));
    }
}
