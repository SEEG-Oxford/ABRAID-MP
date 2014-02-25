package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroupType;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Tests the DiseaseGroupDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupDaoTest extends AbstractSpringIntegrationTests {
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Test
    public void saveAndReloadDiseaseCluster() {
        // Arrange
        String diseaseClusterName = "Test disease cluster";

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        Integer id = diseaseGroup.getId();
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(id);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseClusterName);
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.CLUSTER);
        assertThat(diseaseGroup.getParentGroup()).isNull();
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadDiseaseMicroCluster() {
        // Arrange
        String diseaseClusterName = "Test disease microcluster";
        DiseaseGroup diseaseCluster = diseaseGroupDao.getById(1);

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.MICROCLUSTER);
        diseaseGroup.setParentGroup(diseaseCluster);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        Integer id = diseaseGroup.getId();
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(id);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseClusterName);
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.MICROCLUSTER);
        assertThat(diseaseGroup.getParentGroup()).isNotNull();
        assertThat(diseaseGroup.getParentGroup()).isEqualTo(diseaseCluster);
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
    }

    @Test
    public void saveAndReloadDisease() {
        // Arrange
        String diseaseClusterName = "Test disease";
        DiseaseGroup diseaseMicroCluster = diseaseGroupDao.getById(16);
        DiseaseGroup diseaseCluster = diseaseGroupDao.getById(5);

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.DISEASE);
        diseaseGroup.setParentGroup(diseaseMicroCluster);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        Integer id = diseaseGroup.getId();
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(id);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseClusterName);
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.DISEASE);
        assertThat(diseaseGroup.getParentGroup()).isNotNull();
        assertThat(diseaseGroup.getParentGroup()).isEqualTo(diseaseMicroCluster);
        assertThat(diseaseGroup.getParentGroup().getParentGroup()).isNotNull();
        assertThat(diseaseGroup.getParentGroup().getParentGroup()).isEqualTo(diseaseCluster);
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
    }

    @Test
    public void loadNonExistentDiseaseGroup() {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(-1);
        assertThat(diseaseGroup).isNull();
    }

    @Test
    public void getAllDiseaseGroups() {
        List<DiseaseGroup> diseaseGroups = diseaseGroupDao.getAll();
        assertThat(diseaseGroups).hasSize(51);
    }
}
