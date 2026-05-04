package com.SE320.therapy.mcp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;
import com.SE320.therapy.dto.objects.SeverityLevel;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.repository.UserSessionRepository;
import com.SE320.therapy.service.AiService;
import com.SE320.therapy.service.CrisisService;
import com.SE320.therapy.service.DashboardService;
import com.SE320.therapy.service.DiaryService;
import com.SE320.therapy.service.SessionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TherapyToolsProviderTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private DiaryService diaryService;

    @Mock
    private DashboardService dashboardService;

    @Mock
    private CrisisService crisisService;

    @Mock
    private AiService aiService;

    @Mock
    private UserSessionRepository userSessionRepository;

    private TherapyToolsProvider provider;

    @BeforeEach
    void setUp() {
        provider = new TherapyToolsProvider(
                sessionService,
                diaryService,
                dashboardService,
                crisisService,
                aiService,
                userSessionRepository);
    }

    @Test
    void startSessionDelegatesToSessionService() {
        UUID userId = UUID.randomUUID();
        SessionRunResponse expected = new SessionRunResponse(
                UUID.randomUUID(), userId, 2L, "Thought Record", "IN_PROGRESS", 5, null, LocalDateTime.now(), null);
        when(sessionService.startSession(eq(2L), any(StartSessionRequest.class))).thenReturn(expected);

        SessionRunResponse response = provider.startSession(userId.toString(), 2L);

        assertSame(expected, response);
        ArgumentCaptor<StartSessionRequest> requestCaptor = ArgumentCaptor.forClass(StartSessionRequest.class);
        verify(sessionService).startSession(eq(2L), requestCaptor.capture());
        assertEquals(userId, requestCaptor.getValue().userId());
        assertEquals(5, requestCaptor.getValue().moodBefore());
    }

    @Test
    void chatInSessionResolvesActiveUserSessionBeforeDelegating() {
        UUID userId = UUID.randomUUID();
        UUID activeSessionId = UUID.randomUUID();
        UserSession activeSession = activeSession(activeSessionId, userId, 7L);
        SessionChatResponse expected = new SessionChatResponse(activeSessionId, 7L, null, null);
        when(userSessionRepository.findById(activeSessionId)).thenReturn(Optional.of(activeSession));
        when(sessionService.sendChatMessage(eq(7L), any(SendChatMessageRequest.class))).thenReturn(expected);

        SessionChatResponse response = provider.chatInSession(activeSessionId.toString(), "I feel stuck.");

        assertSame(expected, response);
        ArgumentCaptor<SendChatMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendChatMessageRequest.class);
        verify(sessionService).sendChatMessage(eq(7L), requestCaptor.capture());
        assertEquals(userId, requestCaptor.getValue().userId());
        assertEquals("I feel stuck.", requestCaptor.getValue().message());
    }

    @Test
    void endSessionResolvesActiveUserSessionBeforeDelegating() {
        UUID userId = UUID.randomUUID();
        UUID activeSessionId = UUID.randomUUID();
        UserSession activeSession = activeSession(activeSessionId, userId, 3L);
        SessionRunResponse expected = new SessionRunResponse(
                activeSessionId, userId, 3L, "Behavioral Activation", "COMPLETED", 5, 5, LocalDateTime.now(), LocalDateTime.now());
        when(userSessionRepository.findById(activeSessionId)).thenReturn(Optional.of(activeSession));
        when(sessionService.endSession(eq(3L), any(EndSessionRequest.class))).thenReturn(expected);

        SessionRunResponse response = provider.endSession(activeSessionId.toString(), "Finished check-in.");

        assertSame(expected, response);
        ArgumentCaptor<EndSessionRequest> requestCaptor = ArgumentCaptor.forClass(EndSessionRequest.class);
        verify(sessionService).endSession(eq(3L), requestCaptor.capture());
        assertEquals(userId, requestCaptor.getValue().userId());
        assertEquals(5, requestCaptor.getValue().moodAfter());
    }

    @Test
    void createDiaryEntryMapsMcpInputsToDiaryRequest() {
        UUID userId = UUID.randomUUID();
        DiaryEntryResponse expected = new DiaryEntryResponse(UUID.randomUUID(), "created", LocalDateTime.now());
        when(diaryService.createEntry(eq(userId), any(DiaryEntryCreateRequest.class))).thenReturn(expected);

        DiaryEntryResponse response = provider.createDiaryEntry(
                userId.toString(), "After class", "I will fail everything", "anxious");

        assertSame(expected, response);
        ArgumentCaptor<DiaryEntryCreateRequest> requestCaptor = ArgumentCaptor.forClass(DiaryEntryCreateRequest.class);
        verify(diaryService).createEntry(eq(userId), requestCaptor.capture());
        assertEquals("After class", requestCaptor.getValue().getSituation());
        assertEquals("I will fail everything", requestCaptor.getValue().getAutomaticThought());
        assertEquals(5, requestCaptor.getValue().getMoodBefore());
        assertEquals(5, requestCaptor.getValue().getMoodAfter());
    }

    @Test
    void thoughtAndCrisisToolsDelegateToExistingServices() {
        List<DistortionSuggestion> suggestions = List.of(new DistortionSuggestion("catastrophizing", 0.8, "worst case"));
        CrisisDetectionResponse crisis = new CrisisDetectionResponse(false, SeverityLevel.MILD, List.of(), List.of("Breathe"));
        when(diaryService.suggestDistortions("Everything is ruined")).thenReturn(suggestions);
        when(crisisService.detectCrisisIndicators(any())).thenReturn(crisis);

        assertSame(suggestions, provider.analyzeThought("Everything is ruined"));
        assertSame(crisis, provider.detectCrisis("I am overwhelmed"));
    }

    private UserSession activeSession(UUID activeSessionId, UUID userId, Long cbtSessionId) {
        User user = new User();
        user.setId(userId);

        CBTSession cbtSession = new CBTSession();
        cbtSession.setSessionId(cbtSessionId);

        UserSession userSession = new UserSession();
        userSession.setId(activeSessionId);
        userSession.setUser(user);
        userSession.setCbtSession(cbtSession);
        return userSession;
    }
}
