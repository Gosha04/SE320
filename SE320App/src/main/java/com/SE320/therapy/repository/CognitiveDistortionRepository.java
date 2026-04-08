package com.SE320.therapy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.entity.CognitiveDistortion;

@Repository
public interface CognitiveDistortionRepository extends JpaRepository<CognitiveDistortion, String> {
}
