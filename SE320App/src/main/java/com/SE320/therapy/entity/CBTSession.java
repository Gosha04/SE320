package com.SE320.therapy.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.SE320.therapy.dto.objects.SessionModality;
import com.SE320.therapy.dto.objects.SessionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cbt_sessions")
public class CBTSession {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Legacy compatibility field kept for existing session flows.
    @Column(name = "session_id", unique = true)
    private Long sessionId;

    // Legacy compatibility field kept for existing session flows.
    @Column(name = "user_id")
    private String userId;

    // Legacy compatibility field kept for existing session flows.
    @Column(name = "session_type")
    private String sessionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SessionStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private SessionModule module;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @ElementCollection
    @CollectionTable(name = "cbt_session_objectives", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "objective", nullable = false)
    private List<String> objectives = new ArrayList<>();

    @ElementCollection(targetClass = SessionModality.class)
    @CollectionTable(name = "cbt_session_modalities", joinColumns = @JoinColumn(name = "session_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "modality", nullable = false)
    private List<SessionModality> modalities = new ArrayList<>();

    @Column(name = "order_index")
    private Integer orderIndex;

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (sessionId == null) {
            sessionId = Math.abs(id.getMostSignificantBits());
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public SessionModule getModule() {
        return module;
    }

    public void setModule(SessionModule module) {
        this.module = module;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public void setObjectives(List<String> objectives) {
        this.objectives = objectives;
    }

    public List<SessionModality> getModalities() {
        return modalities;
    }

    public void setModalities(List<SessionModality> modalities) {
        this.modalities = modalities;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
