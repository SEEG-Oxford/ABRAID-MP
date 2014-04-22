package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.model.SourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.text.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the root ModelWrapper controller.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelWrapper/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelWrapper/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelWrapper/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)

public class IndexControllerIntegrationTest extends BaseWebIntegrationTests {
    private MockMvc mockMvc;

    @Autowired
    private IndexController controller;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private ConfigurationService configurationService;

    @ReplaceWithMock
    @Autowired
    private SourceCodeManager sourceCodeManager;

    @Before
    public void setup() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void indexPageReturnsCorrectContent() throws Exception {
        // Arrange
        when(configurationService.getModelRepositoryUrl()).thenReturn("foo1");
        when(configurationService.getModelRepositoryVersion()).thenReturn("foo2");
        when(configurationService.getRExecutablePath()).thenReturn("foo3");
        when(configurationService.getMaxModelRunDuration()).thenReturn(123);
        when(configurationService.getCovariateDirectory()).thenReturn("foo4");
        when(sourceCodeManager.getAvailableVersions()).thenReturn(Arrays.asList("1", "2", "3"));
        List<String> expectedJavaScript = Arrays.asList(
                "url: \"foo1\"",
                "version: \"foo2\"",
                "availableVersions: [\"1\",\"2\",\"3\"]",
                "rPath: \"foo3\"",
                "runDuration: 123",
                "covariateDirectory: \"foo4\"");

        // Act
        ResultActions sendRequest = this.mockMvc.perform(get("/"));

        // Assert
        sendRequest.andExpect(status().isOk());
        sendRequest.andExpect(content().string(containsString("<title>ABRAID-MP ModelWrapper</title>")));
        for (String templatedParameter : expectedJavaScript) {
            sendRequest.andExpect(content().string(containsString(templatedParameter)));
        }
    }

    @Test
    public void indexPageOnlyAcceptsGET() throws Exception {
        when(configurationService.getModelRepositoryUrl()).thenReturn("");
        when(configurationService.getModelRepositoryVersion()).thenReturn("");
        when(configurationService.getRExecutablePath()).thenReturn("");
        when(configurationService.getMaxModelRunDuration()).thenReturn(0);
        when(sourceCodeManager.getAvailableVersions()).thenReturn(new ArrayList<String>());

        this.mockMvc.perform(get("/")).andExpect(status().isOk());
        this.mockMvc.perform(post("/")).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(put("/")).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(delete("/")).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(patch("/")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void authPageAcceptsValidRequest() throws Exception {
        this.mockMvc
                .perform(post("/auth")
                        .param("username", "username")
                        .param("password", "Password1")
                        .param("passwordConfirmation", "Password1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void authPageRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/auth")
                        .param("username", "username")
                        .param("password", "insufficientlycomplexpassword")
                        .param("passwordConfirmation", "insufficientlycomplexpassword"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authPageOnlyAcceptsPOST() throws Exception {
        this.mockMvc.perform(requestToAuth(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToAuth(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(requestToAuth(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToAuth(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToAuth(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    private MockHttpServletRequestBuilder requestToAuth(HttpMethod method) {
        return request(method, "/auth")
                .param("username", "username")
                .param("password", "Password1")
                .param("passwordConfirmation", "Password1");
    }
}
