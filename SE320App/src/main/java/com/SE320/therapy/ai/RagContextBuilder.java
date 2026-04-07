package com.SE320.therapy.ai;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.DiaryEntry;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.DiaryEntryRepository;
import com.SE320.therapy.repository.UserSessionRepository;

@Component
public class RagContextBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;
    private final UserSessionRepository sessionRepository;
    private final DiaryEntryRepository diaryRepository;
    private final ChatMessageRepository chatMessageRepository;

    public RagContextBuilder(
            VectorStore vectorStore,
            EmbeddingService embeddingService,
            UserSessionRepository sessionRepository,
            DiaryEntryRepository diaryRepository,
            ChatMessageRepository chatMessageRepository) {
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
        this.sessionRepository = sessionRepository;
        this.diaryRepository = diaryRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public String buildContext(UUID userId, UUID sessionId, String query) {
        StringBuilder context = new StringBuilder();

        appendKnowledgeContext(context, query);
        appendRelevantPastSessions(context, userId, query);
        appendRecentSessionHistory(context, userId);
        appendDiaryPatterns(context, userId, query);
        appendCurrentSessionTranscript(context, sessionId);

        return context.toString().trim();
    }

    private void appendKnowledgeContext(StringBuilder context, String query) {
        double[] queryEmbedding = embeddingService.embed(query);
        List<VectorMatch> matches = vectorStore.similaritySearch(queryEmbedding, 4);
        if (matches.isEmpty()) {
            return;
        }

        context.append("Relevant CBT knowledge:\n");
        for (VectorMatch match : matches) {
            context.append("- ")
                    .append(match.document().getContent())
                    .append('\n');
        }
        context.append('\n');
    }

    private void appendRelevantPastSessions(StringBuilder context, UUID userId, String query) {
        List<UserSession> recentSessions = sessionRepository.findByUserIdOrderByStartedAtDesc(userId)
                .stream()
                .limit(8)
                .toList();

        List<String> rankedSummaries = rankTexts(buildSessionSummaries(recentSessions), query, 2);
        if (rankedSummaries.isEmpty()) {
            return;
        }

        context.append("Relevant prior sessions:\n");
        for (String summary : rankedSummaries) {
            context.append("- ").append(summary).append('\n');
        }
        context.append('\n');
    }

    private void appendRecentSessionHistory(StringBuilder context, UUID userId) {
        List<UserSession> recentSessions = sessionRepository.findByUserIdOrderByStartedAtDesc(userId)
                .stream()
                .limit(3)
                .toList();

        if (recentSessions.isEmpty()) {
            return;
        }

        context.append("Recent session history:\n");
        for (UserSession session : recentSessions) {
            context.append("- ")
                    .append(resolveSessionLabel(session))
                    .append(" | status: ")
                    .append(session.getStatus());

            if (session.getStartedAt() != null) {
                context.append(" | started: ").append(DATE_FORMAT.format(session.getStartedAt()));
            }
            if (session.getMoodBefore() != null) {
                context.append(" | mood before: ").append(session.getMoodBefore());
            }
            if (session.getMoodAfter() != null) {
                context.append(" | mood after: ").append(session.getMoodAfter());
            }
            context.append('\n');
        }
        context.append('\n');
    }

    private void appendDiaryPatterns(StringBuilder context, UUID userId, String query) {
        List<DiaryEntry> recentEntries = diaryRepository
                .findByUser_IdAndDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(0, 5))
                .getContent();

        if (recentEntries.isEmpty()) {
            return;
        }

        Double averageMoodImprovement = diaryRepository.calculateAverageMoodImprovement(userId);
        List<String> rankedEntries = rankTexts(buildDiarySummaries(recentEntries), query, 3);

        context.append("Diary patterns:\n");
        context.append("- Average mood improvement across diary work: ")
                .append(averageMoodImprovement == null ? "n/a" : String.format("%.2f", averageMoodImprovement))
                .append('\n');

        List<DiaryEntryRepository.DistortionUsageView> topDistortions = diaryRepository.findTopDistortionsByUser(
                userId,
                PageRequest.of(0, 3));
        if (!topDistortions.isEmpty()) {
            String joinedDistortions = topDistortions.stream()
                    .map(distortion -> distortion.getDistortionName() + " (" + distortion.getUsageCount() + ")")
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("");
            context.append("- Frequently tagged distortions: ").append(joinedDistortions).append('\n');
        }

        for (String entry : rankedEntries) {
            context.append("- ").append(entry).append('\n');
        }
        context.append('\n');
    }

    private void appendCurrentSessionTranscript(StringBuilder context, UUID sessionId) {
        List<ChatMessage> messages = chatMessageRepository.findByUserSession_IdOrderByTimestampAsc(sessionId);
        if (messages.isEmpty()) {
            return;
        }

        context.append("Current session transcript:\n");
        int startIndex = Math.max(0, messages.size() - 8);
        for (int i = startIndex; i < messages.size(); i++) {
            ChatMessage message = messages.get(i);
            context.append("- ")
                    .append(message.getRole())
                    .append(": ")
                    .append(message.getContent())
                    .append('\n');
        }
    }

    private List<String> buildSessionSummaries(List<UserSession> sessions) {
        List<String> summaries = new ArrayList<>();
        for (UserSession session : sessions) {
            StringBuilder summary = new StringBuilder(resolveSessionLabel(session))
                    .append(" session with status ")
                    .append(session.getStatus());

            if (session.getMoodBefore() != null || session.getMoodAfter() != null) {
                summary.append(". Mood shift: ")
                        .append(session.getMoodBefore() == null ? "n/a" : session.getMoodBefore())
                        .append(" -> ")
                        .append(session.getMoodAfter() == null ? "n/a" : session.getMoodAfter());
            }

            List<ChatMessage> messages = chatMessageRepository.findByUserSession_IdOrderByTimestampDesc(
                    session.getId(),
                    PageRequest.of(0, 5));
            if (!messages.isEmpty()) {
                summary.append(". Recent topic: ").append(messages.get(0).getContent());
            }

            summaries.add(summary.toString());
        }
        return summaries;
    }

    private List<String> buildDiarySummaries(List<DiaryEntry> entries) {
        List<String> summaries = new ArrayList<>();
        for (DiaryEntry entry : entries) {
            StringBuilder summary = new StringBuilder("Situation: ")
                    .append(entry.getSituation())
                    .append(". Automatic thought: ")
                    .append(entry.getAutomaticThought())
                    .append(". Alternative thought: ")
                    .append(entry.getAlternativeThought())
                    .append(". Mood shift: ")
                    .append(entry.getMoodBefore())
                    .append(" -> ")
                    .append(entry.getMoodAfter());

            if (!entry.getDistortions().isEmpty()) {
                String distortions = entry.getDistortions().stream()
                        .map(distortion -> distortion.getName())
                        .reduce((left, right) -> left + ", " + right)
                        .orElse("");
                summary.append(". Distortions: ").append(distortions);
            }

            summaries.add(summary.toString());
        }
        return summaries;
    }

    private List<String> rankTexts(List<String> candidates, String query, int limit) {
        if (candidates.isEmpty() || limit <= 0) {
            return List.of();
        }

        if (query == null || query.isBlank()) {
            return candidates.stream().limit(limit).toList();
        }

        double[] queryEmbedding = embeddingService.embed(query);
        return candidates.stream()
                .map(candidate -> new RankedText(candidate, cosineSimilarity(queryEmbedding, embeddingService.embed(candidate))))
                .sorted((left, right) -> Double.compare(right.score(), left.score()))
                .limit(limit)
                .map(RankedText::text)
                .toList();
    }

    private double cosineSimilarity(double[] left, double[] right) {
        int length = Math.min(left.length, right.length);
        if (length == 0) {
            return 0.0d;
        }

        double dot = 0.0d;
        double leftMagnitude = 0.0d;
        double rightMagnitude = 0.0d;
        for (int i = 0; i < length; i++) {
            dot += left[i] * right[i];
            leftMagnitude += left[i] * left[i];
            rightMagnitude += right[i] * right[i];
        }

        if (leftMagnitude == 0.0d || rightMagnitude == 0.0d) {
            return 0.0d;
        }

        return dot / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }

    private String resolveSessionLabel(UserSession session) {
        if (session.getCbtSession() == null) {
            return "CBT";
        }
        if (session.getCbtSession().getTitle() != null && !session.getCbtSession().getTitle().isBlank()) {
            return session.getCbtSession().getTitle();
        }
        if (session.getCbtSession().getSessionType() != null && !session.getCbtSession().getSessionType().isBlank()) {
            return session.getCbtSession().getSessionType();
        }
        return "CBT";
    }

    private record RankedText(String text, double score) {
    }
}
