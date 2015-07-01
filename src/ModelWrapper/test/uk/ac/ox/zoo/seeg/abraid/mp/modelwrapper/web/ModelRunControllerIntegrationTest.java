package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.views.ModellingJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.ExecutionRunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.config.run.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.ox.zoo.seeg.abraid.mp.testutils.AbstractDiseaseOccurrenceGeoJsonTests.getTwoDiseaseOccurrenceFeaturesAsJson;

/**
 * Integration test for the model run ModelWrapper controller.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelWrapper/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelWrapper/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelWrapper/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ModelRunControllerIntegrationTest extends BaseWebIntegrationTests {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private MockMvc mockMvc;

    @ReplaceWithMock
    @Autowired
    private ModelRunner modelRunner;

    @ReplaceWithMock
    @Autowired
    private RunConfigurationFactory runConfigurationFactory;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void canTriggerNewRunWithValidContent() throws Exception {
        String runName = "foo_2014-04-24-10-50-27_cd0efc75-42d3-4d96-94b4-287e28fbcdac";
        setUpExpectedRunName(runName);

        this.mockMvc
                .perform(post("/api/model/run")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .content(buildZip("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":" + getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class) + ",\"extentWeightings\":{\"1\":1,\"2\":2}}")))
                        .andExpect(status().isOk())
                        .andExpect(content().string("{\"modelRunName\":\"" + runName + "\"}"));
    }

    @Test
    public void runRejectsRequestWithNoContent() throws Exception {
        this.mockMvc
                .perform(post("/api/model/run").contentType(MediaType.APPLICATION_OCTET_STREAM).content(new byte[0]))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void runRejectsRequestWithInvalidContent() throws Exception {
        this.mockMvc
                .perform(post("/api/model/run")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .content(buildZip("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":null,\"extentWeightings\":null}")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"errorText\":\"Run data must be provided and be valid.\"}"));
    }

    @Test
    public void runPageOnlyAcceptsPOST() throws Exception {
        setUpExpectedRunName("");
        this.mockMvc.perform(createRequest(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(createRequest(HttpMethod.POST)).andExpect(status().isOk());
        this.mockMvc.perform(createRequest(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(createRequest(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(createRequest(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    private String setUpExpectedRunName(String runName) throws ConfigurationException, IOException {
        RunConfiguration runConfiguration = mock(RunConfiguration.class);
        when(runConfiguration.getRunName()).thenReturn(runName);
        when(runConfiguration.getExecutionConfig()).thenReturn(mock(ExecutionRunConfiguration.class));
        when(runConfigurationFactory.createDefaultConfiguration(any(File.class), anyBoolean(), anyString()))
                .thenReturn(runConfiguration);
        return runName;
    }

    private MockHttpServletRequestBuilder createRequest(HttpMethod method) throws IOException, ZipException {
        return request(method, "/api/model/run")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .content(buildZip("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":" + getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class) + ",\"extentWeightings\":{\"1\":1,\"2\":2,\"3\":3}}"));

    }

    private byte[] buildZip(String content) throws IOException, ZipException {
        File dir = testFolder.newFolder();
        Files.createDirectory(Paths.get(dir.getAbsolutePath(), "covariates"));
        FileUtils.writeStringToFile(Paths.get(dir.getAbsolutePath(), "metadata.json").toFile(), content);
        File zipFile = testFolder.newFile();
        Files.delete(zipFile.toPath());
        ZipFile zip = new ZipFile(zipFile);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setIncludeRootFolder(false);
        zip.createZipFileFromFolder(dir, zipParameters, false, 0);
        return FileUtils.readFileToByteArray(zipFile);
    }
}
