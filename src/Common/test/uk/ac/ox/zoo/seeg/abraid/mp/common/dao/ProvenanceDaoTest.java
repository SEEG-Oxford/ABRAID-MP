package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the ProvenanceDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ProvenanceDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private ProvenanceDao provenanceDao;

    @Test
    public void saveAndReloadProvenance() {
        String provenanceName = "Test provenance";

        // Creates and saves a provenance
        Provenance provenance = new Provenance();
        provenance.setName(provenanceName);
        provenanceDao.save(provenance);
        Integer id = provenance.getId();
        flushAndClear();

        // Reloads the same provenance and verifies its properties
        provenance = provenanceDao.getByName(provenanceName);
        assertThat(provenance).isNotNull();
        assertThat(provenance.getId()).isNotNull();
        assertThat(provenance.getId()).isEqualTo(id);
        assertThat(provenance.getName()).isEqualTo(provenanceName);
    }

    @Test
    public void loadNonExistentProvenance() {
        String provenanceName = "This provenance does not exist";
        Provenance provenance = provenanceDao.getByName(provenanceName);
        assertThat(provenance).isNull();
    }

    @Test
    public void getAllProvenances() {
        List<Provenance> provenances = provenanceDao.getAll();
        assertThat(provenances).hasSize(64);
    }
}
