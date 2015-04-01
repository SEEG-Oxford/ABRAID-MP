package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrenceStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.MachineWeightingPredictor;
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.RasterFilePathFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the DiseaseOccurrenceHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelOutputHandler/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelOutputHandler/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelOutputHandler/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DiseaseOccurrenceHandlerIntegrationTest extends AbstractSpringIntegrationTests {
    private static final String LARGE_RASTER_FILENAME =
            "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/service/workflow/support/testdata/test_raster_large_double.tif";

    @Autowired
    private ModelRunService modelRunService;

    @Autowired
    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    @ReplaceWithMock
    private MachineWeightingPredictor machineWeightingPredictor;

    @Autowired
    @ReplaceWithMock
    private RasterFilePathFactory rasterFilePathFactory;

    @Test
    public void handleFirstBatch() throws Exception {
        // Arrange
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        int diseaseGroupId = 87;
        DateTime batchStartDate = new DateTime("2014-02-24"); // Occurrence date for earliest READY occurrence
        DateTime batchEndDate = new DateTime("2014-02-26").minusMillis(1).minusHours(1);
        ModelRun modelRun = createAndSaveTestModelRun(diseaseGroupId, batchStartDate, batchEndDate, null);

        // As this is the first batch, there was no training data available, so no prediction can be made.
        when(machineWeightingPredictor.findMachineWeighting(any(DiseaseOccurrence.class))).thenReturn(null);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        // As this is the first batch, all of them should have final weighting (and final weighting excluding spatial)
        // set to null.
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getFinalWeighting()).isNull();
            assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        }

        // 29 occurrences were batched: 16 of them were sent to the Data Validator i.e. they have status IN_REVIEW
        // and a non-null environmental suitability, but 4 of them were ineligible points so are READY with an
        // environmental suitability. The remaining occurrences are 16 that are AWAITING_BATCHING as a result of
        // the batching initialisation, and 3 that were already DISCARDED_FAILED_QC.
        assertOccurrences(occurrences, DiseaseOccurrenceStatus.IN_REVIEW, 16 + 9, 16 + 9);
        assertOccurrences(occurrences, DiseaseOccurrenceStatus.READY, 4, 4);
        assertOccurrences(occurrences, DiseaseOccurrenceStatus.AWAITING_BATCHING, 16, 0);
        assertOccurrences(occurrences, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 3, 0);

        // And the model run should have been updated correctly
        modelRun = modelRunService.getModelRunByName(modelRun.getName());
        assertThat(modelRun.getBatchingCompletedDate()).isEqualTo(now);
        assertThat(modelRun.getBatchOccurrenceCount()).isEqualTo(29);
    }

    @Test
    public void handleSecondBatch() throws Exception {
        // Arrange
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        int diseaseGroupId = 87;
        DateTime batchStartDate = new LocalDateTime("2014-02-24").toDateTime(); // Occurrence date for earliest READY occurrence
        DateTime batchEndDate = new LocalDateTime("2014-02-26").toDateTime().minusMillis(1);
        createAndSaveTestModelRun(diseaseGroupId, batchStartDate, batchEndDate, DateTime.now().minusWeeks(1));
        ModelRun modelRun2 = createAndSaveTestModelRun(diseaseGroupId, batchStartDate, batchEndDate, null);

        // Act
        diseaseOccurrenceHandler.handle(modelRun2);

        // Assert
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        // As this is the second batch, batching initialisation has not been performed, and therefore:
        // - the final weightings remain not-null
        // - the statuses remain as-is (in particular, none have been set to AWAITING_BATCHING)
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getStatus()).isNotEqualTo(DiseaseOccurrenceStatus.AWAITING_BATCHING);
            // In the test data, occurrences without status READY have null weightings already, so ignore them
            if (occurrence.getStatus().equals(DiseaseOccurrenceStatus.READY)) {
                assertThat(occurrence.getFinalWeighting()).isNotNull();
                assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNotNull();
            }
        }

        assertOccurrences(occurrences, DiseaseOccurrenceStatus.READY, 45, 0);
        assertOccurrences(occurrences, DiseaseOccurrenceStatus.IN_REVIEW, 0, 0);
        assertOccurrences(occurrences, DiseaseOccurrenceStatus.DISCARDED_FAILED_QC, 3, 0);

        // The model run should have been updated correctly
        modelRun2 = modelRunService.getModelRunByName(modelRun2.getName());
        assertThat(modelRun2.getBatchingCompletedDate()).isEqualTo(now);
        assertThat(modelRun2.getBatchOccurrenceCount()).isEqualTo(0);
    }

    private ModelRun createAndSaveTestModelRun(int diseaseGroupId, DateTime batchStartDate, DateTime batchEndDate,
                                               DateTime batchingCompletionDate) {
        String name = Double.toString(Math.random());
        ModelRun modelRun = new ModelRun(
                name, diseaseGroupId, "host", DateTime.now().minusDays(1), DateTime.now(), DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setResponseDate(DateTime.now());
        modelRun.setBatchStartDate(batchStartDate);
        modelRun.setBatchEndDate(batchEndDate);
        modelRun.setBatchingCompletedDate(batchingCompletionDate);
        modelRunService.saveModelRun(modelRun);
        flushAndClear();

        when(rasterFilePathFactory.getFullMeanPredictionRasterFile(eq(modelRun)))
                .thenReturn(new File(LARGE_RASTER_FILENAME));
        return modelRun;
    }

    private void assertOccurrences(List<DiseaseOccurrence> occurrences, DiseaseOccurrenceStatus status,
                                   int expectedSize, int expectedSizeWithEnvironmentalSuitability) {
        List<DiseaseOccurrence> filteredOccurrences = findOccurrencesByStatus(occurrences, status);
        assertThat(filteredOccurrences).hasSize(expectedSize);
        assertThat(getOccurrencesWithEnvironmentalSuitability(filteredOccurrences))
                .hasSize(expectedSizeWithEnvironmentalSuitability);
    }

    private List<DiseaseOccurrence> findOccurrencesByStatus(List<DiseaseOccurrence> occurrences,
                                                            DiseaseOccurrenceStatus status) {
        return select(occurrences,
                having(on(DiseaseOccurrence.class).getStatus(), equalTo(status)));
    }

    private List<DiseaseOccurrence> getOccurrencesWithEnvironmentalSuitability(List<DiseaseOccurrence> occurrences) {
        return select(occurrences,
                having(on(DiseaseOccurrence.class).getEnvironmentalSuitability(), notNullValue()));
    }
}
