package com.SE320.therapy.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SE320.therapy.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    @Query("""
        select cm
        from ChatMessage cm
        where cm.userSession.id = :sessionId
        order by cm.timestamp asc
        """)
    List<ChatMessage> findByUserSession_IdOrderByTimestampAsc(@Param("sessionId") UUID sessionId);

    @Query("""
        select cm
        from ChatMessage cm
        where cm.userSession.id = :sessionId
        order by cm.timestamp desc
        """)
    List<ChatMessage> findByUserSession_IdOrderByTimestampDesc(
            @Param("sessionId") UUID sessionId,
            Pageable pageable);
}
