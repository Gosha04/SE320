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
import com.SE320.therapy.objects.Dashboard;
import com.SE320.therapy.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
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

    @GetMapping("/achievements")
    @ResponseStatus(HttpStatus.OK)
    public List<AchievementResponse> getAchievements(@RequestParam UUID userId) {
        return dashboardService.getAchievements(userId);
    }

    @PostMapping("/achievements")
    @ResponseStatus(HttpStatus.CREATED)
    public AchievementResponse createAchievement(
        @RequestParam UUID userId,
        @RequestBody AchievementRequest request
    ) {
        return dashboardService.createAchievement(userId, request);
    }

    @PutMapping("/achievements/{achievementId}")
    @ResponseStatus(HttpStatus.OK)
    public AchievementResponse updateAchievement(
        @RequestParam UUID userId,
        @PathVariable UUID achievementId,
        @RequestBody AchievementRequest request
    ) {
        return dashboardService.updateAchievement(userId, achievementId, request);
    }

    @DeleteMapping("/achievements/{achievementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAchievement(
        @RequestParam UUID userId,
        @PathVariable UUID achievementId
    ) {
        dashboardService.deleteAchievement(userId, achievementId);
    }
}
