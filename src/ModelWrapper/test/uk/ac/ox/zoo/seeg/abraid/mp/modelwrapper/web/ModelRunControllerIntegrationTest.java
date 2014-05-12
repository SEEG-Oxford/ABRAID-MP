package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

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
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.RunConfigurationFactory;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.ModelRunner;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
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
        this.mockMvc
                .perform(post("/model/run")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":" + getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class) + ",\"extentWeightings\":{\"1\":1,\"2\":2}}"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void runRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/model/run").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void runPageOnlyAcceptsPOST() throws Exception {
        this.mockMvc.perform(createRequest(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(createRequest(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(createRequest(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(createRequest(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(createRequest(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    private MockHttpServletRequestBuilder createRequest(HttpMethod method) {
        return request(method, "/model/run")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"disease\":{\"id\":1,\"name\":\"foo\",\"abbreviation\":\"f\"},\"occurrences\":" + getTwoDiseaseOccurrenceFeaturesAsJson(ModellingJsonView.class) + ",\"extentWeightings\":{\"1\":1,\"2\":2,\"3\":3}}");
    }

}
