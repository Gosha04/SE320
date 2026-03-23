package com.SE320.therapy.repository;

import com.SE320.therapy.entity.DiaryEntry;

import java.util.List;
import java.util.UUID;

public interface DiaryEntryRepository {
    DiaryEntry save(DiaryEntry entry);
    List<DiaryEntry> findByUserId(UUID userId);
    DiaryEntry findById(UUID entryId);
}