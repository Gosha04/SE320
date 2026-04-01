package com.SE320.therapy.service;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.SessionStatus;
import com.SE320.therapy.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    private final List<String> sessionLibrary = List.of(
            "Thought Record",
            "Behavioral Activation",
            "Cognitive Restructuring");

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
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
}