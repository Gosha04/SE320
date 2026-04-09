package com.SE320.therapy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.SE320.therapy.dto.CrisisDetectionRequest;
import com.SE320.therapy.dto.CrisisDetectionResponse;
import com.SE320.therapy.dto.objects.Crisis;
import com.SE320.therapy.dto.objects.SeverityLevel;
import com.SE320.therapy.entity.SafetyPlan;
import com.SE320.therapy.entity.User;
import com.SE320.therapy.repository.SafetyPlanRepository;
import com.SE320.therapy.repository.UserRepository;

@Service
public class CrisisService {

    private final UserRepository userRepository;
    private final SafetyPlanRepository safetyPlanRepository;
    private final AiService aiService;
    private final Crisis crisis;

    public CrisisService(UserRepository userRepository, SafetyPlanRepository safetyPlanRepository) {
        this(userRepository, safetyPlanRepository, null);
    }

    @Autowired
    public CrisisService(UserRepository userRepository, SafetyPlanRepository safetyPlanRepository, AiService aiService) {
        this.userRepository = userRepository;
        this.safetyPlanRepository = safetyPlanRepository;
        this.aiService = aiService;
        this.crisis = buildDefaultCrisis();
    }

    public Crisis getCrisis() {
        System.out.println("[CrisisService] Loading crisis support content...");
        return new Crisis(
                crisis.getWarningSignRecognition(),
                crisis.getDeescalationTechniques(),
                crisis.getSafetyPlanningSteps(),
                crisis.getEmergencyResources()
        );
    }

    public Crisis getCrisis(UUID userId) {
        System.out.println("[CrisisService] Loading crisis support content for user...");
        return new Crisis(
                userId,
                crisis.getWarningSignRecognition(),
                crisis.getDeescalationTechniques(),
                getSafetyPlan(userId),
                crisis.getEmergencyResources()
        );
    }

    public List<String> warningSignRecognition() {
        System.out.println("[CrisisService] Retrieving warning sign recognition guidance...");
        return getCrisis().getWarningSignRecognition();
    }

    public List<String> emergencyResources() {
        System.out.println("[CrisisService] Retrieving emergency support resources...");
        return getCrisis().getEmergencyResources();
    }

    public void panicButton() {
        System.out.println("[CrisisService] Panic button activated.");
        System.out.println("[CrisisService] MOCK MODE: Simulating contact with emergency services.");
        System.out.println("[CrisisService] No real emergency call has been placed.");
        System.out.println("[CrisisService] Alerting saved emergency contacts in mock workflow.");
    }

    public List<String> copingStrategies() {
        System.out.println("[CrisisService] Loading coping strategies...");
        return getCrisis().getDeescalationTechniques();
    }

    public List<String> safetyPlan() {
        System.out.println("[CrisisService] Opening user safety plan...");
        return getCrisis().getSafetyPlanningSteps();
    }

    public List<String> safetyPlan(UUID userId) {
        System.out.println("[CrisisService] Opening persistent user safety plan...");
        return getSafetyPlan(userId);
    }

    public List<String> saveSafetyPlan(UUID userId, List<String> steps) {
        System.out.println("[CrisisService] Saving persistent user safety plan...");
        User user = getRequiredUser(userId);
        SafetyPlan safetyPlan = safetyPlanRepository.findByUser_Id(userId)
                .orElseGet(() -> new SafetyPlan(null, user, List.of()));

        safetyPlan.setUser(user);
        safetyPlan.setSteps(steps);
        return safetyPlanRepository.save(safetyPlan).getSteps();
    }

    public CrisisDetectionResponse detectCrisisIndicators(CrisisDetectionRequest request) {
        System.out.println("[CrisisService] Detecting crisis indicators...");
        validateDetectionRequest(request);

        if (aiService != null && request.getMessage() != null && !request.getMessage().isBlank()) {
            CrisisDetectionResponse response = aiService.detectCrisis(request.getMessage());
            if (request.getObservedIndicators() == null || request.getObservedIndicators().isEmpty()) {
                return response;
            }

            List<String> mergedIndicators = new ArrayList<>(response.matchedIndicators());
            for (String indicator : request.getObservedIndicators()) {
                if (indicator != null && !indicator.isBlank() && !mergedIndicators.contains(indicator.trim())) {
                    mergedIndicators.add(indicator.trim());
                }
            }

            return new CrisisDetectionResponse(
                    !mergedIndicators.isEmpty(),
                    response.severityLevel(),
                    mergedIndicators,
                    response.recommendedNextSteps());
        }

        String message = request.getMessage() == null
                ? ""
                : request.getMessage().toLowerCase(Locale.ROOT);

        List<String> matchedIndicators = new ArrayList<>();

        for (String indicator : request.getObservedIndicators()) {
            if (indicator != null && !indicator.isBlank()) {
                matchedIndicators.add(indicator.trim());
            }
        }

        if (containsKeyword(message, "hopeless", "trapped", "numb", "panic", "can't do this", "give up")) {
            matchedIndicators.add("High emotional distress language detected.");
        }

        if (containsKeyword(message, "hurt myself", "self-harm", "kill myself", "suicide", "end my life")) {
            matchedIndicators.add("Possible self-harm or suicide language detected.");
        }

        SeverityLevel severityLevel = determineSeverity(matchedIndicators);

        List<String> recommendedNextSteps = new ArrayList<>();
        recommendedNextSteps.addAll(crisis.getDeescalationTechniques());

        if (severityLevel == SeverityLevel.SIGNIFICANT) {
            recommendedNextSteps.addAll(crisis.getEmergencyResources());
        } else {
            recommendedNextSteps.addAll(crisis.getSafetyPlanningSteps());
        }

        return new CrisisDetectionResponse(
                !matchedIndicators.isEmpty(),
                severityLevel,
                matchedIndicators,
                recommendedNextSteps
        );
    }

    private Crisis buildDefaultCrisis() {
        List<String> warningSigns = List.of(
                "Notice if stress shifts into feeling trapped, panicked, or emotionally numb.",
                "Watch for withdrawing from friends, family, classes, or responsibilities.",
                "Pay attention to sudden changes in sleep, appetite, or ability to focus.",
                "Take persistent hopeless thoughts or urges to give up as signs to seek help quickly."
        );

        List<String> deescalationTechniques = List.of(
                "Use box breathing: inhale for 4, hold for 4, exhale for 4, hold for 4.",
                "Try the 5-4-3-2-1 grounding exercise to reconnect with your surroundings.",
                "Move to a quieter space and reduce overstimulating noise, lights, or notifications.",
                "Text or call a trusted person and stay connected until the intensity lowers."
        );

        List<String> safetyPlanningSteps = List.of(
                "Identify your personal warning signs and write them down where you can find them fast.",
                "List coping actions you can do on your own before the situation escalates.",
                "Choose trusted contacts you can reach out to for immediate support.",
                "Remove or create distance from anything that could be used for self-harm.",
                "Keep emergency numbers and the nearest urgent care or ER information easy to access."
        );

        List<String> emergencyResources = List.of(
                "Call or text 988 for the Suicide & Crisis Lifeline.",
                "Text HOME to 741741 to connect with the Crisis Text Line.",
                "Call 911 or go to the nearest emergency room if there is immediate danger.",
                "Contact campus counseling, a resident advisor, or local urgent mental health services."
        );

        return new Crisis(warningSigns, deescalationTechniques, safetyPlanningSteps, emergencyResources);
    }

    private List<String> getSafetyPlan(UUID userId) {
        getRequiredUser(userId);
        return safetyPlanRepository.findByUser_Id(userId)
                .map(SafetyPlan::getSteps)
                .filter(steps -> !steps.isEmpty())
                .orElseGet(() -> crisis.getSafetyPlanningSteps());
    }

    private void validateDetectionRequest(CrisisDetectionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Crisis detection request is required.");
        }

        if ((request.getMessage() == null || request.getMessage().isBlank())
                && (request.getObservedIndicators() == null || request.getObservedIndicators().isEmpty())) {
            throw new IllegalArgumentException("A message or observed indicators are required.");
        }
    }

    private boolean containsKeyword(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private SeverityLevel determineSeverity(List<String> matchedIndicators) {
        if (matchedIndicators.stream().anyMatch(indicator ->
                indicator.toLowerCase(Locale.ROOT).contains("self-harm")
                        || indicator.toLowerCase(Locale.ROOT).contains("suicide"))) {
            return SeverityLevel.SIGNIFICANT;
        }

        if (matchedIndicators.size() >= 2) {
            return SeverityLevel.MODERATE;
        }

        return matchedIndicators.isEmpty() ? SeverityLevel.MILD : SeverityLevel.MODERATE;
    }

    private User getRequiredUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }
}
