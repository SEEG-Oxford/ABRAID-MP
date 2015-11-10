package uk.ac.ox.zoo.seeg.abraid.mp.common.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ValidatorDiseaseGroup;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the ExpertDao class.
 *
 * Copyright (c) 2014 University of Oxford
 */
public class ExpertDaoTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private ExpertDao expertDao;

    @Test
    public void saveAndReloadExpert() {
        // Arrange
        String expertName = "Test Expert";
        String expertEmail = "hello@world.com";
        String expertPassword = "password";
        String expertJob = "job";
        String expertInstitution = "institution";
        boolean visibilityRequested = true;
        boolean isSeegMember = true;
        boolean hasSeenHelpText = false;

        Expert expert = new Expert();
        expert.setName(expertName);
        expert.setEmail(expertEmail);
        expert.setPassword(expertPassword);
        expert.setJobTitle(expertJob);
        expert.setInstitution(expertInstitution);
        expert.setVisibilityRequested(visibilityRequested);
        expert.setSeegMember(isSeegMember);
        expert.setHasSeenHelpText(hasSeenHelpText);

        // Act
        expertDao.save(expert);

        // Assert
        assertThat(expert.getCreatedDate()).isNotNull();

        Integer id = expert.getId();
        flushAndClear();
        expert = expertDao.getByEmail(expertEmail);
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isNotNull();
        assertThat(expert.getId()).isEqualTo(id);
        assertThat(expert.getName()).isEqualTo(expertName);
        assertThat(expert.getJobTitle()).isEqualTo(expertJob);
        assertThat(expert.getInstitution()).isEqualTo(expertInstitution);
        assertThat(expert.getCreatedDate()).isNotNull();
        assertThat(expert.getUpdatedDate()).isNotNull();
        assertThat(expert.getVisibilityRequested()).isEqualTo(visibilityRequested);
        assertThat(expert.isSeegMember()).isEqualTo(isSeegMember);
        assertThat(expert.hasSeenHelpText()).isEqualTo(hasSeenHelpText);
    }

    @Test
    public void saveAndReloadExpertOnId() {
        // Arrange
        String expertName = "Test Expert";
        String expertEmail = "hello@world.com";
        String expertPassword = "password";
        String expertJob = "job";
        String expertInstitution = "institution";
        boolean visibilityRequested = true;


        Expert expert = new Expert();
        expert.setName(expertName);
        expert.setEmail(expertEmail);
        expert.setPassword(expertPassword);
        expert.setJobTitle(expertJob);
        expert.setInstitution(expertInstitution);
        //noinspection ConstantConditions
        expert.setVisibilityRequested(visibilityRequested);

        // Act
        expertDao.save(expert);

        // Assert
        assertThat(expert.getCreatedDate()).isNotNull();

        Integer id = expert.getId();
        flushAndClear();

        expert = expertDao.getById(id);
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isNotNull();
        assertThat(expert.getEmail()).isEqualTo(expertEmail);
        assertThat(expert.getName()).isEqualTo(expertName);
        assertThat(expert.getJobTitle()).isEqualTo(expertJob);
        assertThat(expert.getInstitution()).isEqualTo(expertInstitution);
        assertThat(expert.getCreatedDate()).isNotNull();
        assertThat(expert.getUpdatedDate()).isNotNull();
        //noinspection ConstantConditions
        assertThat(expert.getVisibilityRequested()).isEqualTo(visibilityRequested);
    }

    @Test
    public void loadNonExistentExpert() {
        // Arrange
        String expertEmail = "This expert does not exist";

        // Act
        Expert expert = expertDao.getByEmail(expertEmail);

        // Assert
        assertThat(expert).isNull();
    }

    @Test
    public void getAllExperts() {
        // Act
        List<Expert> experts = expertDao.getAll();

        // Assert
        assertThat(experts).hasSize(2);
    }

    @Test
    public void getCountOfPubliclyVisibleReturnsCorrectCount() {
        // Act
        long result = expertDao.getCountOfPubliclyVisible();

        // Assert
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void getCountOfPubliclyVisibleReturnsCorrectCountAfterModifications() {
        // Arrange
        Expert expert = expertDao.getById(2);
        expert.setVisibilityRequested(true);
        expert.setVisibilityApproved(true);
        expertDao.save(expert);

        // Act
        long result = expertDao.getCountOfPubliclyVisible();

        // Assert
        assertThat(result).isEqualTo(2);
    }

    @Test
    public void getPageOfPubliclyVisibleReturnsCorrectExperts() {
        // Act
        List<Expert> result = expertDao.getPageOfPubliclyVisible(1, 10);

        // Assert
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("Helena Patching");
    }

    @Test
         public void getPageOfPubliclyVisibleReturnsCorrectExpertsAfterModifications() {
        // Arrange
        Expert expert = expertDao.getById(2);
        expert.setVisibilityRequested(true);
        expert.setVisibilityApproved(true);
        expertDao.save(expert);

        // Act
        List<Expert> result = expertDao.getPageOfPubliclyVisible(1, 10);

        // Assert
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("Helena Patching");
        assertThat(result.get(1).getName()).isEqualTo("Ed Wiles");
    }

    @Test
    public void getPageOfPubliclyVisibleReturnsCorrectExpertsWithMultiplePages() {
        // Arrange
        for (int i = 0; i <= 9; i++) {
            Expert expert = new Expert();
            expert.setEmail("i=" + i);
            expert.setName("");
            expert.setPassword("");
            expert.setJobTitle("");
            expert.setInstitution("");
            expert.setVisibilityRequested(true);
            expert.setVisibilityApproved(true);
            expertDao.save(expert);
        }

        // Act
        List<Expert> result1 = expertDao.getPageOfPubliclyVisible(1, 3);
        List<Expert> result2 = expertDao.getPageOfPubliclyVisible(2, 3);
        List<Expert> result3 = expertDao.getPageOfPubliclyVisible(3, 3);
        List<Expert> result4 = expertDao.getPageOfPubliclyVisible(4, 3);

        // Assert
        assertThat(result1.size()).isEqualTo(3);
        assertThat(result2.size()).isEqualTo(3);
        assertThat(result3.size()).isEqualTo(3);
        assertThat(result4.size()).isEqualTo(2);
        HashSet<Expert> allResults = new HashSet<>(); // wont take duplicates
        allResults.addAll(result1);
        allResults.addAll(result2);
        allResults.addAll(result3);
        allResults.addAll(result4);
        assertThat(allResults.size()).isEqualTo(11);
    }

    @Test
    public void getExpertByIdReturnsExpertIfItExists() {
        // Act
        Expert expert = expertDao.getById(1);

        // Assert
        assertThat(expert).isNotNull();
        assertThat(expert.getId()).isEqualTo(1);
        assertThat(expert.getName()).isEqualTo("Helena Patching");
        // Upon execution of the next line, the lazily-loaded validatorDiseaseGroups set is actually loaded
        List<ValidatorDiseaseGroup> validatorDiseaseGroups = expert.getValidatorDiseaseGroups();
        assertThat(validatorDiseaseGroups).hasSize(2);
    }

    @Test
    public void getExpertByIdReturnsNullIfItDoesNotExist() {
        // Act
        Expert expert = expertDao.getById(-1);

        // Assert
        assertThat(expert).isNull();
    }
}
