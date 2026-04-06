package com.SE320.therapy.ai;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class KnowledgeBaseLoader {

    private final VectorStore vectorStore;
    private final EmbeddingService embeddingService;

    public KnowledgeBaseLoader(VectorStore vectorStore, EmbeddingService embeddingService) {
        this.vectorStore = vectorStore;
        this.embeddingService = embeddingService;
    }

    @PostConstruct
    public void loadKnowledgeBase() {
        if (vectorStore.size() > 0) {
            return;
        }

        vectorStore.addAll(List.of(
                knowledgeDocument(
                        "distortion-all-or-nothing",
                        "All-or-nothing thinking treats experiences as total success or total failure. A CBT response looks for middle ground, exceptions, and evidence against absolutes.",
                        Map.of("category", "distortion", "topic", "all-or-nothing")),
                knowledgeDocument(
                        "distortion-overgeneralization",
                        "Overgeneralization draws broad conclusions from a single setback. CBT encourages separating one event from a permanent pattern.",
                        Map.of("category", "distortion", "topic", "overgeneralization")),
                knowledgeDocument(
                        "distortion-mental-filtering",
                        "Mental filtering focuses only on the negative details while ignoring the neutral or positive data in the same situation.",
                        Map.of("category", "distortion", "topic", "mental-filtering")),
                knowledgeDocument(
                        "distortion-catastrophizing",
                        "Catastrophizing assumes the worst outcome is likely or unbearable. CBT slows this down by estimating realistic outcomes and coping ability.",
                        Map.of("category", "distortion", "topic", "catastrophizing")),
                knowledgeDocument(
                        "distortion-mind-reading",
                        "Mind reading assumes you know what other people think without enough evidence. CBT uses curiosity, alternative explanations, and evidence checks.",
                        Map.of("category", "distortion", "topic", "mind-reading")),
                knowledgeDocument(
                        "distortion-fortune-telling",
                        "Fortune telling predicts negative outcomes as facts. CBT asks what evidence supports the prediction and what other outcomes are possible.",
                        Map.of("category", "distortion", "topic", "fortune-telling")),
                knowledgeDocument(
                        "distortion-emotional-reasoning",
                        "Emotional reasoning treats feelings as proof. A CBT response validates the feeling while separating it from objective fact.",
                        Map.of("category", "distortion", "topic", "emotional-reasoning")),
                knowledgeDocument(
                        "distortion-should-statements",
                        "Should statements use rigid internal rules that often increase shame, frustration, or resentment. CBT reframes these rules into preferences and realistic goals.",
                        Map.of("category", "distortion", "topic", "should-statements")),
                knowledgeDocument(
                        "distortion-labeling",
                        "Labeling turns a specific mistake into a global identity judgment, such as calling yourself incompetent instead of naming a single error.",
                        Map.of("category", "distortion", "topic", "labeling")),
                knowledgeDocument(
                        "distortion-personalization",
                        "Personalization takes excessive responsibility for events that are influenced by many factors outside one person's control.",
                        Map.of("category", "distortion", "topic", "personalization")),
                knowledgeDocument(
                        "technique-thought-challenging",
                        "Thought challenging helps users examine evidence for and against a thought, notice distortions, and build a more balanced alternative.",
                        Map.of("category", "technique", "topic", "thought-challenging")),
                knowledgeDocument(
                        "technique-behavioral-activation",
                        "Behavioral activation uses small, meaningful actions to rebuild momentum, energy, and reward, especially during burnout or low mood.",
                        Map.of("category", "technique", "topic", "behavioral-activation")),
                knowledgeDocument(
                        "technique-exposure",
                        "Exposure techniques support gradual and planned contact with avoided situations so fear can decrease over time with practice.",
                        Map.of("category", "technique", "topic", "exposure")),
                knowledgeDocument(
                        "technique-relaxation",
                        "Relaxation and mindfulness techniques can reduce physiological stress and create enough calm to think more flexibly.",
                        Map.of("category", "technique", "topic", "relaxation-mindfulness")),
                knowledgeDocument(
                        "technique-problem-solving",
                        "Problem-solving therapy breaks overwhelming problems into concrete steps, options, tradeoffs, and one realistic next action.",
                        Map.of("category", "technique", "topic", "problem-solving")),
                knowledgeDocument(
                        "burnout-maslach",
                        "Burnout often includes emotional exhaustion, depersonalization or cynicism, and reduced personal accomplishment. Recovery work often targets rest, values, boundaries, and sustainable workload changes.",
                        Map.of("category", "burnout", "topic", "maslach")),
                knowledgeDocument(
                        "burnout-boundaries",
                        "Boundary setting for burnout may include workload limits, communication norms, recovery time, and reducing perfectionistic over-functioning.",
                        Map.of("category", "burnout", "topic", "boundaries")),
                knowledgeDocument(
                        "crisis-warning-signs",
                        "Crisis warning signs include suicidal language, hopelessness, feeling like a burden, intent to self-harm, and escalating inability to stay safe.",
                        Map.of("category", "crisis", "topic", "warning-signs")),
                knowledgeDocument(
                        "crisis-de-escalation",
                        "Crisis de-escalation emphasizes calm, direct language, immediate support, encouraging local emergency resources, and prioritizing safety over reflective CBT work.",
                        Map.of("category", "crisis", "topic", "de-escalation")),
                knowledgeDocument(
                        "crisis-safety-planning",
                        "Safety planning typically includes warning signs, internal coping strategies, supportive contacts, professional resources, and reducing access to means of self-harm.",
                        Map.of("category", "crisis", "topic", "safety-planning"))));
    }

    private VectorDocument knowledgeDocument(String id, String content, Map<String, String> metadata) {
        return new VectorDocument(id, content, metadata, embeddingService.embed(content));
    }
}
