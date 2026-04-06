package com.SE320.therapy.service;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.ChatMessageResponse;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionDetailResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.ChatMessage;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.exception.ApiException;
import com.SE320.therapy.objects.ChatRole;
import com.SE320.therapy.objects.InteractionModality;
import com.SE320.therapy.objects.SessionStatus;
import com.SE320.therapy.objects.UserSessionStatus;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE320.therapy.repository.ChatMessageRepository;
import com.SE320.therapy.repository.SessionRepository;
import com.SE320.therapy.repository.UserRepository;
import com.SE320.therapy.repository.UserSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final List<String> sessionLibrary = List.of(
            "Thought Record",
            "Behavioral Activation",
            "Cognitive Restructuring");

    public SessionService(SessionRepository sessionRepository) {
        this(sessionRepository, null, null, null);
    }

    public SessionService(
        SessionRepository sessionRepository,
        UserRepository userRepository,
        UserSessionRepository userSessionRepository,
        ChatMessageRepository chatMessageRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.userSessionRepository = userSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Transactional(readOnly = true)
    public List<SessionLibraryItemResponse> getSessionLibrary() {
        return sessionRepository.findLibrarySessions()
            .stream()
            .map(this::toLibraryItem)
            .toList();
    }

    @Transactional(readOnly = true)
    public SessionDetailResponse getSessionDetail(Long sessionId) {
        return toDetailResponse(getLibrarySession(sessionId));
    }

    @Transactional
    public SessionRunResponse startSession(Long sessionId, StartSessionRequest request) {
        CBTSession session = getLibrarySession(sessionId);
        User user = getUser(request.userId());

        userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                user.getId(),
                sessionId,
                UserSessionStatus.IN_PROGRESS
            )
            .ifPresent(existing -> {
                throw new ApiException(
                    HttpStatus.CONFLICT,
                    "ACTIVE_SESSION_EXISTS",
                    "The user already has an active session for this CBT module.",
                    List.of(new ApiErrorDetail("sessionId", "An active session already exists for this user"))
                );
            });

        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setCbtSession(session);
        userSession.setStatus(UserSessionStatus.IN_PROGRESS);
        userSession.setStartedAt(LocalDateTime.now());
        userSession.setMoodBefore(request.moodBefore());

        return toRunResponse(userSessionRepository.save(userSession));
    }

    @Transactional
    public SessionChatResponse sendChatMessage(Long sessionId, SendChatMessageRequest request) {
        UserSession userSession = getActiveSession(request.userId(), sessionId);
        InteractionModality modality = request.modality() == null ? InteractionModality.TEXT : request.modality();

        ChatMessage userMessage = new ChatMessage();
        userMessage.setUserSession(userSession);
        userMessage.setRole(ChatRole.USER);
        userMessage.setContent(request.message().trim());
        userMessage.setModality(modality);
        userMessage = chatMessageRepository.save(userMessage);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setUserSession(userSession);
        assistantMessage.setRole(ChatRole.ASSISTANT);
        assistantMessage.setContent(buildAssistantReply(userSession.getCbtSession(), request.message().trim()));
        assistantMessage.setModality(modality);
        assistantMessage = chatMessageRepository.save(assistantMessage);

        return new SessionChatResponse(
            userSession.getId(),
            userSession.getCbtSession().getSessionId(),
            toChatMessageResponse(userMessage),
            toChatMessageResponse(assistantMessage)
        );
    }

    @Transactional
    public SessionRunResponse endSession(Long sessionId, EndSessionRequest request) {
        UserSession userSession = getActiveSession(request.userId(), sessionId);
        userSession.setStatus(UserSessionStatus.COMPLETED);
        userSession.setEndedAt(LocalDateTime.now());
        userSession.setMoodAfter(request.moodAfter());
        return toRunResponse(userSessionRepository.save(userSession));
    }

    public List<String> viewSessionLibrary() {
        return sessionLibrary;
    }

    public CBTSession startNewSession(String userId, String sessionType) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (sessionType == null || sessionType.isBlank()) {
            throw new IllegalArgumentException("Session type is required.");
        }

        if (sessionRepository.existsByUserIdAndStatus(userId, SessionStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active session.");
        }

        CBTSession session = new CBTSession();
        session.setUserId(userId);
        session.setSessionType(sessionType);
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartedAt(LocalDateTime.now());

        return sessionRepository.save(session);
    }

    public CBTSession continueSession(String userId, Long sessionId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is required.");
        }

        CBTSession session = sessionRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.getStatus() == SessionStatus.ENDED) {
            throw new IllegalStateException("This session has already ended.");
        }

        session.setStatus(SessionStatus.ACTIVE);
        return sessionRepository.save(session);
    }

    public void endSession(String userId, Long sessionId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is required.");
        }

        CBTSession session = sessionRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found."));

        if (session.getStatus() == SessionStatus.ENDED) {
            throw new IllegalStateException("This session has already ended.");
        }

        session.setStatus(SessionStatus.ENDED);
        session.setEndedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    public List<CBTSession> viewSessionHistory(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        return sessionRepository.findByUserId(userId);
    }

    private UserSession getActiveSession(UUID userId, Long sessionId) {
        getLibrarySession(sessionId);
        getUser(userId);
        return userSessionRepository.findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
                userId,
                sessionId,
                UserSessionStatus.IN_PROGRESS
            )
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "SESSION_NOT_FOUND",
                "No active session was found for the given user and session.",
                List.of(new ApiErrorDetail("sessionId", "No active user session matches this session id"))
            ));
    }

    private CBTSession getLibrarySession(Long sessionId) {
        return sessionRepository.findLibrarySessionBySessionId(sessionId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "SESSION_NOT_FOUND",
                "Session not found.",
                List.of(new ApiErrorDetail("sessionId", "No session exists with the provided id"))
            ));
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                "User not found.",
                List.of(new ApiErrorDetail("userId", "No user exists with the provided id"))
            ));
    }

    private SessionLibraryItemResponse toLibraryItem(CBTSession session) {
        return new SessionLibraryItemResponse(
            session.getSessionId(),
            session.getTitle(),
            session.getDescription(),
            session.getDurationMinutes(),
            session.getOrderIndex(),
            session.getModalities().stream().map(Enum::name).toList()
        );
    }

    private SessionDetailResponse toDetailResponse(CBTSession session) {
        return new SessionDetailResponse(
            session.getSessionId(),
            session.getTitle(),
            session.getDescription(),
            session.getDurationMinutes(),
            session.getOrderIndex(),
            session.getModule() == null ? null : session.getModule().getName(),
            List.copyOf(session.getObjectives()),
            session.getModalities().stream().map(Enum::name).toList()
        );
    }

    private SessionRunResponse toRunResponse(UserSession userSession) {
        return new SessionRunResponse(
            userSession.getId(),
            userSession.getUser().getId(),
            userSession.getCbtSession().getSessionId(),
            userSession.getCbtSession().getTitle(),
            userSession.getStatus().name(),
            userSession.getMoodBefore(),
            userSession.getMoodAfter(),
            userSession.getStartedAt(),
            userSession.getEndedAt()
        );
    }

    private ChatMessageResponse toChatMessageResponse(ChatMessage message) {
        return new ChatMessageResponse(
            message.getId(),
            message.getRole().name(),
            message.getContent(),
            message.getModality().name(),
            message.getTimestamp()
        );
    }

    private String buildAssistantReply(CBTSession session, String message) {
        String sessionTitle = session.getTitle() == null ? "this session" : session.getTitle();
        return "Let's use " + sessionTitle + " to explore that. What evidence supports or challenges: \"" + message + "\"?";
    }
}
