package com.SE320.therapy.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/crisis")
@Validated
public class CrisisController {

    private final CrisisService crisisService;

    public CrisisController(CrisisService crisisService) {
        this.crisisService = crisisService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Crisis getCrisisHub(@RequestParam @NotNull(message = "userId is required") UUID userId) {
        return crisisService.getCrisis(userId);
    }

    @GetMapping("/crisis/coping-strategies")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getCopingStrategies() {
        return crisisService.copingStrategies();
    }

    @GetMapping("/crisis/coping-strategies/page")
    @ResponseStatus(HttpStatus.OK)
    public Page<String> getCopingStrategiesPage(Pageable pageable) {
        return toPage(crisisService.copingStrategies(), pageable);
    }

    @GetMapping("/crisis/safety-plan")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getSafetyPlan(@RequestParam @NotNull(message = "userId is required") UUID userId) {
        return crisisService.safetyPlan(userId);
    }

    @GetMapping("/crisis/safety-plan/page")
    @ResponseStatus(HttpStatus.OK)
    public Page<String> getSafetyPlanPage(
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        Pageable pageable
    ) {
        return toPage(crisisService.safetyPlan(userId), pageable);
    }

    @PostMapping("/crisis/detect")
    @ResponseStatus(HttpStatus.OK)
    public CrisisDetectionResponse detectCrisisIndicators(@Valid @RequestBody CrisisDetectionRequest request) {
        return crisisService.detectCrisisIndicators(request);
    }

    @PutMapping("/crisis/safety-plan")
    @ResponseStatus(HttpStatus.OK)
    public List<String> updateSafetyPlan(
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        @RequestBody @Size(min = 1, message = "steps must contain at least one item")
        List<@NotBlank(message = "steps cannot contain blank items") String> steps
    ) {
        return crisisService.saveSafetyPlan(userId, steps);
    }

    private Page<String> toPage(List<String> values, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageImpl<>(values, Pageable.unpaged(), values.size());
        }

        int start = Math.toIntExact(pageable.getOffset());
        if (start >= values.size()) {
            return new PageImpl<>(List.of(), pageable, values.size());
        }

        int end = Math.min(start + pageable.getPageSize(), values.size());
        return new PageImpl<>(values.subList(start, end), pageable, values.size());
    }
}
