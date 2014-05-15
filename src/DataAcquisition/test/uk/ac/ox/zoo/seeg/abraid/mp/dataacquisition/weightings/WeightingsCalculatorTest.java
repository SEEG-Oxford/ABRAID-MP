package uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.weightings;

import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReview;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceReviewResponse;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.Expert;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.dataacquisition.config.ConfigurationServiceImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the WeightingsCalculator class.
 * Copyright (c) 2014 University of Oxford
 */
public class WeightingsCalculatorTest {

    @Before
    public void setFixedTime() {
        DateTimeUtils.setCurrentMillisFixed(1400148490000L);
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsExecutesWhenLastRetrievalDateIsNull() throws Exception {
        // Arrange
        File file = createPropertiesFile();
        ConfigurationService configurationService = new ConfigurationServiceImpl(file);
        DiseaseService diseaseService = mock(DiseaseService.class);
        WeightingsCalculator target = new WeightingsCalculator(configurationService, diseaseService);

        // Act
        target.updateDiseaseOccurrenceWeightings();
        file.delete();

        // Assert
        assertThat(configurationService.getLastRetrievalDate()).isNotNull();
    }

    private File createPropertiesFile() throws FileNotFoundException {
        File file = new File("foo");
        PrintWriter out = new PrintWriter(file);
        out.println("lastRetrievalDate=");
        out.close();
        return file;
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsDoesNotExecuteWhenAWeekHasNotPassed() throws Exception {
        executeTest(1, 0.0);
    }

    @Test
    public void updateDiseaseOccurrenceWeightingsExecutesWhenAWeekHasPassed() throws Exception {
        executeTest(7, 3.0);
    }

    // An expert (who has a weighting of 3.0) has reviewed YES (value of 1) to an occurrence.
    // If 1 day has passed, the occurrence's weighting should remain at its initial value of 0.0.
    // If 7 days have passed then it should have updated to take a value of 3.0 (expert's weighting x response value).
    private void executeTest(int daysSinceLastRetrievalDate, double expectedWeighting) throws Exception {
        // Arrange
        double initialWeighting = 0.0;
        double expertWeighting = 3.0;

        File propertiesFile = createPropertiesFile();
        ConfigurationService configurationService = new ConfigurationServiceImpl(propertiesFile);
        configurationService.setLastRetrievalDate(LocalDateTime.now().minusDays(daysSinceLastRetrievalDate));

        DiseaseOccurrence occurrence = new DiseaseOccurrence();
        occurrence.setValidationWeighting(initialWeighting);

        Expert expert = new Expert();
        expert.setWeighting(expertWeighting);
        DiseaseOccurrenceReview review = new DiseaseOccurrenceReview(expert, occurrence,
                                                                     DiseaseOccurrenceReviewResponse.YES);
        DiseaseService diseaseService = mock(DiseaseService.class);
        when(diseaseService.getAllReviewsForDiseaseOccurrencesWithNewReviewsSinceLastRetrieval((LocalDateTime) any()))
                .thenReturn(new ArrayList<>(Arrays.asList(review)));

        WeightingsCalculator target = new WeightingsCalculator(configurationService, diseaseService);

        // Act
        target.updateDiseaseOccurrenceWeightings();
        propertiesFile.delete();

        // Assert
        assertThat(occurrence.getValidationWeighting()).isEqualTo(expectedWeighting);
    }
}
