package uk.ac.ox.zoo.seeg.abraid.mp.modeloutputhandler.web;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.ModelRunDao;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQL;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dao.NativeSQLImpl;
import uk.ac.ox.zoo.seeg.abraid.mp.common.domain.ModelRun;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractSpringIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contains integration tests for the MainController class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelOutputHandler/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelOutputHandler/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelOutputHandler/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MainControllerIntegrationTest extends AbstractSpringIntegrationTests {
    private static final String OUTPUT_HANDLER_PATH = "/modeloutputhandler/handleoutputs";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MainController controller;

    @Autowired
    private ModelRunDao modelRunDao;

    @Autowired
    private NativeSQL nativeSQL;

    private static final String TEST_MODEL_NAME = "deng_2014-05-16-13-28-57_482ae3ca-ab30-414d-acce-388baae7d83c";
    private static final String TEST_DATA_PATH = "ModelOutputHandler/test/uk/ac/ox/zoo/seeg/abraid/mp/modeloutputhandler/web/testdata";

    @Before
    public void setup() {
        // Set up Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void handleModelOutputsRejectsNonPOSTRequests() throws Exception {
        this.mockMvc.perform(get(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(put(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(delete(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(patch(OUTPUT_HANDLER_PATH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void handleModelOutputsStoresValidOutputs() throws Exception {
        DateTime expectedResponseDate = DateTime.now();
        DateTimeUtils.setCurrentMillisFixed(expectedResponseDate.getMillis());
        insertModelRun(TEST_MODEL_NAME);
        byte[] body = loadTestFile("valid_outputs.zip");
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        ModelRun run = modelRunDao.getByName(TEST_MODEL_NAME);
        assertThat(run.getResponseDate()).isEqualTo(expectedResponseDate);

        AssertThatRasterInDatabaseMatchesRasterInFile(run, "mean_prediction.asc", NativeSQLImpl.MEAN_PREDICTION_RASTER_COLUMN_NAME);
        AssertThatRasterInDatabaseMatchesRasterInFile(run, "prediction_uncertainty.asc", NativeSQLImpl.PREDICTION_UNCERTAINTY_RASTER_COLUMN_NAME);
    }

    @Test
    public void handleModelOutputsRejectsMalformedZipFile() throws Exception {
        byte[] malformedZipFile = "This is not a zip file".getBytes();
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(malformedZipFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"Probably not a zip file or a corrupted zip file\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingMetadata() throws Exception {
        insertModelRun(TEST_MODEL_NAME);
        byte[] body = loadTestFile("missing_metadata.zip");
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File metadata.json missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsIncorrectModelRunName() throws Exception {
        insertModelRun(TEST_MODEL_NAME);
        byte[] body = loadTestFile("incorrect_model_run_name.zip");
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"Model run with name deng_2014-05-13-11-26-37_0469aac2-d9b2-4104-907e-2886eff11682 does not exist\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingMeanPredictionRaster() throws Exception {
        insertModelRun(TEST_MODEL_NAME);
        byte[] body = loadTestFile("missing_mean_prediction.zip");
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File mean_prediction.asc missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    @Test
    public void handleModelOutputsRejectsMissingPredictionUncertaintyRaster() throws Exception {
        insertModelRun(TEST_MODEL_NAME);
        byte[] body = loadTestFile("missing_prediction_uncertainty.zip");
        this.mockMvc
                .perform(post(OUTPUT_HANDLER_PATH).content(body))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Model outputs handler failed with error \"File prediction_uncertainty.asc missing from model run outputs\". See ModelOutputHandler server logs for more details."));
    }

    private void insertModelRun(String name) {
        ModelRun modelRun = new ModelRun(name, DateTime.now());
        modelRunDao.save(modelRun);
    }

    private byte[] loadTestFile(String fileName) throws IOException {
        return FileUtils.readFileToByteArray(new File(TEST_DATA_PATH, fileName));
    }

    private void AssertThatRasterInDatabaseMatchesRasterInFile(ModelRun run, String fileName, String rasterColumnName) throws IOException {
        byte[] expectedRaster = loadTestFile(fileName);
        byte[] actualRaster = nativeSQL.loadRasterForModelRun(run.getId(), rasterColumnName);
        Assert.assertThat(new String(actualRaster), equalToIgnoringWhiteSpace(new String(expectedRaster)));
    }
}
