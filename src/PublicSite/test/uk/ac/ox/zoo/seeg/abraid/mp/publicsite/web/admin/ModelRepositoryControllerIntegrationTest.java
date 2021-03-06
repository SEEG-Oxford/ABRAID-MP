package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin;

import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.common.config.ConfigurationService;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.workflow.support.SourceCodeManager;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* Integration test for the ModelRepositoryController.
* Copyright (c) 2014 University of Oxford
*/
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "file:PublicSite/web/WEB-INF/applicationContext.xml" })
public class ModelRepositoryControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    private MockMvc mockMvc;

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

    private MockHttpServletRequestBuilder requestToSync(HttpMethod method) {
        return request(method, "/admin/model/repo/sync")
                .param("repositoryUrl", "repositoryUrl");
    }

    @Test
    public void syncPageOnlyAcceptsPOST() throws Exception {
        this.mockMvc.perform(requestToSync(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToSync(HttpMethod.POST)).andExpect(status().isOk());
        this.mockMvc.perform(requestToSync(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToSync(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToSync(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void syncPageAcceptsValidRequest() throws Exception {
        this.mockMvc
                .perform(post("/admin/model/repo/sync")
                        .param("repositoryUrl", "repositoryUrl"))
                .andExpect(status().isOk());
    }

    @Test
    public void syncPageRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/admin/model/repo/sync")
                        .param("repositoryUrl", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void syncPageReturnsCorrectContent() throws Exception {
        when(sourceCodeManager.getAvailableVersions()).thenReturn(Arrays.asList("1", "2", "3"));

        this.mockMvc
                .perform(post("/admin/model/repo/sync")
                        .param("repositoryUrl", "repositoryUrl"))
                .andExpect(status().isOk())
                .andExpect(content().string("[\"1\",\"2\",\"3\"]"));
    }

    private MockHttpServletRequestBuilder requestToVersion(HttpMethod method) {
        return request(method, "/admin/model/repo/version")
                .param("version", "versionNumber");
    }

    @Test
    public void versionPageOnlyAcceptsPOST() throws Exception {
        when(sourceCodeManager.getAvailableVersions()).thenReturn(Arrays.asList("versionNumber"));

        this.mockMvc.perform(requestToVersion(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToVersion(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(requestToVersion(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToVersion(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToVersion(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void versionPageAcceptsValidRequest() throws Exception {
        when(sourceCodeManager.getAvailableVersions()).thenReturn(Arrays.asList("versionNumber"));

        this.mockMvc
                .perform(post("/admin/model/repo/version")
                        .param("version", "versionNumber"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void versionPageRejectsInvalidRequest() throws Exception {
        when(sourceCodeManager.getAvailableVersions()).thenReturn(Arrays.asList("versionNumber"));

        this.mockMvc
                .perform(post("/admin/model/repo/version")
                        .param("version", "unknown_number"))
                .andExpect(status().isBadRequest());
    }
}
