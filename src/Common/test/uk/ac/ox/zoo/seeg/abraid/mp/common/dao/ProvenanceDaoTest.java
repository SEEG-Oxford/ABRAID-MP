package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Provenance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the ProvenanceDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ProvenanceDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ProvenanceDao provenanceDao;

    @Test
    public void saveAndReloadProvenance() {
        // Arrange
        String provenanceName = "Test provenance";
        Provenance provenance = new Provenance();
        provenance.setName(provenanceName);

        // Act
        provenanceDao.save(provenance);

        // Assert
        assertThat(provenance.getCreatedDate()).isNotNull();

        Integer id = provenance.getId();
        flushAndClear();
        provenance = provenanceDao.getByName(provenanceName);
        assertThat(provenance).isNotNull();
        assertThat(provenance.getId()).isNotNull();
        assertThat(provenance.getId()).isEqualTo(id);
        assertThat(provenance.getName()).isEqualTo(provenanceName);
        assertThat(provenance.getCreatedDate()).isNotNull();
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
        assertThat(provenances).hasSize(2);
    }
}
