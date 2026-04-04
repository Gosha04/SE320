package com.SE320.therapy.repository;

import com.SE320.therapy.entity.DiaryEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, UUID> {

    List<DiaryEntry> findByUser_IdAndDeletedFalse(UUID userId);

    Page<DiaryEntry> findByUser_IdAndDeletedFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    @Query("""
        select d.id as distortionId, d.name as distortionName, count(d) as usageCount
        from DiaryEntry e
        join e.distortions d
        where e.user.id = :userId and e.deleted = false
        group by d.id, d.name
        order by count(d) desc
        """)
    List<DistortionUsageView> findTopDistortionsByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        select avg(e.moodAfter - e.moodBefore)
        from DiaryEntry e
        where e.user.id = :userId and e.deleted = false
        """)
    Double calculateAverageMoodImprovement(@Param("userId") UUID userId);

    Optional<DiaryEntry> findByIdAndDeletedFalse(UUID entryId);

    interface DistortionUsageView {
        String getDistortionId();
        String getDistortionName();
        long getUsageCount();
    }
}
