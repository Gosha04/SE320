"use client"

import React, { useState } from "react"
import { useApp, type DiaryEntry } from "@/lib/app-context"
import { BottomNav } from "@/components/flows/dashboard"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Textarea } from "@/components/ui/textarea"
import {
  ArrowLeft,
  ArrowRight,
  Plus,
  BookOpen,
  Calendar,
  ChevronRight,
  TrendingUp,
  CheckCircle2,
  Lightbulb,
  Brain,
  AlertCircle,
  BarChart3,
  Eye,
  FileText,
  Sparkles,
  MessageCircle,
  ChevronDown,
  ChevronUp,
} from "lucide-react"

/* ──────────── Screen 1: Diary Home ──────────── */
export function DiaryHomeScreen() {
  const { setScreen, diaryEntries, selectDiaryEntry } = useApp()
  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pb-4 pt-4">
        <div className="mb-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button onClick={() => setScreen("dashboard")}>
              <ArrowLeft className="h-5 w-5 text-foreground" />
            </button>
            <h1 className="text-xl font-bold text-foreground">Thought Diary</h1>
          </div>
          <button
            className="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-primary-foreground"
            onClick={() => setScreen("diary-new")}
            aria-label="New entry"
          >
            <Plus className="h-5 w-5" />
          </button>
        </div>

        <Card className="border-primary/20 bg-primary/5 p-4">
          <div className="flex items-center gap-3">
            <BookOpen className="h-5 w-5 text-primary" />
            <div>
              <p className="text-sm font-medium text-foreground">
                {diaryEntries.length} entries this week
              </p>
              <p className="text-xs text-muted-foreground">
                Keep tracking to reveal patterns
              </p>
            </div>
            <button
              className="ml-auto"
              onClick={() => setScreen("diary-insights")}
            >
              <BarChart3 className="h-5 w-5 text-primary" />
            </button>
          </div>
        </Card>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto px-6">
        {diaryEntries.map((entry) => (
          <button
            key={entry.id}
            className="flex w-full flex-col rounded-xl border border-border bg-card p-4 text-left transition-colors hover:border-primary/50"
            onClick={() => selectDiaryEntry(entry.id)}
          >
            <div className="mb-2 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Calendar className="h-3.5 w-3.5 text-muted-foreground" />
                <span className="text-xs text-muted-foreground">{entry.date}</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="text-xs text-muted-foreground">Mood:</span>
                <span className="text-xs font-medium text-foreground">
                  {entry.moodBefore}
                </span>
                <ArrowRight className="h-3 w-3 text-success" />
                <span className="text-xs font-medium text-success">
                  {entry.moodAfter}
                </span>
              </div>
            </div>
            <p className="mb-2 text-sm font-medium text-foreground line-clamp-1">
              {entry.situation}
            </p>
            <div className="flex flex-wrap gap-1">
              {entry.emotions.map((emo) => (
                <Badge key={emo} variant="secondary" className="text-[10px]">
                  {emo}
                </Badge>
              ))}
              {entry.distortions.map((dist) => (
                <Badge
                  key={dist}
                  variant="outline"
                  className="border-warning/30 bg-warning/5 text-[10px] text-warning-foreground"
                >
                  {dist}
                </Badge>
              ))}
            </div>
          </button>
        ))}
      </div>

      <div className="px-6 py-4">
        <Button
          className="h-12 w-full rounded-2xl text-sm font-semibold"
          onClick={() => setScreen("diary-new")}
        >
          <Plus className="mr-2 h-4 w-4" />
          New Entry
        </Button>
      </div>
      <BottomNav />
    </div>
  )
}

/* ──────────── Screen 2: New Entry ──────────── */
export function DiaryNewScreen() {
  const { setScreen } = useApp()
  const [mood, setMood] = useState(5)
  const moods = ["Very Low", "Low", "Below Avg", "Neutral", "Okay", "Good", "Very Good", "Great", "Excellent", "Amazing"]

  return (
    <div className="flex h-full flex-col bg-background px-6 pt-4">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => setScreen("diary-home")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="text-xl font-bold text-foreground">New Entry</h1>
      </div>

      <div className="mb-4 flex gap-1">
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
      </div>

      <h2 className="mb-2 text-lg font-bold text-foreground">How are you feeling?</h2>
      <p className="mb-6 text-sm text-muted-foreground">
        Rate your current mood before we begin.
      </p>

      <div className="mb-4 text-center">
        <span className="text-5xl font-bold text-primary">{mood}</span>
        <p className="mt-1 text-sm text-muted-foreground">{moods[mood - 1]}</p>
      </div>

      <input
        type="range"
        min="1"
        max="10"
        value={mood}
        onChange={(e) => setMood(parseInt(e.target.value))}
        className="mb-8 w-full accent-primary"
      />

      <Card className="mb-6 border-border bg-card p-4">
        <div className="flex items-start gap-3">
          <Lightbulb className="mt-0.5 h-5 w-5 text-primary" />
          <p className="text-sm leading-relaxed text-muted-foreground">
            Thought records help you identify patterns between situations, thoughts, and emotions. The more consistently you record, the more insights you will discover.
          </p>
        </div>
      </Card>

      <div className="mt-auto pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("diary-situation")}
        >
          Start Recording
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 3: Situation ──────────── */
export function DiarySituationScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background px-6 pt-4">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => setScreen("diary-new")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="text-base font-semibold text-foreground">Situation</h1>
        <Badge variant="secondary" className="ml-auto text-xs">Step 1/5</Badge>
      </div>

      <div className="mb-4 flex gap-1">
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
      </div>

      <h2 className="mb-2 text-lg font-bold text-foreground">What happened?</h2>
      <p className="mb-4 text-sm text-muted-foreground">
        Describe the situation that triggered your thoughts or feelings.
      </p>

      <Textarea
        placeholder="e.g., My manager gave me critical feedback during a team meeting..."
        className="mb-4 min-h-[140px] rounded-xl border-border"
      />

      <div className="mb-4">
        <p className="mb-2 text-sm font-medium text-foreground">When did this happen?</p>
        <div className="flex gap-2">
          {["Just now", "Today", "Yesterday", "This week"].map((time) => (
            <button
              key={time}
              className="flex-1 rounded-lg border border-border bg-card px-2 py-2 text-xs font-medium text-foreground transition-colors hover:border-primary hover:bg-primary/5"
            >
              {time}
            </button>
          ))}
        </div>
      </div>

      <div className="mb-6">
        <p className="mb-2 text-sm font-medium text-foreground">Where were you?</p>
        <div className="flex flex-wrap gap-2">
          {["Work", "Home", "Commuting", "Social", "Other"].map((place) => (
            <button
              key={place}
              className="rounded-full border border-border bg-card px-4 py-1.5 text-xs font-medium text-foreground transition-colors hover:border-primary hover:bg-primary/5"
            >
              {place}
            </button>
          ))}
        </div>
      </div>

      <div className="mt-auto pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("diary-thoughts")}
        >
          Next: Thoughts & Emotions
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 4: Thoughts & Emotions ──────────── */
export function DiaryThoughtsScreen() {
  const { setScreen } = useApp()
  const [selectedEmotions, setSelectedEmotions] = useState<string[]>([])

  const emotions = [
    "Anxious", "Frustrated", "Sad", "Guilty", "Angry",
    "Overwhelmed", "Hopeless", "Ashamed", "Lonely", "Exhausted",
    "Worried", "Irritable",
  ]

  const toggleEmotion = (emotion: string) => {
    setSelectedEmotions((prev) =>
      prev.includes(emotion) ? prev.filter((e) => e !== emotion) : [...prev, emotion]
    )
  }

  return (
    <div className="flex h-full flex-col bg-background px-6 pt-4">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => setScreen("diary-situation")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="text-base font-semibold text-foreground">Thoughts & Emotions</h1>
        <Badge variant="secondary" className="ml-auto text-xs">Step 2/5</Badge>
      </div>

      <div className="mb-4 flex gap-1">
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
      </div>

      <h2 className="mb-2 text-lg font-bold text-foreground">
        What went through your mind?
      </h2>
      <p className="mb-4 text-sm text-muted-foreground">
        Write the exact thoughts you had in that moment.
      </p>

      <Textarea
        placeholder="e.g., Everyone thinks I'm incompetent. I'm going to lose my job..."
        className="mb-6 min-h-[100px] rounded-xl border-border"
      />

      <h3 className="mb-3 text-sm font-semibold text-foreground">
        What emotions did you feel?
      </h3>
      <div className="mb-6 flex flex-wrap gap-2">
        {emotions.map((emotion) => (
          <button
            key={emotion}
            className={`rounded-full border px-3 py-1.5 text-xs font-medium transition-colors ${
              selectedEmotions.includes(emotion)
                ? "border-primary bg-primary/10 text-primary"
                : "border-border bg-card text-muted-foreground hover:border-primary/50"
            }`}
            onClick={() => toggleEmotion(emotion)}
          >
            {emotion}
          </button>
        ))}
      </div>

      {selectedEmotions.length > 0 && (
        <div className="mb-4">
          <p className="mb-2 text-sm font-medium text-foreground">
            Intensity ({selectedEmotions[0]})
          </p>
          <input type="range" min="1" max="10" defaultValue="7" className="w-full accent-primary" />
          <div className="flex justify-between">
            <span className="text-xs text-muted-foreground">Mild</span>
            <span className="text-xs text-muted-foreground">Intense</span>
          </div>
        </div>
      )}

      <div className="mt-auto pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("diary-distortions")}
        >
          Next: Identify Distortions
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 5: Cognitive Distortions ──────────── */
export function DiaryDistortionsScreen() {
  const { setScreen } = useApp()
  const [selected, setSelected] = useState<string[]>([])

  const distortions = [
    { name: "All-or-Nothing", desc: "Seeing things in black or white categories" },
    { name: "Catastrophizing", desc: "Expecting the worst possible outcome" },
    { name: "Mind Reading", desc: "Assuming you know what others think" },
    { name: "Overgeneralizing", desc: "Using one event to predict a pattern" },
    { name: "Labeling", desc: "Attaching a fixed label to yourself" },
    { name: "Should Statements", desc: "Rigid rules about how things must be" },
    { name: "Personalization", desc: "Blaming yourself for external events" },
    { name: "Emotional Reasoning", desc: "Believing feelings equal reality" },
  ]

  const toggle = (name: string) => {
    setSelected((prev) =>
      prev.includes(name) ? prev.filter((n) => n !== name) : [...prev, name]
    )
  }

  return (
    <div className="flex h-full flex-col bg-background px-6 pt-4">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => setScreen("diary-thoughts")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="text-base font-semibold text-foreground">Cognitive Distortions</h1>
        <Badge variant="secondary" className="ml-auto text-xs">Step 3/5</Badge>
      </div>

      <div className="mb-4 flex gap-1">
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-border" />
      </div>

      <h2 className="mb-2 text-lg font-bold text-foreground">
        Spot the thinking traps
      </h2>
      <p className="mb-4 text-sm text-muted-foreground">
        Which thinking patterns do you recognize in your thoughts?
      </p>

      <Card className="mb-4 border-primary/20 bg-primary/5 p-3">
        <div className="flex items-start gap-2">
          <Brain className="mt-0.5 h-4 w-4 text-primary" />
          <p className="text-xs leading-relaxed text-foreground/80">
            {"AI Suggestion: Based on your thoughts, you might be experiencing "}
            <strong>Mind Reading</strong> and <strong>Catastrophizing</strong>.
          </p>
        </div>
      </Card>

      <div className="flex-1 space-y-2 overflow-y-auto">
        {distortions.map(({ name, desc }) => (
          <button
            key={name}
            className={`flex w-full items-center gap-3 rounded-xl border p-3 text-left transition-colors ${
              selected.includes(name)
                ? "border-primary bg-primary/5"
                : "border-border bg-card hover:border-primary/50"
            }`}
            onClick={() => toggle(name)}
          >
            <div
              className={`flex h-6 w-6 items-center justify-center rounded-lg text-xs ${
                selected.includes(name) ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground"
              }`}
            >
              {selected.includes(name) ? <CheckCircle2 className="h-4 w-4" /> : <AlertCircle className="h-4 w-4" />}
            </div>
            <div>
              <p className="text-sm font-medium text-foreground">{name}</p>
              <p className="text-xs text-muted-foreground">{desc}</p>
            </div>
          </button>
        ))}
      </div>

      <div className="pb-6 pt-4">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          disabled={selected.length === 0}
          onClick={() => setScreen("diary-reframe")}
        >
          Next: Reframe
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 6: Reframe ──────────── */
export function DiaryReframeScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background px-6 pt-4">
      <div className="mb-6 flex items-center gap-3">
        <button onClick={() => setScreen("diary-distortions")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="text-base font-semibold text-foreground">Reframing</h1>
        <Badge variant="secondary" className="ml-auto text-xs">Step 4/5</Badge>
      </div>

      <div className="mb-4 flex gap-1">
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
        <div className="h-1.5 w-8 rounded-full bg-primary" />
      </div>

      <h2 className="mb-2 text-lg font-bold text-foreground">
        Create a balanced thought
      </h2>
      <p className="mb-4 text-sm text-muted-foreground">
        Rewrite your thoughts in a more balanced and compassionate way.
      </p>

      <Card className="mb-4 border-warning/20 bg-warning/5 p-4">
        <p className="mb-1 text-xs font-semibold text-warning-foreground">ORIGINAL THOUGHT</p>
        <p className="text-sm text-foreground/80">
          {"Everyone thinks I'm incompetent. I'm going to lose my job."}
        </p>
      </Card>

      <Card className="mb-4 border-primary/20 bg-primary/5 p-3">
        <div className="flex items-start gap-2">
          <Lightbulb className="mt-0.5 h-4 w-4 text-primary" />
          <div>
            <p className="text-xs font-medium text-foreground">AI Prompt:</p>
            <p className="text-xs text-muted-foreground">
              What evidence do you have that people actually think you are incompetent? What would a supportive friend say?
            </p>
          </div>
        </div>
      </Card>

      <Textarea
        placeholder="e.g., One piece of feedback doesn't mean I'm incompetent. My recent project was praised by the team..."
        className="mb-4 min-h-[120px] rounded-xl border-border"
      />

      <div className="mb-4">
        <p className="mb-2 text-sm font-medium text-foreground">
          How much do you believe this new thought?
        </p>
        <input type="range" min="0" max="100" defaultValue="60" className="w-full accent-primary" />
        <div className="flex justify-between">
          <span className="text-xs text-muted-foreground">Not at all</span>
          <span className="text-xs text-muted-foreground">Completely</span>
        </div>
      </div>

      <div className="mt-auto pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("diary-saved")}
        >
          Save Entry
          <CheckCircle2 className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 7: Entry Saved ──────────── */
export function DiarySavedScreen() {
  const { setScreen, addDiaryEntry } = useApp()
  const [saved, setSaved] = useState(false)

  if (!saved) {
    const entry: DiaryEntry = {
      id: Date.now().toString(),
      date: "2026-02-20",
      situation: "Critical feedback during team meeting",
      thoughts: "Everyone thinks I'm incompetent",
      emotions: ["Anxious", "Frustrated"],
      distortions: ["Mind Reading", "Catastrophizing"],
      reframe: "One piece of feedback doesn't define my competence. My recent work has been well-received.",
      moodBefore: 3,
      moodAfter: 6,
    }
    addDiaryEntry(entry)
    setSaved(true)
  }

  return (
    <div className="flex h-full flex-col items-center bg-background px-6 pt-12">
      <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-success/10">
        <CheckCircle2 className="h-10 w-10 text-success" />
      </div>
      <h2 className="mb-2 text-2xl font-bold text-foreground">Entry Saved!</h2>
      <p className="mb-8 text-center text-sm text-muted-foreground">
        Great job reflecting on your thoughts. Consistent tracking builds self-awareness.
      </p>

      <Card className="mb-4 w-full border-border bg-card p-5">
        <h3 className="mb-3 text-sm font-semibold text-muted-foreground">MOOD SHIFT</h3>
        <div className="flex items-center justify-center gap-6">
          <div className="text-center">
            <span className="text-3xl font-bold text-foreground">3</span>
            <p className="text-xs text-muted-foreground">Before</p>
          </div>
          <ArrowRight className="h-6 w-6 text-success" />
          <div className="text-center">
            <span className="text-3xl font-bold text-success">6</span>
            <p className="text-xs text-muted-foreground">After</p>
          </div>
        </div>
      </Card>

      <Card className="mb-6 w-full border-border bg-card p-5">
        <h3 className="mb-3 text-sm font-semibold text-muted-foreground">INSIGHTS</h3>
        <div className="space-y-2">
          <div className="flex items-start gap-2">
            <TrendingUp className="mt-0.5 h-4 w-4 text-primary" />
            <p className="text-sm text-foreground">
              Mind Reading is your most common distortion this week.
            </p>
          </div>
          <div className="flex items-start gap-2">
            <Lightbulb className="mt-0.5 h-4 w-4 text-accent" />
            <p className="text-sm text-foreground">
              Work situations trigger 70% of your negative thoughts.
            </p>
          </div>
        </div>
      </Card>

      <div className="mt-auto flex w-full gap-3 pb-6">
        <Button
          variant="outline"
          className="h-12 flex-1 rounded-2xl"
          onClick={() => setScreen("diary-home")}
        >
          View Diary
        </Button>
        <Button
          className="h-12 flex-1 rounded-2xl"
          onClick={() => setScreen("diary-insights")}
        >
          See Patterns
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 8: Pattern Insights ──────────── */
export function DiaryInsightsScreen() {
  const { setScreen, diaryEntries } = useApp()

  const emotionCounts: Record<string, number> = {}
  const distortionCounts: Record<string, number> = {}
  diaryEntries.forEach((entry) => {
    entry.emotions.forEach((e) => {
      emotionCounts[e] = (emotionCounts[e] || 0) + 1
    })
    entry.distortions.forEach((d) => {
      distortionCounts[d] = (distortionCounts[d] || 0) + 1
    })
  })

  const topEmotions = Object.entries(emotionCounts)
    .sort(([, a], [, b]) => b - a)
    .slice(0, 5)
  const topDistortions = Object.entries(distortionCounts)
    .sort(([, a], [, b]) => b - a)
    .slice(0, 5)

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pb-4 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("diary-home")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">Pattern Insights</h1>
        </div>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto px-6">
        <Card className="border-border bg-card p-4">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Mood Trend</h3>
          <div className="flex items-end gap-2">
            {[3, 4, 5, 3, 6, 5, 7].map((val, i) => (
              <div key={i} className="flex flex-1 flex-col items-center gap-1">
                <div
                  className="w-full rounded-t-md bg-primary/20"
                  style={{ height: `${val * 10}px` }}
                >
                  <div
                    className="w-full rounded-t-md bg-primary"
                    style={{ height: `${val * 10}px` }}
                  />
                </div>
                <span className="text-[10px] text-muted-foreground">
                  {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"][i]}
                </span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="border-border bg-card p-4">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Top Emotions</h3>
          <div className="space-y-2">
            {topEmotions.map(([emotion, count]) => (
              <div key={emotion} className="flex items-center gap-3">
                <span className="w-24 text-sm text-foreground">{emotion}</span>
                <div className="flex-1">
                  <div
                    className="h-5 rounded-full bg-primary/20"
                    style={{ width: `${(count / diaryEntries.length) * 100}%` }}
                  >
                    <div
                      className="h-5 rounded-full bg-primary"
                      style={{ width: "100%" }}
                    />
                  </div>
                </div>
                <span className="text-xs text-muted-foreground">{count}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="border-border bg-card p-4">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Thinking Traps</h3>
          <div className="space-y-2">
            {topDistortions.map(([distortion, count]) => (
              <div key={distortion} className="flex items-center gap-3">
                <span className="w-24 text-sm text-foreground">{distortion}</span>
                <div className="flex-1">
                  <div
                    className="h-5 rounded-full bg-warning/20"
                    style={{ width: `${(count / diaryEntries.length) * 100}%` }}
                  >
                    <div
                      className="h-5 rounded-full bg-warning"
                      style={{ width: "100%" }}
                    />
                  </div>
                </div>
                <span className="text-xs text-muted-foreground">{count}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="border-primary/20 bg-primary/5 p-4">
          <div className="flex items-start gap-3">
            <Brain className="mt-0.5 h-5 w-5 text-primary" />
            <div>
              <p className="text-sm font-medium text-foreground">AI Insight</p>
              <p className="text-xs leading-relaxed text-muted-foreground">
                {"Your mood improves an average of 2.5 points after completing thought records. Work-related situations trigger most of your negative thoughts, especially around team interactions. Consider exploring the \"Setting Boundaries\" CBT session."}
              </p>
            </div>
          </div>
        </Card>
      </div>

      <div className="px-6 py-4">
        <Button
          variant="outline"
          className="h-12 w-full rounded-2xl"
          onClick={() => setScreen("diary-home")}
        >
          Back to Diary
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Diary Detail Data ──────────── */
const mobileDiaryTranscripts: Record<string, { role: "ai" | "user"; speaker: string; text: string; time: string }[]> = {
  "1": [
    { role: "ai", speaker: "AI Guide", text: "You noted feeling overwhelmed by deadlines. What was the first thought?", time: "0:00" },
    { role: "user", speaker: "You", text: "I can never keep up. Everyone else manages fine but I'm always behind.", time: "0:15" },
    { role: "ai", speaker: "AI Guide", text: "I notice all-or-nothing thinking and comparison. What evidence challenges these?", time: "0:28" },
    { role: "user", speaker: "You", text: "I did finish last week's project on time. A colleague mentioned she was stressed too.", time: "0:48" },
    { role: "ai", speaker: "AI Guide", text: "So you can keep up, and stress is not unique to you. Can you form a balanced thought?", time: "1:05" },
    { role: "user", speaker: "You", text: "I have managed heavy workloads before. I can prioritize and ask for help.", time: "1:22" },
  ],
  "2": [
    { role: "ai", speaker: "AI Guide", text: "You wrote about skipping exercise for three days. What thoughts came up?", time: "0:00" },
    { role: "user", speaker: "You", text: "I have no discipline. What's the point of trying?", time: "0:12" },
    { role: "ai", speaker: "AI Guide", text: "That is labeling and catastrophizing. Let's challenge them.", time: "0:25" },
    { role: "user", speaker: "You", text: "I exercised four days last week. That does take some discipline.", time: "0:40" },
    { role: "ai", speaker: "AI Guide", text: "Exactly. Missing a few days doesn't erase consistency. What would a friend say?", time: "0:55" },
    { role: "user", speaker: "You", text: "It's okay to rest. I can start again with something small like a walk.", time: "1:10" },
  ],
}

const mobileDiaryAi: Record<string, { summary: string; insights: string[]; recs: string[] }> = {
  "1": {
    summary: "Captured a burnout trigger -- deadline pressure. All-or-nothing thinking and comparison were the primary distortions. Successfully reframed to a balanced alternative. Mood improved from 3 to 6.",
    insights: ["Work deadline pressure appears in 4 of your last 7 entries", "All-or-nothing thinking activates most under deadline stress", "Evidence-based challenges work best for your thinking style"],
    recs: ["Under deadline stress, ask: 'When did I handle this before?'", "Challenge comparisons by checking in with colleagues about their stress", "Consider the 'Setting Boundaries' CBT session"],
  },
  "2": {
    summary: "Addressed self-criticism around missed exercise. Labeling and catastrophizing were identified. Self-compassion was the key breakthrough. Mood improved from 2 to 5.",
    insights: ["Self-critical thoughts spike when you break routines", "Labeling is more damaging than the actual missed exercise", "Strong capacity for self-compassion once guided"],
    recs: ["Set a 'minimum viable exercise' for rest days", "Ask: 'What would I tell a friend?'", "Track movement flexibly, not just full workouts"],
  },
}

/* ──────────── Diary Detail Screen (Mobile) ──────────── */
export function DiaryDetailScreen() {
  const { setScreen, selectedDiaryId, diaryEntries } = useApp()
  const [activeTab, setActiveTab] = useState<"overview" | "transcript" | "ai">("overview")
  const [transcriptExpanded, setTranscriptExpanded] = useState(false)

  const entry = diaryEntries.find((e) => e.id === selectedDiaryId) || diaryEntries[0]
  const transcript = mobileDiaryTranscripts[entry.id] || mobileDiaryTranscripts["1"]
  const aiData = mobileDiaryAi[entry.id] || mobileDiaryAi["1"]

  const tabs = [
    { id: "overview" as const, label: "Overview", icon: BookOpen },
    { id: "transcript" as const, label: "Transcript", icon: FileText },
    { id: "ai" as const, label: "AI Analysis", icon: Brain },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("diary-home")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <div className="flex-1 overflow-hidden">
          <p className="truncate text-sm font-semibold text-foreground">{entry.situation}</p>
          <p className="text-[10px] text-muted-foreground">{new Date(entry.date).toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" })}</p>
        </div>
        <div className="flex items-center gap-1">
          <span className="text-xs font-medium text-foreground">{entry.moodBefore}</span>
          <ArrowRight className="h-3 w-3 text-success" />
          <span className="text-xs font-medium text-success">{entry.moodAfter}</span>
        </div>
      </div>

      {/* Tab Nav */}
      <div className="flex gap-0.5 border-b border-border bg-card px-2 pt-1">
        {tabs.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            className={`flex flex-1 items-center justify-center gap-1 rounded-t-lg px-2 py-2 text-[10px] font-medium transition-colors ${
              activeTab === id ? "border-b-2 border-primary bg-background text-primary" : "text-muted-foreground"
            }`}
            onClick={() => setActiveTab(id)}
          >
            <Icon className="h-3 w-3" />
            {label}
          </button>
        ))}
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto px-4 py-4">
        {activeTab === "overview" && (
          <div className="space-y-3">
            <Card className="border-warning/20 bg-warning/5 p-3">
              <p className="mb-1 text-[10px] font-semibold text-warning-foreground">ORIGINAL THOUGHT</p>
              <p className="text-xs leading-relaxed text-foreground/80">{entry.thoughts}</p>
            </Card>
            <Card className="border-success/20 bg-success/5 p-3">
              <p className="mb-1 text-[10px] font-semibold text-success">BALANCED REFRAME</p>
              <p className="text-xs leading-relaxed text-foreground/80">{entry.reframe}</p>
            </Card>
            <div className="grid grid-cols-3 gap-2">
              <Card className="border-border bg-card p-3 text-center">
                <p className="text-[10px] text-muted-foreground">Before</p>
                <p className="text-lg font-bold text-foreground">{entry.moodBefore}</p>
              </Card>
              <Card className="border-success/30 bg-success/5 p-3 text-center">
                <p className="text-[10px] text-muted-foreground">After</p>
                <p className="text-lg font-bold text-success">{entry.moodAfter}</p>
              </Card>
              <Card className="border-border bg-card p-3 text-center">
                <p className="text-[10px] text-muted-foreground">Change</p>
                <p className="text-lg font-bold text-primary">+{entry.moodAfter - entry.moodBefore}</p>
              </Card>
            </div>
            <Card className="border-border bg-card p-3">
              <p className="mb-2 text-[10px] font-semibold text-muted-foreground">EMOTIONS & DISTORTIONS</p>
              <div className="flex flex-wrap gap-1">
                {entry.emotions.map((e) => <Badge key={e} variant="secondary" className="text-[10px]">{e}</Badge>)}
                {entry.distortions.map((d) => <Badge key={d} variant="outline" className="border-warning/30 bg-warning/5 text-[10px] text-warning-foreground">{d}</Badge>)}
              </div>
            </Card>
          </div>
        )}

        {activeTab === "transcript" && (
          <div className="space-y-3">
            <p className="text-[10px] text-muted-foreground">{transcript.length} messages -- Guided thought record</p>
            {(transcriptExpanded ? transcript : transcript.slice(0, 3)).map((msg, i) => (
              <div key={i} className="flex gap-2">
                <div className={`flex h-6 w-6 shrink-0 items-center justify-center rounded-full ${msg.role === "ai" ? "bg-primary/10" : "bg-accent/10"}`}>
                  {msg.role === "ai" ? <Brain className="h-3 w-3 text-primary" /> : <MessageCircle className="h-3 w-3 text-accent" />}
                </div>
                <div className="flex-1">
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-semibold text-foreground">{msg.speaker}</span>
                    <span className="text-[9px] text-muted-foreground">{msg.time}</span>
                  </div>
                  <p className="mt-0.5 text-xs leading-relaxed text-foreground/80">{msg.text}</p>
                </div>
              </div>
            ))}
            {!transcriptExpanded && transcript.length > 3 && (
              <button className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-border py-2 text-[10px] text-muted-foreground" onClick={() => setTranscriptExpanded(true)}>
                <ChevronDown className="h-3 w-3" />
                Show {transcript.length - 3} more
              </button>
            )}
            {transcriptExpanded && (
              <button className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-border py-2 text-[10px] text-muted-foreground" onClick={() => setTranscriptExpanded(false)}>
                <ChevronUp className="h-3 w-3" />
                Collapse
              </button>
            )}
          </div>
        )}

        {activeTab === "ai" && (
          <div className="space-y-3">
            <Card className="border-border bg-card p-4">
              <div className="flex items-start gap-2">
                <Brain className="mt-0.5 h-4 w-4 text-primary" />
                <div>
                  <h3 className="text-xs font-semibold text-foreground">AI Summary</h3>
                  <p className="mt-1 text-xs leading-relaxed text-foreground/80">{aiData.summary}</p>
                </div>
              </div>
            </Card>
            <Card className="border-primary/20 bg-primary/5 p-3">
              <div className="flex items-start gap-2">
                <Sparkles className="mt-0.5 h-4 w-4 text-primary" />
                <p className="text-[10px] text-muted-foreground">AI insights based on this entry and your patterns.</p>
              </div>
            </Card>
            {aiData.insights.map((insight, i) => (
              <Card key={i} className="border-l-2 border-l-primary border-border bg-card p-3">
                <div className="flex items-start gap-2">
                  <Lightbulb className="mt-0.5 h-3.5 w-3.5 text-primary" />
                  <p className="text-xs leading-relaxed text-foreground/80">{insight}</p>
                </div>
              </Card>
            ))}
            <Card className="border-border bg-card p-3">
              <p className="mb-2 text-[10px] font-semibold text-muted-foreground">RECOMMENDATIONS</p>
              <div className="space-y-2">
                {aiData.recs.map((rec, i) => (
                  <div key={i} className="flex items-start gap-2">
                    <div className="mt-1.5 h-1 w-1 shrink-0 rounded-full bg-primary" />
                    <p className="text-xs leading-relaxed text-foreground/80">{rec}</p>
                  </div>
                ))}
              </div>
            </Card>
          </div>
        )}
      </div>
    </div>
  )
}
