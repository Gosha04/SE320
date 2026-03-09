package com.SE320.therapy.service;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;

import java.util.List;
import java.util.UUID;

public interface DiaryService {
    void createEntry(UUID userId, DiaryEntryCreateRequest request);
    List<DiaryEntrySummary> getEntries(UUID userId);
    DiaryInsights getInsights(UUID userId);
}