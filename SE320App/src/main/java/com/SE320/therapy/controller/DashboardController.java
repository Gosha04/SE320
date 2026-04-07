package com.SE320.therapy.controller;

import java.util.List;
import java.util.UUID;

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

import com.SE320.therapy.dto.AchievementRequest;
import com.SE320.therapy.dto.AchievementResponse;
import com.SE320.therapy.objects.BurnoutRecovery;
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.objects.MonthlyTrends;
import com.SE320.therapy.objects.WeeklyProgress;
import com.SE320.therapy.service.DashboardService;

@RestController
@RequestMapping("/progress")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Dashboard getDashboard(@RequestParam UUID userId) {
        return dashboardService.getDashboard(userId);
    }

    @GetMapping("/progress/monthly")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyTrends getMonthlyTrends(@RequestParam UUID userId) {
        return dashboardService.getMonthlyTrends(userId);
    }

    @GetMapping("/progress/weekly")
    @ResponseStatus(HttpStatus.OK)
    public WeeklyProgress getWeeklyProgress(@RequestParam UUID userId) {
        return dashboardService.getWeeklyProgress(userId);
    }

    @GetMapping("/progress/burnout")
    @ResponseStatus(HttpStatus.OK)
    public BurnoutRecovery getBurnoutRecovery(@RequestParam UUID userId) {
        return dashboardService.getBurnoutRecovery(userId);
    }

    @GetMapping("/progress/achievements")
    @ResponseStatus(HttpStatus.OK)
    public List<AchievementResponse> getAchievements(@RequestParam UUID userId) {
        return dashboardService.getAchievements(userId);
    }

    @PostMapping("/progress/achievements") // Bonus
    @ResponseStatus(HttpStatus.CREATED)
    public AchievementResponse createAchievement(
        @RequestParam UUID userId,
        @RequestBody AchievementRequest request
    ) {
        return dashboardService.createAchievement(userId, request);
    }

    @PutMapping("/progress/achievements/{achievementId}") // Bonus
    @ResponseStatus(HttpStatus.OK)
    public AchievementResponse updateAchievement(
        @RequestParam UUID userId,
        @PathVariable UUID achievementId,
        @RequestBody AchievementRequest request
    ) {
        return dashboardService.updateAchievement(userId, achievementId, request);
    }

    @DeleteMapping("/progress/achievements/{achievementId}") // Bonus
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAchievement(
        @RequestParam UUID userId,
        @PathVariable UUID achievementId
    ) {
        dashboardService.deleteAchievement(userId, achievementId);
    }
}
