package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroupType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the DiseaseGroupDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class DiseaseGroupDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseGroupDao diseaseGroupDao;

    @Autowired
    private ValidatorDiseaseGroupDao validatorDiseaseGroupDao;

    @Test
    public void saveAndReloadDiseaseCluster() {
        // Arrange
        String diseaseClusterName = "Test disease cluster";
        String diseaseClusterPublicName = "Test disease cluster public name";
        String diseaseClusterShortName = "Short name";
        String diseaseClusterAbbreviation = "tdc";
        int validatorDiseaseGroupId = 1;
        ValidatorDiseaseGroup validatorDiseaseGroup = validatorDiseaseGroupDao.getById(validatorDiseaseGroupId);
        DateTime validationProcessStartDate = DateTime.now().minusHours(1);
        DateTime lastModelRunPrepDate = DateTime.now().minusHours(2);
        int modelRunMinNewOccurrences = 100;
        double weighting = 0.5;

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);
        diseaseGroup.setPublicName(diseaseClusterPublicName);
        diseaseGroup.setShortName(diseaseClusterShortName);
        diseaseGroup.setAbbreviation(diseaseClusterAbbreviation);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        diseaseGroup.setValidationProcessStartDate(validationProcessStartDate);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseGroup.setModelRunMinNewOccurrences(modelRunMinNewOccurrences);
        diseaseGroup.setWeighting(weighting);
        diseaseGroup.setGlobal(true);

        // Act
        diseaseGroupDao.save(diseaseGroup);

        // Assert
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
        Integer id = diseaseGroup.getId();
        flushAndClear();
        diseaseGroup = diseaseGroupDao.getById(id);
        assertThat(diseaseGroup).isNotNull();
        assertThat(diseaseGroup.getName()).isEqualTo(diseaseClusterName);
        assertThat(diseaseGroup.getGroupType()).isEqualTo(DiseaseGroupType.CLUSTER);
        assertThat(diseaseGroup.getPublicName()).isEqualTo(diseaseClusterPublicName);
        assertThat(diseaseGroup.getShortName()).isEqualTo(diseaseClusterShortName);
        assertThat(diseaseGroup.getAbbreviation()).isEqualTo(diseaseClusterAbbreviation);
        assertThat(diseaseGroup.getValidatorDiseaseGroup()).isNotNull();
        assertThat(diseaseGroup.getValidatorDiseaseGroup().getId()).isEqualTo(validatorDiseaseGroupId);
        assertThat(diseaseGroup.getValidationProcessStartDate()).isEqualTo(validationProcessStartDate);
        assertThat(diseaseGroup.getLastModelRunPrepDate()).isEqualTo(lastModelRunPrepDate);
        assertThat(diseaseGroup.getModelRunMinNewOccurrences()).isEqualTo(modelRunMinNewOccurrences);
        assertThat(diseaseGroup.getWeighting()).isEqualTo(weighting);
        assertThat(diseaseGroup.isGlobal()).isTrue();
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
        String diseaseName = "Test single disease";
        String diseaseMicroClusterName = "Test microcluster";
        DiseaseGroup diseaseCluster = diseaseGroupDao.getById(5);
        DiseaseGroup diseaseMicroCluster = new DiseaseGroup(diseaseCluster, diseaseMicroClusterName,
                DiseaseGroupType.MICROCLUSTER);
        DiseaseGroup disease = new DiseaseGroup(diseaseMicroCluster, diseaseName, DiseaseGroupType.SINGLE);

        // Act
        diseaseGroupDao.save(diseaseMicroCluster);
        diseaseGroupDao.save(disease);
        Integer id = disease.getId();
        flushAndClear();

        // Assert
        disease = diseaseGroupDao.getById(id);
        assertThat(disease).isNotNull();
        assertThat(disease.getName()).isEqualTo(diseaseName);
        assertThat(disease.getGroupType()).isEqualTo(DiseaseGroupType.SINGLE);
        assertThat(disease.getParentGroup()).isNotNull();
        assertThat(disease.getParentGroup()).isEqualTo(diseaseMicroCluster);
        assertThat(disease.getParentGroup().getParentGroup()).isNotNull();
        assertThat(disease.getParentGroup().getParentGroup()).isEqualTo(diseaseCluster);
        assertThat(disease.getCreatedDate()).isNotNull();
    }

    @Test
    public void loadNonExistentDiseaseGroup() {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(-1);
        assertThat(diseaseGroup).isNull();
    }

    @Test
    public void getAllDiseaseGroups() {
        List<DiseaseGroup> diseaseGroups = diseaseGroupDao.getAll();
        assertThat(diseaseGroups).hasSize(396);
    }

    private boolean assertThatContainsId(List<ValidatorDiseaseGroup> validatorDiseaseGroups, int id) {
        for (ValidatorDiseaseGroup validatorDiseaseGroup : validatorDiseaseGroups) {
            if (validatorDiseaseGroup.getId() == id) {
                return true;
            }
        }

        return false;
    }
}
