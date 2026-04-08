package com.SE320.therapy.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.entity.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    @Query("select a from Achievement a where a.user.id = :userId")
    List<Achievement> findByUser_Id(@Param("userId") UUID userId);

    @Query("select a from Achievement a where a.user.id = :userId")
    Page<Achievement> findByUser_Id(@Param("userId") UUID userId, Pageable pageable);

    @Query("select count(a) > 0 from Achievement a where a.user.id = :userId and a.title = :title")
    boolean existsByUser_IdAndTitle(@Param("userId") UUID userId, @Param("title") String title);

    @Query("select a from Achievement a where a.user.id = :userId and a.title = :title")
    Optional<Achievement> findByUser_IdAndTitle(@Param("userId") UUID userId, @Param("title") String title);

    @Query("select a from Achievement a where a.id = :id and a.user.id = :userId")
    Optional<Achievement> findByIdAndUser_Id(@Param("id") UUID id, @Param("userId") UUID userId);
}
