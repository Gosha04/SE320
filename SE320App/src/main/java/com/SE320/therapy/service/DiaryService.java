package com.SE320.therapy.service;

import java.util.List;
import java.util.UUID;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;

public interface DiaryService {
    DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreateRequest request);
    List<DiaryEntrySummary> getEntries(UUID userId);
    DiaryEntryDetail getEntryDetail(UUID entryId);
    void deleteEntry(UUID entryId);
    DiaryInsights getInsights(UUID userId);
}