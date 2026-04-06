package com.SE320.therapy.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SE320.therapy.dto.CrisisDetectionRequest;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.objects.Crisis;
import com.SE320.therapy.service.CrisisService;

@RestController
@RequestMapping("/crisis")
public class CrisisController {

    private final CrisisService crisisService;

    public CrisisController(CrisisService crisisService) {
        this.crisisService = crisisService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Crisis getCrisisHub(@RequestParam UUID userId) {
        return crisisService.getCrisis(userId);
    }

    @GetMapping("/crisis/coping-strategies")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCopingStrategies() {
        return crisisService.copingStrategies();
    }

    @GetMapping("/crisis/safety-plan")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getSafetyPlan(@RequestParam UUID userId) {
        return crisisService.safetyPlan(userId);
    }

    @PostMapping("/crisis/detect")
    @ResponseStatus(HttpStatus.OK)
    public CrisisDetectionResponse detectCrisisIndicators(@RequestBody CrisisDetectionRequest request) {
        return crisisService.detectCrisisIndicators(request);
    }

    @PutMapping("/crisis/safety-plan")
    @ResponseStatus(HttpStatus.OK)
    public List<String> updateSafetyPlan(
        @RequestParam UUID userId,
        @RequestBody List<String> steps
    ) {
        return crisisService.saveSafetyPlan(userId, steps);
    }
}
