package com.SE320.therapy.service.rag;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.objects.ChatRole;
import com.SE320.therapy.objects.InteractionModality;
import com.SE320.therapy.objects.UserSessionStatus;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.UserSessionRepository;

class RagContextBuilderTest {

    private final EmbeddingService embeddingService = new HashingEmbeddingService();
    private SimpleVectorStore vectorStore;
    private UserSessionRepository sessionRepository;
    private DiaryEntryRepository diaryRepository;
    private ChatMessageRepository chatMessageRepository;
    private RagContextBuilder ragContextBuilder;

    @BeforeEach
    void setUp() {
        vectorStore = new SimpleVectorStore();
        sessionRepository = mock(UserSessionRepository.class);
        diaryRepository = mock(DiaryEntryRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);

        vectorStore.addAll(List.of(
                new VectorDocument(
                        "kb-1",
                        "Behavioral activation uses small actions to rebuild momentum during burnout.",
                        null,
                        embeddingService.embed("Behavioral activation uses small actions to rebuild momentum during burnout.")),
                new VectorDocument(
                        "kb-2",
                        "Catastrophizing assumes the worst outcome is likely and ignores coping ability.",
                        null,
                        embeddingService.embed("Catastrophizing assumes the worst outcome is likely and ignores coping ability."))));

        ragContextBuilder = new RagContextBuilder(
                vectorStore,
                embeddingService,
                sessionRepository,
                diaryRepository,
                chatMessageRepository);
    }

    @Test
    void buildContext_combinesKnowledgeHistoryDiaryAndTranscript() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        UserSession pastSession = userSession(sessionId, "Behavioral Activation Planning", UserSessionStatus.COMPLETED, 3, 6);
        when(sessionRepository.findByUserIdOrderByStartedAtDesc(userId)).thenReturn(List.of(pastSession));

        DiaryEntry diaryEntry = diaryEntry(userId, "Work inbox piled up", "I will never catch up", "I can finish one priority item first.");
        when(diaryRepository.findByUser_IdAndDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(0, 5)))
                .thenReturn(new PageImpl<>(List.of(diaryEntry)));
        when(diaryRepository.calculateAverageMoodImprovement(userId)).thenReturn(2.0d);
        when(diaryRepository.findTopDistortionsByUser(userId, PageRequest.of(0, 3))).thenReturn(List.of());

        ChatMessage userMessage = chatMessage(sessionId, ChatRole.USER, "I am exhausted and falling behind.");
        ChatMessage assistantMessage = chatMessage(sessionId, ChatRole.ASSISTANT, "Let's slow it down and pick one manageable next step.");
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId))
                .thenReturn(List.of(userMessage, assistantMessage));
        when(chatMessageRepository.findByUserSession_IdOrderByTimestampDesc(sessionId, PageRequest.of(0, 5)))
                .thenReturn(List.of(userMessage));

        String context = ragContextBuilder.buildContext(userId, sessionId, "burnout and falling behind at work");

        assertTrue(context.contains("Relevant CBT knowledge:"));
        assertTrue(context.contains("Behavioral activation uses small actions"));
        assertTrue(context.contains("Recent session history:"));
        assertTrue(context.contains("Diary patterns:"));
        assertTrue(context.contains("Current session transcript:"));
        assertTrue(context.contains("I am exhausted and falling behind."));
    }

    private UserSession userSession(UUID sessionId, String title, UserSessionStatus status, Integer moodBefore, Integer moodAfter) {
        CBTSession cbtSession = new CBTSession();
        cbtSession.setId(UUID.randomUUID());
        cbtSession.setTitle(title);

        User user = new User();
        user.setId(UUID.randomUUID());

        UserSession userSession = new UserSession();
        userSession.setId(sessionId);
        userSession.setUser(user);
        userSession.setCbtSession(cbtSession);
        userSession.setStatus(status);
        userSession.setStartedAt(LocalDateTime.now().minusDays(1));
        userSession.setMoodBefore(moodBefore);
        userSession.setMoodAfter(moodAfter);
        return userSession;
    }

    private DiaryEntry diaryEntry(UUID userId, String situation, String automaticThought, String alternativeThought) {
        User user = new User();
        user.setId(userId);

        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setId(UUID.randomUUID());
        diaryEntry.setUser(user);
        diaryEntry.setSituation(situation);
        diaryEntry.setAutomaticThought(automaticThought);
        diaryEntry.setAlternativeThought(alternativeThought);
        diaryEntry.setMoodBefore(3);
        diaryEntry.setMoodAfter(5);
        diaryEntry.setCreatedAt(LocalDateTime.now().minusHours(4));
        return diaryEntry;
    }

    private ChatMessage chatMessage(UUID sessionId, ChatRole role, String content) {
        UserSession userSession = new UserSession();
        userSession.setId(sessionId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(UUID.randomUUID());
        chatMessage.setUserSession(userSession);
        chatMessage.setRole(role);
        chatMessage.setModality(InteractionModality.TEXT);
        chatMessage.setContent(content);
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }
}
