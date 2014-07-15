package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.AlertDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.LocationDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Alert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseGroup;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Location;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the ModelRunRequesterHelper returns expected list of occurrences under each condition.
 * Copyright (c) 2014 University of Oxford
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelRunRequesterHelperIntegrationTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AlertDao alertDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Test
    public void selectModelRunDiseaseOccurrencesThrowsExceptionWhenFewerOccurrencesThanMDV() {
        // Arrange
        int diseaseGroupId = 87;

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        catchException(helper).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId)).hasSize(27);
        assertThat(diseaseService.getDiseaseGroupById(diseaseGroupId).getMinDataVolume()).isEqualTo(500);
        assertThat(caughtException()).isInstanceOf(ModelRunRequesterException.class);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsNull() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(minDataVolume);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(null);
        diseaseService.saveDiseaseGroup(diseaseGroup);

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
        diseaseService.saveDiseaseGroup(diseaseGroup);

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
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void helperThrowsExceptionWhenMDSNotMetBeforeRunningOutOfOccurrencesForAfricanDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(20);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(true);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        catchException(helper).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunRequesterException.class);
    }

    @Test
    public void helperReturnsNullWhenMDSNotMetBeforeRunningOutOfOccurrencesForOtherDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(20);       // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(false);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        catchException(helper).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunRequesterException.class);
    }

    @Test
    public void helperAddsOccurrencesUntilMinDataSpreadIsSatisfiedForAfricanDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        addOccurrences(diseaseGroup);
        diseaseGroup.setMinDataVolume(2);               // Helper will initially get the first 2 occurrences
        diseaseGroup.setOccursInAfrica(true);
        diseaseGroup.setMinDistinctCountries(2);
        diseaseGroup.setHighFrequencyThreshold(2);      // Then add the 3rd occurrence to satisfy high frequency check
        diseaseGroup.setMinHighFrequencyCountries(1);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(3);
        assertThat(extractDistinctGaulCodes(occurrences)).hasSize(2);

    }

    // Set up so that there is at least 1 occurrence in (minDistinctCountries = 2) countries,
    // and at least (highFrequencyThreshold = 2) occurrences in (minHighFrequencyCountries = 1) country.
    private void addOccurrences(DiseaseGroup diseaseGroup) {
        Location location1 = locationDao.getById(52);   // Ghana
        Location location2 = locationDao.getById(56);   // Zimbabwe

        Alert alert = alertDao.getById(212855);

        DiseaseOccurrence o1 = new DiseaseOccurrence(1, diseaseGroup, location1, alert, true, 0.1, DateTime.now());
        DiseaseOccurrence o2 = new DiseaseOccurrence(2, diseaseGroup, location2, alert, true, 0.1, DateTime.now());
        DiseaseOccurrence o3 = new DiseaseOccurrence(3, diseaseGroup, location2, alert, true, 0.1, DateTime.now());

        diseaseOccurrenceDao.save(o1);
        diseaseOccurrenceDao.save(o2);
        diseaseOccurrenceDao.save(o3);
    }

    @Test
    public void helperAddsOccurrencesUntilMinDataSpreadIsSatisfiedForOtherDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        int minDistinctCountries = 2;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(1);
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(minDistinctCountries);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunRequesterHelper helper = new ModelRunRequesterHelper(diseaseService, locationService, diseaseGroupId);

        // Act
        List<DiseaseOccurrence> occurrences = helper.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(2);
        assertThat(extractDistinctGaulCodes(occurrences)).hasSize(minDistinctCountries);
    }

    private Set<Integer> extractDistinctGaulCodes(List<DiseaseOccurrence> occurrences) {
        Set<Location> locations = new HashSet<>(extract(occurrences, on(DiseaseOccurrence.class).getLocation()));
        return new HashSet<>(convert(locations, new Converter<Location, Integer>() {
            public Integer convert(Location location) { return location.getCountryGaulCode(); }
        }));
    }
}
