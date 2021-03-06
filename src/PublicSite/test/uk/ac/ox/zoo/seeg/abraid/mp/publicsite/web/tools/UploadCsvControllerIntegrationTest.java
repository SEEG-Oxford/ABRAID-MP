package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the UploadCsvController class.
 *
 * Copyright (c) 2014 University of Oxford
 */
@ContextConfiguration(loader = SpringockitoWebContextLoader.class, locations = {
        "file:PublicSite/web/WEB-INF/abraid-servlet-beans.xml",
        "file:PublicSite/web/WEB-INF/applicationContext.xml" })
public class UploadCsvControllerIntegrationTest extends AbstractPublicSiteIntegrationTests {
    private static final String UPLOAD_URL = "/tools/uploadcsv/upload";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private FreeMarkerConfigurer freemarkerConfig;

    @Before
    public void setup() throws IOException {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        setUpCurrentUserService();
    }

    private void setUpCurrentUserService() {
        when(currentUserService.getCurrentUserId()).thenReturn(1);
    }

    @Test
    public void uploadCSVOnlyAcceptsPOST() throws Exception {
        String csv = "\n";
        MockMultipartFile file = new MockMultipartFile("file", "/path/to/filename",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, csv.getBytes());

        this.mockMvc.perform(requestToUploadCSV(file, false, false, 87)).andExpect(status().isOk());
        this.mockMvc.perform(requestToUploadCSV(file, false, true, 87)).andExpect(status().isOk());
        this.mockMvc.perform(requestToUploadCSV(file, true, false, 87)).andExpect(status().isOk());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    private MockHttpServletRequestBuilder requestToUploadCSV(MockMultipartFile file, boolean isBias, boolean isGoldStandard, int diseaseGroup) {
        return fileUpload(UPLOAD_URL).file(file)
                .param("isGoldStandard", Boolean.toString(isGoldStandard))
                .param("isBias", Boolean.toString(isBias))
                .param("diseaseGroup", Integer.toString(diseaseGroup));
    }

    private MockHttpServletRequestBuilder requestToUploadCSV(HttpMethod method) {
        return request(method, UPLOAD_URL).contentType(MediaType.MULTIPART_FORM_DATA);
    }
}
