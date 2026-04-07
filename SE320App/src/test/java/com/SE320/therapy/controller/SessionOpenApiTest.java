package com.SE320.therapy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.SE320.therapy.config.SecurityConfig;
import com.SE320.therapy.exception.ApiExceptionHandler;
import com.SE320.therapy.service.SessionService;

@SpringBootTest(classes = SessionOpenApiTest.TestApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.cli.enabled=false")
class SessionOpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test
    void openApiDocs_includeSessionPathsAndResponses() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.paths['/sessions']").exists())
            .andExpect(jsonPath("$.paths['/sessions'].get.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/sessions'].get.responses['401']").exists())
            .andExpect(jsonPath("$.paths['/sessions'].get.responses['403']").exists())
            .andExpect(jsonPath("$.paths['/sessions/{sessionId}']").exists())
            .andExpect(jsonPath("$.paths['/sessions/{sessionId}'].get.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/sessions/{sessionId}/start']").exists())
            .andExpect(jsonPath("$.paths['/sessions/{sessionId}/start'].post.responses['201']").exists())
            .andExpect(jsonPath("$.paths['/sessions/{sessionId}/chat'].post.responses['200']").exists())
            .andExpect(jsonPath("$.paths['/sessions/{sessionId}/end'].post.responses['200']").exists());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(excludeName = {
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
        "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
    })
    @Import({ SessionController.class, SecurityConfig.class, ApiExceptionHandler.class })
    static class TestApplication {
    }
}
