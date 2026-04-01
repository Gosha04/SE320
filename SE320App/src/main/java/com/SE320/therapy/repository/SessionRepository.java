package com.SE320.therapy.repository;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<CBTSession, Long> {

    List<CBTSession> findByUserId(String userId);

    Optional<CBTSession> findBySessionIdAndUserId(Long sessionId, String userId);

    boolean existsByUserIdAndStatus(String userId, SessionStatus status);
}