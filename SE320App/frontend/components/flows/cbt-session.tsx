"use client"

import React, { useState, useEffect, useRef } from "react"
import { useApp } from "@/lib/app-context"
import { BottomNav } from "@/components/flows/dashboard"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import { Textarea } from "@/components/ui/textarea"
import {
  Brain,
  ArrowLeft,
  ArrowRight,
  Play,
  Clock,
  Lock,
  CheckCircle2,
  Star,
  Send,
  Mic,
  Video,
  Lightbulb,
  MessageCircle,
  BarChart3,
  Sparkles,
  Camera,
  LogOut,
  FileText,
  TrendingUp,
  Target,
  BookOpen,
  AlertTriangle,
  ChevronDown,
  ChevronUp,
  History,
  CalendarDays,
  Eye,
} from "lucide-react"

/* ── Shared End Session Button (Mobile) ── */
function EndSessionBtn({ onClick }: { onClick: () => void }) {
  return (
    <button
      className="flex items-center gap-1 rounded-lg border border-destructive/20 bg-destructive/5 px-2 py-1 text-[10px] font-medium text-destructive"
      onClick={onClick}
    >
      <LogOut className="h-3 w-3" />
      End
    </button>
  )
}

/* ──────────── Screen 1: Session Library ──────────── */
export function SessionLibraryScreen() {
  const { setScreen } = useApp()

  const sessions = [
    {
      id: 1,
      title: "Understanding Burnout",
      duration: "20 min",
      status: "completed",
      module: "Foundation",
    },
    {
      id: 2,
      title: "Identifying Thought Patterns",
      duration: "25 min",
      status: "completed",
      module: "Foundation",
    },
    {
      id: 3,
      title: "Cognitive Distortions",
      duration: "25 min",
      status: "completed",
      module: "Core Skills",
    },
    {
      id: 4,
      title: "Thought Challenging",
      duration: "30 min",
      status: "completed",
      module: "Core Skills",
    },
    {
      id: 5,
      title: "Managing Workload Stress",
      duration: "25 min",
      status: "current",
      module: "Application",
    },
    {
      id: 6,
      title: "Setting Boundaries",
      duration: "25 min",
      status: "locked",
      module: "Application",
    },
    {
      id: 7,
      title: "Building Resilience",
      duration: "30 min",
      status: "locked",
      module: "Advanced",
    },
    {
      id: 8,
      title: "Relapse Prevention",
      duration: "25 min",
      status: "locked",
      module: "Advanced",
    },
    {
      id: 9,
      title: "General Discussion",
      duration: "Open",
      status: "available",
      module: "Open Sessions",
    },
  ]

  const modules = [...new Set(sessions.map((s) => s.module))]

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pb-4 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("dashboard")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="flex-1 text-xl font-bold text-foreground">CBT Sessions</h1>
          <button
            className="flex items-center gap-1 rounded-lg border border-border bg-card px-2.5 py-1.5 text-[10px] font-medium text-muted-foreground"
            onClick={() => setScreen("session-history")}
          >
            <History className="h-3 w-3" />
            History
          </button>
        </div>
        <div className="flex items-center gap-3 rounded-xl bg-primary/5 p-3">
          <Progress value={50} className="h-2 flex-1" />
          <span className="text-xs font-medium text-primary">4/8</span>
        </div>
      </div>

      <div className="flex-1 space-y-6 overflow-y-auto px-6 pb-4">
        {modules.map((mod) => (
          <div key={mod}>
            <h3 className="mb-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              {mod}
            </h3>
            <div className="space-y-2">
              {sessions
                .filter((s) => s.module === mod)
                .map((session) => (
                  <button
                    key={session.id}
                    className={`flex w-full items-center gap-3 rounded-xl border p-4 text-left transition-colors ${
                      session.status === "current"
                        ? "border-primary bg-primary/5"
                        : session.status === "available"
                        ? "border-accent bg-accent/5"
                        : session.status === "locked"
                        ? "border-border bg-muted/50 opacity-60"
                        : "border-border bg-card hover:border-primary/50"
                    }`}
                    onClick={() => {
                      if (session.status !== "locked") setScreen("session-intro")
                    }}
                    disabled={session.status === "locked"}
                  >
                    <div
                      className={`flex h-10 w-10 items-center justify-center rounded-xl ${
                        session.status === "completed"
                          ? "bg-success/10"
                          : session.status === "current"
                          ? "bg-primary/10"
                          : session.status === "available"
                          ? "bg-accent/10"
                          : "bg-muted"
                      }`}
                    >
                      {session.status === "completed" ? (
                        <CheckCircle2 className="h-5 w-5 text-success" />
                      ) : session.status === "locked" ? (
                        <Lock className="h-5 w-5 text-muted-foreground" />
                      ) : session.status === "available" ? (
                        <MessageCircle className="h-5 w-5 text-accent" />
                      ) : (
                        <Play className="h-5 w-5 text-primary" />
                      )}
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-medium text-foreground">
                        {session.title}
                      </p>
                      <div className="flex items-center gap-2">
                        <Clock className="h-3 w-3 text-muted-foreground" />
                        <span className="text-xs text-muted-foreground">
                          {session.duration}
                        </span>
                      </div>
                    </div>
                    {session.status === "current" && (
                      <Badge className="bg-primary text-primary-foreground text-xs">
                        Next
                      </Badge>
                    )}
                    {session.status === "available" && (
                      <Badge className="bg-accent text-accent-foreground text-xs">
                        Open
                      </Badge>
                    )}
                  </button>
                ))}
            </div>
          </div>
        ))}
      </div>
      <BottomNav />
    </div>
  )
}

/* ──────────── Screen 2: Session Intro ──────────── */
export function SessionIntroScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background px-6 pt-4">
      <button
        className="mb-6 self-start"
        onClick={() => setScreen("session-library")}
      >
        <ArrowLeft className="h-5 w-5 text-foreground" />
      </button>
      <Badge variant="secondary" className="mb-4 w-fit text-xs">
        Session 5 of 8
      </Badge>
      <h1 className="mb-2 text-2xl font-bold text-foreground">
        Managing Workload Stress
      </h1>
      <p className="mb-6 text-sm leading-relaxed text-muted-foreground">
        Learn to identify and challenge unhelpful thinking patterns related to work overload and develop healthier cognitive strategies.
      </p>

      <Card className="mb-4 border-border bg-card p-4">
        <h3 className="mb-3 text-sm font-semibold text-foreground">Session Objectives</h3>
        <div className="space-y-2.5">
          {[
            "Identify automatic thoughts about workload",
            "Challenge all-or-nothing thinking patterns",
            "Practice cognitive restructuring technique",
            "Create an actionable stress management plan",
          ].map((obj, i) => (
            <div key={i} className="flex items-start gap-2.5">
              <div className="mt-0.5 flex h-5 w-5 items-center justify-center rounded-full bg-primary/10 text-xs font-bold text-primary">
                {i + 1}
              </div>
              <p className="text-sm text-foreground">{obj}</p>
            </div>
          ))}
        </div>
      </Card>

      <Card className="mb-4 border-border bg-card p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Clock className="h-4 w-4 text-muted-foreground" />
            <span className="text-sm text-foreground">Duration</span>
          </div>
          <span className="text-sm font-medium text-foreground">25 minutes</span>
        </div>
      </Card>

      <Card className="mb-6 border-primary/20 bg-primary/5 p-4">
        <div className="flex items-start gap-3">
          <Lightbulb className="mt-0.5 h-5 w-5 text-primary" />
          <div>
            <p className="text-sm font-medium text-foreground">Multi-modal Session</p>
            <p className="text-xs text-muted-foreground">
              This session includes text, voice, and video interactions with your AI therapist.
            </p>
          </div>
        </div>
      </Card>

      <div className="mt-auto pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("session-chat")}
        >
          <Play className="mr-2 h-5 w-5" />
          Begin Session
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 3: AI Chat Session ──────────── */
export function SessionChatScreen() {
  const { setScreen } = useApp()
  const [messages, setMessages] = useState([
    {
      role: "ai",
      text: "Welcome to today's session on managing workload stress. I'm here to guide you through some evidence-based techniques. How are you feeling right now?",
      time: "10:00",
    },
  ])
  const [input, setInput] = useState("")
  const [typing, setTyping] = useState(false)
  const scrollRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: "smooth" })
  }, [messages, typing])

  const aiResponses = [
    "Thank you for sharing that. It sounds like you are carrying quite a heavy load. Let us explore that feeling together. Can you describe a specific moment this week when you felt most overwhelmed?",
    "I can see why that would be stressful. What thoughts were going through your mind in that moment? Try to recall the exact words you were thinking.",
    "That is a very common automatic thought. Let us examine it together. What evidence supports this thought, and what evidence challenges it?",
    "Great insight. Now let us move to a thought-challenging exercise where we will work on reframing this pattern. Click 'Continue' to proceed.",
  ]

  const handleSend = () => {
    if (!input.trim()) return
    const newMessages = [
      ...messages,
      { role: "user", text: input, time: "Now" },
    ]
    setMessages(newMessages)
    setInput("")
    setTyping(true)

    setTimeout(() => {
      setTyping(false)
      const responseIdx = Math.min(newMessages.filter((m) => m.role === "user").length - 1, aiResponses.length - 1)
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: aiResponses[responseIdx], time: "Now" },
      ])
    }, 1500)
  }

  const userMessageCount = messages.filter((m) => m.role === "user").length

  return (
    <div className="flex h-full flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-intro")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/10">
          <Brain className="h-5 w-5 text-primary" />
        </div>
        <div className="flex-1">
          <p className="text-sm font-semibold text-foreground">Dr. MindBridge</p>
          <p className="text-xs text-success">Online</p>
        </div>
        <EndSessionBtn onClick={() => setScreen("session-completion")} />
        <Progress value={(userMessageCount / 4) * 100} className="h-1.5 w-12" />
      </div>

      {/* Messages */}
      <div ref={scrollRef} className="flex-1 space-y-4 overflow-y-auto px-4 py-4">
        {messages.map((msg, i) => (
          <div
            key={i}
            className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}
          >
            <div
              className={`max-w-[80%] rounded-2xl px-4 py-3 ${
                msg.role === "user"
                  ? "bg-primary text-primary-foreground"
                  : "bg-secondary text-secondary-foreground"
              }`}
            >
              <p className="text-sm leading-relaxed">{msg.text}</p>
            </div>
          </div>
        ))}
        {typing && (
          <div className="flex justify-start">
            <div className="flex items-center gap-1 rounded-2xl bg-secondary px-4 py-3">
              <div className="h-2 w-2 animate-bounce rounded-full bg-muted-foreground" />
              <div className="h-2 w-2 animate-bounce rounded-full bg-muted-foreground [animation-delay:0.1s]" />
              <div className="h-2 w-2 animate-bounce rounded-full bg-muted-foreground [animation-delay:0.2s]" />
            </div>
          </div>
        )}
      </div>

      {/* Continue to next screen after enough messages */}
      {userMessageCount >= 3 && (
        <div className="px-4 pb-2">
          <Button
            className="h-10 w-full rounded-xl text-sm font-semibold"
            onClick={() => setScreen("session-multimodal")}
          >
            Continue to Exercise
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </div>
      )}

      {/* Input */}
      <div className="flex items-end gap-2 border-t border-border bg-card px-4 py-3">
        <button className="flex h-10 w-10 items-center justify-center rounded-full bg-secondary text-muted-foreground" aria-label="Camera">
          <Camera className="h-5 w-5" />
        </button>
        <div className="flex-1">
          <Textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Type your response..."
            className="min-h-[40px] max-h-[100px] resize-none rounded-xl border-border"
            rows={1}
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault()
                handleSend()
              }
            }}
          />
        </div>
        <button
          className="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-primary-foreground"
          onClick={handleSend}
          aria-label="Send message"
        >
          <Send className="h-4 w-4" />
        </button>
      </div>
    </div>
  )
}

/* ──────────── Screen 4: Multi-modal Capture ──────────── */
export function SessionMultimodalScreen() {
  const { setScreen } = useApp()
  const [selectedMode, setSelectedMode] = useState<"text" | "voice" | "video">("text")
  const [recording, setRecording] = useState(false)

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-chat")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="flex-1 text-base font-semibold text-foreground">Express Yourself</h1>
        <EndSessionBtn onClick={() => setScreen("session-completion")} />
      </div>

      <div className="flex-1 px-6 pt-6">
        <p className="mb-6 text-sm text-muted-foreground">
          Describe a recent work situation that stressed you. Choose your preferred format:
        </p>

        <div className="mb-6 flex gap-2">
          {[
            { mode: "text" as const, icon: MessageCircle, label: "Text" },
            { mode: "voice" as const, icon: Mic, label: "Voice" },
            { mode: "video" as const, icon: Video, label: "Video" },
          ].map(({ mode, icon: Icon, label }) => (
            <button
              key={mode}
              className={`flex flex-1 flex-col items-center gap-1.5 rounded-xl border p-4 transition-colors ${
                selectedMode === mode
                  ? "border-primary bg-primary/5"
                  : "border-border bg-card"
              }`}
              onClick={() => setSelectedMode(mode)}
            >
              <Icon
                className={`h-6 w-6 ${
                  selectedMode === mode ? "text-primary" : "text-muted-foreground"
                }`}
              />
              <span
                className={`text-xs font-medium ${
                  selectedMode === mode ? "text-primary" : "text-muted-foreground"
                }`}
              >
                {label}
              </span>
            </button>
          ))}
        </div>

        {selectedMode === "text" && (
          <Textarea
            placeholder="Describe what happened, how you felt, and what you were thinking..."
            className="min-h-[160px] rounded-xl border-border"
          />
        )}

        {selectedMode === "voice" && (
          <div className="flex flex-col items-center gap-4 rounded-xl border border-border bg-card p-8">
            <button
              className={`flex h-20 w-20 items-center justify-center rounded-full transition-colors ${
                recording ? "bg-destructive/10 animate-pulse" : "bg-primary/10"
              }`}
              onClick={() => setRecording(!recording)}
              aria-label={recording ? "Stop recording" : "Start recording"}
            >
              <Mic
                className={`h-8 w-8 ${recording ? "text-destructive" : "text-primary"}`}
              />
            </button>
            <p className="text-sm text-muted-foreground">
              {recording ? "Recording... Tap to stop" : "Tap to start recording"}
            </p>
            {recording && (
              <div className="flex items-center gap-1">
                {[...Array(12)].map((_, i) => (
                  <div
                    key={i}
                    className="w-1 animate-pulse rounded-full bg-primary"
                    style={{
                      height: `${Math.random() * 24 + 8}px`,
                      animationDelay: `${i * 0.08}s`,
                    }}
                  />
                ))}
              </div>
            )}
          </div>
        )}

        {selectedMode === "video" && (
          <div className="flex flex-col items-center gap-4 rounded-xl border border-border bg-foreground/5 p-8">
            <div className="flex h-32 w-full items-center justify-center rounded-lg bg-foreground/10">
              <Video className="h-12 w-12 text-muted-foreground" />
            </div>
            <p className="text-sm text-muted-foreground">
              Record a short video of yourself describing the situation
            </p>
            <Button variant="outline" className="rounded-xl">
              <Camera className="mr-2 h-4 w-4" />
              Start Recording
            </Button>
          </div>
        )}
      </div>

      <div className="px-6 pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("session-scenario")}
        >
          Submit & Continue
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 5: Interactive Scenario ──────────── */
export function SessionScenarioScreen() {
  const { setScreen } = useApp()
  const [selectedChoice, setSelectedChoice] = useState<number | null>(null)

  const choices = [
    {
      text: "I need to do everything myself or it will not be done right.",
      type: "unhelpful",
      feedback: "This is all-or-nothing thinking. It puts unrealistic pressure on yourself.",
    },
    {
      text: "I can prioritize and delegate some tasks to manage this.",
      type: "helpful",
      feedback: "Great balanced thinking! This recognizes your capacity while being realistic.",
    },
    {
      text: "Everyone else can handle this, something is wrong with me.",
      type: "unhelpful",
      feedback: "This is personalization and comparison. Everyone has different capacities.",
    },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-multimodal")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="flex-1 text-base font-semibold text-foreground">Interactive Scenario</h1>
        <EndSessionBtn onClick={() => setScreen("session-completion")} />
        <Badge variant="secondary" className="text-xs">
          Step 3/5
        </Badge>
      </div>

      <div className="flex-1 overflow-y-auto px-6 pt-6">
        <Card className="mb-6 border-primary/20 bg-primary/5 p-4">
          <p className="text-sm font-medium text-foreground">Scenario:</p>
          <p className="mt-1 text-sm leading-relaxed text-foreground/80">
            Your manager just assigned three new high-priority tasks while you are already
            behind on your current deadlines. You feel your heart racing as you look at
            your to-do list.
          </p>
        </Card>

        <h3 className="mb-3 text-sm font-semibold text-foreground">
          What is your first thought?
        </h3>

        <div className="space-y-3">
          {choices.map((choice, i) => (
            <button
              key={i}
              className={`w-full rounded-xl border p-4 text-left transition-colors ${
                selectedChoice === i
                  ? choice.type === "helpful"
                    ? "border-success bg-success/5"
                    : "border-warning bg-warning/5"
                  : "border-border bg-card hover:border-primary/50"
              }`}
              onClick={() => setSelectedChoice(i)}
            >
              <p className="text-sm text-foreground">{choice.text}</p>
              {selectedChoice === i && (
                <div className="mt-3 flex items-start gap-2 border-t border-border/50 pt-3">
                  <Lightbulb className="mt-0.5 h-4 w-4 text-primary" />
                  <p className="text-xs leading-relaxed text-muted-foreground">
                    {choice.feedback}
                  </p>
                </div>
              )}
            </button>
          ))}
        </div>
      </div>

      {selectedChoice !== null && (
        <div className="px-6 pb-6 pt-4">
          <Button
            className="h-14 w-full rounded-2xl text-base font-semibold"
            onClick={() => setScreen("session-thought-challenge")}
          >
            Continue to Thought Challenge
            <ArrowRight className="ml-2 h-5 w-5" />
          </Button>
        </div>
      )}
    </div>
  )
}

/* ──────────── Screen 6: Thought Challenge ──────────── */
export function SessionThoughtChallengeScreen() {
  const { setScreen } = useApp()
  const [step, setStep] = useState(0)

  const steps = [
    {
      title: "Identify the Thought",
      desc: "I need to do everything perfectly or I'm a failure.",
      prompt: "What cognitive distortion do you notice here?",
    },
    {
      title: "Examine the Evidence",
      desc: "What evidence supports or contradicts this thought?",
      prompt: "Think about times you have successfully delegated or managed heavy workloads.",
    },
    {
      title: "Generate Alternative",
      desc: "Create a more balanced thought to replace the distortion.",
      prompt: "What would you tell a friend in this situation?",
    },
    {
      title: "Rate Your Belief",
      desc: "How much do you believe the original thought now?",
      prompt: "Use the slider to indicate your belief level.",
    },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-scenario")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="flex-1 text-base font-semibold text-foreground">Thought Challenge</h1>
        <EndSessionBtn onClick={() => setScreen("session-completion")} />
        <Badge variant="secondary" className="text-xs">
          Step 4/5
        </Badge>
      </div>

      <div className="flex-1 overflow-y-auto px-6 pt-6">
        <div className="mb-6">
          <div className="mb-4 flex gap-1">
            {steps.map((_, i) => (
              <div
                key={i}
                className={`h-1.5 flex-1 rounded-full ${
                  i <= step ? "bg-primary" : "bg-border"
                }`}
              />
            ))}
          </div>
          <h2 className="mb-1 text-lg font-bold text-foreground">
            {steps[step].title}
          </h2>
          <p className="text-sm text-muted-foreground">{steps[step].desc}</p>
        </div>

        <Card className="mb-4 border-border bg-card p-4">
          <div className="flex items-start gap-3">
            <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
            <p className="text-sm leading-relaxed text-foreground/80">
              {steps[step].prompt}
            </p>
          </div>
        </Card>

        {step < 3 && (
          <Textarea
            placeholder="Write your response..."
            className="min-h-[120px] rounded-xl border-border"
          />
        )}

        {step === 3 && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-sm text-muted-foreground">Not at all</span>
              <span className="text-sm text-muted-foreground">Completely</span>
            </div>
            <input
              type="range"
              min="0"
              max="100"
              defaultValue="30"
              className="w-full accent-primary"
            />
            <Card className="border-success/20 bg-success/5 p-4">
              <div className="flex items-start gap-2">
                <CheckCircle2 className="mt-0.5 h-5 w-5 text-success" />
                <div>
                  <p className="text-sm font-medium text-foreground">
                    Great progress!
                  </p>
                  <p className="text-xs text-muted-foreground">
                    Your belief in the distorted thought has decreased. This is a
                    key indicator of cognitive restructuring.
                  </p>
                </div>
              </div>
            </Card>
          </div>
        )}
      </div>

      <div className="px-6 pb-6 pt-4">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => {
            if (step < steps.length - 1) {
              setStep(step + 1)
            } else {
              setScreen("session-progress")
            }
          }}
        >
          {step < steps.length - 1 ? "Next Step" : "View Progress"}
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 7: Session Progress Tracker ──────────── */
export function SessionProgressScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background">
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-thought-challenge")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="flex-1 text-base font-semibold text-foreground">Session Progress</h1>
        <EndSessionBtn onClick={() => setScreen("session-completion")} />
      </div>

      <div className="flex-1 overflow-y-auto px-6 pt-6">
        <div className="mb-6 text-center">
          <div className="relative mx-auto mb-4 flex h-32 w-32 items-center justify-center">
            <svg className="h-32 w-32 -rotate-90" viewBox="0 0 120 120">
              <circle
                cx="60"
                cy="60"
                r="52"
                fill="none"
                stroke="currentColor"
                strokeWidth="8"
                className="text-border"
              />
              <circle
                cx="60"
                cy="60"
                r="52"
                fill="none"
                stroke="currentColor"
                strokeWidth="8"
                strokeDasharray={`${0.8 * 327} ${327}`}
                strokeLinecap="round"
                className="text-primary"
              />
            </svg>
            <span className="absolute text-2xl font-bold text-foreground">80%</span>
          </div>
          <h2 className="text-lg font-bold text-foreground">Almost There!</h2>
          <p className="text-sm text-muted-foreground">Session 5 of 8 in progress</p>
        </div>

        <div className="space-y-3">
          {[
            { label: "Introduction", status: "done", icon: CheckCircle2 },
            { label: "AI Conversation", status: "done", icon: CheckCircle2 },
            { label: "Multi-modal Capture", status: "done", icon: CheckCircle2 },
            { label: "Interactive Scenario", status: "done", icon: CheckCircle2 },
            { label: "Thought Challenge", status: "done", icon: CheckCircle2 },
            { label: "Summary & Reflection", status: "current", icon: BarChart3 },
          ].map(({ label, status, icon: Icon }) => (
            <div
              key={label}
              className={`flex items-center gap-3 rounded-xl border p-3 ${
                status === "current"
                  ? "border-primary bg-primary/5"
                  : "border-border bg-card"
              }`}
            >
              <Icon
                className={`h-5 w-5 ${
                  status === "done" ? "text-success" : "text-primary"
                }`}
              />
              <span className="flex-1 text-sm font-medium text-foreground">{label}</span>
              {status === "done" && (
                <span className="text-xs text-success">Complete</span>
              )}
              {status === "current" && (
                <span className="text-xs text-primary">In Progress</span>
              )}
            </div>
          ))}
        </div>
      </div>

      <div className="px-6 pb-6 pt-4">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("session-completion")}
        >
          Complete Session
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ─────���────── Screen 8: Session Completion & Summary ──────────── */
export function SessionCompletionScreen() {
  const { setScreen, incrementSessions } = useApp()
  const [celebrated, setCelebrated] = useState(false)
  const [activeTab, setActiveTab] = useState<"summary" | "transcript" | "insights" | "recs">("summary")
  const [transcriptExpanded, setTranscriptExpanded] = useState(false)

  useEffect(() => {
    if (!celebrated) {
      incrementSessions()
      setCelebrated(true)
    }
  }, [celebrated, incrementSessions])

  const transcript = [
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "Welcome to today's session on managing workload stress. How are you feeling right now?", time: "0:00" },
    { role: "user" as const, speaker: "You", text: "I've been feeling really overwhelmed by my workload lately. Too many deadlines.", time: "0:32" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "It sounds like you are carrying quite a heavy load. Can you describe a specific moment this week when you felt most overwhelmed?", time: "0:48" },
    { role: "user" as const, speaker: "You", text: "Yesterday my manager assigned three new tasks when I was already behind. I thought: I just can't handle this.", time: "1:25" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "What thoughts were going through your mind in that moment? Try to recall the exact words.", time: "1:42" },
    { role: "user" as const, speaker: "You", text: "I need to do everything myself or it won't be done right. If I can't manage this, something must be wrong with me.", time: "2:15" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "The first thought is all-or-nothing thinking. The second is personalization. What evidence supports or challenges these?", time: "2:30" },
    { role: "user" as const, speaker: "You", text: "I have successfully delegated before. My colleague said she feels overwhelmed too. So maybe it's not just me.", time: "3:45" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "Excellent. You've identified evidence that contradicts both automatic thoughts. Feeling overwhelmed is universal -- not a personal failing.", time: "4:02" },
  ]

  const insights = [
    { title: "All-or-Nothing Thinking", severity: "moderate" as const, desc: "Strong tendency toward binary thinking about work performance." },
    { title: "Personalization", severity: "mild" as const, desc: "Attributed overwhelm as a personal deficiency rather than a normal response." },
    { title: "Self-Awareness Strength", severity: "positive" as const, desc: "Excellent ability to step back and examine thoughts objectively." },
    { title: "Physical Stress Response", severity: "moderate" as const, desc: "Chest tightness and tension correlated with cognitive distortions." },
  ]

  const recommendations = [
    { icon: BookOpen, text: "Identify one all-or-nothing thought each day and write a balanced alternative" },
    { icon: Target, text: "Keep a delegation log -- track one task per day you could or did delegate" },
    { icon: TrendingUp, text: "Before Session 6, notice one situation where you wanted to set a boundary but didn't" },
    { icon: AlertTriangle, text: "If chest tightness occurs 3+ times daily, use the breathing exercise in Crisis Support" },
  ]

  const tabs = [
    { id: "summary" as const, label: "Summary", icon: BarChart3 },
    { id: "transcript" as const, label: "Transcript", icon: FileText },
    { id: "insights" as const, label: "Insights", icon: Lightbulb },
    { id: "recs" as const, label: "Actions", icon: Target },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <div className="flex h-8 w-8 items-center justify-center rounded-full bg-success/10">
          <CheckCircle2 className="h-4 w-4 text-success" />
        </div>
        <div className="flex-1">
          <p className="text-sm font-semibold text-foreground">Session Complete</p>
          <p className="text-[10px] text-muted-foreground">Managing Workload Stress</p>
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

      {/* Tab Content */}
      <div className="flex-1 overflow-y-auto px-4 py-4">
        {/* Summary Tab */}
        {activeTab === "summary" && (
          <div className="space-y-4">
            <Card className="border-border bg-card p-4">
              <div className="flex items-start gap-2">
                <Brain className="mt-0.5 h-4 w-4 text-primary" />
                <div>
                  <h3 className="text-xs font-semibold text-foreground">AI Summary</h3>
                  <p className="mt-1 text-xs leading-relaxed text-foreground/80">
                    A productive 24-minute session focused on identifying and challenging cognitive distortions related to workload stress. You successfully engaged in cognitive restructuring, transitioning from anxiety and self-criticism to a more balanced perspective.
                  </p>
                </div>
              </div>
            </Card>

            <div className="grid grid-cols-2 gap-2">
              {[
                { label: "Duration", value: "24 min" },
                { label: "Exercises", value: "3/3" },
                { label: "Distortions", value: "2 found" },
                { label: "Belief Change", value: "-40%", highlight: true },
              ].map(({ label, value, highlight }) => (
                <Card key={label} className={`border-border p-3 ${highlight ? "border-success/30 bg-success/5" : "bg-card"}`}>
                  <p className="text-[10px] text-muted-foreground">{label}</p>
                  <p className={`text-sm font-bold ${highlight ? "text-success" : "text-foreground"}`}>{value}</p>
                </Card>
              ))}
            </div>

            <Card className="border-border bg-card p-4">
              <h3 className="mb-2 text-xs font-semibold text-foreground">Emotional Arc</h3>
              <div className="flex items-center gap-2">
                <div className="flex items-center gap-1 rounded-full bg-destructive/10 px-2 py-0.5">
                  <div className="h-1.5 w-1.5 rounded-full bg-destructive" />
                  <span className="text-[10px] font-medium text-destructive">Tense</span>
                </div>
                <div className="h-px flex-1 bg-gradient-to-r from-destructive/30 via-warning/30 to-success/30" />
                <div className="flex items-center gap-1 rounded-full bg-success/10 px-2 py-0.5">
                  <div className="h-1.5 w-1.5 rounded-full bg-success" />
                  <span className="text-[10px] font-medium text-success">Balanced</span>
                </div>
              </div>
            </Card>

            <Card className="border-border bg-card p-4">
              <h3 className="mb-2 text-xs font-semibold text-foreground">Techniques Applied</h3>
              <div className="space-y-2">
                {["Socratic Questioning", "Cognitive Restructuring", "Evidence Gathering"].map((t, i) => (
                  <div key={i} className="flex items-center gap-2">
                    <div className="flex h-5 w-5 items-center justify-center rounded-full bg-primary/10 text-[10px] font-bold text-primary">{i + 1}</div>
                    <p className="text-xs text-foreground">{t}</p>
                  </div>
                ))}
              </div>
            </Card>
          </div>
        )}

        {/* Transcript Tab */}
        {activeTab === "transcript" && (
          <div className="space-y-3">
            <p className="text-[10px] text-muted-foreground">{transcript.length} messages -- 24 min</p>
            {(transcriptExpanded ? transcript : transcript.slice(0, 4)).map((msg, i) => (
              <div key={i} className="flex gap-2">
                <div className={`flex h-6 w-6 shrink-0 items-center justify-center rounded-full ${
                  msg.role === "ai" ? "bg-primary/10" : "bg-accent/10"
                }`}>
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
            {!transcriptExpanded && transcript.length > 4 && (
              <button
                className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-border py-2 text-[10px] text-muted-foreground"
                onClick={() => setTranscriptExpanded(true)}
              >
                <ChevronDown className="h-3 w-3" />
                Show {transcript.length - 4} more
              </button>
            )}
            {transcriptExpanded && (
              <button
                className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-border py-2 text-[10px] text-muted-foreground"
                onClick={() => setTranscriptExpanded(false)}
              >
                <ChevronUp className="h-3 w-3" />
                Collapse
              </button>
            )}
          </div>
        )}

        {/* Insights Tab */}
        {activeTab === "insights" && (
          <div className="space-y-3">
            <Card className="border-primary/20 bg-primary/5 p-3">
              <div className="flex items-start gap-2">
                <Sparkles className="mt-0.5 h-4 w-4 text-primary" />
                <p className="text-[10px] text-muted-foreground">AI-generated insights based on your conversation and responses.</p>
              </div>
            </Card>
            {insights.map((item, i) => (
              <Card key={i} className={`border-border bg-card p-3 ${
                item.severity === "positive" ? "border-l-2 border-l-success" :
                item.severity === "moderate" ? "border-l-2 border-l-warning" : "border-l-2 border-l-primary"
              }`}>
                <div className="flex items-start justify-between gap-2">
                  <h4 className="text-xs font-semibold text-foreground">{item.title}</h4>
                  <span className={`rounded-full px-1.5 py-0.5 text-[9px] font-medium ${
                    item.severity === "positive" ? "bg-success/10 text-success" :
                    item.severity === "moderate" ? "bg-warning/10 text-warning" : "bg-primary/10 text-primary"
                  }`}>
                    {item.severity === "positive" ? "Strength" : item.severity === "moderate" ? "Moderate" : "Mild"}
                  </span>
                </div>
                <p className="mt-1 text-xs leading-relaxed text-foreground/80">{item.desc}</p>
              </Card>
            ))}
          </div>
        )}

        {/* Recommendations Tab */}
        {activeTab === "recs" && (
          <div className="space-y-3">
            <Card className="border-primary/20 bg-primary/5 p-3">
              <div className="flex items-start gap-2">
                <Target className="mt-0.5 h-4 w-4 text-primary" />
                <p className="text-[10px] text-muted-foreground">Personalized action items based on your session.</p>
              </div>
            </Card>
            {recommendations.map(({ icon: Icon, text }, i) => (
              <Card key={i} className="border-border bg-card p-3">
                <div className="flex items-start gap-2.5">
                  <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-lg bg-primary/10">
                    <Icon className="h-3.5 w-3.5 text-primary" />
                  </div>
                  <p className="text-xs leading-relaxed text-foreground/80">{text}</p>
                </div>
              </Card>
            ))}
            <Card className="border-accent/20 bg-accent/5 p-3">
              <div className="flex items-start gap-2">
                <TrendingUp className="mt-0.5 h-4 w-4 text-accent" />
                <div>
                  <p className="text-xs font-semibold text-foreground">Homework</p>
                  <p className="mt-0.5 text-xs leading-relaxed text-foreground/80">Identify one all-or-nothing thought daily and write a balanced alternative in your Thought Diary.</p>
                </div>
              </div>
            </Card>
          </div>
        )}
      </div>

      {/* Bottom CTA */}
      <div className="border-t border-border bg-card px-4 py-3">
        <Button className="h-12 w-full rounded-2xl text-sm font-semibold" onClick={() => setScreen("session-rating")}>
          Rate This Session
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 9: Session Rating ──────────── */
export function SessionRatingScreen() {
  const { setScreen } = useApp()
  const [rating, setRating] = useState(0)
  const [helpful, setHelpful] = useState<string | null>(null)
  const [submitted, setSubmitted] = useState(false)

  if (submitted) {
    return (
      <div className="flex h-full flex-col items-center justify-center bg-background px-6">
        <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-primary/10">
          <Sparkles className="h-10 w-10 text-primary" />
        </div>
        <h2 className="mb-2 text-xl font-bold text-foreground">Thank You!</h2>
        <p className="mb-8 text-center text-sm text-muted-foreground">
          Your feedback helps us improve your therapy experience.
        </p>
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("dashboard")}
        >
          Back to Dashboard
        </Button>
      </div>
    )
  }

  return (
    <div className="flex h-full flex-col bg-background px-6 pt-6">
      <button className="mb-6 self-start" onClick={() => setScreen("session-completion")}>
        <ArrowLeft className="h-5 w-5 text-foreground" />
      </button>
      <h2 className="mb-2 text-2xl font-bold text-foreground">Rate Your Session</h2>
      <p className="mb-8 text-sm text-muted-foreground">
        Your feedback helps us personalize future sessions.
      </p>

      <div className="mb-8">
        <p className="mb-3 text-sm font-medium text-foreground">
          How would you rate this session?
        </p>
        <div className="flex justify-center gap-2">
          {[1, 2, 3, 4, 5].map((star) => (
            <button
              key={star}
              onClick={() => setRating(star)}
              className="p-1"
              aria-label={`Rate ${star} stars`}
            >
              <Star
                className={`h-10 w-10 transition-colors ${
                  star <= rating
                    ? "fill-warning text-warning"
                    : "text-border"
                }`}
              />
            </button>
          ))}
        </div>
      </div>

      <div className="mb-8">
        <p className="mb-3 text-sm font-medium text-foreground">
          What was most helpful?
        </p>
        <div className="flex flex-wrap gap-2">
          {[
            "AI Conversation",
            "Thought Challenge",
            "Scenario Exercise",
            "Key Takeaways",
            "Homework",
          ].map((item) => (
            <button
              key={item}
              className={`rounded-full border px-4 py-2 text-sm transition-colors ${
                helpful === item
                  ? "border-primary bg-primary/10 text-primary"
                  : "border-border bg-card text-muted-foreground"
              }`}
              onClick={() => setHelpful(item)}
            >
              {item}
            </button>
          ))}
        </div>
      </div>

      <div className="mb-6">
        <p className="mb-3 text-sm font-medium text-foreground">
          Additional feedback (optional)
        </p>
        <Textarea
          placeholder="Share your thoughts..."
          className="min-h-[80px] rounded-xl border-border"
        />
      </div>

      <div className="mt-auto pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          disabled={rating === 0}
          onClick={() => setSubmitted(true)}
        >
          Submit Feedback
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Session History Data ──────────── */
const completedSessionsData = [
  { id: 1, title: "Understanding Burnout", date: "2026-01-15", duration: "20 min", module: "Foundation", rating: 5, moodBefore: 3, moodAfter: 6, beliefChange: -30, distortions: ["Catastrophizing", "Overgeneralizing"], summary: "Explored burnout dimensions and identified perfectionism as a key personal trigger.", insights: ["Burnout pattern strongly linked to perfectionism", "Physical symptoms correlate with high-stress work weeks"] },
  { id: 2, title: "Identifying Thought Patterns", date: "2026-01-22", duration: "25 min", module: "Foundation", rating: 4, moodBefore: 4, moodAfter: 7, beliefChange: -35, distortions: ["All-or-Nothing", "Mind Reading"], summary: "Learned to recognize automatic negative thoughts. Practiced thought logging.", insights: ["Tendency to assume worst about colleagues' opinions", "Automatic thoughts strongest on Monday mornings"] },
  { id: 3, title: "Cognitive Distortions", date: "2026-02-01", duration: "25 min", module: "Core Skills", rating: 5, moodBefore: 3, moodAfter: 6, beliefChange: -40, distortions: ["Personalization", "Should Statements"], summary: "Deep dive into cognitive distortions. Identified personalization and should statements.", insights: ["Should statements are second most frequent distortion", "Reframing 'I should' to 'I could' reduced distress by 40%"] },
  { id: 4, title: "Thought Challenging", date: "2026-02-10", duration: "30 min", module: "Core Skills", rating: 4, moodBefore: 4, moodAfter: 7, beliefChange: -45, distortions: ["All-or-Nothing", "Labeling"], summary: "Applied thought record technique to real work scenarios. Practiced balanced thoughts.", insights: ["Ability to generate alternatives improved significantly", "Evidence-based challenges most effective for your thinking style"] },
]

const mobileSessionTranscripts: Record<number, { role: "ai" | "user"; speaker: string; text: string; time: string }[]> = {
  1: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Today we explore what burnout really is. Can you tell me what burnout means to you?", time: "0:00" },
    { role: "user", speaker: "You", text: "I feel like I am constantly running on empty. I wake up dreading work.", time: "0:25" },
    { role: "ai", speaker: "Dr. MindBridge", text: "The Maslach Burnout Inventory identifies three dimensions: exhaustion, depersonalization, reduced accomplishment. Which resonates?", time: "0:42" },
    { role: "user", speaker: "You", text: "Emotional exhaustion for sure. And I catch myself being cynical about projects I used to care about.", time: "1:10" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That cynicism is depersonalization -- a protective mechanism. What triggers the exhaustion most?", time: "1:32" },
    { role: "user", speaker: "You", text: "My inability to say no. I take on every task because I think it won't be done right otherwise.", time: "2:05" },
  ],
  2: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Today we learn to identify automatic thoughts. Think of a stressful moment this week.", time: "0:00" },
    { role: "user", speaker: "You", text: "My team lead asked me to redo a report. I thought: they think my work is terrible.", time: "0:30" },
    { role: "ai", speaker: "Dr. MindBridge", text: "Classic automatic thought. What emotion did it create?", time: "0:48" },
    { role: "user", speaker: "You", text: "Anxiety and shame. I felt my face get hot.", time: "1:12" },
    { role: "ai", speaker: "Dr. MindBridge", text: "The physical response shows how powerful these thoughts are. What evidence challenges the thought?", time: "1:30" },
    { role: "user", speaker: "You", text: "They praised my last three reports. And the feedback was about formatting, not content.", time: "2:00" },
  ],
  3: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Today we dive into cognitive distortions. Have you noticed any patterns since last session?", time: "0:00" },
    { role: "user", speaker: "You", text: "I noticed all-or-nothing thinking. When I make one mistake I think the whole project is ruined.", time: "0:35" },
    { role: "ai", speaker: "Dr. MindBridge", text: "Excellent self-awareness. Another one to watch: personalization -- taking responsibility for things outside your control.", time: "0:55" },
    { role: "user", speaker: "You", text: "When our team missed a deadline I felt it was entirely my fault, even though three people were responsible.", time: "1:20" },
  ],
  4: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Let's put skills into practice. Recall a challenging situation.", time: "0:00" },
    { role: "user", speaker: "You", text: "I got passed over for a project. My thought: I'm not good enough for important work.", time: "0:28" },
    { role: "ai", speaker: "Dr. MindBridge", text: "What cognitive distortion might be at play?", time: "0:45" },
    { role: "user", speaker: "You", text: "Labeling and all-or-nothing -- either I get every project or I'm failing.", time: "1:10" },
    { role: "ai", speaker: "Dr. MindBridge", text: "Can you create a balanced alternative thought?", time: "1:30" },
    { role: "user", speaker: "You", text: "Not getting this project doesn't define my worth. There could be many reasons unrelated to my ability.", time: "1:55" },
  ],
}

/* ──────────── Session History (Mobile) ──────────── */
export function SessionHistoryScreen() {
  const { setScreen, selectSession } = useApp()

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-library")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <h1 className="text-base font-semibold text-foreground">Completed Sessions</h1>
      </div>

      <div className="flex-1 space-y-2 overflow-y-auto px-4 py-4">
        {completedSessionsData.map((session) => (
          <button
            key={session.id}
            className="flex w-full items-center gap-3 rounded-xl border border-border bg-card p-4 text-left transition-colors hover:border-primary/50"
            onClick={() => selectSession(session.id)}
          >
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-success/10">
              <CheckCircle2 className="h-5 w-5 text-success" />
            </div>
            <div className="flex-1 overflow-hidden">
              <p className="text-sm font-medium text-foreground">{session.title}</p>
              <p className="truncate text-xs text-muted-foreground">{session.summary}</p>
              <div className="mt-1 flex items-center gap-3">
                <span className="flex items-center gap-1 text-[10px] text-muted-foreground">
                  <CalendarDays className="h-2.5 w-2.5" />
                  {new Date(session.date).toLocaleDateString("en-US", { month: "short", day: "numeric" })}
                </span>
                <span className="flex items-center gap-0.5">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star key={i} className={`h-2.5 w-2.5 ${i < session.rating ? "fill-primary text-primary" : "text-border"}`} />
                  ))}
                </span>
                <span className="text-[10px] font-medium text-success">{session.beliefChange}%</span>
              </div>
            </div>
            <Eye className="h-4 w-4 text-muted-foreground" />
          </button>
        ))}
      </div>
    </div>
  )
}

/* ──────────── Session Detail (Mobile) ──────────── */
export function SessionDetailScreen() {
  const { setScreen, selectedSessionId } = useApp()
  const [activeTab, setActiveTab] = useState<"summary" | "transcript" | "insights">("summary")
  const [transcriptExpanded, setTranscriptExpanded] = useState(false)

  const session = completedSessionsData.find((s) => s.id === selectedSessionId) || completedSessionsData[0]
  const transcript = mobileSessionTranscripts[session.id] || []

  const tabs = [
    { id: "summary" as const, label: "Summary", icon: BarChart3 },
    { id: "transcript" as const, label: "Transcript", icon: FileText },
    { id: "insights" as const, label: "Insights", icon: Lightbulb },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      {/* Header */}
      <div className="flex items-center gap-3 border-b border-border bg-card px-4 py-3">
        <button onClick={() => setScreen("session-history")}>
          <ArrowLeft className="h-5 w-5 text-foreground" />
        </button>
        <div className="flex-1 overflow-hidden">
          <p className="truncate text-sm font-semibold text-foreground">{session.title}</p>
          <p className="text-[10px] text-muted-foreground">{new Date(session.date).toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" })}</p>
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
        {activeTab === "summary" && (
          <div className="space-y-3">
            <Card className="border-border bg-card p-4">
              <div className="flex items-start gap-2">
                <Brain className="mt-0.5 h-4 w-4 text-primary" />
                <div>
                  <h3 className="text-xs font-semibold text-foreground">AI Summary</h3>
                  <p className="mt-1 text-xs leading-relaxed text-foreground/80">{session.summary}</p>
                </div>
              </div>
            </Card>
            <div className="grid grid-cols-2 gap-2">
              <Card className="border-border bg-card p-3">
                <p className="text-[10px] text-muted-foreground">Mood Shift</p>
                <p className="text-sm font-bold text-foreground">{session.moodBefore} <ArrowRight className="inline h-3 w-3 text-success" /> <span className="text-success">{session.moodAfter}</span></p>
              </Card>
              <Card className="border-success/30 bg-success/5 p-3">
                <p className="text-[10px] text-muted-foreground">Belief Change</p>
                <p className="text-sm font-bold text-success">{session.beliefChange}%</p>
              </Card>
            </div>
            <Card className="border-border bg-card p-3">
              <p className="mb-2 text-[10px] font-semibold text-muted-foreground">DISTORTIONS</p>
              <div className="flex flex-wrap gap-1">
                {session.distortions.map((d) => (
                  <Badge key={d} variant="outline" className="border-warning/30 bg-warning/5 text-[10px] text-warning-foreground">{d}</Badge>
                ))}
              </div>
            </Card>
            <Card className="border-border bg-card p-3">
              <p className="mb-1 text-[10px] font-semibold text-muted-foreground">RATING</p>
              <div className="flex items-center gap-0.5">
                {Array.from({ length: 5 }).map((_, i) => (
                  <Star key={i} className={`h-4 w-4 ${i < session.rating ? "fill-primary text-primary" : "text-border"}`} />
                ))}
              </div>
            </Card>
          </div>
        )}

        {activeTab === "transcript" && (
          <div className="space-y-3">
            <p className="text-[10px] text-muted-foreground">{transcript.length} messages -- {session.duration}</p>
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

        {activeTab === "insights" && (
          <div className="space-y-3">
            <Card className="border-primary/20 bg-primary/5 p-3">
              <div className="flex items-start gap-2">
                <Sparkles className="mt-0.5 h-4 w-4 text-primary" />
                <p className="text-[10px] text-muted-foreground">AI-generated insights from this session.</p>
              </div>
            </Card>
            {session.insights.map((insight, i) => (
              <Card key={i} className="border-l-2 border-l-primary border-border bg-card p-3">
                <div className="flex items-start gap-2">
                  <Lightbulb className="mt-0.5 h-3.5 w-3.5 text-primary" />
                  <p className="text-xs leading-relaxed text-foreground/80">{insight}</p>
                </div>
              </Card>
            ))}
            <Card className="border-border bg-card p-3">
              <p className="mb-2 text-[10px] font-semibold text-muted-foreground">DISTORTIONS FOUND</p>
              {session.distortions.map((d, i) => (
                <div key={i} className="mb-1 flex items-center gap-2 rounded-lg bg-warning/5 p-2">
                  <AlertTriangle className="h-3 w-3 text-warning" />
                  <span className="text-xs text-foreground">{d}</span>
                </div>
              ))}
            </Card>
          </div>
        )}
      </div>
    </div>
  )
}
