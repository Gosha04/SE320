package com.SE320.therapy.repository;

import com.SE320.therapy.entity.CBTSession;
import com.SE320.therapy.objects.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<CBTSession, UUID> {

    @Query("""
        select s from CBTSession s
        where s.userId is null
        order by coalesce(s.orderIndex, 999999), s.title
        """)
    List<CBTSession> findLibrarySessions();

    @Query("""
        select s from CBTSession s
        where s.userId is null
          and s.sessionId = :sessionId
        """)
    Optional<CBTSession> findLibrarySessionBySessionId(@Param("sessionId") Long sessionId);

    @Query("select s from CBTSession s where s.userId = :userId")
    List<CBTSession> findByUserId(@Param("userId") String userId);

    @Query("select s from CBTSession s where s.sessionId = :sessionId and s.userId = :userId")
    Optional<CBTSession> findBySessionIdAndUserId(@Param("sessionId") Long sessionId, @Param("userId") String userId);

    @Query("select count(s) > 0 from CBTSession s where s.userId = :userId and s.status = :status")
    boolean existsByUserIdAndStatus(@Param("userId") String userId, @Param("status") SessionStatus status);
}
