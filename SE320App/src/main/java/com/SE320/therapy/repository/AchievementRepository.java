package com.SE320.therapy.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.entity.Achievement;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {

    List<Achievement> findByUser_Id(UUID userId);

    boolean existsByUser_IdAndTitle(UUID userId, String title);

    Optional<Achievement> findByUser_IdAndTitle(UUID userId, String title);

    Optional<Achievement> findByIdAndUser_Id(UUID id, UUID userId);
}
