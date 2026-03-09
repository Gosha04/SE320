package com.SE320.therapy.repository;

import com.SE320.therapy.entity.DiaryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryDiaryEntryRepository implements DiaryEntryRepository {
    private final List<DiaryEntry> entries = new ArrayList<>();

    @Override
    public DiaryEntry save(DiaryEntry entry) {
        entries.add(entry);
        return entry;
    }

    @Override
    public List<DiaryEntry> findByUserId(UUID userId) {
        List<DiaryEntry> results = new ArrayList<>();

        for (DiaryEntry entry : entries) {
            if (entry.getUserId().equals(userId) && !entry.isDeleted()) {
                results.add(entry);
            }
        }

        return results;
    }
}
