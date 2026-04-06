package com.SE320.therapy.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.entity.UserSession;
import com.SE320.therapy.objects.UserSessionStatus;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    @Query("select us from UserSession us where us.user.id = :userId order by us.startedAt desc")
    List<UserSession> findByUserIdOrderByStartedAtDesc(@Param("userId") UUID userId);

    @Query("select count(us) from UserSession us where us.user.id = :userId and us.status = :status")
    long countCompletedSessionsByUser(@Param("userId") UUID userId, @Param("status") UserSessionStatus status);

    @Query("""
        select us from UserSession us
        where us.user.id = :userId
          and us.startedAt between :start and :end
        order by us.startedAt desc
        """)
    List<UserSession> findByUserAndDateRange(
        @Param("userId") UUID userId,
        @Param("start") java.time.LocalDateTime start,
        @Param("end") java.time.LocalDateTime end
    );

    @Query("""
        select us from UserSession us
        where us.user.id = :userId
          and us.status = :status
        order by us.startedAt desc
        """)
    Optional<UserSession> findFirstByUserIdAndStatusOrderByStartedAtDesc(
        @Param("userId") UUID userId,
        @Param("status") UserSessionStatus status
    );

    @Query("""
        select us from UserSession us
        where us.user.id = :userId
          and us.cbtSession.sessionId = :sessionId
          and us.status = :status
        order by us.startedAt desc
        """)
    Optional<UserSession> findFirstByUserIdAndSessionIdAndStatusOrderByStartedAtDesc(
        @Param("userId") UUID userId,
        @Param("sessionId") Long sessionId,
        @Param("status") UserSessionStatus status
    );
}
