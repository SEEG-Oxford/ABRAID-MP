package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.DiseaseService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

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
            "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_large_double.tif";

    @Autowired
    private ModelRunService modelRunService;

    @Autowired
    private DiseaseOccurrenceHandler diseaseOccurrenceHandler;

    @Autowired
    private DiseaseService diseaseService;

    @Test
    public void handleFirstBatch() throws Exception {
        // Arrange
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        int diseaseGroupId = 87;
        DateTime batchEndDate = new DateTime("2014-02-25T02:45:35");
        ModelRun modelRun = createAndSaveTestModelRun(diseaseGroupId, batchEndDate, null);

        // Act
        diseaseOccurrenceHandler.handle(modelRun);

        // Assert
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        // As this is the first batch, all of them should have final weighting (and final weighting excluding spatial)
        // set to null
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getFinalWeighting()).isNull();
            assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNull();
        }

        // All occurrences with is validated = true before the batch end date should have an environmental suitability,
        // and the others should not. Note that the batch end date's time is always 23:59:59.999 i.e. end of day.
        assertOccurrences(occurrences, true, 42, 27);
        assertOccurrences(occurrences, false, 3, 0);
        assertOccurrences(occurrences, null, 3, 0);

        // And the model run should have been updated correctly
        modelRun = modelRunService.getModelRunByName(modelRun.getName());
        assertThat(modelRun.getBatchingCompletedDate()).isEqualTo(now);
        assertThat(modelRun.getBatchOccurrenceCount()).isEqualTo(27);
    }

    @Test
    public void handleSecondBatch() throws Exception {
        // Arrange
        DateTime now = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(now.getMillis());

        int diseaseGroupId = 87;
        DateTime batchEndDate = new DateTime("2014-02-25T02:45:35");
        createAndSaveTestModelRun(diseaseGroupId, batchEndDate, DateTime.now().minusWeeks(1));
        ModelRun modelRun2 = createAndSaveTestModelRun(diseaseGroupId, batchEndDate, null);

        // Act
        diseaseOccurrenceHandler.handle(modelRun2);

        // Assert
        List<DiseaseOccurrence> occurrences = diseaseService.getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        // As this is the second batch, the final weighting (and final weighting excluding spatial) will not have
        // been nulled
        for (DiseaseOccurrence occurrence : occurrences) {
            assertThat(occurrence.getFinalWeighting()).isNotNull();
            assertThat(occurrence.getFinalWeightingExcludingSpatial()).isNotNull();
        }

        // Because the final weightings are all not null, none of them will have been assigned an environmental
        // suitability
        assertOccurrences(occurrences, true, 42, 0);
        assertOccurrences(occurrences, false, 3, 0);
        assertOccurrences(occurrences, null, 3, 0);

        // And the model run should have been updated correctly
        modelRun2 = modelRunService.getModelRunByName(modelRun2.getName());
        assertThat(modelRun2.getBatchingCompletedDate()).isEqualTo(now);
        assertThat(modelRun2.getBatchOccurrenceCount()).isEqualTo(0);
    }

    private ModelRun createAndSaveTestModelRun(int diseaseGroupId, DateTime batchEndDate,
                                               DateTime batchingCompletionDate) throws Exception {
        String name = Double.toString(Math.random());
        ModelRun modelRun = new ModelRun(name, diseaseGroupId, DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRun.setBatchEndDate(batchEndDate);
        modelRun.setBatchingCompletedDate(batchingCompletionDate);
        modelRunService.saveModelRun(modelRun);
        flushAndClear();

        byte[] gdalRaster = FileUtils.readFileToByteArray(new File(LARGE_RASTER_FILENAME));
        return modelRun;
    }

    private void assertOccurrences(List<DiseaseOccurrence> occurrences, Boolean isValidated, int expectedSize,
                                   int expectedSizeWithEnvironmentalSuitability) {
        List<DiseaseOccurrence> filteredOccurrences = findOccurrencesWithIsValidatedFlag(occurrences, isValidated);
        assertThat(filteredOccurrences).hasSize(expectedSize);
        assertThat(getOccurrencesWithEnvironmentalSuitability(filteredOccurrences))
                .hasSize(expectedSizeWithEnvironmentalSuitability);
    }

    private List<DiseaseOccurrence> findOccurrencesWithIsValidatedFlag(List<DiseaseOccurrence> occurrences,
                                                                       Boolean isValidated) {
        return select(occurrences,
                having(on(DiseaseOccurrence.class).isValidated(), equalTo(isValidated)));
    }

    private List<DiseaseOccurrence> getOccurrencesWithEnvironmentalSuitability(List<DiseaseOccurrence> occurrences) {
        return select(occurrences,
                having(on(DiseaseOccurrence.class).getEnvironmentalSuitability(), notNullValue()));
    }
}
