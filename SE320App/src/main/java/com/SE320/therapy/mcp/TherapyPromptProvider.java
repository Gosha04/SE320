package com.SE320.therapy.mcp;

import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springaicommunity.mcp.annotation.McpArg;
import org.springaicommunity.mcp.annotation.McpPrompt;

@Component
public class TherapyPromptProvider {

    @McpPrompt(name = "thought_analysis", description = "Structured prompt for analyzing an automatic thought for cognitive distortions.")
    public GetPromptResult thoughtAnalysis(
            @McpArg(name = "thought", description = "Automatic thought to analyze", required = true) String thought) {
        String message = """
                Analyze this automatic thought using CBT principles.

                Thought: "%s"

                Return:
                1. Likely cognitive distortions with confidence.
                2. Evidence that supports and challenges the thought.
                3. Two balanced reframes.
                4. One small next action.
                Keep the tone warm, nonjudgmental, and non-diagnostic.
                """.formatted(thought);
        return prompt("Thought Analysis", message);
    }

    @McpPrompt(name = "session_summary", description = "Prompt for generating a therapeutic session summary.")
    public GetPromptResult sessionSummary(
            @McpArg(name = "sessionId", description = "Active or completed user session id", required = true) String sessionId) {
        String message = """
                Generate a concise CBT session summary for session id %s.

                Include:
                1. Main theme discussed.
                2. CBT skills practiced.
                3. Mood or risk signals to monitor.
                4. One practical next step before the next session.
                Avoid diagnosis and keep the language supportive.
                """.formatted(sessionId);
        return prompt("Session Summary", message);
    }

    @McpPrompt(name = "weekly_check_in", description = "Guided weekly check-in template with mood and progress questions.")
    public GetPromptResult weeklyCheckIn(
            @McpArg(name = "userId", description = "User UUID", required = true) String userId) {
        String message = """
                Run a weekly CBT check-in for user %s.

                Ask about:
                1. Overall mood this week.
                2. Most common automatic thought.
                3. One situation that improved after reframing.
                4. Current stress level and coping strategy use.
                5. A realistic goal for the next seven days.
                Prioritize safety if the user mentions crisis indicators.
                """.formatted(userId);
        return prompt("Weekly Check-In", message);
    }

    private GetPromptResult prompt(String description, String message) {
        return new GetPromptResult(
                description,
                List.of(new PromptMessage(Role.USER, new TextContent(message))));
    }
}
