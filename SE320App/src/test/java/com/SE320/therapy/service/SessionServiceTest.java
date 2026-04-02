package com.SE320.therapy.service;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.objects.SessionStatus;
import com.SE320.therapy.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionServiceTest {

    private SessionRepository sessionRepository;
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionRepository = mock(SessionRepository.class);
        sessionService = new SessionService(sessionRepository);
    }

    @Test
    void viewSessionLibrary_returnsExpectedLibrary() {
        List<String> library = sessionService.viewSessionLibrary();

        assertEquals(3, library.size());
        assertEquals("Thought Record", library.get(0));
        assertEquals("Behavioral Activation", library.get(1));
        assertEquals("Cognitive Restructuring", library.get(2));
    }

    @Test
    void startNewSession_createsSession_whenInputIsValid() {
        when(sessionRepository.existsByUserIdAndStatus("user1", SessionStatus.ACTIVE)).thenReturn(false);
        when(sessionRepository.save(any(CBTSession.class))).thenAnswer(invocation -> {
            CBTSession session = invocation.getArgument(0);
            session.setSessionId(1L);
            return session;
        });

        CBTSession session = sessionService.startNewSession("user1", "Thought Record");

        assertNotNull(session);
        assertEquals(1L, session.getSessionId());
        assertEquals("user1", session.getUserId());
        assertEquals("Thought Record", session.getSessionType());
        assertEquals(SessionStatus.ACTIVE, session.getStatus());
        assertNotNull(session.getStartedAt());

        verify(sessionRepository).existsByUserIdAndStatus("user1", SessionStatus.ACTIVE);
        verify(sessionRepository).save(any(CBTSession.class));
    }

    @Test
    void startNewSession_throwsException_whenUserIdIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.startNewSession("", "Thought Record"));

        assertEquals("User ID is required.", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void startNewSession_throwsException_whenSessionTypeIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.startNewSession("user1", ""));

        assertEquals("Session type is required.", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void startNewSession_throwsException_whenUserAlreadyHasActiveSession() {
        when(sessionRepository.existsByUserIdAndStatus("user1", SessionStatus.ACTIVE)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> sessionService.startNewSession("user1", "Thought Record"));

        assertEquals("User already has an active session.", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void continueSession_returnsSession_whenSessionExistsAndNotEnded() {
        CBTSession existingSession = new CBTSession();
        existingSession.setSessionId(1L);
        existingSession.setUserId("user1");
        existingSession.setSessionType("Thought Record");
        existingSession.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findBySessionIdAndUserId(1L, "user1"))
                .thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(existingSession)).thenReturn(existingSession);

        CBTSession result = sessionService.continueSession("user1", 1L);

        assertEquals(1L, result.getSessionId());
        assertEquals(SessionStatus.ACTIVE, result.getStatus());

        verify(sessionRepository).findBySessionIdAndUserId(1L, "user1");
        verify(sessionRepository).save(existingSession);
    }

    @Test
    void continueSession_throwsException_whenUserIdIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.continueSession("", 1L));

        assertEquals("User ID is required.", exception.getMessage());
        verify(sessionRepository, never()).findBySessionIdAndUserId(anyLong(), anyString());
    }

    @Test
    void continueSession_throwsException_whenSessionIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.continueSession("user1", null));

        assertEquals("Session ID is required.", exception.getMessage());
        verify(sessionRepository, never()).findBySessionIdAndUserId(anyLong(), anyString());
    }

    @Test
    void continueSession_throwsException_whenSessionDoesNotExist() {
        when(sessionRepository.findBySessionIdAndUserId(99L, "user1"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.continueSession("user1", 99L));

        assertEquals("Session not found.", exception.getMessage());
    }

    @Test
    void continueSession_throwsException_whenSessionAlreadyEnded() {
        CBTSession endedSession = new CBTSession();
        endedSession.setSessionId(1L);
        endedSession.setUserId("user1");
        endedSession.setSessionType("Thought Record");
        endedSession.setStatus(SessionStatus.ENDED);

        when(sessionRepository.findBySessionIdAndUserId(1L, "user1"))
                .thenReturn(Optional.of(endedSession));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> sessionService.continueSession("user1", 1L));

        assertEquals("This session has already ended.", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void endSession_updatesSession_whenSessionExistsAndActive() {
        CBTSession existingSession = new CBTSession();
        existingSession.setSessionId(1L);
        existingSession.setUserId("user1");
        existingSession.setSessionType("Thought Record");
        existingSession.setStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findBySessionIdAndUserId(1L, "user1"))
                .thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(existingSession)).thenReturn(existingSession);

        sessionService.endSession("user1", 1L);

        assertEquals(SessionStatus.ENDED, existingSession.getStatus());
        assertNotNull(existingSession.getEndedAt());

        verify(sessionRepository).findBySessionIdAndUserId(1L, "user1");
        verify(sessionRepository).save(existingSession);
    }

    @Test
    void endSession_throwsException_whenUserIdIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.endSession("", 1L));

        assertEquals("User ID is required.", exception.getMessage());
        verify(sessionRepository, never()).findBySessionIdAndUserId(anyLong(), anyString());
    }

    @Test
    void endSession_throwsException_whenSessionIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.endSession("user1", null));

        assertEquals("Session ID is required.", exception.getMessage());
        verify(sessionRepository, never()).findBySessionIdAndUserId(anyLong(), anyString());
    }

    @Test
    void endSession_throwsException_whenSessionDoesNotExist() {
        when(sessionRepository.findBySessionIdAndUserId(99L, "user1"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.endSession("user1", 99L));

        assertEquals("Session not found.", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void endSession_throwsException_whenSessionAlreadyEnded() {
        CBTSession endedSession = new CBTSession();
        endedSession.setSessionId(1L);
        endedSession.setUserId("user1");
        endedSession.setSessionType("Thought Record");
        endedSession.setStatus(SessionStatus.ENDED);

        when(sessionRepository.findBySessionIdAndUserId(1L, "user1"))
                .thenReturn(Optional.of(endedSession));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> sessionService.endSession("user1", 1L));

        assertEquals("This session has already ended.", exception.getMessage());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void viewSessionHistory_returnsSessions_whenUserIdIsValid() {
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

        when(sessionRepository.findByUserId("user1")).thenReturn(List.of(session1, session2));

        List<CBTSession> history = sessionService.viewSessionHistory("user1");

        assertEquals(2, history.size());
        verify(sessionRepository).findByUserId("user1");
    }

    @Test
    void viewSessionHistory_throwsException_whenUserIdIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sessionService.viewSessionHistory(""));

        assertEquals("User ID is required.", exception.getMessage());
        verify(sessionRepository, never()).findByUserId(anyString());
    }
}