package com.SE320.therapy.controller;

import com.SE320.therapy.dto.ApiErrorEnvelope;
import com.SE320.therapy.dto.EndSessionRequest;
import com.SE320.therapy.dto.SendChatMessageRequest;
import com.SE320.therapy.dto.SessionChatResponse;
import com.SE320.therapy.dto.SessionDetailResponse;
import com.SE320.therapy.dto.SessionLibraryItemResponse;
import com.SE320.therapy.dto.SessionRunResponse;
import com.SE320.therapy.dto.StartSessionRequest;
import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.service.SessionApiService;
import com.SE320.therapy.service.SessionService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@Validated
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final SessionApiService sessionApiService;

    // No-arg constructor for subclasses in tests that override methods and don’t need a real service.
    public SessionController() {
        this.sessionService = null;
        this.sessionApiService = null;
    }

    public SessionController(SessionService sessionService) {
        this(sessionService, null);
    }

    public SessionController(SessionService sessionService, SessionApiService sessionApiService) {
        this.sessionService = sessionService;
        this.sessionApiService = sessionApiService;
    }

    @Operation(summary = "Get session library", description = "Returns the CBT session library available to start.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session library returned successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @GetMapping
    public List<SessionLibraryItemResponse> getSessionLibrary() {
        return sessionApiService.getSessionLibrary();
    }

    @Operation(summary = "Get session details", description = "Returns the details for a CBT session in the library.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session details returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid session id", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "404", description = "Session not found", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @GetMapping("/{sessionId}")
    public SessionDetailResponse getSessionDetail(
        @PathVariable("sessionId") @Positive(message = "sessionId must be a positive number") Long sessionId
    ) {
        return sessionApiService.getSessionDetail(sessionId);
    }

    @Operation(summary = "Start CBT session", description = "Starts a user session for the selected CBT module.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Session started successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "404", description = "Session or user not found", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "409", description = "An active session already exists", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/{sessionId}/start")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionRunResponse startSession(
        @PathVariable("sessionId") @Positive(message = "sessionId must be a positive number") Long sessionId,
        @Valid @RequestBody StartSessionRequest request
    ) {
        return sessionApiService.startSession(sessionId, request);
    }

    @Operation(summary = "Send chat message", description = "Sends a chat message inside an active user session and returns the assistant reply.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Chat processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "404", description = "Active session not found", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/{sessionId}/chat")
    public SessionChatResponse sendChatMessage(
        @PathVariable("sessionId") @Positive(message = "sessionId must be a positive number") Long sessionId,
        @Valid @RequestBody SendChatMessageRequest request
    ) {
        return sessionApiService.sendChatMessage(sessionId, request);
    }

    @Operation(summary = "End session", description = "Ends the active user session for the selected CBT module.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session ended successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class))),
        @ApiResponse(responseCode = "404", description = "Active session not found", content = @Content(schema = @Schema(implementation = ApiErrorEnvelope.class)))
    })
    @PostMapping("/{sessionId}/end")
    public SessionRunResponse endActiveSession(
        @PathVariable("sessionId") @Positive(message = "sessionId must be a positive number") Long sessionId,
        @Valid @RequestBody EndSessionRequest request
    ) {
        return sessionApiService.endSession(sessionId, request);
    }

    public List<String> viewSessionLibrary() {
        return sessionService.viewSessionLibrary();
    }

    public CBTSession startNewSession(@RequestParam("userId") String userId, @RequestParam("sessionType") String sessionType) {
        return sessionService.startNewSession(userId, sessionType);
    }

    public List<CBTSession> viewSessionHistory(@PathVariable("userId") String userId) {
        return sessionService.viewSessionHistory(userId);
    }

    public CBTSession continueSession(@RequestParam("userId") String userId, @PathVariable("sessionId") Long sessionId) {
        return sessionService.continueSession(userId, sessionId);
    }

    public void endSession(@RequestParam("userId") String userId, @PathVariable("sessionId") Long sessionId) {
        sessionService.endSession(userId, sessionId);
    }
}
