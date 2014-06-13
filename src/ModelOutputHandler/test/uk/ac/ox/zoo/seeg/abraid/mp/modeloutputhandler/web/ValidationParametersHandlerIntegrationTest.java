package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.DiseaseOccurrenceDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.DiseaseOccurrence;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRunStatus;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.ModelRunService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Integration tests for the ValidationParametersHandler class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelOutputHandler/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelOutputHandler/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelOutputHandler/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ValidationParametersHandlerIntegrationTest extends AbstractSpringIntegrationTests {
    private static final String LARGE_RASTER_FILENAME = "Common/test/uk/ac/ox/zoo/seeg/abraid/mp/common/dao/test_raster_large_double.asc";

    @Autowired
    private ModelRunService modelRunService;

    @Autowired
    private ValidationParametersHandler validationParametersHandler;

    @Autowired
    private DiseaseOccurrenceDao diseaseOccurrenceDao;

    @Test
    public void handleValidationParametersWithCompletedModelRun() throws Exception {
        // Arrange
        int diseaseGroupId = 87;
        ModelRun modelRun = createAndSaveTestModelRun(diseaseGroupId);

        // Act
        validationParametersHandler.handleValidationParameters(modelRun);

        // Assert
        List<DiseaseOccurrence> occurrences = getDiseaseOccurrencesByDiseaseGroupId(diseaseGroupId);

        List<DiseaseOccurrence> occurrencesWithIsValidatedTrue = findOccurrencesWithIsValidatedFlag(occurrences, true);
        assertThat(occurrencesWithIsValidatedTrue).hasSize(45);
        assertThat(getOccurrencesWithNullEnvironmentalSuitability(occurrencesWithIsValidatedTrue)).hasSize(0);

        List<DiseaseOccurrence> occurrencesWithIsValidatedFalse = findOccurrencesWithIsValidatedFlag(occurrences, false);
        assertThat(occurrencesWithIsValidatedFalse).hasSize(0);

        List<DiseaseOccurrence> occurrencesWithIsValidatedNull = findOccurrencesWithIsValidatedFlag(occurrences, null);
        assertThat(occurrencesWithIsValidatedNull).hasSize(3);
        assertThat(getOccurrencesWithNullEnvironmentalSuitability(occurrencesWithIsValidatedNull)).hasSize(3);
    }

    private ModelRun createAndSaveTestModelRun(int diseaseGroupId) throws Exception {
        ModelRun modelRun = new ModelRun("test" + diseaseGroupId, diseaseGroupId, DateTime.now());
        modelRun.setStatus(ModelRunStatus.COMPLETED);
        modelRunService.saveModelRun(modelRun);

        byte[] gdalRaster = FileUtils.readFileToByteArray(new File(LARGE_RASTER_FILENAME));
        modelRunService.updateMeanPredictionRasterForModelRun(modelRun.getId(), gdalRaster);
        return modelRun;
    }

    private List<DiseaseOccurrence> getDiseaseOccurrencesByDiseaseGroupId(int diseaseGroupId) {
        List<DiseaseOccurrence> allOccurrences = diseaseOccurrenceDao.getAll();
        return select(allOccurrences,
                having(on(DiseaseOccurrence.class).getDiseaseGroup().getId(), IsEqual.equalTo(diseaseGroupId)));
    }

    private List<DiseaseOccurrence> findOccurrencesWithIsValidatedFlag(List<DiseaseOccurrence> occurrences,
                                                                       Boolean isValidated) {
        return select(occurrences,
                having(on(DiseaseOccurrence.class).isValidated(), IsEqual.equalTo(isValidated)));
    }

    private List<DiseaseOccurrence> getOccurrencesWithNullEnvironmentalSuitability(List<DiseaseOccurrence> occurrences) {
        return select(occurrences,
                having(on(DiseaseOccurrence.class).getEnvironmentalSuitability(), IsNull.nullValue()));
    }
}
