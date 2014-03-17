package uk.ac.ox.zoo.seeg.abraid.mp.modelwrapper.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.text.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the root ModelWrapper controller.
 * Copyright (c) 2014 University of Oxford
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:ModelWrapper/web/WEB-INF/abraid-servlet-beans.xml",
        "file:ModelWrapper/web/WEB-INF/applicationContext.xml"
})
@WebAppConfiguration("file:ModelWrapper/web")
public class IndexControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private IndexController controller;

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
    public void indexPageReturnsCorrectContent() throws Exception {
        this.mockMvc
                .perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>ABRAID-MP: ModelWrapper</title>")));
    }

    @Test
    public void indexPageOnlyAcceptsGET() throws Exception {
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
