package com.SE320.therapy.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.SE320.therapy.objects.OnboardingPath;
import com.SE320.therapy.objects.SeverityLevel;
import com.SE320.therapy.objects.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "name")
    private String name;

    @Column(name = "onboarding_complete")
    private Boolean onboardingComplete;

    @Enumerated(EnumType.STRING)
    @Column(name = "onboarding_path")
    private OnboardingPath onboardingPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity_level")
    private SeverityLevel severityLevel;

    @Column(name = "streak_days")
    private Integer streakDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<UserSession> userSessions = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<DiaryEntry> diaryEntries = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<TrustedContact> trustedContacts = new ArrayList<>();

    // Backwards-compatible transient fields used by existing service/controller
    // code.
    @Transient
    private UserType legacyUserType;

    @Transient
    private Integer legacyPhoneNumber;

    @Transient
    private boolean legacyOnline;

    public User(UUID id, String email, String passwordHash, String name, Boolean onboardingComplete,
            OnboardingPath onboardingPath, SeverityLevel severityLevel, Integer streakDays,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.onboardingComplete = onboardingComplete;
        this.onboardingPath = onboardingPath;
        this.severityLevel = severityLevel;
        this.streakDays = streakDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.legacyOnline = false;
    }

    public User(UUID id, UserType userType, String firstName, String lastName,
            String email, Integer phoneNumber, String passwordHash) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
        this.onboardingComplete = false;
        this.onboardingPath = null;
        this.severityLevel = null;
        this.streakDays = 0;
        this.legacyUserType = userType;
        this.legacyPhoneNumber = phoneNumber;
        this.legacyOnline = false;
    }

    public User() {
    }

    @PrePersist
    public void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOnboardingComplete() {
        return onboardingComplete;
    }

    public void setOnboardingComplete(Boolean onboardingComplete) {
        this.onboardingComplete = onboardingComplete;
    }

    public OnboardingPath getOnboardingPath() {
        return onboardingPath;
    }

    public void setOnboardingPath(OnboardingPath onboardingPath) {
        this.onboardingPath = onboardingPath;
    }

    public SeverityLevel getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(SeverityLevel severityLevel) {
        this.severityLevel = severityLevel;
    }

    public Integer getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(Integer streakDays) {
        this.streakDays = streakDays;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserType getUserType() {
        return legacyUserType;
    }

    public void setUserType(UserType userType) {
        this.legacyUserType = userType;
    }

    public Integer getPhoneNumber() {
        return legacyPhoneNumber;
    }

    public void setPhoneNumber(Integer phoneNumber) {
        this.legacyPhoneNumber = phoneNumber;
    }

    public boolean getOnline() {
        return legacyOnline;
    }

    public void setOnline(boolean online) {
        this.legacyOnline = online;
    }

    public String getFirstName() {
        if (name == null || name.isBlank()) {
            return "";
        }
        String[] parts = name.trim().split("\\s+", 2);
        return parts[0];
    }

    public String getLastName() {
        if (name == null || name.isBlank()) {
            return "";
        }
        String[] parts = name.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    public void setFirstName(String firstName) {
        String lastName = getLastName();
        this.name = ((firstName == null ? "" : firstName) + " " + lastName).trim();
    }

    public void setLastName(String lastName) {
        String firstName = getFirstName();
        this.name = (firstName + " " + (lastName == null ? "" : lastName)).trim();
    }

    public List<UserSession> getUserSessions() {
        return userSessions;
    }

    public void setUserSessions(List<UserSession> userSessions) {
        this.userSessions = userSessions;
    }

    public List<DiaryEntry> getDiaryEntries() {
        return diaryEntries;
    }

    public void setDiaryEntries(List<DiaryEntry> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }

    public List<TrustedContact> getTrustedContacts() {
        return trustedContacts;
    }

    public void setTrustedContacts(List<TrustedContact> trustedContacts) {
        this.trustedContacts = trustedContacts;
    }
}
