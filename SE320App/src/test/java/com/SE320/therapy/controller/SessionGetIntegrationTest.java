package com.SE320.therapy.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.SE320.therapy.dto.objects.SessionModality;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.SessionModule;
import com.SE320.therapy.exception.ApiExceptionHandler;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.repository.UserSessionRepository;
import com.SE320.therapy.service.SessionService;

class SessionGetIntegrationTest {

    private MockMvc mockMvc;
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        UserSessionRepository userSessionRepository = mock(UserSessionRepository.class);
        ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);

        SessionService sessionService = new SessionService(
            sessionRepository,
            userRepository,
            userSessionRepository,
            chatMessageRepository
        );

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new SessionController(sessionService))
            .setControllerAdvice(new ApiExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void getSessionLibrary_returnsLibraryFromRealService() throws Exception {
        CBTSession librarySession = new CBTSession();
        librarySession.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2"));
        librarySession.setSessionId(1002L);
        librarySession.setTitle("Cognitive Restructuring");
        librarySession.setDescription("Challenge unhelpful thoughts");
        librarySession.setDurationMinutes(25);
        librarySession.setOrderIndex(2);
        librarySession.setModalities(List.of(SessionModality.COGNITIVE));

        when(sessionRepository.findLibrarySessions()).thenReturn(List.of(librarySession));

        mockMvc.perform(get("/sessions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].sessionId").value(1002))
            .andExpect(jsonPath("$[0].title").value("Cognitive Restructuring"))
            .andExpect(jsonPath("$[0].durationMinutes").value(25));
    }

    @Test
    void getSessionDetail_returnsDetailFromRealService() throws Exception {
        CBTSession librarySession = new CBTSession();
        librarySession.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3"));
        librarySession.setSessionId(1003L);
        librarySession.setTitle("Behavioral Activation");
        librarySession.setDescription("Rebuild momentum with small actions");
        librarySession.setDurationMinutes(30);
        librarySession.setOrderIndex(3);
        librarySession.setObjectives(List.of("Identify one avoided activity"));
        librarySession.setModalities(List.of(SessionModality.BEHAVIORAL));

        SessionModule module = new SessionModule();
        module.setName("Burnout Recovery");
        librarySession.setModule(module);

        when(sessionRepository.findLibrarySessionBySessionId(1003L)).thenReturn(Optional.of(librarySession));

        mockMvc.perform(get("/sessions/1003"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value(1003))
            .andExpect(jsonPath("$.title").value("Behavioral Activation"))
            .andExpect(jsonPath("$.moduleName").value("Burnout Recovery"))
            .andExpect(jsonPath("$.objectives[0]").value("Identify one avoided activity"));
    }
}
