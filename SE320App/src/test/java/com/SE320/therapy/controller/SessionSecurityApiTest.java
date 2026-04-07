package com.SE320.therapy.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.SE320.therapy.config.SecurityConfig;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.exception.ApiExceptionHandler;
import com.SE320.therapy.service.SessionService;

import static org.mockito.Mockito.when;

@WebMvcTest(SessionController.class)
@Import({ SecurityConfig.class, ApiExceptionHandler.class })
@TestPropertySource(properties = "app.cli.enabled=false")
class SessionSecurityApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SessionService sessionService;

    @Test
    void getSessionLibrary_withoutAuthentication_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/sessions"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void getSessionLibrary_withAdminRole_returnsForbidden() throws Exception {
        mockMvc.perform(get("/sessions").with(user("admin").roles("ADMIN")))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error.code").value("FORBIDDEN"))
            .andExpect(jsonPath("$.error.details[0].field").value("authorization"));
    }

    @Test
    void getSessionLibrary_withPatientRole_returnsOk() throws Exception {
        when(sessionService.getSessionLibrary()).thenReturn(List.of(
            new SessionLibraryItemResponse(1001L, "Thought Record", "Intro", 20, 1, List.of("COGNITIVE"))
        ));

        mockMvc.perform(get("/sessions").with(user("patient").roles("PATIENT")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].sessionId").value(1001));
    }
}
