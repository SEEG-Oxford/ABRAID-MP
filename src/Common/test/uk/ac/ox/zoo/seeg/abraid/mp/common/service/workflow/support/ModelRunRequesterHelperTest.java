package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import org.junit.Test;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the ModelRunRequesterHelper returns expected list of occurrences under each condition.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoContextLoader.class,
        locations = "classpath:uk/ac/ox/zoo/seeg/abraid/mp/common/config/beans.xml")
public class ModelRunRequesterHelperTest extends AbstractSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private LocationService locationService;

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsNullWhenFewerOccurrencesThanMDV() throws Exception {
        // Arrange
        int diseaseGroupId = 87;

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)).hasSize(27);
        assertThat(diseaseService.getDiseaseGroupById(diseaseGroupId).getMinDataVolume()).isEqualTo(500);
        assertThat(occurrences).isNull();
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsNull() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(minDataVolume);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(null);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsTrueButParametersNotDefined() {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(minDataVolume);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(true);
        diseaseGroup.setHighFrequencyThreshold(null);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsFalseButParametersNotDefined() {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(minDataVolume);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(null);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsNullWhenMDSNotMetBeforeRunningOutOfOccurrences() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(20);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(false);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).isNull();
    }
}
