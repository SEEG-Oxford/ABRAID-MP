package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.admin.covariates;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractAuthenticatingTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the covariates controller.
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "file:PublicSite/web/WEB-INF/applicationContext.xml" })
public class CovariatesControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private CovariatesControllerHelper covariatesControllerHelper;

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

        // Setup user
        PublicSiteUser loggedInUser = mock(PublicSiteUser.class);
        when(loggedInUser.getId()).thenReturn(1);
        AbstractAuthenticatingTests.setupCurrentUser(loggedInUser);
    }

    @Test
    public void updatePageAcceptsValidRequest() throws Exception {
        this.mockMvc
                .perform(post("/admin/covariates/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TEST_COVARIATE_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updatePageRejectsInvalidRequest() throws Exception {
        this.mockMvc
                .perform(post("/admin/covariates/config")
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
        return request(method, "/admin/covariates/config")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TEST_COVARIATE_JSON);
    }

    public static final String TEST_COVARIATE_JSON =
            "{\n" +
            "  \"files\": [\n" +
            "    { \"path\": \"access.tif\", \"name\": \"1\", \"hide\": false, \"enabled\": [ 22 ] },\n" +
            "    { \"path\": \"duffy_neg.tif\", \"name\": \"2\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"gecon.tif\", \"name\": \"3\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"mod_dem.tif\", \"name\": \"4\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"prec57a0.tif\", \"name\": \"5\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"prec57a1.tif\", \"name\": \"6\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"prec57mn.tif\", \"name\": \"8\", \"hide\": false, \"enabled\": [ 22, 60 ] },\n" +
            "    { \"path\": \"prec57a2.tif\", \"name\": \"7\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"prec57mx.tif\", \"name\": \"9\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"prec57p1.tif\", \"name\": \"10\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"prec57p2.tif\", \"name\": \"11\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"tempaucpf.tif\", \"name\": \"12\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"tempaucpv.tif\", \"name\": \"13\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"tempsuit.tif\", \"name\": \"14\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"upr_p.tif\", \"name\": \"15\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"upr_u.tif\", \"name\": \"16\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"wd0107a0.tif\", \"name\": \"17\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"wd0107mn.tif\", \"name\": \"18\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"wd0107mx.tif\", \"name\": \"19\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"wd0114a0.tif\", \"name\": \"20\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"wd0114mn.tif\", \"name\": \"21\", \"hide\": false, \"enabled\": [] },\n" +
            "    { \"path\": \"wd0114mx.tif\", \"name\": \"22\", \"hide\": false, \"enabled\": [] }\n" +
            "  ]\n" +
            "};";
}
