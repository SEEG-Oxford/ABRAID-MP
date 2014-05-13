package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
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
import uk.ac.ox.zoo.seeg.abraid.mp.common.web.json.views.ModellingJsonView;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.IOException;

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
    public void canTriggerNewRunWithJsonContent() throws Exception {
        String runName = "foo_2014-04-24-10-50-27_cd0efc75-42d3-4d96-94b4-287e28fbcdac";
        setUpExpectedRunName(runName);

        this.mockMvc
                .perform(post("/model/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":" + getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class) + ",\"extentWeightings\":{\"1\":1,\"2\":2}}"))
                        .andExpect(status().isOk())
                        .andExpect(content().string("{\"modelRunName\":\"" + runName + "\"}"));
    }

    @Test
    public void runRejectsRequestWithNoContent() throws Exception {
        this.mockMvc
                .perform(post("/model/run").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void runRejectsRequestWithInvalidContent() throws Exception {
        this.mockMvc
                .perform(post("/model/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":null,\"extentWeightings\":null}"))
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
        when(runConfigurationFactory.createDefaultConfiguration(anyInt(), anyBoolean(), anyString(), anyString()))
                .thenReturn(runConfiguration);
        return runName;
    }

    private MockHttpServletRequestBuilder createRequest(HttpMethod method) {
        return request(method, "/model/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":" + getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class) + ",\"extentWeightings\":{\"1\":1,\"2\":2,\"3\":3}}");
    }
}
