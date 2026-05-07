"use client"

import React, { useState } from "react"
import { useApp, type DiaryEntry } from "@/lib/app-context"
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

export function WebDiaryHomeScreen() {
  const { setScreen, diaryEntries, selectDiaryEntry } = useApp()
  return (
    <div className="p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Thought Diary</h1>
          <p className="text-sm text-muted-foreground">{diaryEntries.length} entries this week - Keep tracking to reveal patterns</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" className="rounded-xl" onClick={() => setScreen("diary-insights")}>
            <BarChart3 className="mr-2 h-4 w-4" />
            Insights
          </Button>
          <Button className="rounded-xl" onClick={() => setScreen("diary-new")}>
            <Plus className="mr-2 h-4 w-4" />
            New Entry
          </Button>
        </div>
      </div>

      <div className="grid gap-4 lg:grid-cols-2 xl:grid-cols-3">
        {diaryEntries.map((entry) => (
          <button
            key={entry.id}
            className="flex flex-col rounded-xl border border-border bg-card p-5 text-left transition-all hover:border-primary/50 hover:shadow-md"
            onClick={() => selectDiaryEntry(entry.id)}
          >
            <div className="mb-3 flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Calendar className="h-3.5 w-3.5 text-muted-foreground" />
                <span className="text-xs text-muted-foreground">{entry.date}</span>
              </div>
              <div className="flex items-center gap-1">
                <span className="text-xs text-muted-foreground">Mood:</span>
                <span className="text-xs font-medium text-foreground">{entry.moodBefore}</span>
                <ArrowRight className="h-3 w-3 text-success" />
                <span className="text-xs font-medium text-success">{entry.moodAfter}</span>
              </div>
            </div>
            <p className="mb-3 text-sm font-medium text-foreground">{entry.situation}</p>
            <p className="mb-3 text-xs text-muted-foreground line-clamp-2">{entry.thoughts}</p>
            <div className="flex flex-wrap gap-1">
              {entry.emotions.map((emo) => (
                <Badge key={emo} variant="secondary" className="text-[10px]">{emo}</Badge>
              ))}
              {entry.distortions.map((dist) => (
                <Badge key={dist} variant="outline" className="border-warning/30 bg-warning/5 text-[10px] text-warning-foreground">{dist}</Badge>
              ))}
            </div>
          </button>
        ))}
      </div>
    </div>
  )
}

export function WebDiaryNewScreen() {
  const { setScreen } = useApp()
  const [mood, setMood] = useState(5)
  const moods = ["Very Low", "Low", "Below Avg", "Neutral", "Okay", "Good", "Very Good", "Great", "Excellent", "Amazing"]

  return (
    <div className="mx-auto max-w-lg p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-home")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Diary
      </button>
      <div className="mb-4 flex gap-1">
        {[true, false, false, false, false].map((active, i) => (
          <div key={i} className={`h-1.5 w-8 rounded-full ${active ? "bg-primary" : "bg-border"}`} />
        ))}
      </div>
      <h2 className="mb-2 text-2xl font-bold text-foreground">How are you feeling?</h2>
      <p className="mb-8 text-muted-foreground">Rate your current mood before we begin.</p>
      <div className="mb-4 text-center">
        <span className="text-5xl font-bold text-primary">{mood}</span>
        <p className="mt-1 text-muted-foreground">{moods[mood - 1]}</p>
      </div>
      <input type="range" min="1" max="10" value={mood} onChange={(e) => setMood(parseInt(e.target.value))} className="mb-8 w-full accent-primary" />
      <Card className="mb-8 border-border bg-card p-4">
        <div className="flex items-start gap-3">
          <Lightbulb className="mt-0.5 h-5 w-5 text-primary" />
          <p className="text-sm leading-relaxed text-muted-foreground">
            Thought records help you identify patterns between situations, thoughts, and emotions. The more consistently you record, the more insights you will discover.
          </p>
        </div>
      </Card>
      <Button className="h-14 w-full rounded-2xl text-base font-semibold" onClick={() => setScreen("diary-situation")}>
        Start Recording
        <ArrowRight className="ml-2 h-5 w-5" />
      </Button>
    </div>
  )
}

export function WebDiarySituationScreen() {
  const { setScreen } = useApp()
  return (
    <div className="mx-auto max-w-lg p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-new")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <Badge variant="secondary">Step 1/5</Badge>
      </div>
      <div className="mb-4 flex gap-1">
        {[true, true, false, false, false].map((active, i) => (
          <div key={i} className={`h-1.5 w-8 rounded-full ${active ? "bg-primary" : "bg-border"}`} />
        ))}
      </div>
      <h2 className="mb-2 text-xl font-bold text-foreground">What happened?</h2>
      <p className="mb-6 text-muted-foreground">Describe the situation that triggered your thoughts or feelings.</p>
      <Textarea placeholder="e.g., My manager gave me critical feedback during a team meeting..." className="mb-4 min-h-[140px] rounded-xl border-border" />
      <div className="mb-4">
        <p className="mb-2 text-sm font-medium text-foreground">When did this happen?</p>
        <div className="flex gap-2">
          {["Just now", "Today", "Yesterday", "This week"].map((time) => (
            <button key={time} className="flex-1 rounded-lg border border-border bg-card px-2 py-2 text-xs font-medium text-foreground transition-colors hover:border-primary hover:bg-primary/5">{time}</button>
          ))}
        </div>
      </div>
      <div className="mb-8">
        <p className="mb-2 text-sm font-medium text-foreground">Where were you?</p>
        <div className="flex flex-wrap gap-2">
          {["Work", "Home", "Commuting", "Social", "Other"].map((place) => (
            <button key={place} className="rounded-full border border-border bg-card px-4 py-1.5 text-xs font-medium text-foreground transition-colors hover:border-primary hover:bg-primary/5">{place}</button>
          ))}
        </div>
      </div>
      <Button className="h-14 w-full rounded-2xl text-base font-semibold" onClick={() => setScreen("diary-thoughts")}>
        Next: Thoughts & Emotions
        <ArrowRight className="ml-2 h-5 w-5" />
      </Button>
    </div>
  )
}

export function WebDiaryThoughtsScreen() {
  const { setScreen } = useApp()
  const [selectedEmotions, setSelectedEmotions] = useState<string[]>([])
  const emotions = ["Anxious", "Frustrated", "Sad", "Guilty", "Angry", "Overwhelmed", "Hopeless", "Ashamed", "Lonely", "Exhausted", "Worried", "Irritable"]
  const toggleEmotion = (emotion: string) => {
    setSelectedEmotions((prev) => prev.includes(emotion) ? prev.filter((e) => e !== emotion) : [...prev, emotion])
  }

  return (
    <div className="mx-auto max-w-lg p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-situation")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <Badge variant="secondary">Step 2/5</Badge>
      </div>
      <div className="mb-4 flex gap-1">
        {[true, true, true, false, false].map((active, i) => (
          <div key={i} className={`h-1.5 w-8 rounded-full ${active ? "bg-primary" : "bg-border"}`} />
        ))}
      </div>
      <h2 className="mb-2 text-xl font-bold text-foreground">What went through your mind?</h2>
      <p className="mb-6 text-muted-foreground">Write the exact thoughts you had in that moment.</p>
      <Textarea placeholder="e.g., Everyone thinks I'm incompetent. I'm going to lose my job..." className="mb-6 min-h-[100px] rounded-xl border-border" />
      <h3 className="mb-3 text-sm font-semibold text-foreground">What emotions did you feel?</h3>
      <div className="mb-6 flex flex-wrap gap-2">
        {emotions.map((emotion) => (
          <button
            key={emotion}
            className={`rounded-full border px-3 py-1.5 text-xs font-medium transition-colors ${
              selectedEmotions.includes(emotion) ? "border-primary bg-primary/10 text-primary" : "border-border bg-card text-muted-foreground hover:border-primary/50"
            }`}
            onClick={() => toggleEmotion(emotion)}
          >
            {emotion}
          </button>
        ))}
      </div>
      {selectedEmotions.length > 0 && (
        <div className="mb-6">
          <p className="mb-2 text-sm font-medium text-foreground">Intensity ({selectedEmotions[0]})</p>
          <input type="range" min="1" max="10" defaultValue="7" className="w-full accent-primary" />
          <div className="flex justify-between">
            <span className="text-xs text-muted-foreground">Mild</span>
            <span className="text-xs text-muted-foreground">Intense</span>
          </div>
        </div>
      )}
      <Button className="h-14 w-full rounded-2xl text-base font-semibold" onClick={() => setScreen("diary-distortions")}>
        Next: Identify Distortions
        <ArrowRight className="ml-2 h-5 w-5" />
      </Button>
    </div>
  )
}

export function WebDiaryDistortionsScreen() {
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
    setSelected((prev) => prev.includes(name) ? prev.filter((n) => n !== name) : [...prev, name])
  }

  return (
    <div className="mx-auto max-w-2xl p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-thoughts")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <Badge variant="secondary">Step 3/5</Badge>
      </div>
      <div className="mb-4 flex gap-1">
        {[true, true, true, true, false].map((active, i) => (
          <div key={i} className={`h-1.5 w-8 rounded-full ${active ? "bg-primary" : "bg-border"}`} />
        ))}
      </div>
      <h2 className="mb-2 text-xl font-bold text-foreground">Spot the thinking traps</h2>
      <p className="mb-4 text-muted-foreground">Which thinking patterns do you recognize in your thoughts?</p>
      <Card className="mb-6 border-primary/20 bg-primary/5 p-3">
        <div className="flex items-start gap-2">
          <Brain className="mt-0.5 h-4 w-4 text-primary" />
          <p className="text-xs leading-relaxed text-foreground/80">
            AI Suggestion: Based on your thoughts, you might be experiencing <strong>Mind Reading</strong> and <strong>Catastrophizing</strong>.
          </p>
        </div>
      </Card>
      <div className="grid gap-2 lg:grid-cols-2">
        {distortions.map(({ name, desc }) => (
          <button
            key={name}
            className={`flex items-center gap-3 rounded-xl border p-4 text-left transition-all ${
              selected.includes(name) ? "border-primary bg-primary/5 shadow-sm" : "border-border bg-card hover:border-primary/50"
            }`}
            onClick={() => toggle(name)}
          >
            <div className={`flex h-7 w-7 items-center justify-center rounded-lg text-xs ${
              selected.includes(name) ? "bg-primary text-primary-foreground" : "bg-muted text-muted-foreground"
            }`}>
              {selected.includes(name) ? <CheckCircle2 className="h-4 w-4" /> : <AlertCircle className="h-4 w-4" />}
            </div>
            <div>
              <p className="text-sm font-medium text-foreground">{name}</p>
              <p className="text-xs text-muted-foreground">{desc}</p>
            </div>
          </button>
        ))}
      </div>
      <div className="mt-8">
        <Button className="h-14 w-full rounded-2xl text-base font-semibold" disabled={selected.length === 0} onClick={() => setScreen("diary-reframe")}>
          Next: Reframe
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

export function WebDiaryReframeScreen() {
  const { setScreen } = useApp()
  return (
    <div className="mx-auto max-w-lg p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-distortions")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <Badge variant="secondary">Step 4/5</Badge>
      </div>
      <div className="mb-4 flex gap-1">
        {[true, true, true, true, true].map((_, i) => (
          <div key={i} className="h-1.5 w-8 rounded-full bg-primary" />
        ))}
      </div>
      <h2 className="mb-2 text-xl font-bold text-foreground">Create a balanced thought</h2>
      <p className="mb-6 text-muted-foreground">Rewrite your thoughts in a more balanced and compassionate way.</p>
      <Card className="mb-4 border-warning/20 bg-warning/5 p-4">
        <p className="mb-1 text-xs font-semibold text-warning-foreground">ORIGINAL THOUGHT</p>
        <p className="text-sm text-foreground/80">{"Everyone thinks I'm incompetent. I'm going to lose my job."}</p>
      </Card>
      <Card className="mb-4 border-primary/20 bg-primary/5 p-3">
        <div className="flex items-start gap-2">
          <Lightbulb className="mt-0.5 h-4 w-4 text-primary" />
          <div>
            <p className="text-xs font-medium text-foreground">AI Prompt:</p>
            <p className="text-xs text-muted-foreground">What evidence do you have that people actually think you are incompetent? What would a supportive friend say?</p>
          </div>
        </div>
      </Card>
      <Textarea placeholder="e.g., One piece of feedback doesn't mean I'm incompetent. My recent project was praised by the team..." className="mb-6 min-h-[120px] rounded-xl border-border" />
      <div className="mb-8">
        <p className="mb-2 text-sm font-medium text-foreground">How much do you believe this new thought?</p>
        <input type="range" min="0" max="100" defaultValue="60" className="w-full accent-primary" />
        <div className="flex justify-between">
          <span className="text-xs text-muted-foreground">Not at all</span>
          <span className="text-xs text-muted-foreground">Completely</span>
        </div>
      </div>
      <Button className="h-14 w-full rounded-2xl text-base font-semibold" onClick={() => setScreen("diary-saved")}>
        Save Entry
        <CheckCircle2 className="ml-2 h-5 w-5" />
      </Button>
    </div>
  )
}

export function WebDiarySavedScreen() {
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
      reframe: "One piece of feedback doesn't define my competence.",
      moodBefore: 3,
      moodAfter: 6,
    }
    addDiaryEntry(entry)
    setSaved(true)
  }

  return (
    <div className="mx-auto flex max-w-lg flex-col items-center p-6 lg:p-8">
      <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-success/10">
        <CheckCircle2 className="h-10 w-10 text-success" />
      </div>
      <h2 className="mb-2 text-2xl font-bold text-foreground">Entry Saved!</h2>
      <p className="mb-8 text-center text-muted-foreground">Great job reflecting on your thoughts.</p>
      <div className="w-full space-y-4">
        <Card className="border-border bg-card p-5">
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
        <Card className="border-border bg-card p-5">
          <h3 className="mb-3 text-sm font-semibold text-muted-foreground">INSIGHTS</h3>
          <div className="space-y-2">
            <div className="flex items-start gap-2">
              <TrendingUp className="mt-0.5 h-4 w-4 text-primary" />
              <p className="text-sm text-foreground">Mind Reading is your most common distortion this week.</p>
            </div>
            <div className="flex items-start gap-2">
              <Lightbulb className="mt-0.5 h-4 w-4 text-accent" />
              <p className="text-sm text-foreground">Work situations trigger 70% of your negative thoughts.</p>
            </div>
          </div>
        </Card>
      </div>
      <div className="mt-8 flex w-full gap-3">
        <Button variant="outline" className="h-12 flex-1 rounded-2xl" onClick={() => setScreen("diary-home")}>View Diary</Button>
        <Button className="h-12 flex-1 rounded-2xl" onClick={() => setScreen("diary-insights")}>See Patterns</Button>
      </div>
    </div>
  )
}

export function WebDiaryInsightsScreen() {
  const { setScreen, diaryEntries } = useApp()

  const emotionCounts: Record<string, number> = {}
  const distortionCounts: Record<string, number> = {}
  diaryEntries.forEach((entry) => {
    entry.emotions.forEach((e) => { emotionCounts[e] = (emotionCounts[e] || 0) + 1 })
    entry.distortions.forEach((d) => { distortionCounts[d] = (distortionCounts[d] || 0) + 1 })
  })
  const topEmotions = Object.entries(emotionCounts).sort(([, a], [, b]) => b - a).slice(0, 5)
  const topDistortions = Object.entries(distortionCounts).sort(([, a], [, b]) => b - a).slice(0, 5)

  return (
    <div className="p-6 lg:p-8">
      <div className="mb-6 flex items-center gap-3">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-home")}>
          <ArrowLeft className="h-4 w-4" />
          Back to Diary
        </button>
      </div>
      <h1 className="mb-6 text-2xl font-bold text-foreground">Pattern Insights</h1>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card className="border-border bg-card p-5">
          <h3 className="mb-4 text-sm font-semibold text-foreground">Mood Trend</h3>
          <div className="flex items-end gap-3">
            {[3, 4, 5, 3, 6, 5, 7].map((val, i) => (
              <div key={i} className="flex flex-1 flex-col items-center gap-1">
                <div className="w-full rounded-t-md bg-primary" style={{ height: `${val * 12}px` }} />
                <span className="text-[10px] text-muted-foreground">{["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"][i]}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="border-border bg-card p-5">
          <h3 className="mb-4 text-sm font-semibold text-foreground">Top Emotions</h3>
          <div className="space-y-3">
            {topEmotions.map(([emotion, count]) => (
              <div key={emotion} className="flex items-center gap-3">
                <span className="w-24 text-sm text-foreground">{emotion}</span>
                <div className="flex-1"><div className="h-5 rounded-full bg-primary" style={{ width: `${(count / diaryEntries.length) * 100}%` }} /></div>
                <span className="text-xs text-muted-foreground">{count}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="border-border bg-card p-5">
          <h3 className="mb-4 text-sm font-semibold text-foreground">Thinking Traps</h3>
          <div className="space-y-3">
            {topDistortions.map(([distortion, count]) => (
              <div key={distortion} className="flex items-center gap-3">
                <span className="w-28 text-sm text-foreground">{distortion}</span>
                <div className="flex-1"><div className="h-5 rounded-full bg-warning" style={{ width: `${(count / diaryEntries.length) * 100}%` }} /></div>
                <span className="text-xs text-muted-foreground">{count}</span>
              </div>
            ))}
          </div>
        </Card>

        <Card className="border-primary/20 bg-primary/5 p-5">
          <div className="flex items-start gap-3">
            <Brain className="mt-0.5 h-5 w-5 text-primary" />
            <div>
              <p className="text-sm font-medium text-foreground">AI Insight</p>
              <p className="text-xs leading-relaxed text-muted-foreground">
                {"Your mood improves an average of 2.5 points after completing thought records. Work-related situations trigger most of your negative thoughts. Consider the \"Setting Boundaries\" CBT session."}
              </p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}

/* ──────────── Diary Detail Data ──────────── */
const diaryTranscripts: Record<string, { role: "ai" | "user"; speaker: string; text: string; time: string }[]> = {
  "1": [
    { role: "ai", speaker: "AI Guide", text: "You noted feeling overwhelmed by work deadlines. Let's explore this together. What was the first thought that came to mind?", time: "0:00" },
    { role: "user", speaker: "You", text: "I can never keep up. Everyone else manages fine but I'm always behind.", time: "0:15" },
    { role: "ai", speaker: "AI Guide", text: "I notice two patterns here -- 'never' is all-or-nothing thinking, and 'everyone else manages fine' is comparison. What evidence challenges these thoughts?", time: "0:28" },
    { role: "user", speaker: "You", text: "Well, I did finish last week's big project on time. And my colleague mentioned she was stressed too.", time: "0:48" },
    { role: "ai", speaker: "AI Guide", text: "Excellent. So the evidence shows you can keep up, and stress is not unique to you. Can you form a balanced thought?", time: "1:05" },
    { role: "user", speaker: "You", text: "I have successfully managed heavy workloads before. I can prioritize and ask for help.", time: "1:22" },
    { role: "ai", speaker: "AI Guide", text: "That is a much more balanced perspective. Notice how your distress level decreased as we worked through this? Your mood shifted from 3 to 6.", time: "1:40" },
  ],
  "2": [
    { role: "ai", speaker: "AI Guide", text: "You wrote about skipping exercise for the third day. What thoughts came up?", time: "0:00" },
    { role: "user", speaker: "You", text: "I have no discipline. What's the point of trying?", time: "0:12" },
    { role: "ai", speaker: "AI Guide", text: "That first thought -- 'I have no discipline' -- is labeling. And 'what's the point' is catastrophizing. Let's challenge them.", time: "0:25" },
    { role: "user", speaker: "You", text: "I guess I exercised four days last week. That takes some discipline.", time: "0:40" },
    { role: "ai", speaker: "AI Guide", text: "Exactly. Missing a few days does not erase a pattern of consistency. What would a compassionate friend say?", time: "0:55" },
    { role: "user", speaker: "You", text: "They would say it's okay to rest and that I can start again with something small, like a short walk.", time: "1:10" },
  ],
}

const diaryAiSummaries: Record<string, { summary: string; insights: string[]; recommendations: string[] }> = {
  "1": {
    summary: "This entry captured a common burnout trigger -- deadline pressure at work. The primary distortions identified were all-or-nothing thinking ('I can never keep up') and social comparison ('everyone else manages fine'). Through the guided reframing process, you successfully generated evidence against both distortions and formed a balanced alternative thought. Your mood improved from 3 to 6, a 100% improvement.",
    insights: [
      "Work deadline pressure is a recurring trigger -- appears in 4 of your last 7 entries",
      "All-or-nothing thinking is your most activated distortion under deadline stress",
      "You respond well to evidence-based challenging -- belief shifts are strongest when you recall concrete past successes",
      "The comparison pattern ('everyone else') tends to amplify your initial distress significantly",
    ],
    recommendations: [
      "When noticing deadline stress, immediately ask: 'When did I handle this before?'",
      "Challenge comparison thoughts by checking in with a colleague about their stress levels",
      "Consider using the 'Setting Boundaries' CBT session to work on saying no to excessive workload",
    ],
  },
  "2": {
    summary: "This entry addressed self-criticism around missed exercise, revealing labeling ('I have no discipline') and catastrophizing ('what's the point'). The reframing process helped you recognize that missing three days doesn't erase a consistent exercise pattern. Self-compassion was the key breakthrough -- framing rest as acceptable rather than failure. Mood improved from 2 to 5.",
    insights: [
      "Self-critical thoughts spike when you break a streak or routine",
      "Labeling ('I have no discipline') is more emotionally damaging than the actual missed exercise",
      "You show strong capacity for self-compassion once guided to consider a friend's perspective",
      "The all-or-nothing pattern ('either I exercise daily or I've failed') feeds the labeling distortion",
    ],
    recommendations: [
      "Set a 'minimum viable exercise' goal (e.g., 5-min walk) for rest days to prevent all-or-nothing thinking",
      "When you miss a routine, ask: 'What would I tell a friend in this situation?'",
      "Track your exercise with a flexible mindset -- count any movement, not just full workouts",
    ],
  },
}

/* ──────────── Web Diary Detail Screen ──────────── */
export function WebDiaryDetailScreen() {
  const { setScreen, selectedDiaryId, diaryEntries } = useApp()
  const [activeTab, setActiveTab] = useState<"overview" | "transcript" | "ai-analysis">("overview")
  const [transcriptExpanded, setTranscriptExpanded] = useState(false)

  const entry = diaryEntries.find((e) => e.id === selectedDiaryId) || diaryEntries[0]
  const transcript = diaryTranscripts[entry.id] || diaryTranscripts["1"]
  const aiData = diaryAiSummaries[entry.id] || diaryAiSummaries["1"]

  const tabs = [
    { id: "overview" as const, label: "Overview", icon: BookOpen },
    { id: "transcript" as const, label: "Transcript", icon: FileText },
    { id: "ai-analysis" as const, label: "AI Analysis", icon: Brain },
  ]

  return (
    <div className="mx-auto max-w-4xl p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("diary-home")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Diary
      </button>

      {/* Header */}
      <div className="mb-6">
        <div className="flex items-start justify-between">
          <div>
            <h1 className="text-2xl font-bold text-foreground">{entry.situation}</h1>
            <div className="mt-2 flex items-center gap-4 text-sm text-muted-foreground">
              <span className="flex items-center gap-1">
                <Calendar className="h-3.5 w-3.5" />
                {new Date(entry.date).toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" })}
              </span>
              <span className="flex items-center gap-1">
                Mood: <span className="font-medium text-foreground">{entry.moodBefore}</span>
                <ArrowRight className="h-3.5 w-3.5 text-success" />
                <span className="font-medium text-success">{entry.moodAfter}</span>
              </span>
            </div>
          </div>
        </div>
        <div className="mt-3 flex flex-wrap gap-1.5">
          {entry.emotions.map((emo) => (
            <Badge key={emo} variant="secondary">{emo}</Badge>
          ))}
          {entry.distortions.map((dist) => (
            <Badge key={dist} variant="outline" className="border-warning/30 bg-warning/5 text-warning-foreground">{dist}</Badge>
          ))}
        </div>
      </div>

      {/* Tab Navigation */}
      <div className="mb-6 flex gap-1 rounded-xl border border-border bg-muted/50 p-1">
        {tabs.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            className={`flex flex-1 items-center justify-center gap-2 rounded-lg px-4 py-2.5 text-sm font-medium transition-all ${
              activeTab === id ? "bg-card text-foreground shadow-sm" : "text-muted-foreground hover:text-foreground"
            }`}
            onClick={() => setActiveTab(id)}
          >
            <Icon className="h-4 w-4" />
            {label}
          </button>
        ))}
      </div>

      {/* Overview Tab */}
      {activeTab === "overview" && (
        <div className="space-y-6">
          <div className="grid gap-4 lg:grid-cols-2">
            <Card className="border-warning/20 bg-warning/5 p-5">
              <p className="mb-1 text-xs font-semibold text-warning-foreground">ORIGINAL THOUGHT</p>
              <p className="text-sm leading-relaxed text-foreground/80">{entry.thoughts}</p>
            </Card>
            <Card className="border-success/20 bg-success/5 p-5">
              <p className="mb-1 text-xs font-semibold text-success">BALANCED REFRAME</p>
              <p className="text-sm leading-relaxed text-foreground/80">{entry.reframe}</p>
            </Card>
          </div>

          <div className="grid gap-4 lg:grid-cols-3">
            <Card className="border-border bg-card p-4 text-center">
              <p className="text-xs text-muted-foreground">Mood Before</p>
              <p className="mt-1 text-3xl font-bold text-foreground">{entry.moodBefore}</p>
            </Card>
            <Card className="border-success/30 bg-success/5 p-4 text-center">
              <p className="text-xs text-muted-foreground">Mood After</p>
              <p className="mt-1 text-3xl font-bold text-success">{entry.moodAfter}</p>
            </Card>
            <Card className="border-border bg-card p-4 text-center">
              <p className="text-xs text-muted-foreground">Improvement</p>
              <p className="mt-1 text-3xl font-bold text-primary">+{entry.moodAfter - entry.moodBefore}</p>
            </Card>
          </div>

          <Card className="border-border bg-card p-5">
            <h3 className="mb-3 text-sm font-semibold text-foreground">Distortions Identified</h3>
            <div className="space-y-2">
              {entry.distortions.map((d) => (
                <div key={d} className="flex items-center gap-3 rounded-lg bg-warning/5 p-3">
                  <AlertCircle className="h-4 w-4 text-warning" />
                  <span className="text-sm text-foreground">{d}</span>
                </div>
              ))}
            </div>
          </Card>
        </div>
      )}

      {/* Transcript Tab */}
      {activeTab === "transcript" && (
        <div className="space-y-4">
          <Card className="border-border bg-card p-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <FileText className="h-4 w-4 text-muted-foreground" />
                <span className="text-sm text-muted-foreground">{transcript.length} messages -- Guided thought record</span>
              </div>
              <button className="flex items-center gap-1 text-xs font-medium text-primary hover:underline" onClick={() => setTranscriptExpanded(!transcriptExpanded)}>
                {transcriptExpanded ? "Collapse" : "Expand All"}
                {transcriptExpanded ? <ChevronUp className="h-3 w-3" /> : <ChevronDown className="h-3 w-3" />}
              </button>
            </div>
          </Card>

          <div className="space-y-3">
            {(transcriptExpanded ? transcript : transcript.slice(0, 4)).map((msg, i) => (
              <div key={i} className={`flex gap-3 ${msg.role === "user" ? "flex-row-reverse" : ""}`}>
                <div className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${msg.role === "ai" ? "bg-primary/10" : "bg-accent/10"}`}>
                  {msg.role === "ai" ? <Brain className="h-4 w-4 text-primary" /> : <MessageCircle className="h-4 w-4 text-accent" />}
                </div>
                <Card className={`flex-1 border-border p-4 ${msg.role === "user" ? "bg-primary/5" : "bg-card"}`}>
                  <div className="mb-1.5 flex items-center justify-between">
                    <span className="text-xs font-semibold text-foreground">{msg.speaker}</span>
                    <span className="text-[10px] text-muted-foreground">{msg.time}</span>
                  </div>
                  <p className="text-sm leading-relaxed text-foreground/80">{msg.text}</p>
                </Card>
              </div>
            ))}
          </div>

          {!transcriptExpanded && transcript.length > 4 && (
            <button className="flex w-full items-center justify-center gap-2 rounded-xl border border-dashed border-border py-3 text-sm text-muted-foreground transition-colors hover:border-primary hover:text-primary" onClick={() => setTranscriptExpanded(true)}>
              <ChevronDown className="h-4 w-4" />
              Show {transcript.length - 4} more messages
            </button>
          )}
        </div>
      )}

      {/* AI Analysis Tab */}
      {activeTab === "ai-analysis" && (
        <div className="space-y-6">
          <Card className="border-border bg-card p-6">
            <div className="flex items-start gap-3">
              <Brain className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <h3 className="text-base font-semibold text-foreground">AI Summary</h3>
                <p className="mt-2 text-sm leading-relaxed text-foreground/80">{aiData.summary}</p>
              </div>
            </div>
          </Card>

          <Card className="border-primary/20 bg-primary/5 p-5">
            <div className="flex items-start gap-3">
              <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-sm font-medium text-foreground">AI Insights</p>
                <p className="text-xs text-muted-foreground">Based on this entry and your overall diary patterns.</p>
              </div>
            </div>
          </Card>

          {aiData.insights.map((insight, i) => (
            <Card key={i} className="border-l-4 border-l-primary border-border bg-card p-5">
              <div className="flex items-start gap-3">
                <Lightbulb className="mt-0.5 h-5 w-5 text-primary" />
                <p className="text-sm leading-relaxed text-foreground/80">{insight}</p>
              </div>
            </Card>
          ))}

          <Card className="border-border bg-card p-5">
            <h4 className="mb-3 text-sm font-semibold text-foreground">Personalized Recommendations</h4>
            <div className="space-y-2.5">
              {aiData.recommendations.map((rec, i) => (
                <div key={i} className="flex items-start gap-2">
                  <div className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-primary" />
                  <p className="text-sm leading-relaxed text-foreground/80">{rec}</p>
                </div>
              ))}
            </div>
          </Card>
        </div>
      )}
    </div>
  )
}
