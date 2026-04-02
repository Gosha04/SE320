package com.SE320.therapy.controller;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.service.SessionService;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SessionController {

    private final SessionService sessionService;

    // No-arg constructor for subclasses in tests that override methods and don’t need a real service.
    public SessionController() {
        this.sessionService = null;
    }

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public List<String> viewSessionLibrary() {
        return sessionService.viewSessionLibrary();
    }

    public CBTSession startNewSession(String userId, String sessionType) {
        return sessionService.startNewSession(userId, sessionType);
    }

    public List<CBTSession> viewSessionHistory(String userId) {
        return sessionService.viewSessionHistory(userId);
    }

    public CBTSession continueSession(String userId, Long sessionId) {
        return sessionService.continueSession(userId, sessionId);
    }

    public void endSession(String userId, Long sessionId) {
        sessionService.endSession(userId, sessionId);
    }
}