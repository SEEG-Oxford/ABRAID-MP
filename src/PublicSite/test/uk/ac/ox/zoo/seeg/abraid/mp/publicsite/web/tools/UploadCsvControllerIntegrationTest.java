package uk.ac.ox.zoo.seeg.abraid.mp.publicsite.web.tools;

import org.junit.Before;
import org.junit.Test;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.AbstractPublicSiteIntegrationTests;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.domain.PublicSiteUser;
import uk.ac.ox.zoo.seeg.abraid.mp.publicsite.security.CurrentUserService;
import uk.ac.ox.zoo.seeg.abraid.mp.testutils.SpringockitoWebContextLoader;

import java.io.IOException;
import java.util.ArrayList;

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
    private static final String URL = "/tools/uploadcsv";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @ReplaceWithMock
    @Autowired
    private CurrentUserService currentUserService;

    @Before
    public void setup() throws IOException {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        setUpCurrentUserService();
    }

    private void setUpCurrentUserService() {
        PublicSiteUser user = new PublicSiteUser(1, "test@email.com", "Test User", "Hashed password",
                new ArrayList<GrantedAuthority>());
        when(currentUserService.getCurrentUser()).thenReturn(user);
    }

    @Test
    public void updatePageOnlyAcceptsPOST() throws Exception {
        String csv = "\n";
        MockMultipartFile file = new MockMultipartFile("file", "/path/to/filename",
                MediaType.APPLICATION_OCTET_STREAM_VALUE, csv.getBytes());

        this.mockMvc.perform(requestToUploadCSV(file)).andExpect(status().isOk());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.GET)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.PUT)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.DELETE)).andExpect(status().isMethodNotAllowed());
        this.mockMvc.perform(requestToUploadCSV(HttpMethod.PATCH)).andExpect(status().isMethodNotAllowed());
    }

    private MockMultipartHttpServletRequestBuilder requestToUploadCSV(MockMultipartFile file) {
        return fileUpload(URL).file(file);
    }

    private MockHttpServletRequestBuilder requestToUploadCSV(HttpMethod method) {
        return request(method, URL).contentType(MediaType.MULTIPART_FORM_DATA);
    }
}
