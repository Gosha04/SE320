package com.SE320.therapy.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.DistortionSuggestionRequest;
import com.SE320.therapy.service.DiaryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public DiaryEntryResponse createEntry(
        @RequestParam UUID userId,
        @Valid @RequestBody DiaryEntryCreateRequest request
    ) {
        return diaryService.createEntry(userId, request);
    }

    @GetMapping("/entries")
    @ResponseStatus(HttpStatus.OK)
    public Page<DiaryEntrySummary> getEntries(
        @RequestParam UUID userId,
        Pageable pageable
    ) {
        return diaryService.getEntries(userId, pageable);
    }

    @GetMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.OK)
    public DiaryEntryDetail getEntryDetail(@PathVariable UUID entryId) {
        return diaryService.getEntryDetail(entryId);
    }

    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(@PathVariable UUID entryId) {
        diaryService.deleteEntry(entryId);
    }

    @GetMapping("/insights")
    @ResponseStatus(HttpStatus.OK)
    public DiaryInsights getInsights(@RequestParam UUID userId) {
        return diaryService.getInsights(userId);
    }

    @PostMapping("/distortions/suggest")
    @ResponseStatus(HttpStatus.OK)
    public List<DistortionSuggestion> suggestDistortions(
        @Valid @RequestBody DistortionSuggestionRequest request
    ) {
        return diaryService.suggestDistortions(request.getThought());
    }
}