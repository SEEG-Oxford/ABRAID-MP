package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covarites;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateConfiguration;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonCovariateFile;
import uk.ac.ox.zoo.seeg.abraid.mp.common.dto.json.JsonDisease;
import uk.ac.ox.zoo.seeg.abraid.mp.common.service.core.CovariateService;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.text.StringContains.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the covariates controller.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("file:ModelWrapper/web")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CovariatesControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private CovariateService covariateService;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Before
    public void setup() throws IOException {
        // Add CommonWeb to the freemarker lookup path. In deployment the files will have been copied to local.
        TemplateLoader normalLoader = freemarkerConfig.getConfiguration().getTemplateLoader();
        freemarkerConfig.getConfiguration().setTemplateLoader(new MultiTemplateLoader(new TemplateLoader[] {
                new FileTemplateLoader(new File("CommonWeb/web/WEB-INF/freemarker")),
                normalLoader
        }));

        // Setup Spring test in standalone mode
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void covariatesPageReturnsCorrectContent() throws Exception {
        // Arrange
        when(covariateService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariateService.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariateService.getCovariateConfiguration().setDiseases(new ArrayList<JsonDisease>());
        String expectedJavaScript = "var initialData = {\"diseases\":[],\"files\":[]};";

        // Act
        ResultActions sendRequest = this.mockMvc.perform(get("/covariates"));

        // Assert
        sendRequest.andExpect(status().isOk());
        sendRequest.andExpect(content().string(containsString("<title>ABRAID-MP ModelWrapper</title>")));
        sendRequest.andExpect(content().string(containsString(expectedJavaScript)));
    }

    @Test
    public void covariatesPageOnlyAcceptsGET() throws Exception {
        when(covariateService.getCovariateConfiguration()).thenReturn(new JsonCovariateConfiguration());
        covariateService.getCovariateConfiguration().setFiles(new ArrayList<JsonCovariateFile>());
        covariateService.getCovariateConfiguration().setDiseases(new ArrayList<JsonDisease>());

        this.mockMvc.perform(get("/covariates")).andExpect(status().isOk());
        this.mockMvc.perform(post("/covariates")).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(put("/covariates")).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(delete("/covariates")).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(patch("/covariates")).andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void updatePageAcceptsValidRequest() throws Exception {
        this.mockMvc
                .perform(post("/covariates/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_COVARIATE_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updatePageRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/covariates/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updatePageOnlyAcceptsPOST() throws Exception {
        this.mockMvc.perform(requestToUpdate(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUpdate(HttpMethod.POST)).andExpect(status().isNoContent());
        this.mockMvc.perform(requestToUpdate(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUpdate(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUpdate(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    private MockHttpServletRequestBuilder requestToUpdate(HttpMethod method) {
        return request(method, "/covariates/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TEST_COVARIATE_JSON);
    }

    public static final String TEST_COVARIATE_JSON =
            "{\n" +
            "  \"diseases\" : [ {\n" +
            "    \"id\" : 22,\n" +
            "    \"name\" : \"Ascariasis\"\n" +
            "  }, {\n" +
            "    \"id\" : 64,\n" +
            "    \"name\" : \"Cholera\"\n" +
            "  } ],\n" +
            "  \"files\" : [ {\n" +
            "    \"path\" : \"f1\",\n" +
            "    \"name\" : \"a\",\n" +
            "    \"info\" : null,\n" +
            "    \"hide\" : false,\n" +
            "    \"enabled\" : [ 22 ]\n" +
            "  }, {\n" +
            "    \"path\" : \"f2\",\n" +
            "    \"name\" : \"\",\n" +
            "    \"info\" : null,\n" +
            "    \"hide\" : true,\n" +
            "    \"enabled\" : [ ]\n" +
            "  } ]\n" +
            "}";
}
