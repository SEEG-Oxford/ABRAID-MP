package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.configuration.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the misc forms ModelWrapper controller.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:ModelWrapper/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelWrapper/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelWrapper/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MiscControllerIntegrationTest extends BaseWebIntegrationTests {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); ///CHECKSTYLE:SUPPRESS VisibilityModifier

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private ConfigurationService configurationService;

    @Before
    public void setup() {
        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    private MockHttpServletRequestBuilder requestToRPath(HttpMethod method) {
        return request(method, "/misc/rpath")
                .param("value", "foo");
    }

    @Test
    public void rPathPageOnlyAcceptsPOST() throws Exception {
        this.mockMvc.perform(requestToRPath(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToRPath(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(requestToRPath(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToRPath(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToRPath(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void rPathPageAcceptsValidRequest() throws Exception {
        this.mockMvc
                .perform(post("/misc/rpath")
                        .param("value", "foo"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void rPathPageRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/misc/rpath")
                        .param(".", ""))
                .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder requestToRunDuration(HttpMethod method) {
        return request(method, "/misc/runduration")
                .param("value", "1234");
    }

    @Test
    public void runDurationPageOnlyAcceptsPOST() throws Exception {
        this.mockMvc.perform(requestToRunDuration(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToRunDuration(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(requestToRunDuration(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToRunDuration(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToRunDuration(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void runDurationPageAcceptsValidRequest() throws Exception {
        this.mockMvc
                .perform(post("/misc/runduration")
                        .param("value", "1234"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void runDurationPageRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/misc/runduration")
                        .param("value", "not_number"))
                .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder requestToCovariateDirectory(HttpMethod method) {
        return request(method, "/misc/covariatedirectory")
                .param("value", "dir");
    }

    @Test
    public void covariateDirectoryPageOnlyAcceptsPOST() throws Exception {
        when(configurationService.getCovariateDirectory()).thenReturn("dir");
        this.mockMvc.perform(requestToCovariateDirectory(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToCovariateDirectory(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(requestToCovariateDirectory(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToCovariateDirectory(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToCovariateDirectory(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void covariateDirectoryPageAcceptsValidRequest() throws Exception {
        when(configurationService.getCovariateDirectory()).thenReturn("dir");
        this.mockMvc
                .perform(post("/misc/covariatedirectory")
                        .param("value", testFolder.newFolder().getAbsolutePath()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void covariateDirectoryPageRejectsInvalidRequest() throws Exception {
        when(configurationService.getCovariateDirectory()).thenReturn("dir");
        this.mockMvc
                .perform(post("/misc/covariatedirectory")
                        .param("value", ""))
                .andExpect(status().isBadRequest());
    }
}
