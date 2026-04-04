package com.SE320.therapy.entity;

import java.time.Month;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "unlocked", nullable = false)
    private boolean unlocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "unlocked_month")
    private Month unlockedMonth;

    public Achievement() {
    }

    public Achievement(UUID id, User user, String title, String description, boolean unlocked, Month unlockedMonth) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.unlocked = unlocked;
        this.unlockedMonth = unlockedMonth;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public String toString() {
        return "Achievement {\n"
                + "id=" + id + "\n"
                + "title=" + title + "\n"
                + "description=" + description + "\n"
                + "unlocked=" + unlocked + "\n"
                + "unlockedMonth=" + unlockedMonth + "\n"
                + "}";
    }

    public UUID getId() {
        return id;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
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

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Month getUnlockedMonth() {
        return unlockedMonth;
    }

    public void setUnlockedMonth(Month unlockedMonth) {
        this.unlockedMonth = unlockedMonth;
    }
}
