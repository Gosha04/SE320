package com.SE320.therapy.controller;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.objects.SessionStatus;
import com.SE320.therapy.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SessionControllerTest {

    private SessionService sessionService;
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        sessionService = mock(SessionService.class);
        sessionController = new SessionController(sessionService);
    }

    @Test
    void viewSessionLibrary_returnsLibraryFromService() {
        List<String> library = List.of(
                "Thought Record",
                "Behavioral Activation",
                "Cognitive Restructuring");

        when(sessionService.viewSessionLibrary()).thenReturn(library);

        List<String> result = sessionController.viewSessionLibrary();

        assertEquals(3, result.size());
        assertEquals("Thought Record", result.get(0));
        assertEquals("Behavioral Activation", result.get(1));
        assertEquals("Cognitive Restructuring", result.get(2));

        verify(sessionService).viewSessionLibrary();
    }

    @Test
    void startNewSession_returnsSessionFromService() {
        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartedAt(LocalDateTime.now());

        when(sessionService.startNewSession("user1", "Thought Record")).thenReturn(session);

        CBTSession result = sessionController.startNewSession("user1", "Thought Record");

        assertEquals(1L, result.getSessionId());
        assertEquals("user1", result.getUserId());
        assertEquals("Thought Record", result.getSessionType());
        assertEquals(SessionStatus.ACTIVE, result.getStatus());

        verify(sessionService).startNewSession("user1", "Thought Record");
    }

    @Test
    void viewSessionHistory_returnsHistoryFromService() {
        CBTSession session1 = new CBTSession();
        session1.setSessionId(1L);
        session1.setUserId("user1");
        session1.setSessionType("Thought Record");
        session1.setStatus(SessionStatus.ACTIVE);

        CBTSession session2 = new CBTSession();
        session2.setSessionId(2L);
        session2.setUserId("user1");
        session2.setSessionType("Behavioral Activation");
        session2.setStatus(SessionStatus.ENDED);

        when(sessionService.viewSessionHistory("user1")).thenReturn(List.of(session1, session2));

        List<CBTSession> result = sessionController.viewSessionHistory("user1");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getSessionId());
        assertEquals(2L, result.get(1).getSessionId());

        verify(sessionService).viewSessionHistory("user1");
    }

    @Test
    void continueSession_returnsSessionFromService() {
        CBTSession session = new CBTSession();
        session.setSessionId(1L);
        session.setUserId("user1");
        session.setSessionType("Thought Record");
        session.setStatus(SessionStatus.ACTIVE);

        when(sessionService.continueSession("user1", 1L)).thenReturn(session);

        CBTSession result = sessionController.continueSession("user1", 1L);

        assertEquals(1L, result.getSessionId());
        assertEquals("user1", result.getUserId());
        assertEquals("Thought Record", result.getSessionType());
        assertEquals(SessionStatus.ACTIVE, result.getStatus());

        verify(sessionService).continueSession("user1", 1L);
    }

    @Test
    void endSession_delegatesToService() {
        sessionController.endSession("user1", 1L);

        verify(sessionService).endSession("user1", 1L);
    }
}