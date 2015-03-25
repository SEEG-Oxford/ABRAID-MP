package uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support;

import ch.lambdaj.function.convert.Converter;
import org.apache.commons.mail.EmailException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.AbstractCommonSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.*;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.EmailService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.GeometryService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.LocationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.*;
import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests that the ModelRunOccurrencesSelector returns expected list of occurrences under each condition.
 * Copyright (c) 2014 University of Oxford
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelRunOccurrencesSelectorIntegrationTest extends AbstractCommonSpringIntegrationTests {
    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private GeometryService geometryService;

    @Autowired
    private AlertDao alertDao;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Autowired
    private FeedDao feedDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private ProvenanceDao provenanceDao;

    private EmailService emailService;

    @Before
    public void setUp() {
        emailService = mock(EmailService.class);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesThrowsExceptionWhenFewerOccurrencesThanMDV() {
        // Arrange
        int diseaseGroupId = 87;

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        catchException(selector).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, false)).hasSize(27);
        assertThat(diseaseService.getDiseaseGroupById(diseaseGroupId).getMinDataVolume()).isEqualTo(500);
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        verifySendEmail();
    }

    @Test
    public void selectModelRunDiseaseOccurrencesThrowsExceptionWhenFewerOccurrencesThanMDVAndAutomaticModelRunsAreDisabled() {
        // Arrange - NB. Automatic model runs are disabled by default
        int diseaseGroupId = 87;

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        catchException(selector).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, false)).hasSize(27);
        assertThat(diseaseService.getDiseaseGroupById(diseaseGroupId).getMinDataVolume()).isEqualTo(500);
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        verifySendEmail();
    }

    @Test
    public void selectModelRunDiseaseOccurrencesThrowsExceptionWhenFewerOccurrencesThanMDVAndGoldStandardDataUsed() {
        // Arrange - NB. Automatic model runs are disabled by default
        int diseaseGroupId = 87;
        addManuallyUploadedGoldStandardOccurrences();

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, true);

        // Act
        catchException(selector).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(diseaseService.getDiseaseOccurrencesForModelRunRequest(diseaseGroupId, true)).hasSize(2);
        assertThat(diseaseService.getDiseaseGroupById(diseaseGroupId).getMinDataVolume()).isEqualTo(500);
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        verifySendEmail();
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsNull() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        diseaseGroup.setMinDataVolume(minDataVolume);                   // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(null);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> occurrences = selector.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsAllOccurrencesWhenAutomaticModelRunsAreDisabled() throws Exception {
        // Arrange - NB. Automatic model runs are disabled by default
        int diseaseGroupId = 87;
        int minDataVolume = 20;
        int expectedAllOccurrencesSize = 27;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setMinDataVolume(minDataVolume);       // Ensure MDVSatisfied check will pass
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> occurrences = selector.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(expectedAllOccurrencesSize);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsTrueButParametersNotDefined() {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        diseaseGroup.setMinDataVolume(minDataVolume);                   // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(true);
        diseaseGroup.setHighFrequencyThreshold(null);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> occurrences = selector.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void selectModelRunDiseaseOccurrencesReturnsFirstSubsetWhenOccursInAfricaIsFalseButParametersNotDefined() {
        // Arrange
        int diseaseGroupId = 87;
        int minDataVolume = 20;

        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        diseaseGroup.setMinDataVolume(minDataVolume);                   // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(null);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> occurrences = selector.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(minDataVolume);
    }

    @Test
    public void selectorThrowsExceptionWhenMDSNotMetBeforeRunningOutOfOccurrencesForAfricanDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        diseaseGroup.setMinDataVolume(20);                              // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(true);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        catchException(selector).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        verifySendEmail();
    }

    @Test
    public void selectorReturnsNullWhenMDSNotMetBeforeRunningOutOfOccurrencesForOtherDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        diseaseGroup.setMinDataVolume(20);                              // Ensure MDVSatisfied check will pass
        diseaseGroup.setOccursInAfrica(false);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        catchException(selector).selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(caughtException()).isInstanceOf(ModelRunWorkflowException.class);
        verifySendEmail();
    }

    @Test
    public void selectorAddsOccurrencesUntilMinDataSpreadIsSatisfiedForAfricanDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        addOccurrences(diseaseGroup);
        diseaseGroup.setMinDataVolume(2);               // Selector will initially get the first 2 occurrences
        diseaseGroup.setOccursInAfrica(true);
        diseaseGroup.setMinDistinctCountries(2);
        diseaseGroup.setHighFrequencyThreshold(2);      // Then add the 3rd occurrence to satisfy high frequency check
        diseaseGroup.setMinHighFrequencyCountries(1);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> occurrences = selector.selectModelRunDiseaseOccurrences();

        // Assert
        assertThat(occurrences).hasSize(3);
        assertThat(extractDistinctGaulCodes(occurrences)).hasSize(2);
    }

    // Set up so that there is at least 1 occurrence in (minDistinctCountries = 2) countries,
    // and at least (highFrequencyThreshold = 2) occurrences in (minHighFrequencyCountries = 1) country.
    // NB. Locations must not have LocationPrecision.COUNTRY because they are not included in model run request.
    private void addOccurrences(DiseaseGroup diseaseGroup) {
        Location location1 = locationDao.getById(24204);  // Precise location in Somalia
        Location location2 = locationDao.getById(1574);   // Admin 1 location in Zimbabwe

        Alert alert = alertDao.getById(212855);

        DiseaseOccurrence o1 = new DiseaseOccurrence(1, diseaseGroup, location1, alert, DiseaseOccurrenceStatus.READY, 0.1, DateTime.now());
        DiseaseOccurrence o2 = new DiseaseOccurrence(2, diseaseGroup, location2, alert, DiseaseOccurrenceStatus.READY, 0.1, DateTime.now());
        DiseaseOccurrence o3 = new DiseaseOccurrence(3, diseaseGroup, location2, alert, DiseaseOccurrenceStatus.READY, 0.1, DateTime.now());

        diseaseOccurrenceDao.save(o1);
        diseaseOccurrenceDao.save(o2);
        diseaseOccurrenceDao.save(o3);
    }

    @Test
    public void selectorAddsOccurrencesUntilMinDataSpreadIsSatisfiedForOtherDiseaseGroup() {
        // Arrange
        int diseaseGroupId = 87;
        int minDistinctCountries = 2;
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(diseaseGroupId);
        diseaseGroup.setAutomaticModelRunsStartDate(DateTime.now());    // Enable automatic model runs
        diseaseGroup.setMinDataVolume(1);
        diseaseGroup.setOccursInAfrica(false);
        diseaseGroup.setMinDistinctCountries(minDistinctCountries);
        diseaseService.saveDiseaseGroup(diseaseGroup);

        ModelRunOccurrencesSelector selector = new ModelRunOccurrencesSelector(diseaseService, geometryService,
                emailService, diseaseGroupId, false);

        // Act
        List<DiseaseOccurrence> occurrences = selector.selectModelRunDiseaseOccurrences();

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

    private void addManuallyUploadedGoldStandardOccurrences() {
        Feed feed = new Feed("Test feed", provenanceDao.getByName(ProvenanceNames.MANUAL_GOLD_STANDARD));
        feedDao.save(feed);

        createManuallyUploadedDiseaseOccurrenceForDengue(feed);
        createManuallyUploadedDiseaseOccurrenceForDengue(feed);
    }

    private void createManuallyUploadedDiseaseOccurrenceForDengue(Feed feed) {
        DiseaseGroup diseaseGroup = diseaseService.getDiseaseGroupById(87);
        Location location = locationDao.getById(80);
        Alert alert = new Alert();
        alert.setFeed(feed);

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setDiseaseGroup(diseaseGroup);
        occurrence.setLocation(location);
        occurrence.setAlert(alert);
        occurrence.setFinalWeighting(1.0);
        occurrence.setFinalWeightingExcludingSpatial(1.0);
        occurrence.setStatus(DiseaseOccurrenceStatus.READY);
        occurrence.setOccurrenceDate(DateTime.now());
        diseaseOccurrenceDao.save(occurrence);
    }

    private void verifySendEmail() {
        try {
            verify(emailService).sendEmail(eq("Minimum Data Volume/Spread Not Satisfied"), anyString());
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
    }
}
