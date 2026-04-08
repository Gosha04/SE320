package com.SE320.therapy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.SE320.therapy.dto.ApiErrorDetail;
import com.SE320.therapy.dto.DiaryEntryCreateRequest;
import com.SE320.therapy.dto.DiaryEntryDetail;
import com.SE320.therapy.dto.DiaryEntryResponse;
import com.SE320.therapy.dto.DiaryEntrySummary;
import com.SE320.therapy.dto.DiaryInsights;
import com.SE320.therapy.dto.DistortionSuggestion;
import com.SE320.therapy.exception.ApiException;
import com.SE320.therapy.exception.ApiExceptionHandler;
import com.SE320.therapy.service.DiaryService;

class DiaryControllerApiTest {

    private MockMvc mockMvc;
    private StubDiaryService diaryService;

    @BeforeEach
    void setUp() {
        diaryService = new StubDiaryService();
        DiaryController controller = new DiaryController(diaryService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ApiExceptionHandler())
            .setValidator(validator)
            .build();
    }

    @Test
    void createEntry_missingFields_returnsValidationError() throws Exception {
        mockMvc.perform(post("/diary/entries?userId=aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb")
                .contentType("application/json")
                .content("""
                    {
                      "situation": "",
                      "automaticThought": "",
                      "alternativeThought": "",
                      "moodBefore": 0,
                      "moodAfter": 11
                    }
                    """))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error.code").value("UNPROCESSABLE_ENTITY"))
            .andExpect(jsonPath("$.error.message").value("Validation failed"))
            .andExpect(jsonPath("$.error.details.length()").value(5));
    }

    @Test
    void createEntry_missingUserId_returnsValidationError() throws Exception {
        mockMvc.perform(post("/diary/entries")
                .contentType("application/json")
                .content("""
                    {
                      "situation": "Team meeting went badly",
                      "automaticThought": "I always mess things up",
                      "alternativeThought": "One hard meeting does not define me",
                      "moodBefore": 3,
                      "moodAfter": 5
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
            .andExpect(jsonPath("$.error.details[0].field").value("userId"));
    }

    @Test
    void createEntry_userNotFound_returnsStructuredError() throws Exception {
        diaryService.createException = new ApiException(
            org.springframework.http.HttpStatus.NOT_FOUND,
            "USER_NOT_FOUND",
            "No user was found for the provided userId.",
            List.of(new ApiErrorDetail("userId", "No user exists with the provided id"))
        );

        mockMvc.perform(post("/diary/entries?userId=aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb")
                .contentType("application/json")
                .content("""
                    {
                      "situation": "Team meeting went badly",
                      "automaticThought": "I always mess things up",
                      "alternativeThought": "One hard meeting does not define me",
                      "moodBefore": 3,
                      "moodAfter": 5
                    }
                    """))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.code").value("USER_NOT_FOUND"))
            .andExpect(jsonPath("$.error.details[0].field").value("userId"));
    }

    @Test
    void deleteEntry_notFound_returnsStructuredError() throws Exception {
        diaryService.deleteException = new ApiException(
            org.springframework.http.HttpStatus.NOT_FOUND,
            "DIARY_ENTRY_NOT_FOUND",
            "No diary entry was found for the provided entryId.",
            List.of(new ApiErrorDetail("entryId", "No active diary entry exists with the provided id"))
        );

        mockMvc.perform(delete("/diary/entries/11111111-2222-3333-4444-555555555555"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.code").value("DIARY_ENTRY_NOT_FOUND"))
            .andExpect(jsonPath("$.error.details[0].field").value("entryId"));
    }

    @Test
    void suggestDistortions_blankThought_returnsValidationError() throws Exception {
        mockMvc.perform(post("/diary/distortions/suggest")
                .contentType("application/json")
                .content("""
                    {
                      "thought": "   "
                    }
                    """))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.error.code").value("UNPROCESSABLE_ENTITY"))
            .andExpect(jsonPath("$.error.details[0].field").value("thought"));
    }

    static class StubDiaryService implements DiaryService {
        RuntimeException createException;
        RuntimeException deleteException;

        @Override
        public DiaryEntryResponse createEntry(UUID userId, DiaryEntryCreateRequest request) {
            if (createException != null) {
                throw createException;
            }
            return new DiaryEntryResponse(
                UUID.fromString("11111111-2222-3333-4444-555555555555"),
                "Diary entry created successfully.",
                LocalDateTime.of(2026, 1, 15, 10, 30)
            );
        }

        @Override
        public Page<DiaryEntrySummary> getEntries(UUID userId, Pageable pageable) {
            return new PageImpl<>(List.of());
        }

        @Override
        public DiaryEntryDetail getEntryDetail(UUID entryId) {
            return null;
        }

        @Override
        public void deleteEntry(UUID entryId) {
            if (deleteException != null) {
                throw deleteException;
            }
        }

        @Override
        public DiaryInsights getInsights(UUID userId) {
            return new DiaryInsights(0, 0.0, 0);
        }

        @Override
        public List<DistortionSuggestion> suggestDistortions(String thought) {
            return List.of();
        }
    }
}
