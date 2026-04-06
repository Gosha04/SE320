package com.SE320.therapy.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;

public interface DiaryService {
    DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreateRequest request);
    Page<DiaryEntrySummary> getEntries(UUID userId, Pageable pageable);
    DiaryEntryDetail getEntryDetail(UUID entryId);
    void deleteEntry(UUID entryId);
    DiaryInsights getInsights(UUID userId);
    List<DistortionSuggestion> suggestDistortions(String thought);
}