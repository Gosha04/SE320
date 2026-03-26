package com.SE320.therapy.service;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.SessionStatus;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SessionService {

    private final List<String> sessionLibrary = List.of(
            "Thought Record",
            "Behavioral Activation",
            "Cognitive Restructuring");

    private final List<CBTSession> sessionHistory = new ArrayList<>();

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

        for (CBTSession session : sessionHistory) {
            if (session.getUserId().equals(userId) && session.getStatus() == SessionStatus.ACTIVE) {
                throw new IllegalStateException("This session has already ended.");
            }
        }

        CBTSession session = new CBTSession();
        session.setSessionId((long) (sessionHistory.size() + 1));
        session.setUserId(userId);
        session.setSessionType(sessionType);
        session.setStatus(SessionStatus.ACTIVE);
        session.setStartedAt(LocalDateTime.now());

        sessionHistory.add(session);
        return session;
    }

    public CBTSession continueSession(String userId, Long sessionId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is required.");
        }

        for (CBTSession session : sessionHistory) {
            if (session.getSessionId().equals(sessionId) && session.getUserId().equals(userId)) {
                if (session.getStatus() == SessionStatus.ENDED) {
                    throw new IllegalStateException("This session has already ended.");
                }

                session.setStatus(SessionStatus.ACTIVE);
                return session;
            }
        }

        throw new IllegalArgumentException("Session not found.");
    }

    public void endSession(String userId, Long sessionId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is required.");
        }

        for (CBTSession session : sessionHistory) {
            if (session.getSessionId().equals(sessionId) && session.getUserId().equals(userId)) {
                if (session.getStatus() == SessionStatus.ENDED) {
                    throw new IllegalStateException("This session has already ended.");
                }
            }

            session.setStatus(SessionStatus.ENDED);
            session.setEndedAt(LocalDateTime.now());
            return;
        }

        throw new IllegalArgumentException("Session not found.");
    }

    public List<CBTSession> viewSessionHistory(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID is required.");
        }

        List<CBTSession> userSessions = new ArrayList<>();

        for (CBTSession session : sessionHistory) {
            if (session.getUserId().equals(userId)) {
                userSessions.add(session);
            }
        }

        return userSessions;
    }
}
