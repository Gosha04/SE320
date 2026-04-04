package com.SE320.therapy.controller;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.service.SessionService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    // No-arg constructor for subclasses in tests that override methods and don’t need a real service.
    public SessionController() {
        this.sessionService = null;
    }

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/library")
    public List<String> viewSessionLibrary() {
        return sessionService.viewSessionLibrary();
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public CBTSession startNewSession(@RequestParam String userId, @RequestParam String sessionType) {
        return sessionService.startNewSession(userId, sessionType);
    }

    @GetMapping("/history/{userId}")
    public List<CBTSession> viewSessionHistory(@PathVariable String userId) {
        return sessionService.viewSessionHistory(userId);
    }

    @PostMapping("/{sessionId}/continue")
    public CBTSession continueSession(@RequestParam String userId, @PathVariable Long sessionId) {
        return sessionService.continueSession(userId, sessionId);
    }

    @PostMapping("/{sessionId}/end")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void endSession(@RequestParam String userId, @PathVariable Long sessionId) {
        sessionService.endSession(userId, sessionId);
    }
}
