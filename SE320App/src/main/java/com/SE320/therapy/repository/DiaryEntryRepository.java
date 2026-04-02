package com.SE320.therapy.repository;

import com.SE320.therapy.entity.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, UUID> {

    List<DiaryEntry> findByUser_IdAndDeletedFalse(UUID userId);

    Optional<DiaryEntry> findByIdAndDeletedFalse(UUID entryId);
}