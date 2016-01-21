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
 * Copyright (c) 2015 University of Oxford
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
            "{" +
                "\"files\":[" +
                    "{\"id\":1,\"subFiles\":[{\"id\":1,\"path\":\"access.tif\"}],\"name\":\"EC JRC Urban Accessability\",\"hide\":false,\"discrete\":false,\"enabled\":[87,64,250,277,60]}," +
                    "{\"id\":2,\"subFiles\":[{\"id\":2,\"path\":\"duffy_neg.tif\"}],\"name\":\"Prevalence of Duffy negativity (%)\",\"hide\":false,\"discrete\":false,\"enabled\":[253]}," +
                    "{\"id\":3,\"subFiles\":[{\"id\":3,\"path\":\"gecon.tif\"}],\"name\":\"G-Econ relative poverty\",\"hide\":false,\"discrete\":false,\"enabled\":[87,64,250,277,60]}," +
                    "{\"id\":4,\"subFiles\":[{\"id\":4,\"path\":\"mod_dem.tif\"}],\"name\":\"MODIS Elevation\",\"hide\":false,\"discrete\":false,\"enabled\":[64]}," +
                    "{\"id\":5,\"subFiles\":[{\"id\":5,\"path\":\"prec57a0.tif\"}],\"name\":\"WorldClim monthly precipitation (mean)\",\"hide\":false,\"discrete\":false,\"enabled\":[64,249]}," +
                    "{\"id\":6,\"subFiles\":[{\"id\":6,\"path\":\"prec57a1.tif\"}],\"name\":\"WorldClim monthly precipitation (1st amplitude)\",\"hide\":false,\"discrete\":false,\"enabled\":[249]}," +
                    "{\"id\":7,\"subFiles\":[{\"id\":7,\"path\":\"prec57a2.tif\"}],\"name\":\"WorldClim monthly precipitation (2nd amplitude)\",\"hide\":false,\"discrete\":false,\"enabled\":[249]}," +
                    "{\"id\":8,\"subFiles\":[{\"id\":8,\"path\":\"prec57mn.tif\"}],\"name\":\"WorldClim monthly precipitation (minimum)\",\"hide\":false,\"discrete\":false,\"enabled\":[87,188,250,277,189,190,60]}," +
                    "{\"id\":9,\"subFiles\":[{\"id\":9,\"path\":\"prec57mx.tif\"}],\"name\":\"WorldClim monthly precipitation (maximum)\",\"hide\":false,\"discrete\":false,\"enabled\":[87,249,188,250,277,191,189,60]}," +
                    "{\"id\":10,\"subFiles\":[{\"id\":10,\"path\":\"prec57p1.tif\"}],\"name\":\"WorldClim monthly precipitation (1st phase)\",\"hide\":false,\"discrete\":false,\"enabled\":[249]}," +
                    "{\"id\":11,\"subFiles\":[{\"id\":11,\"path\":\"prec57p2.tif\"}],\"name\":\"WorldClim monthly precipitation (2nd phase)\",\"hide\":false,\"discrete\":false,\"enabled\":[249]}," +
                    "{\"id\":12,\"subFiles\":[{\"id\":12,\"path\":\"tempaucpf.tif\"}],\"name\":\"Temperature suitability index (Malaria Pf)\",\"hide\":false,\"discrete\":false,\"enabled\":[249,250]}," +
                    "{\"id\":13,\"subFiles\":[{\"id\":13,\"path\":\"tempaucpv.tif\"}],\"name\":\"Temperature suitability index (Malaria Pv)\",\"hide\":false,\"discrete\":false,\"enabled\":[202,250,253]}," +
                    "{\"id\":14,\"subFiles\":[{\"id\":14,\"path\":\"tempsuit.tif\"}],\"name\":\"Temperature suitability index (Dengue)\",\"hide\":false,\"discrete\":false,\"enabled\":[60,87]}," +
                    "{\"id\":15,\"subFiles\":[{\"id\":21,\"path\":\"upr_p.tif\"}],\"name\":\"GRUMP peri-urban surface\",\"hide\":false,\"discrete\":true,\"enabled\":[191,277,253,250,249,202,190,189,188,87,64,60]}," +
                    "{\"id\":16,\"subFiles\":[{\"id\":22,\"path\":\"upr_u.tif\"}],\"name\":\"GRUMP urban surface\",\"hide\":false,\"discrete\":true,\"enabled\":[191,277,253,250,249,202,188,87,64,60,190]}," +
                    "{\"id\":17,\"subFiles\":[{\"id\":15,\"path\":\"wd0107a0.tif\"}],\"name\":\"AVHRR Land Surface Temperature (mean)\",\"hide\":false,\"discrete\":false,\"enabled\":[188,191,277]}," +
                    "{\"id\":18,\"subFiles\":[{\"id\":16,\"path\":\"wd0107mn.tif\"}],\"name\":\"AVHRR Land Surface Temperature (minimum)\",\"hide\":false,\"discrete\":false,\"enabled\":[188,250,191,189,190]}," +
                    "{\"id\":19,\"subFiles\":[{\"id\":17,\"path\":\"wd0107mx.tif\"}],\"name\":\"AVHRR Land Surface Temperature (maximum)\",\"hide\":false,\"discrete\":false,\"enabled\":[188,250,191,189]}," +
                    "{\"id\":20,\"subFiles\":[{\"id\":18,\"path\":\"wd0114a0.tif\"}],\"name\":\"AVHRR Normalized Difference Vegetation Index (mean)\",\"hide\":false,\"discrete\":false,\"enabled\":[87,249,202,188,250,253,277,60]}," +
                    "{\"id\":21,\"subFiles\":[{\"id\":19,\"path\":\"wd0114mn.tif\"}],\"name\":\"AVHRR Normalized Difference Vegetation Index (minimum)\",\"hide\":false,\"discrete\":false,\"enabled\":[191]}," +
                    "{\"id\":22,\"subFiles\":[{\"id\":20,\"path\":\"wd0114mx.tif\"}],\"name\":\"AVHRR Normalized Difference Vegetation Index (maximum)\",\"hide\":false,\"discrete\":false,\"enabled\":[188,191]}" +
                "]" +
            "};";
}
