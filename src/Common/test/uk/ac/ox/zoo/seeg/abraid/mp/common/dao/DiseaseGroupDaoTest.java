package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseExtent;
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
        int minNewOccurrences = 100;
        double weighting = 0.5;

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        DiseaseExtent parameters = new DiseaseExtent(diseaseGroup);

        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.CLUSTER);
        diseaseGroup.setPublicName(diseaseClusterPublicName);
        diseaseGroup.setShortName(diseaseClusterShortName);
        diseaseGroup.setAbbreviation(diseaseClusterAbbreviation);
        diseaseGroup.setValidatorDiseaseGroup(validatorDiseaseGroup);
        diseaseGroup.setValidationProcessStartDate(validationProcessStartDate);
        diseaseGroup.setLastModelRunPrepDate(lastModelRunPrepDate);
        diseaseGroup.setMinNewOccurrencesTrigger(minNewOccurrences);
        diseaseGroup.setWeighting(weighting);
        diseaseGroup.setGlobal(true);
        diseaseGroup.setDiseaseExtentParameters(parameters);

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
        assertThat(diseaseGroup.getMinNewOccurrencesTrigger()).isEqualTo(minNewOccurrences);
        assertThat(diseaseGroup.getWeighting()).isEqualTo(weighting);
        assertThat(diseaseGroup.isGlobal()).isTrue();
        assertThat(diseaseGroup.getParentGroup()).isNull();
        assertThat(diseaseGroup.getCreatedDate()).isNotNull();
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isEqualToComparingFieldByField(parameters);
    }

    @Test
    public void saveAndReloadDiseaseMicroCluster() {
        // Arrange
        String diseaseClusterName = "Test disease microcluster";
        DiseaseGroup diseaseCluster = diseaseGroupDao.getById(1);

        DiseaseGroup diseaseGroup = new DiseaseGroup();
        DiseaseExtent parameters = new DiseaseExtent(diseaseGroup);

        diseaseGroup.setName(diseaseClusterName);
        diseaseGroup.setGroupType(DiseaseGroupType.MICROCLUSTER);
        diseaseGroup.setParentGroup(diseaseCluster);
        diseaseGroup.setDiseaseExtentParameters(parameters);

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
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isEqualToComparingFieldByField(parameters);
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

        DiseaseExtent parameters = new DiseaseExtent(disease);
        disease.setDiseaseExtentParameters(parameters);

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
        assertThat(disease.getDiseaseExtentParameters()).isEqualToComparingFieldByField(parameters);
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

    @Test
    public void getIdsForAutomaticModelRuns() {
        List<Integer> ids = diseaseGroupDao.getIdsForAutomaticModelRuns();
        assertThat(ids).hasSize(1);
        assertThat(ids.get(0)).isEqualTo(87);
    }

    @Test
    public void getExistingDiseaseExtent() {
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        DiseaseExtent diseaseExtent = diseaseGroup.getDiseaseExtentParameters();
        assertThat(diseaseExtent.getMaxMonthsAgo()).isEqualTo(60);
        assertThat(diseaseExtent.getMinValidationWeighting()).isEqualTo(0.6);
    }

    @Test
    public void updateExistingDiseaseExtent() {
        // Arrange
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(87);
        DiseaseExtent diseaseExtent = diseaseGroup.getDiseaseExtentParameters();

        // Act
        diseaseExtent.setMaxMonthsAgo(120);
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(87);
        assertThat(diseaseGroup.getDiseaseExtentParameters().getMaxMonthsAgo()).isEqualTo(120);
    }

    @Test
    public void addDiseaseExtentToDiseaseGroupSavesWithSameDiseaseGroupId() {
        // Arrange
        int diseaseGroupId = 22;
        DiseaseGroup diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        DiseaseExtent diseaseExtent = new DiseaseExtent(diseaseGroup);
        diseaseGroup.setDiseaseExtentParameters(diseaseExtent);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        diseaseGroup = diseaseGroupDao.getById(diseaseGroupId);
        assertThat(diseaseGroup.getDiseaseExtentParameters()).isNotNull();
        assertThat(diseaseGroup.getDiseaseExtentParameters().getDiseaseGroupId()).isEqualTo(diseaseGroupId);
    }

    @Test
    public void saveNewDiseaseGroupSavesDiseaseExtentWithSameId() {
        // Arrange
        DiseaseGroup diseaseGroup = initialiseDiseaseGroup();
        DiseaseExtent parameters = new DiseaseExtent(diseaseGroup);
        diseaseGroup.setDiseaseExtentParameters(parameters);

        // Act
        diseaseGroupDao.save(diseaseGroup);
        flushAndClear();

        // Assert
        assertThat(diseaseGroup.getId()).isNotNull();
        assertThat(parameters.getDiseaseGroupId()).isEqualTo(diseaseGroup.getId());
    }

    private DiseaseGroup initialiseDiseaseGroup() {
        DiseaseGroup diseaseGroup = new DiseaseGroup("Name");
        diseaseGroup.setGroupType(DiseaseGroupType.SINGLE);
        diseaseGroup.setAutomaticModelRuns(false);
        return diseaseGroup;
    }
}
