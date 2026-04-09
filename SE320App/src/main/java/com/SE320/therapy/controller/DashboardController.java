package com.SE320.therapy.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import com.SE320.therapy.dto.AchievementRequest;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.dto.objects.BurnoutRecovery;
import com.SE320.therapy.dto.objects.Dashboard;
import com.SE320.therapy.dto.objects.MonthlyTrends;
import com.SE320.therapy.dto.objects.WeeklyProgress;
import com.SE320.therapy.service.DashboardService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/progress")
@Validated
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Dashboard getDashboard(@RequestParam @NotNull(message = "userId is required") UUID userId) {
        return dashboardService.getDashboard(userId);
    }

    @GetMapping("/progress/monthly")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyTrends getMonthlyTrends(@RequestParam @NotNull(message = "userId is required") UUID userId) {
        return dashboardService.getMonthlyTrends(userId);
    }

    @GetMapping("/progress/weekly")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyProgress getWeeklyProgress(@RequestParam @NotNull(message = "userId is required") UUID userId) {
        return dashboardService.getWeeklyProgress(userId);
    }

    @GetMapping("/progress/burnout")
    @ResponseStatus(HttpStatus.OK)
    public BurnoutRecovery getBurnoutRecovery(@RequestParam @NotNull(message = "userId is required") UUID userId) {
        return dashboardService.getBurnoutRecovery(userId);
    }

    @GetMapping("/progress/achievements")
    @ResponseStatus(HttpStatus.OK)
    public Page<AchievementResponse> getAchievements(
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        Pageable pageable
    ) {
        return dashboardService.getAchievements(userId, pageable);
    }

    public List<AchievementResponse> getAchievements(UUID userId) {
        return dashboardService.getAchievements(userId, Pageable.unpaged()).getContent();
    }

    @PostMapping("/progress/achievements") // Bonus
    @ResponseStatus(HttpStatus.CREATED)
    public AchievementResponse createAchievement(
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        @Valid @RequestBody AchievementRequest request
    ) {
        return dashboardService.createAchievement(userId, request);
    }

    @PutMapping("/progress/achievements/{achievementId}") // Bonus
    @ResponseStatus(HttpStatus.OK)
    public AchievementResponse updateAchievement(
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        @PathVariable @NotNull(message = "achievementId is required") UUID achievementId,
        @Valid @RequestBody AchievementRequest request
    ) {
        return dashboardService.updateAchievement(userId, achievementId, request);
    }

    @DeleteMapping("/progress/achievements/{achievementId}") // Bonus
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAchievement(
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        @PathVariable @NotNull(message = "achievementId is required") UUID achievementId
    ) {
        dashboardService.deleteAchievement(userId, achievementId);
    }
}
