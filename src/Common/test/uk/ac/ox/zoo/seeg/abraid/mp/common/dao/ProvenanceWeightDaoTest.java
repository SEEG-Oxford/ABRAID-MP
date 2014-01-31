package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ProvenanceWeight;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ProvenanceWeightDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ProvenanceWeightDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private ProvenanceWeightDao provenanceWeightDao;

    @Test
    public void saveAndReloadProvenanceWeight() {
        String name = "Very High";

        ProvenanceWeight weight = new ProvenanceWeight(name);
        provenanceWeightDao.save(weight);
        Integer id = weight.getId();
        flushAndClear();

        // Reloads the same entity and verifies its properties
        weight = provenanceWeightDao.getById(id);
        assertThat(weight).isNotNull();
        assertThat(weight.getName()).isEqualTo(name);
    }

    @Test
    public void loadNonExistentProvenanceWeight() {
        ProvenanceWeight weight = provenanceWeightDao.getById(-1);
        assertThat(weight).isNull();
    }

    @Test
    public void getAllProvenanceWeight() {
        List<ProvenanceWeight> weights = provenanceWeightDao.getAll();
        assertThat(weights).hasSize(0);
    }
}
