package com.SE320.therapy.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.entity.SafetyPlan;

@Repository
public interface SafetyPlanRepository extends JpaRepository<SafetyPlan, UUID> {
    Optional<SafetyPlan> findByUser_Id(UUID userId);
}
