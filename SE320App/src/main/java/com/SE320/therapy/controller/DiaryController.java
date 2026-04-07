package com.SE320.therapy.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.dto.DistortionSuggestionRequest;
import com.SE320.therapy.service.DiaryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/diary")
@Validated
@Tag(name = "Diary", description = "Endpoints for thought diary entries, insights, and distortion suggestions")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @Operation(
        summary = "Create diary entry",
        description = "Creates a new diary entry for the provided user."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Diary entry created successfully",
            content = @Content(schema = @Schema(implementation = DiaryEntryResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    @PostMapping("/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public DiaryEntryResponse createEntry(
        @Parameter(description = "Unique identifier of the user who owns the diary entry", required = true)
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Diary entry payload containing the situation, thought, alternative thought, and mood ratings",
            required = true
        )
        @Valid @RequestBody DiaryEntryCreateRequest request
    ) {
        return diaryService.createEntry(userId, request);
    }

    @Operation(
        summary = "List diary entries",
        description = "Returns diary entries for a user with Spring pagination support."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Diary entries retrieved successfully",
            content = @Content(schema = @Schema(implementation = Page.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content = @Content
        )
    })
    @GetMapping("/entries")
    @ResponseStatus(HttpStatus.OK)
    public Page<DiaryEntrySummary> getEntries(
        @Parameter(description = "Unique identifier of the user whose entries should be returned", required = true)
        @RequestParam @NotNull(message = "userId is required") UUID userId,
        @Parameter(description = "Pagination information including page number, size, and sorting")
        Pageable pageable
    ) {
        return diaryService.getEntries(userId, pageable);
    }

    @Operation(
        summary = "Get diary entry detail",
        description = "Returns the full detail for a single diary entry."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Diary entry detail retrieved successfully",
            content = @Content(schema = @Schema(implementation = DiaryEntryDetail.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Diary entry not found",
            content = @Content
        )
    })
    @GetMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.OK)
    public DiaryEntryDetail getEntryDetail(
        @Parameter(description = "Unique identifier of the diary entry", required = true)
        @PathVariable @NotNull(message = "entryId is required") UUID entryId
    ) {
        return diaryService.getEntryDetail(entryId);
    }

    @Operation(
        summary = "Delete diary entry",
        description = "Soft deletes a diary entry so it no longer appears in active results."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "Diary entry deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Diary entry not found",
            content = @Content
        )
    })
    @DeleteMapping("/entries/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEntry(
        @Parameter(description = "Unique identifier of the diary entry to delete", required = true)
        @PathVariable @NotNull(message = "entryId is required") UUID entryId
    ) {
        diaryService.deleteEntry(entryId);
    }

    @Operation(
        summary = "Get diary insights",
        description = "Returns aggregate diary insights such as entry count and mood improvement."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Diary insights retrieved successfully",
            content = @Content(schema = @Schema(implementation = DiaryInsights.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content = @Content
        )
    })
    @GetMapping("/insights")
    @ResponseStatus(HttpStatus.OK)
    public DiaryInsights getInsights(
        @Parameter(description = "Unique identifier of the user whose insights should be returned", required = true)
        @RequestParam @NotNull(message = "userId is required") UUID userId
    ) {
        return diaryService.getInsights(userId);
    }

    @Operation(
        summary = "Suggest cognitive distortions",
        description = "Analyzes a thought and returns suggested cognitive distortions with confidence scores."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Distortion suggestions generated successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DistortionSuggestion.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input provided",
            content = @Content
        )
    })
    @PostMapping("/distortions/suggest")
    @ResponseStatus(HttpStatus.OK)
    public List<DistortionSuggestion> suggestDistortions(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Thought text to analyze for likely cognitive distortions",
            required = true
        )
        @Valid @RequestBody DistortionSuggestionRequest request
    ) {
        return diaryService.suggestDistortions(request.getThought());
    }
}
