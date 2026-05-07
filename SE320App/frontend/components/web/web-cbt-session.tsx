"use client"

import React, { useState, useEffect, useRef, useCallback } from "react"
import { useApp } from "@/lib/app-context"
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
  MicOff,
  Video,
  VideoOff,
  Lightbulb,
  MessageCircle,
  BarChart3,
  Sparkles,
  Camera,
  CameraOff,
  Square,
  Volume2,
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

/* ── Shared End Session Button ── */
function EndSessionButton({ onClick }: { onClick: () => void }) {
  return (
    <button
      className="flex items-center gap-1.5 rounded-lg border border-destructive/20 bg-destructive/5 px-3 py-1.5 text-xs font-medium text-destructive transition-colors hover:bg-destructive/10"
      onClick={onClick}
    >
      <LogOut className="h-3.5 w-3.5" />
      End Session
    </button>
  )
}

/* ──────────── Screen 1: Session Library (Web) ──────────── */
export function WebSessionLibraryScreen() {
  const { setScreen, sessions: backendSessions, selectSession } = useApp()

  const sessions = [
    { id: 1, title: "Understanding Burnout", duration: "20 min", status: "completed", module: "Foundation" },
    { id: 2, title: "Identifying Thought Patterns", duration: "25 min", status: "completed", module: "Foundation" },
    { id: 3, title: "Cognitive Distortions", duration: "25 min", status: "completed", module: "Core Skills" },
    { id: 4, title: "Thought Challenging", duration: "30 min", status: "completed", module: "Core Skills" },
    { id: 5, title: "Managing Workload Stress", duration: "25 min", status: "current", module: "Application" },
    { id: 6, title: "Setting Boundaries", duration: "25 min", status: "locked", module: "Application" },
    { id: 7, title: "Building Resilience", duration: "30 min", status: "locked", module: "Advanced" },
    { id: 8, title: "Relapse Prevention", duration: "25 min", status: "locked", module: "Advanced" },
    { id: 9, title: "General Discussion", duration: "Open", status: "available", module: "Open Sessions" },
  ]

  const visibleSessions = backendSessions.length > 0
    ? backendSessions.map((session, index) => ({
        id: session.id ?? session.sessionId ?? index + 1,
        title: session.title ?? session.name ?? `Session ${index + 1}`,
        duration: session.duration ?? (session.durationMinutes ? `${session.durationMinutes} min` : "Open"),
        status: session.status?.toLowerCase() ?? "available",
        module: session.module ?? "Backend Sessions",
      }))
    : sessions

  const modules = [...new Set(visibleSessions.map((s) => s.module))]

  return (
    <div className="p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground">CBT Sessions</h1>
          <p className="text-sm text-muted-foreground">Your evidence-based therapy program</p>
        </div>
        <div className="flex items-center gap-3">
          <Button variant="outline" className="rounded-xl" onClick={() => setScreen("session-history")}>
            <History className="mr-2 h-4 w-4" />
            Completed Sessions
          </Button>
          <div className="flex items-center gap-3 rounded-xl bg-primary/5 px-4 py-2">
            <Progress value={50} className="h-2 w-24" />
            <span className="text-sm font-medium text-primary">4/8 complete</span>
          </div>
        </div>
      </div>

      <div className="grid gap-8 lg:grid-cols-2">
        {modules.map((mod) => (
          <div key={mod}>
            <h3 className="mb-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">{mod}</h3>
            <div className="space-y-2">
              {visibleSessions.filter((s) => s.module === mod).map((session) => (
                <button
                  key={session.id}
                  className={`flex w-full items-center gap-4 rounded-xl border p-4 text-left transition-all ${
                    session.status === "current"
                      ? "border-primary bg-primary/5 shadow-sm"
                      : session.status === "available"
                      ? "border-accent bg-accent/5 shadow-sm"
                      : session.status === "locked"
                      ? "border-border bg-muted/50 opacity-60"
                      : "border-border bg-card hover:border-primary/50 hover:shadow-sm"
                  }`}
                  onClick={() => {
                    if (session.status !== "locked") {
                      selectSession(session.id)
                      setScreen("session-intro")
                    }
                  }}
                  disabled={session.status === "locked"}
                >
                  <div className={`flex h-12 w-12 items-center justify-center rounded-xl ${
                    session.status === "completed" ? "bg-success/10" : session.status === "current" ? "bg-primary/10" : session.status === "available" ? "bg-accent/10" : "bg-muted"
                  }`}>
                    {session.status === "completed" ? (
                      <CheckCircle2 className="h-6 w-6 text-success" />
                    ) : session.status === "locked" ? (
                      <Lock className="h-6 w-6 text-muted-foreground" />
                    ) : session.status === "available" ? (
                      <MessageCircle className="h-6 w-6 text-accent" />
                    ) : (
                      <Play className="h-6 w-6 text-primary" />
                    )}
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-foreground">{session.title}</p>
                    <div className="flex items-center gap-2">
                      <Clock className="h-3 w-3 text-muted-foreground" />
                      <span className="text-xs text-muted-foreground">{session.duration}</span>
                    </div>
                  </div>
                  {session.status === "current" && (
                    <Badge className="bg-primary text-primary-foreground text-xs">Next</Badge>
                  )}
                  {session.status === "available" && (
                    <Badge className="bg-accent text-accent-foreground text-xs">Open</Badge>
                  )}
                </button>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

/* ──────────── Screen 2: Session Intro (Web) ──────────── */
export function WebSessionIntroScreen() {
  const { setScreen, beginSession, selectedSessionId } = useApp()
  return (
    <div className="mx-auto max-w-3xl p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-library")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Sessions
      </button>

      <Badge variant="secondary" className="mb-4 text-xs">Session 5 of 8</Badge>
      <h1 className="mb-3 text-3xl font-bold text-foreground">Managing Workload Stress</h1>
      <p className="mb-8 text-base leading-relaxed text-muted-foreground">
        Learn to identify and challenge unhelpful thinking patterns related to work overload and develop healthier cognitive strategies.
      </p>

      <div className="mb-6 grid gap-4 lg:grid-cols-2">
        <Card className="border-border bg-card p-5">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Session Objectives</h3>
          <div className="space-y-3">
            {[
              "Identify automatic thoughts about workload",
              "Challenge all-or-nothing thinking patterns",
              "Practice cognitive restructuring technique",
              "Create an actionable stress management plan",
            ].map((obj, i) => (
              <div key={i} className="flex items-start gap-3">
                <div className="mt-0.5 flex h-6 w-6 items-center justify-center rounded-full bg-primary/10 text-xs font-bold text-primary">{i + 1}</div>
                <p className="text-sm text-foreground">{obj}</p>
              </div>
            ))}
          </div>
        </Card>

        <div className="space-y-4">
          <Card className="border-border bg-card p-5">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <Clock className="h-5 w-5 text-muted-foreground" />
                <span className="text-sm text-foreground">Duration</span>
              </div>
              <span className="text-sm font-medium text-foreground">25 minutes</span>
            </div>
          </Card>

          <Card className="border-primary/20 bg-primary/5 p-5">
            <div className="flex items-start gap-3">
              <Lightbulb className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-sm font-medium text-foreground">Multi-modal Session</p>
                <p className="text-xs text-muted-foreground">
                  This session includes text, voice, and video interactions with your AI therapist. Camera and microphone access will be requested.
                </p>
              </div>
            </div>
          </Card>

          <Card className="border-accent/20 bg-accent/5 p-5">
            <div className="flex items-start gap-3">
              <Camera className="mt-0.5 h-5 w-5 text-accent" />
              <div>
                <p className="text-sm font-medium text-foreground">Camera & Voice Ready</p>
                <p className="text-xs text-muted-foreground">
                  Ensure your camera and microphone are working for the full interactive experience.
                </p>
              </div>
            </div>
          </Card>
        </div>
      </div>

      <Button
        className="h-14 w-full rounded-2xl text-base font-semibold lg:w-auto lg:px-12"
        onClick={async () => {
          await beginSession(selectedSessionId ?? 1)
          setScreen("session-chat")
        }}
      >
        <Play className="mr-2 h-5 w-5" />
        Begin Session
      </Button>
    </div>
  )
}

/* ──────────── Screen 3: AI Chat with Camera/Voice (Web) ──────────── */
export function WebSessionChatScreen() {
  const { setScreen, sendChatMessage } = useApp()
  const [messages, setMessages] = useState([
    {
      role: "ai",
      text: "Welcome to today's session on managing workload stress. I'm Dr. MindBridge, your AI therapist. I can see you through the camera and hear you via the microphone. How are you feeling right now?",
      time: "10:00",
    },
  ])
  const [input, setInput] = useState("")
  const [typing, setTyping] = useState(false)
  const scrollRef = useRef<HTMLDivElement>(null)

  // Camera state
  const [cameraActive, setCameraActive] = useState(false)
  const [cameraError, setCameraError] = useState<string | null>(null)
  const videoRef = useRef<HTMLVideoElement>(null)
  const streamRef = useRef<MediaStream | null>(null)

  // Voice state
  const [micActive, setMicActive] = useState(false)
  const [isRecording, setIsRecording] = useState(false)
  const [audioLevel, setAudioLevel] = useState(0)
  const audioContextRef = useRef<AudioContext | null>(null)
  const analyserRef = useRef<AnalyserNode | null>(null)
  const animFrameRef = useRef<number | null>(null)
  const micStreamRef = useRef<MediaStream | null>(null)

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: "smooth" })
  }, [messages, typing])

  // Camera controls
  const startCamera = useCallback(async () => {
    try {
      setCameraError(null)
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: 320, height: 240, facingMode: "user" },
      })
      streamRef.current = stream
      if (videoRef.current) {
        videoRef.current.srcObject = stream
      }
      setCameraActive(true)
    } catch (err) {
      setCameraError("Camera access denied. Please allow camera permissions.")
      setCameraActive(false)
    }
  }, [])

  const stopCamera = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop())
      streamRef.current = null
    }
    if (videoRef.current) {
      videoRef.current.srcObject = null
    }
    setCameraActive(false)
  }, [])

  // Microphone controls
  const startMic = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      micStreamRef.current = stream
      const audioContext = new AudioContext()
      audioContextRef.current = audioContext
      const analyser = audioContext.createAnalyser()
      analyserRef.current = analyser
      analyser.fftSize = 256
      const source = audioContext.createMediaStreamSource(stream)
      source.connect(analyser)

      const dataArray = new Uint8Array(analyser.frequencyBinCount)
      const updateLevel = () => {
        analyser.getByteFrequencyData(dataArray)
        const avg = dataArray.reduce((a, b) => a + b, 0) / dataArray.length
        setAudioLevel(avg / 255)
        animFrameRef.current = requestAnimationFrame(updateLevel)
      }
      updateLevel()
      setMicActive(true)
    } catch {
      setMicActive(false)
    }
  }, [])

  const stopMic = useCallback(() => {
    if (animFrameRef.current) cancelAnimationFrame(animFrameRef.current)
    if (audioContextRef.current) audioContextRef.current.close()
    if (micStreamRef.current) {
      micStreamRef.current.getTracks().forEach((track) => track.stop())
      micStreamRef.current = null
    }
    setMicActive(false)
    setAudioLevel(0)
  }, [])

  // Toggle recording (simulated voice input)
  const toggleRecording = useCallback(() => {
    if (isRecording) {
      setIsRecording(false)
      // Simulate voice transcription
      const voiceMessages = [
        "I've been feeling really overwhelmed by my workload lately.",
        "I keep thinking I can't handle all these responsibilities.",
        "Sometimes I feel like everyone else manages better than me.",
        "I want to learn how to deal with this stress more effectively.",
      ]
      const randomMsg = voiceMessages[Math.floor(Math.random() * voiceMessages.length)]
      setInput(randomMsg)
    } else {
      setIsRecording(true)
    }
  }, [isRecording])

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopCamera()
      stopMic()
    }
  }, [stopCamera, stopMic])

  const aiResponses = [
    "Thank you for sharing that. I can see you seem tense. It sounds like you are carrying quite a heavy load. Let us explore that feeling together. Can you describe a specific moment this week when you felt most overwhelmed?",
    "I can see why that would be stressful. Your body language tells me this is really affecting you. What thoughts were going through your mind in that moment? Try to recall the exact words.",
    "That is a very common automatic thought. Through our camera session, I noticed your expression changed as you recalled that. Let us examine it together. What evidence supports this thought, and what evidence challenges it?",
    "Great insight. I can see you are starting to relax a bit as we work through this. Let us move to a thought-challenging exercise where we will work on reframing this pattern.",
  ]

  const handleSend = async () => {
    if (!input.trim()) return
    const message = input
    const newMessages = [...messages, { role: "user", text: message, time: "Now" }]
    setMessages(newMessages)
    setInput("")
    setTyping(true)

    try {
      const response = await sendChatMessage(message)
      setTyping(false)
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: response, time: "Now" },
      ])
    } catch {
      setTyping(false)
      const responseIdx = Math.min(newMessages.filter((m) => m.role === "user").length - 1, aiResponses.length - 1)
      setMessages((prev) => [
        ...prev,
        { role: "ai", text: aiResponses[responseIdx], time: "Now" },
      ])
    }
  }

  const userMessageCount = messages.filter((m) => m.role === "user").length

  return (
    <div className="flex h-full">
      {/* Main Chat Area */}
      <div className="flex flex-1 flex-col">
        {/* Header */}
        <div className="flex items-center gap-3 border-b border-border bg-card px-6 py-3">
          <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-intro")}>
            <ArrowLeft className="h-4 w-4" />
          </button>
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
            <Brain className="h-5 w-5 text-primary" />
          </div>
          <div className="flex-1">
            <p className="text-sm font-semibold text-foreground">Dr. MindBridge</p>
            <p className="text-xs text-success">Online - AI Therapist</p>
          </div>
          <div className="flex items-center gap-3">
            <span className="text-xs text-muted-foreground">Progress</span>
            <Progress value={(userMessageCount / 4) * 100} className="h-2 w-24" />
            <EndSessionButton onClick={() => setScreen("session-completion")} />
          </div>
        </div>

        {/* Messages */}
        <div ref={scrollRef} className="flex-1 space-y-4 overflow-y-auto px-6 py-4">
          {messages.map((msg, i) => (
            <div key={i} className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}>
              <div className={`max-w-[65%] rounded-2xl px-5 py-3 ${
                msg.role === "user" ? "bg-primary text-primary-foreground" : "bg-secondary text-secondary-foreground"
              }`}>
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

        {/* Continue button */}
        {userMessageCount >= 3 && (
          <div className="px-6 pb-2">
            <Button className="h-10 w-full rounded-xl text-sm font-semibold" onClick={() => setScreen("session-multimodal")}>
              Continue to Exercise
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </div>
        )}

        {/* Input Area */}
        <div className="flex items-end gap-3 border-t border-border bg-card px-6 py-4">
          {/* Voice recording button */}
          <button
            className={`flex h-11 w-11 items-center justify-center rounded-xl transition-colors ${
              isRecording
                ? "bg-destructive/10 text-destructive animate-pulse"
                : micActive
                ? "bg-success/10 text-success"
                : "bg-secondary text-muted-foreground hover:text-foreground"
            }`}
            onClick={() => {
              if (!micActive) {
                startMic().then(() => toggleRecording())
              } else {
                toggleRecording()
              }
            }}
            aria-label={isRecording ? "Stop recording" : "Start voice input"}
          >
            {isRecording ? <Square className="h-4 w-4" /> : <Mic className="h-5 w-5" />}
          </button>

          {/* Audio level indicator */}
          {micActive && (
            <div className="flex h-11 items-center gap-0.5">
              {Array.from({ length: 8 }).map((_, i) => (
                <div
                  key={i}
                  className="w-1 rounded-full bg-primary transition-all"
                  style={{
                    height: `${Math.max(4, audioLevel * 44 * (0.5 + Math.random() * 0.5))}px`,
                  }}
                />
              ))}
            </div>
          )}

          <div className="flex-1">
            <Textarea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder={isRecording ? "Listening... speak now" : "Type your response or use voice..."}
              className="min-h-[44px] max-h-[120px] resize-none rounded-xl border-border"
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
            className="flex h-11 w-11 items-center justify-center rounded-xl bg-primary text-primary-foreground transition-colors hover:bg-primary/90"
            onClick={handleSend}
            aria-label="Send message"
          >
            <Send className="h-4 w-4" />
          </button>
        </div>
      </div>

      {/* Right Panel - Camera & Session Info */}
      <div className="hidden w-80 flex-col border-l border-border bg-card xl:flex">
        {/* Camera Feed */}
        <div className="border-b border-border p-4">
          <div className="mb-3 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground">Camera</h3>
            <div className="flex gap-1.5">
              <button
                className={`flex h-8 w-8 items-center justify-center rounded-lg transition-colors ${
                  cameraActive ? "bg-success/10 text-success" : "bg-secondary text-muted-foreground"
                }`}
                onClick={() => cameraActive ? stopCamera() : startCamera()}
                aria-label={cameraActive ? "Turn off camera" : "Turn on camera"}
              >
                {cameraActive ? <Video className="h-4 w-4" /> : <VideoOff className="h-4 w-4" />}
              </button>
              <button
                className={`flex h-8 w-8 items-center justify-center rounded-lg transition-colors ${
                  micActive ? "bg-success/10 text-success" : "bg-secondary text-muted-foreground"
                }`}
                onClick={() => micActive ? stopMic() : startMic()}
                aria-label={micActive ? "Turn off mic" : "Turn on mic"}
              >
                {micActive ? <Mic className="h-4 w-4" /> : <MicOff className="h-4 w-4" />}
              </button>
            </div>
          </div>

          <div className="relative aspect-[4/3] overflow-hidden rounded-xl bg-foreground/5">
            {cameraActive ? (
              <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className="h-full w-full object-cover"
              />
            ) : (
              <div className="flex h-full flex-col items-center justify-center gap-2">
                {cameraError ? (
                  <>
                    <CameraOff className="h-8 w-8 text-muted-foreground" />
                    <p className="px-4 text-center text-xs text-muted-foreground">{cameraError}</p>
                  </>
                ) : (
                  <>
                    <Camera className="h-8 w-8 text-muted-foreground" />
                    <p className="text-xs text-muted-foreground">Camera off</p>
                    <Button size="sm" variant="outline" className="mt-1 rounded-lg text-xs" onClick={startCamera}>
                      Enable Camera
                    </Button>
                  </>
                )}
              </div>
            )}
            {cameraActive && (
              <div className="absolute bottom-2 left-2 flex items-center gap-1 rounded-full bg-foreground/50 px-2 py-1">
                <div className="h-1.5 w-1.5 rounded-full bg-destructive animate-pulse" />
                <span className="text-[10px] font-medium text-background">LIVE</span>
              </div>
            )}
          </div>

          {/* Mic status */}
          {micActive && (
            <div className="mt-3 flex items-center gap-2 rounded-lg bg-success/5 px-3 py-2">
              <Volume2 className="h-3.5 w-3.5 text-success" />
              <span className="text-xs text-success">Microphone active</span>
              <div className="ml-auto flex gap-0.5">
                {Array.from({ length: 5 }).map((_, i) => (
                  <div
                    key={i}
                    className="w-0.5 rounded-full bg-success transition-all"
                    style={{ height: `${Math.max(4, audioLevel * 16 * (0.5 + Math.random() * 0.5))}px` }}
                  />
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Session Info */}
        <div className="flex-1 overflow-y-auto p-4">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Session Info</h3>
          <div className="space-y-3">
            <div className="rounded-lg bg-secondary p-3">
              <p className="text-xs text-muted-foreground">Session</p>
              <p className="text-sm font-medium text-foreground">Managing Workload Stress</p>
            </div>
            <div className="rounded-lg bg-secondary p-3">
              <p className="text-xs text-muted-foreground">Duration</p>
              <p className="text-sm font-medium text-foreground">25 minutes</p>
            </div>
            <div className="rounded-lg bg-secondary p-3">
              <p className="text-xs text-muted-foreground">Progress</p>
              <Progress value={(userMessageCount / 4) * 100} className="mt-1 h-2" />
            </div>
          </div>

          <h3 className="mb-3 mt-6 text-sm font-semibold text-foreground">Current Focus</h3>
          <Card className="border-primary/20 bg-primary/5 p-3">
            <div className="flex items-start gap-2">
              <Lightbulb className="mt-0.5 h-4 w-4 text-primary" />
              <p className="text-xs leading-relaxed text-foreground/80">
                Exploring automatic thoughts about workload and identifying cognitive distortions.
              </p>
            </div>
          </Card>

          <h3 className="mb-3 mt-6 text-sm font-semibold text-foreground">Tips</h3>
          <div className="space-y-2">
            <div className="flex items-start gap-2 rounded-lg bg-secondary p-2">
              <MessageCircle className="mt-0.5 h-3.5 w-3.5 text-muted-foreground" />
              <p className="text-xs text-muted-foreground">Be honest and specific about your feelings</p>
            </div>
            <div className="flex items-start gap-2 rounded-lg bg-secondary p-2">
              <Brain className="mt-0.5 h-3.5 w-3.5 text-muted-foreground" />
              <p className="text-xs text-muted-foreground">Try to recall exact thoughts from stressful moments</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

/* ──────────── Screen 4: Multi-modal with Real Camera/Voice (Web) ──────────── */
export function WebSessionMultimodalScreen() {
  const { setScreen } = useApp()
  const [selectedMode, setSelectedMode] = useState<"text" | "voice" | "video">("text")

  // Camera
  const [cameraActive, setCameraActive] = useState(false)
  const [isVideoRecording, setIsVideoRecording] = useState(false)
  const videoRef = useRef<HTMLVideoElement>(null)
  const streamRef = useRef<MediaStream | null>(null)

  // Voice
  const [isVoiceRecording, setIsVoiceRecording] = useState(false)
  const [voiceDuration, setVoiceDuration] = useState(0)
  const voiceIntervalRef = useRef<NodeJS.Timeout | null>(null)
  const [audioLevel, setAudioLevel] = useState(0)
  const audioCtxRef = useRef<AudioContext | null>(null)
  const analyserRef = useRef<AnalyserNode | null>(null)
  const animRef = useRef<number | null>(null)
  const micStreamRef = useRef<MediaStream | null>(null)

  const startCamera = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { width: 640, height: 480, facingMode: "user" },
        audio: true,
      })
      streamRef.current = stream
      if (videoRef.current) {
        videoRef.current.srcObject = stream
      }
      setCameraActive(true)
    } catch {
      setCameraActive(false)
    }
  }, [])

  const stopCamera = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop())
      streamRef.current = null
    }
    setCameraActive(false)
    setIsVideoRecording(false)
  }, [])

  const startVoice = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
      micStreamRef.current = stream
      const audioContext = new AudioContext()
      audioCtxRef.current = audioContext
      const analyser = audioContext.createAnalyser()
      analyserRef.current = analyser
      analyser.fftSize = 256
      const source = audioContext.createMediaStreamSource(stream)
      source.connect(analyser)

      const dataArray = new Uint8Array(analyser.frequencyBinCount)
      const updateLevel = () => {
        analyser.getByteFrequencyData(dataArray)
        const avg = dataArray.reduce((a, b) => a + b, 0) / dataArray.length
        setAudioLevel(avg / 255)
        animRef.current = requestAnimationFrame(updateLevel)
      }
      updateLevel()
      setIsVoiceRecording(true)
      setVoiceDuration(0)
      voiceIntervalRef.current = setInterval(() => {
        setVoiceDuration((d) => d + 1)
      }, 1000)
    } catch {
      setIsVoiceRecording(false)
    }
  }, [])

  const stopVoice = useCallback(() => {
    if (animRef.current) cancelAnimationFrame(animRef.current)
    if (audioCtxRef.current) audioCtxRef.current.close()
    if (micStreamRef.current) {
      micStreamRef.current.getTracks().forEach((t) => t.stop())
      micStreamRef.current = null
    }
    if (voiceIntervalRef.current) clearInterval(voiceIntervalRef.current)
    setIsVoiceRecording(false)
    setAudioLevel(0)
  }, [])

  useEffect(() => {
    return () => {
      stopCamera()
      stopVoice()
    }
  }, [stopCamera, stopVoice])

  const formatTime = (s: number) => `${Math.floor(s / 60)}:${(s % 60).toString().padStart(2, "0")}`

  return (
    <div className="mx-auto max-w-4xl p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-chat")}>
          <ArrowLeft className="h-4 w-4" />
          Back to Chat
        </button>
        <EndSessionButton onClick={() => setScreen("session-completion")} />
      </div>

      <h2 className="mb-2 text-2xl font-bold text-foreground">Express Yourself</h2>
      <p className="mb-6 text-muted-foreground">
        Describe a recent work situation that stressed you. Choose your preferred format:
      </p>

      {/* Mode selector */}
      <div className="mb-8 flex gap-3">
        {[
          { mode: "text" as const, icon: MessageCircle, label: "Type" },
          { mode: "voice" as const, icon: Mic, label: "Voice" },
          { mode: "video" as const, icon: Video, label: "Video" },
        ].map(({ mode, icon: Icon, label }) => (
          <button
            key={mode}
            className={`flex flex-1 items-center justify-center gap-2 rounded-xl border p-4 transition-all ${
              selectedMode === mode
                ? "border-primary bg-primary/5 shadow-sm"
                : "border-border bg-card hover:border-primary/50"
            }`}
            onClick={() => setSelectedMode(mode)}
          >
            <Icon className={`h-5 w-5 ${selectedMode === mode ? "text-primary" : "text-muted-foreground"}`} />
            <span className={`text-sm font-medium ${selectedMode === mode ? "text-primary" : "text-muted-foreground"}`}>{label}</span>
          </button>
        ))}
      </div>

      {/* Text mode */}
      {selectedMode === "text" && (
        <Textarea
          placeholder="Describe what happened, how you felt, and what you were thinking..."
          className="min-h-[200px] rounded-xl border-border"
        />
      )}

      {/* Voice mode with real microphone */}
      {selectedMode === "voice" && (
        <Card className="border-border bg-card p-8">
          <div className="flex flex-col items-center gap-6">
            <button
              className={`flex h-28 w-28 items-center justify-center rounded-full transition-all ${
                isVoiceRecording ? "bg-destructive/10 shadow-lg" : "bg-primary/10 hover:bg-primary/20"
              }`}
              onClick={() => isVoiceRecording ? stopVoice() : startVoice()}
              aria-label={isVoiceRecording ? "Stop recording" : "Start recording"}
            >
              {isVoiceRecording ? (
                <Square className="h-10 w-10 text-destructive" />
              ) : (
                <Mic className="h-10 w-10 text-primary" />
              )}
            </button>

            {isVoiceRecording ? (
              <>
                <div className="flex items-center gap-2">
                  <div className="h-2 w-2 rounded-full bg-destructive animate-pulse" />
                  <span className="text-sm font-medium text-foreground">Recording - {formatTime(voiceDuration)}</span>
                </div>
                <div className="flex items-center gap-1">
                  {Array.from({ length: 24 }).map((_, i) => (
                    <div
                      key={i}
                      className="w-1.5 rounded-full bg-primary transition-all"
                      style={{
                        height: `${Math.max(4, audioLevel * 60 * (0.3 + Math.random() * 0.7))}px`,
                      }}
                    />
                  ))}
                </div>
              </>
            ) : (
              <p className="text-sm text-muted-foreground">Click the microphone to start recording</p>
            )}
          </div>
        </Card>
      )}

      {/* Video mode with real camera */}
      {selectedMode === "video" && (
        <Card className="border-border bg-card p-6">
          <div className="relative aspect-video overflow-hidden rounded-xl bg-foreground/5">
            {cameraActive ? (
              <>
                <video
                  ref={videoRef}
                  autoPlay
                  playsInline
                  muted
                  className="h-full w-full object-cover"
                />
                {isVideoRecording && (
                  <div className="absolute left-4 top-4 flex items-center gap-1.5 rounded-full bg-destructive px-3 py-1">
                    <div className="h-2 w-2 rounded-full bg-destructive-foreground animate-pulse" />
                    <span className="text-xs font-medium text-destructive-foreground">REC</span>
                  </div>
                )}
              </>
            ) : (
              <div className="flex h-full flex-col items-center justify-center gap-4">
                <Camera className="h-16 w-16 text-muted-foreground" />
                <p className="text-sm text-muted-foreground">Camera preview will appear here</p>
                <Button onClick={startCamera} className="rounded-xl">
                  <Camera className="mr-2 h-4 w-4" />
                  Enable Camera
                </Button>
              </div>
            )}
          </div>

          {cameraActive && (
            <div className="mt-4 flex justify-center gap-3">
              <Button
                variant={isVideoRecording ? "destructive" : "default"}
                className="rounded-xl"
                onClick={() => setIsVideoRecording(!isVideoRecording)}
              >
                {isVideoRecording ? (
                  <>
                    <Square className="mr-2 h-4 w-4" />
                    Stop Recording
                  </>
                ) : (
                  <>
                    <Play className="mr-2 h-4 w-4" />
                    Start Recording
                  </>
                )}
              </Button>
              <Button variant="outline" className="rounded-xl" onClick={stopCamera}>
                <CameraOff className="mr-2 h-4 w-4" />
                Turn Off Camera
              </Button>
            </div>
          )}
        </Card>
      )}

      <div className="mt-8">
        <Button className="h-14 w-full rounded-2xl text-base font-semibold lg:w-auto lg:px-12" onClick={() => setScreen("session-scenario")}>
          Submit & Continue
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 5: Scenario (Web) ──────────── */
export function WebSessionScenarioScreen() {
  const { setScreen } = useApp()
  const [selectedChoice, setSelectedChoice] = useState<number | null>(null)

  const choices = [
    { text: "I need to do everything myself or it will not be done right.", type: "unhelpful", feedback: "This is all-or-nothing thinking. It puts unrealistic pressure on yourself." },
    { text: "I can prioritize and delegate some tasks to manage this.", type: "helpful", feedback: "Great balanced thinking! This recognizes your capacity while being realistic." },
    { text: "Everyone else can handle this, something is wrong with me.", type: "unhelpful", feedback: "This is personalization and comparison. Everyone has different capacities." },
  ]

  return (
    <div className="mx-auto max-w-3xl p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-multimodal")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <div className="flex items-center gap-3">
          <Badge variant="secondary">Step 3/5</Badge>
          <EndSessionButton onClick={() => setScreen("session-completion")} />
        </div>
      </div>

      <h2 className="mb-4 text-2xl font-bold text-foreground">Interactive Scenario</h2>

      <Card className="mb-6 border-primary/20 bg-primary/5 p-5">
        <p className="text-sm font-medium text-foreground">Scenario:</p>
        <p className="mt-1 text-sm leading-relaxed text-foreground/80">
          Your manager just assigned three new high-priority tasks while you are already behind on your current deadlines. You feel your heart racing as you look at your to-do list.
        </p>
      </Card>

      <h3 className="mb-4 text-base font-semibold text-foreground">What is your first thought?</h3>

      <div className="space-y-3">
        {choices.map((choice, i) => (
          <button
            key={i}
            className={`w-full rounded-xl border p-5 text-left transition-all ${
              selectedChoice === i
                ? choice.type === "helpful" ? "border-success bg-success/5 shadow-sm" : "border-warning bg-warning/5 shadow-sm"
                : "border-border bg-card hover:border-primary/50 hover:shadow-sm"
            }`}
            onClick={() => setSelectedChoice(i)}
          >
            <p className="text-sm text-foreground">{choice.text}</p>
            {selectedChoice === i && (
              <div className="mt-3 flex items-start gap-2 border-t border-border/50 pt-3">
                <Lightbulb className="mt-0.5 h-4 w-4 text-primary" />
                <p className="text-xs leading-relaxed text-muted-foreground">{choice.feedback}</p>
              </div>
            )}
          </button>
        ))}
      </div>

      {selectedChoice !== null && (
        <div className="mt-8">
          <Button className="h-14 rounded-2xl text-base font-semibold lg:px-12" onClick={() => setScreen("session-thought-challenge")}>
            Continue to Thought Challenge
            <ArrowRight className="ml-2 h-5 w-5" />
          </Button>
        </div>
      )}
    </div>
  )
}

/* ──────────── Screen 6: Thought Challenge (Web) ──────────── */
export function WebSessionThoughtChallengeScreen() {
  const { setScreen } = useApp()
  const [step, setStep] = useState(0)

  const steps = [
    { title: "Identify the Thought", desc: "I need to do everything perfectly or I'm a failure.", prompt: "What cognitive distortion do you notice here?" },
    { title: "Examine the Evidence", desc: "What evidence supports or contradicts this thought?", prompt: "Think about times you have successfully delegated or managed heavy workloads." },
    { title: "Generate Alternative", desc: "Create a more balanced thought to replace the distortion.", prompt: "What would you tell a friend in this situation?" },
    { title: "Rate Your Belief", desc: "How much do you believe the original thought now?", prompt: "Use the slider to indicate your belief level." },
  ]

  return (
    <div className="mx-auto max-w-3xl p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-scenario")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <div className="flex items-center gap-3">
          <Badge variant="secondary">Step 4/5</Badge>
          <EndSessionButton onClick={() => setScreen("session-completion")} />
        </div>
      </div>

      <div className="mb-8 flex gap-2">
        {steps.map((_, i) => (
          <div key={i} className={`h-2 flex-1 rounded-full ${i <= step ? "bg-primary" : "bg-border"}`} />
        ))}
      </div>

      <h2 className="mb-2 text-2xl font-bold text-foreground">{steps[step].title}</h2>
      <p className="mb-6 text-muted-foreground">{steps[step].desc}</p>

      <Card className="mb-6 border-border bg-card p-5">
        <div className="flex items-start gap-3">
          <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
          <p className="text-sm leading-relaxed text-foreground/80">{steps[step].prompt}</p>
        </div>
      </Card>

      {step < 3 && (
        <Textarea placeholder="Write your response..." className="min-h-[160px] rounded-xl border-border" />
      )}

      {step === 3 && (
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-sm text-muted-foreground">Not at all</span>
            <span className="text-sm text-muted-foreground">Completely</span>
          </div>
          <input type="range" min="0" max="100" defaultValue="30" className="w-full accent-primary" />
          <Card className="border-success/20 bg-success/5 p-5">
            <div className="flex items-start gap-2">
              <CheckCircle2 className="mt-0.5 h-5 w-5 text-success" />
              <div>
                <p className="text-sm font-medium text-foreground">Great progress!</p>
                <p className="text-xs text-muted-foreground">Your belief in the distorted thought has decreased. This is a key indicator of cognitive restructuring.</p>
              </div>
            </div>
          </Card>
        </div>
      )}

      <div className="mt-8">
        <Button
          className="h-14 rounded-2xl text-base font-semibold lg:px-12"
          onClick={() => {
            if (step < steps.length - 1) setStep(step + 1)
            else setScreen("session-progress")
          }}
        >
          {step < steps.length - 1 ? "Next Step" : "View Progress"}
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 7: Session Progress (Web) ──────────── */
export function WebSessionProgressScreen() {
  const { setScreen } = useApp()
  return (
    <div className="mx-auto max-w-3xl p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-thought-challenge")}>
          <ArrowLeft className="h-4 w-4" />
          Back
        </button>
        <EndSessionButton onClick={() => setScreen("session-completion")} />
      </div>

      <div className="mb-8 text-center">
        <div className="relative mx-auto mb-4 flex h-36 w-36 items-center justify-center">
          <svg className="h-36 w-36 -rotate-90" viewBox="0 0 120 120">
            <circle cx="60" cy="60" r="52" fill="none" stroke="currentColor" strokeWidth="8" className="text-border" />
            <circle cx="60" cy="60" r="52" fill="none" stroke="currentColor" strokeWidth="8" strokeDasharray={`${0.8 * 327} ${327}`} strokeLinecap="round" className="text-primary" />
          </svg>
          <span className="absolute text-3xl font-bold text-foreground">80%</span>
        </div>
        <h2 className="text-2xl font-bold text-foreground">Almost There!</h2>
        <p className="text-muted-foreground">Session 5 of 8 in progress</p>
      </div>

      <div className="mx-auto max-w-md space-y-3">
        {[
          { label: "Introduction", status: "done" },
          { label: "AI Conversation", status: "done" },
          { label: "Multi-modal Capture", status: "done" },
          { label: "Interactive Scenario", status: "done" },
          { label: "Thought Challenge", status: "done" },
          { label: "Summary & Reflection", status: "current" },
        ].map(({ label, status }) => (
          <div key={label} className={`flex items-center gap-3 rounded-xl border p-4 ${
            status === "current" ? "border-primary bg-primary/5" : "border-border bg-card"
          }`}>
            {status === "done" ? (
              <CheckCircle2 className="h-5 w-5 text-success" />
            ) : (
              <BarChart3 className="h-5 w-5 text-primary" />
            )}
            <span className="flex-1 text-sm font-medium text-foreground">{label}</span>
            <span className={`text-xs ${status === "done" ? "text-success" : "text-primary"}`}>
              {status === "done" ? "Complete" : "In Progress"}
            </span>
          </div>
        ))}
      </div>

      <div className="mt-8 text-center">
        <Button className="h-14 rounded-2xl text-base font-semibold lg:px-12" onClick={() => setScreen("session-completion")}>
          Complete Session
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 8: Session Completion & Summary (Web) ──────────── */
export function WebSessionCompletionScreen() {
  const { setScreen, incrementSessions } = useApp()
  const [celebrated, setCelebrated] = useState(false)
  const [activeTab, setActiveTab] = useState<"summary" | "transcript" | "insights" | "recommendations">("summary")
  const [transcriptExpanded, setTranscriptExpanded] = useState(false)

  useEffect(() => {
    if (!celebrated) {
      incrementSessions()
      setCelebrated(true)
    }
  }, [celebrated, incrementSessions])

  const transcript = [
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "Welcome to today's session on managing workload stress. I'm Dr. MindBridge, your AI therapist. I can see you through the camera and hear you via the microphone. How are you feeling right now?", time: "0:00" },
    { role: "user" as const, speaker: "You", text: "I've been feeling really overwhelmed by my workload lately. I have too many deadlines and I can't seem to keep up.", time: "0:32" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "Thank you for sharing that. I can see you seem tense. It sounds like you are carrying quite a heavy load. Let's explore that feeling together. Can you describe a specific moment this week when you felt most overwhelmed?", time: "0:48" },
    { role: "user" as const, speaker: "You", text: "Yesterday my manager assigned three new high-priority tasks when I was already behind. I felt my chest tighten and thought I just can't handle this.", time: "1:25" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "I can see why that would be stressful. Your body language tells me this is really affecting you. What thoughts were going through your mind in that moment? Try to recall the exact words.", time: "1:42" },
    { role: "user" as const, speaker: "You", text: "I kept thinking: I need to do everything myself or it won't be done right. And if I can't manage this, something must be wrong with me.", time: "2:15" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "Those are very common automatic thoughts. I noticed your expression changed as you recalled that. Let's examine these together. The first thought -- 'I need to do everything myself' -- is an example of all-or-nothing thinking. The second -- 'something is wrong with me' -- is personalization. What evidence supports these thoughts, and what evidence challenges them?", time: "2:30" },
    { role: "user" as const, speaker: "You", text: "Well, I guess I have successfully delegated before. And my colleague told me she also feels overwhelmed sometimes. So maybe it's not just me.", time: "3:45" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "Great insight. I can see you're starting to relax a bit as we work through this. You've identified evidence that directly contradicts both automatic thoughts. Delegation has worked for you before, and feeling overwhelmed is a universal experience -- not a personal failing. Let's move to a thought-challenging exercise where we'll work on reframing this pattern.", time: "4:02" },
    { role: "user" as const, speaker: "You", text: "That makes sense. I think I can try to reframe it as: I can prioritize what matters most and ask for help when I need it.", time: "5:10" },
    { role: "ai" as const, speaker: "Dr. MindBridge", text: "That's an excellent balanced thought. Notice how it acknowledges reality while being compassionate toward yourself. Your belief in the original distorted thought has decreased significantly -- this is a key indicator of cognitive restructuring working. Great progress today.", time: "5:28" },
  ]

  const aiSummary = {
    overview: "This was a productive 24-minute session focused on identifying and challenging cognitive distortions related to workload stress. The participant demonstrated strong self-awareness and was able to successfully engage in cognitive restructuring.",
    emotionalArc: "The session began with notable tension and feelings of overwhelm. Through guided exploration, the participant transitioned from anxiety and self-criticism to a more balanced and self-compassionate perspective. Body language showed visible relaxation by mid-session.",
    techniques: [
      { name: "Socratic Questioning", description: "Used to guide discovery of automatic thoughts and evidence examination" },
      { name: "Cognitive Restructuring", description: "Successfully reframed all-or-nothing thinking into balanced thoughts" },
      { name: "Behavioral Evidence Gathering", description: "Participant recalled past successful delegation as counter-evidence" },
    ],
    metrics: [
      { label: "Session Duration", value: "24 minutes" },
      { label: "Exercises Completed", value: "3 of 3" },
      { label: "Distortions Identified", value: "2 (All-or-nothing, Personalization)" },
      { label: "Belief Change", value: "-40%", highlight: true },
      { label: "Engagement Level", value: "High" },
      { label: "Camera/Voice Used", value: "Yes" },
    ],
  }

  const aiInsights = [
    {
      title: "Primary Cognitive Pattern: All-or-Nothing Thinking",
      severity: "moderate" as const,
      description: "You showed a strong tendency toward binary thinking about work performance -- either doing everything perfectly or failing entirely. This pattern creates unnecessary pressure and blocks you from seeing middle-ground solutions like delegation or prioritization.",
      evidence: "Stated: 'I need to do everything myself or it won't be done right.'",
    },
    {
      title: "Secondary Pattern: Personalization",
      severity: "mild" as const,
      description: "You attributed the feeling of being overwhelmed as a personal deficiency rather than recognizing it as a normal response to a genuinely heavy workload. This was quickly corrected when you recalled that colleagues share similar experiences.",
      evidence: "Stated: 'If I can't manage this, something must be wrong with me.'",
    },
    {
      title: "Strength: Self-Awareness & Openness",
      severity: "positive" as const,
      description: "You demonstrated excellent ability to step back and examine your thoughts objectively once prompted. Your willingness to consider alternative perspectives was a key driver of progress in this session.",
      evidence: "Quickly generated counter-evidence and a balanced alternative thought.",
    },
    {
      title: "Physical Stress Response Detected",
      severity: "moderate" as const,
      description: "Visible tension in shoulders and facial expressions at session start, with chest tightness reported. These physical symptoms correlate with the cognitive distortions and may benefit from somatic relaxation techniques.",
      evidence: "Self-reported: 'I felt my chest tighten.' Camera observation: visible shoulder tension.",
    },
  ]

  const recommendations = [
    {
      category: "Daily Practice",
      icon: BookOpen,
      items: [
        "Identify one all-or-nothing thought each day and write down a balanced alternative",
        "Spend 5 minutes each morning doing a body scan to notice where you hold stress",
        "Keep a delegation log -- track one task per day you could or did delegate",
      ],
    },
    {
      category: "Thought Diary Prompts",
      icon: FileText,
      items: [
        "When I feel overwhelmed, the thought I have is ___. A more balanced view is ___.",
        "Evidence that I can handle my workload: ___.",
        "One thing I delegated today and how it went: ___.",
      ],
    },
    {
      category: "Next Session Preview",
      icon: Target,
      items: [
        "Session 6: 'Setting Boundaries' -- Learn to say no without guilt",
        "We'll build on today's progress with workload boundaries",
        "Practice: Before Session 6, notice one situation where you wanted to set a boundary but didn't",
      ],
    },
    {
      category: "Warning Signs to Watch",
      icon: AlertTriangle,
      items: [
        "If you notice chest tightness or tension more than 3 times per day, use the breathing exercise in Crisis Support",
        "If overwhelm leads to disengagement or avoidance, reach out to your support network",
        "If sleep is disrupted for more than 3 consecutive nights, consider scheduling an extra session",
      ],
    },
  ]

  const tabs = [
    { id: "summary" as const, label: "Summary", icon: BarChart3 },
    { id: "transcript" as const, label: "Transcript", icon: FileText },
    { id: "insights" as const, label: "AI Insights", icon: Lightbulb },
    { id: "recommendations" as const, label: "Recommendations", icon: Target },
  ]

  return (
    <div className="mx-auto max-w-5xl p-6 lg:p-8">
      {/* Header */}
      <div className="mb-8 flex items-start justify-between">
        <div className="flex items-center gap-5">
          <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-success/10">
            <CheckCircle2 className="h-8 w-8 text-success" />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-foreground">Session Complete</h1>
            <p className="text-sm text-muted-foreground">Managing Workload Stress -- Session 5 of 8</p>
          </div>
        </div>
        <Button className="rounded-xl" onClick={() => setScreen("session-rating")}>
          Rate & Finish
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>

      {/* Tab Navigation */}
      <div className="mb-6 flex gap-1 rounded-xl border border-border bg-muted/50 p-1">
        {tabs.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            className={`flex flex-1 items-center justify-center gap-2 rounded-lg px-4 py-2.5 text-sm font-medium transition-all ${
              activeTab === id
                ? "bg-card text-foreground shadow-sm"
                : "text-muted-foreground hover:text-foreground"
            }`}
            onClick={() => setActiveTab(id)}
          >
            <Icon className="h-4 w-4" />
            {label}
          </button>
        ))}
      </div>

      {/* Summary Tab */}
      {activeTab === "summary" && (
        <div className="space-y-6">
          {/* Overview */}
          <Card className="border-border bg-card p-6">
            <h3 className="mb-3 flex items-center gap-2 text-base font-semibold text-foreground">
              <Brain className="h-5 w-5 text-primary" />
              AI Session Summary
            </h3>
            <p className="text-sm leading-relaxed text-foreground/80">{aiSummary.overview}</p>
          </Card>

          {/* Metrics Grid */}
          <div className="grid gap-4 lg:grid-cols-3">
            {aiSummary.metrics.map(({ label, value, highlight }) => (
              <Card key={label} className={`border-border p-4 ${highlight ? "border-success/30 bg-success/5" : "bg-card"}`}>
                <p className="text-xs text-muted-foreground">{label}</p>
                <p className={`mt-1 text-lg font-bold ${highlight ? "text-success" : "text-foreground"}`}>{value}</p>
              </Card>
            ))}
          </div>

          {/* Emotional Arc */}
          <Card className="border-border bg-card p-6">
            <h3 className="mb-3 text-base font-semibold text-foreground">Emotional Arc</h3>
            <p className="mb-4 text-sm leading-relaxed text-foreground/80">{aiSummary.emotionalArc}</p>
            <div className="flex items-center gap-3">
              <div className="flex items-center gap-1 rounded-full bg-destructive/10 px-3 py-1">
                <div className="h-2 w-2 rounded-full bg-destructive" />
                <span className="text-xs font-medium text-destructive">Tense</span>
              </div>
              <div className="h-px flex-1 bg-gradient-to-r from-destructive/30 via-warning/30 to-success/30" />
              <div className="flex items-center gap-1 rounded-full bg-success/10 px-3 py-1">
                <div className="h-2 w-2 rounded-full bg-success" />
                <span className="text-xs font-medium text-success">Balanced</span>
              </div>
            </div>
          </Card>

          {/* Techniques Used */}
          <Card className="border-border bg-card p-6">
            <h3 className="mb-4 text-base font-semibold text-foreground">Techniques Applied</h3>
            <div className="space-y-3">
              {aiSummary.techniques.map(({ name, description }, i) => (
                <div key={i} className="flex items-start gap-3 rounded-lg bg-secondary p-3">
                  <div className="mt-0.5 flex h-6 w-6 items-center justify-center rounded-full bg-primary/10 text-xs font-bold text-primary">{i + 1}</div>
                  <div>
                    <p className="text-sm font-medium text-foreground">{name}</p>
                    <p className="text-xs text-muted-foreground">{description}</p>
                  </div>
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
                <span className="text-sm text-muted-foreground">{transcript.length} messages -- 24 min duration</span>
              </div>
              <button
                className="flex items-center gap-1 text-xs font-medium text-primary hover:underline"
                onClick={() => setTranscriptExpanded(!transcriptExpanded)}
              >
                {transcriptExpanded ? "Collapse" : "Expand All"}
                {transcriptExpanded ? <ChevronUp className="h-3 w-3" /> : <ChevronDown className="h-3 w-3" />}
              </button>
            </div>
          </Card>

          <div className="space-y-3">
            {(transcriptExpanded ? transcript : transcript.slice(0, 6)).map((msg, i) => (
              <div key={i} className={`flex gap-3 ${msg.role === "user" ? "flex-row-reverse" : ""}`}>
                <div className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${
                  msg.role === "ai" ? "bg-primary/10" : "bg-accent/10"
                }`}>
                  {msg.role === "ai" ? (
                    <Brain className="h-4 w-4 text-primary" />
                  ) : (
                    <MessageCircle className="h-4 w-4 text-accent" />
                  )}
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

          {!transcriptExpanded && transcript.length > 6 && (
            <button
              className="flex w-full items-center justify-center gap-2 rounded-xl border border-dashed border-border py-3 text-sm text-muted-foreground transition-colors hover:border-primary hover:text-primary"
              onClick={() => setTranscriptExpanded(true)}
            >
              <ChevronDown className="h-4 w-4" />
              Show {transcript.length - 6} more messages
            </button>
          )}
        </div>
      )}

      {/* AI Insights Tab */}
      {activeTab === "insights" && (
        <div className="space-y-4">
          <Card className="border-primary/20 bg-primary/5 p-4">
            <div className="flex items-start gap-3">
              <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-sm font-medium text-foreground">AI-Generated Insights</p>
                <p className="text-xs text-muted-foreground">Based on your conversation, responses, and camera/voice observations during this session.</p>
              </div>
            </div>
          </Card>

          {aiInsights.map((insight, i) => (
            <Card key={i} className={`border-border bg-card p-5 ${
              insight.severity === "positive" ? "border-l-4 border-l-success" :
              insight.severity === "moderate" ? "border-l-4 border-l-warning" :
              "border-l-4 border-l-primary"
            }`}>
              <div className="mb-2 flex items-start justify-between gap-3">
                <h4 className="text-sm font-semibold text-foreground">{insight.title}</h4>
                <Badge variant="secondary" className={`shrink-0 text-[10px] ${
                  insight.severity === "positive" ? "bg-success/10 text-success" :
                  insight.severity === "moderate" ? "bg-warning/10 text-warning" :
                  "bg-primary/10 text-primary"
                }`}>
                  {insight.severity === "positive" ? "Strength" : insight.severity === "moderate" ? "Moderate" : "Mild"}
                </Badge>
              </div>
              <p className="mb-3 text-sm leading-relaxed text-foreground/80">{insight.description}</p>
              <div className="rounded-lg bg-secondary p-3">
                <p className="text-xs text-muted-foreground"><span className="font-medium">Evidence: </span>{insight.evidence}</p>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* Recommendations Tab */}
      {activeTab === "recommendations" && (
        <div className="space-y-6">
          <Card className="border-primary/20 bg-primary/5 p-4">
            <div className="flex items-start gap-3">
              <Target className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-sm font-medium text-foreground">Personalized Recommendations</p>
                <p className="text-xs text-muted-foreground">Tailored action items based on your session performance, identified patterns, and recovery goals.</p>
              </div>
            </div>
          </Card>

          <div className="grid gap-6 lg:grid-cols-2">
            {recommendations.map(({ category, icon: Icon, items }, i) => (
              <Card key={i} className="border-border bg-card p-5">
                <div className="mb-4 flex items-center gap-2">
                  <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary/10">
                    <Icon className="h-4 w-4 text-primary" />
                  </div>
                  <h4 className="text-sm font-semibold text-foreground">{category}</h4>
                </div>
                <div className="space-y-2.5">
                  {items.map((item, j) => (
                    <div key={j} className="flex items-start gap-2">
                      <div className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-primary" />
                      <p className="text-sm leading-relaxed text-foreground/80">{item}</p>
                    </div>
                  ))}
                </div>
              </Card>
            ))}
          </div>

          {/* Homework Card */}
          <Card className="border-accent/20 bg-accent/5 p-5">
            <div className="flex items-start gap-3">
              <TrendingUp className="mt-0.5 h-5 w-5 text-accent" />
              <div>
                <p className="text-sm font-semibold text-foreground">This Week's Homework</p>
                <p className="mt-1 text-sm leading-relaxed text-foreground/80">Practice identifying one all-or-nothing thought daily and write down a balanced alternative in your Thought Diary. Try to delegate at least one task per day and note how it felt.</p>
              </div>
            </div>
          </Card>
        </div>
      )}

      {/* Bottom CTA */}
      <div className="mt-8 flex items-center justify-between rounded-2xl border border-border bg-card p-5">
        <div>
          <p className="text-sm font-medium text-foreground">Ready to wrap up?</p>
          <p className="text-xs text-muted-foreground">Rate this session and unlock your next module.</p>
        </div>
        <Button className="rounded-xl" onClick={() => setScreen("session-rating")}>
          Rate This Session
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 9: Session Rating (Web) ──────────── */
export function WebSessionRatingScreen() {
  const { setScreen } = useApp()
  const [rating, setRating] = useState(0)
  const [helpful, setHelpful] = useState<string | null>(null)
  const [submitted, setSubmitted] = useState(false)

  if (submitted) {
    return (
      <div className="flex min-h-[70vh] flex-col items-center justify-center p-8">
        <div className="mx-auto mb-6 flex h-24 w-24 items-center justify-center rounded-full bg-primary/10">
          <Sparkles className="h-12 w-12 text-primary" />
        </div>
        <h2 className="mb-2 text-3xl font-bold text-foreground">Thank You!</h2>
        <p className="mb-2 text-center text-muted-foreground">Your feedback helps us improve your therapy experience.</p>
        <p className="mb-8 text-center text-sm text-muted-foreground">Session 5 of 8 is now complete. Your next session, "Setting Boundaries," will unlock soon.</p>
        <div className="flex gap-3">
          <Button variant="outline" className="h-14 rounded-2xl text-base font-semibold lg:px-8" onClick={() => setScreen("session-library")}>
            View All Sessions
          </Button>
          <Button className="h-14 rounded-2xl text-base font-semibold lg:px-8" onClick={() => setScreen("dashboard")}>
            Back to Dashboard
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-lg p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-completion")}>
        <ArrowLeft className="h-4 w-4" />
        Back
      </button>

      <h2 className="mb-2 text-2xl font-bold text-foreground">Rate Your Session</h2>
      <p className="mb-8 text-muted-foreground">Your feedback helps us personalize future sessions.</p>

      <div className="mb-8">
        <p className="mb-3 text-sm font-medium text-foreground">How would you rate this session?</p>
        <div className="flex justify-center gap-3">
          {[1, 2, 3, 4, 5].map((star) => (
            <button key={star} onClick={() => setRating(star)} className="p-1" aria-label={`Rate ${star} stars`}>
              <Star className={`h-10 w-10 transition-colors ${star <= rating ? "fill-warning text-warning" : "text-border"}`} />
            </button>
          ))}
        </div>
      </div>

      <div className="mb-8">
        <p className="mb-3 text-sm font-medium text-foreground">What was most helpful?</p>
        <div className="flex flex-wrap gap-2">
          {["AI Conversation", "Thought Challenge", "Scenario Exercise", "Key Takeaways", "Homework"].map((item) => (
            <button
              key={item}
              className={`rounded-full border px-4 py-2 text-sm transition-colors ${
                helpful === item ? "border-primary bg-primary/10 text-primary" : "border-border bg-card text-muted-foreground"
              }`}
              onClick={() => setHelpful(item)}
            >
              {item}
            </button>
          ))}
        </div>
      </div>

      <div className="mb-8">
        <p className="mb-3 text-sm font-medium text-foreground">Additional feedback (optional)</p>
        <Textarea placeholder="Share your thoughts..." className="min-h-[100px] rounded-xl border-border" />
      </div>

      <Button className="h-14 w-full rounded-2xl text-base font-semibold" disabled={rating === 0} onClick={() => setSubmitted(true)}>
        Submit Feedback
      </Button>
    </div>
  )
}

/* ──────────── Session History (Web) ──────────── */

const completedSessionsData = [
  {
    id: 1,
    title: "Understanding Burnout",
    date: "2026-01-15",
    duration: "20 min",
    module: "Foundation",
    rating: 5,
    moodBefore: 3,
    moodAfter: 6,
    beliefChange: -30,
    distortions: ["Catastrophizing", "Overgeneralizing"],
    summary: "Explored the physiological and psychological aspects of burnout. Identified key personal triggers including perfectionism and inability to disconnect from work.",
    insights: ["Your burnout pattern is strongly linked to perfectionism", "Physical symptoms (headaches, fatigue) correlate with high-stress work weeks"],
  },
  {
    id: 2,
    title: "Identifying Thought Patterns",
    date: "2026-01-22",
    duration: "25 min",
    module: "Foundation",
    rating: 4,
    moodBefore: 4,
    moodAfter: 7,
    beliefChange: -35,
    distortions: ["All-or-Nothing", "Mind Reading"],
    summary: "Learned to recognize automatic negative thoughts and their connection to emotional responses. Practiced thought logging techniques.",
    insights: ["You tend to assume the worst about colleagues' opinions", "Automatic thoughts appear strongest on Monday mornings"],
  },
  {
    id: 3,
    title: "Cognitive Distortions",
    date: "2026-02-01",
    duration: "25 min",
    module: "Core Skills",
    rating: 5,
    moodBefore: 3,
    moodAfter: 6,
    beliefChange: -40,
    distortions: ["Personalization", "Should Statements"],
    summary: "Deep dive into the 10 most common cognitive distortions. Successfully identified personalization and should statements as primary patterns.",
    insights: ["'Should statements' are your second most frequent distortion", "Reframing 'I should' to 'I could' reduced distress by 40%"],
  },
  {
    id: 4,
    title: "Thought Challenging",
    date: "2026-02-10",
    duration: "30 min",
    module: "Core Skills",
    rating: 4,
    moodBefore: 4,
    moodAfter: 7,
    beliefChange: -45,
    distortions: ["All-or-Nothing", "Labeling"],
    summary: "Applied the thought record technique to real work scenarios. Practiced generating balanced alternative thoughts and rating belief changes.",
    insights: ["Your ability to generate alternatives improved significantly", "Evidence-based challenges are most effective for your thinking style"],
  },
]

const sessionTranscripts: Record<number, { role: "ai" | "user"; speaker: string; text: string; time: string }[]> = {
  1: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Welcome. Today we will explore what burnout really is and how it manifests in your life. Can you tell me what burnout means to you?", time: "0:00" },
    { role: "user", speaker: "You", text: "I feel like I am constantly running on empty. I wake up dreading work and I used to love my job.", time: "0:25" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That shift from passion to dread is a hallmark of burnout. The Maslach Burnout Inventory identifies three dimensions: emotional exhaustion, depersonalization, and reduced personal accomplishment. Which resonates most?", time: "0:42" },
    { role: "user", speaker: "You", text: "Definitely emotional exhaustion. I feel like I have nothing left to give at the end of each day. And sometimes I catch myself being cynical about projects I used to care about.", time: "1:10" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That cynicism is depersonalization -- a protective mechanism your mind uses when emotionally depleted. What do you think triggers the exhaustion most?", time: "1:32" },
    { role: "user", speaker: "You", text: "Probably my inability to say no. I take on every task because I think if I don't do it perfectly, it won't be done right.", time: "2:05" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That is a significant insight. Perfectionism is one of the strongest predictors of burnout. Let's explore how we can begin to challenge that belief in our upcoming sessions.", time: "2:28" },
  ],
  2: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Today we will learn to identify automatic thoughts -- those quick, involuntary mental responses to situations. Think of a stressful moment this week.", time: "0:00" },
    { role: "user", speaker: "You", text: "My team lead asked me to redo a report. My immediate thought was: they think my work is terrible.", time: "0:30" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That is a classic automatic thought. Notice how quickly it appeared -- almost like a reflex. What emotion did that thought create?", time: "0:48" },
    { role: "user", speaker: "You", text: "Anxiety, mostly. And some shame. I felt my face get hot.", time: "1:12" },
    { role: "ai", speaker: "Dr. MindBridge", text: "The physical response -- the flushing -- shows how powerful these thoughts are. They activate your stress response. Now, what evidence supports and challenges the thought that they think your work is terrible?", time: "1:30" },
    { role: "user", speaker: "You", text: "I guess the evidence against it is that they praised my last three reports. And the feedback was about formatting, not content.", time: "2:00" },
  ],
  3: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Today we dive into cognitive distortions -- systematic errors in thinking that reinforce negative beliefs. Let me present you with some common ones.", time: "0:00" },
    { role: "user", speaker: "You", text: "I have been noticing the all-or-nothing thinking you mentioned last session. When I make one mistake I think the whole project is ruined.", time: "0:35" },
    { role: "ai", speaker: "Dr. MindBridge", text: "Excellent self-awareness. That is indeed all-or-nothing thinking. Another one to watch for is personalization -- taking responsibility for things outside your control. Does that sound familiar?", time: "0:55" },
    { role: "user", speaker: "You", text: "Yes, when our team missed a deadline last week I felt like it was entirely my fault, even though three people were responsible.", time: "1:20" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That is personalization in action. Recognizing these patterns is the first step toward changing them. Let's practice reframing some of your recent thoughts using what you now know.", time: "1:45" },
  ],
  4: [
    { role: "ai", speaker: "Dr. MindBridge", text: "Today we will put your skills into practice with the thought record technique. Recall a challenging situation and we will work through it step by step.", time: "0:00" },
    { role: "user", speaker: "You", text: "I got passed over for a project I wanted. My thought was: I am not good enough for the important work.", time: "0:28" },
    { role: "ai", speaker: "Dr. MindBridge", text: "Let's challenge that. What cognitive distortion might be at play here?", time: "0:45" },
    { role: "user", speaker: "You", text: "Labeling -- I am labeling myself as not good enough based on one decision. And maybe all-or-nothing -- either I get every project I want, or I am failing.", time: "1:10" },
    { role: "ai", speaker: "Dr. MindBridge", text: "Very well identified. Now can you create a balanced alternative thought?", time: "1:30" },
    { role: "user", speaker: "You", text: "Not getting this project does not define my worth. I have been assigned other important work, and there could be many reasons for the decision that have nothing to do with my ability.", time: "1:55" },
    { role: "ai", speaker: "Dr. MindBridge", text: "That is an excellent balanced thought. How much do you believe it on a scale of 0-100?", time: "2:15" },
    { role: "user", speaker: "You", text: "About 65 percent. More than I expected actually.", time: "2:30" },
  ],
}

export function WebSessionHistoryScreen() {
  const { setScreen, selectSession } = useApp()

  return (
    <div className="p-6 lg:p-8">
      <div className="mb-6 flex items-center gap-3">
        <button className="flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-library")}>
          <ArrowLeft className="h-4 w-4" />
          Back to Sessions
        </button>
      </div>

      <div className="mb-6">
        <h1 className="text-2xl font-bold text-foreground">Completed Sessions</h1>
        <p className="text-sm text-muted-foreground">{completedSessionsData.length} sessions completed -- review transcripts, summaries, and AI insights</p>
      </div>

      <div className="space-y-3">
        {completedSessionsData.map((session) => (
          <button
            key={session.id}
            className="flex w-full items-center gap-5 rounded-xl border border-border bg-card p-5 text-left transition-all hover:border-primary/50 hover:shadow-md"
            onClick={() => selectSession(session.id)}
          >
            <div className="flex h-14 w-14 items-center justify-center rounded-xl bg-success/10">
              <CheckCircle2 className="h-7 w-7 text-success" />
            </div>
            <div className="flex-1">
              <div className="flex items-center gap-2">
                <p className="text-base font-semibold text-foreground">{session.title}</p>
                <Badge variant="secondary" className="text-[10px]">{session.module}</Badge>
              </div>
              <p className="mt-1 text-sm text-muted-foreground line-clamp-1">{session.summary}</p>
              <div className="mt-2 flex items-center gap-4">
                <div className="flex items-center gap-1 text-xs text-muted-foreground">
                  <CalendarDays className="h-3 w-3" />
                  {new Date(session.date).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" })}
                </div>
                <div className="flex items-center gap-1 text-xs text-muted-foreground">
                  <Clock className="h-3 w-3" />
                  {session.duration}
                </div>
                <div className="flex items-center gap-0.5">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star key={i} className={`h-3 w-3 ${i < session.rating ? "fill-primary text-primary" : "text-border"}`} />
                  ))}
                </div>
                <div className="flex items-center gap-1 text-xs font-medium text-success">
                  <TrendingUp className="h-3 w-3" />
                  {session.beliefChange}% belief
                </div>
              </div>
            </div>
            <Eye className="h-5 w-5 text-muted-foreground" />
          </button>
        ))}
      </div>
    </div>
  )
}

/* ──────────── Session Detail (Web) ──────────── */
export function WebSessionDetailScreen() {
  const { setScreen, selectedSessionId } = useApp()
  const [activeTab, setActiveTab] = useState<"summary" | "transcript" | "insights">("summary")
  const [transcriptExpanded, setTranscriptExpanded] = useState(false)

  const session = completedSessionsData.find((s) => s.id === selectedSessionId) || completedSessionsData[0]
  const transcript = sessionTranscripts[session.id] || []

  const tabs = [
    { id: "summary" as const, label: "Summary", icon: BarChart3 },
    { id: "transcript" as const, label: "Transcript", icon: FileText },
    { id: "insights" as const, label: "AI Insights", icon: Lightbulb },
  ]

  return (
    <div className="mx-auto max-w-4xl p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("session-history")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Completed Sessions
      </button>

      {/* Header */}
      <div className="mb-6 flex items-start justify-between">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-success/10">
            <CheckCircle2 className="h-7 w-7 text-success" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-bold text-foreground">{session.title}</h1>
              <Badge variant="secondary">{session.module}</Badge>
            </div>
            <div className="mt-1 flex items-center gap-4 text-sm text-muted-foreground">
              <span className="flex items-center gap-1">
                <CalendarDays className="h-3.5 w-3.5" />
                {new Date(session.date).toLocaleDateString("en-US", { month: "long", day: "numeric", year: "numeric" })}
              </span>
              <span className="flex items-center gap-1">
                <Clock className="h-3.5 w-3.5" />
                {session.duration}
              </span>
              <span className="flex items-center gap-0.5">
                {Array.from({ length: 5 }).map((_, i) => (
                  <Star key={i} className={`h-3.5 w-3.5 ${i < session.rating ? "fill-primary text-primary" : "text-border"}`} />
                ))}
              </span>
            </div>
          </div>
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

      {/* Summary Tab */}
      {activeTab === "summary" && (
        <div className="space-y-6">
          <Card className="border-border bg-card p-6">
            <div className="flex items-start gap-3">
              <Brain className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <h3 className="text-base font-semibold text-foreground">AI Session Summary</h3>
                <p className="mt-2 text-sm leading-relaxed text-foreground/80">{session.summary}</p>
              </div>
            </div>
          </Card>

          <div className="grid gap-4 lg:grid-cols-4">
            <Card className="border-border bg-card p-4">
              <p className="text-xs text-muted-foreground">Duration</p>
              <p className="mt-1 text-lg font-bold text-foreground">{session.duration}</p>
            </Card>
            <Card className="border-border bg-card p-4">
              <p className="text-xs text-muted-foreground">Mood Shift</p>
              <p className="mt-1 text-lg font-bold text-foreground">{session.moodBefore} <ArrowRight className="inline h-4 w-4 text-success" /> <span className="text-success">{session.moodAfter}</span></p>
            </Card>
            <Card className="border-success/30 bg-success/5 p-4">
              <p className="text-xs text-muted-foreground">Belief Change</p>
              <p className="mt-1 text-lg font-bold text-success">{session.beliefChange}%</p>
            </Card>
            <Card className="border-border bg-card p-4">
              <p className="text-xs text-muted-foreground">Rating</p>
              <div className="mt-1 flex items-center gap-0.5">
                {Array.from({ length: 5 }).map((_, i) => (
                  <Star key={i} className={`h-5 w-5 ${i < session.rating ? "fill-primary text-primary" : "text-border"}`} />
                ))}
              </div>
            </Card>
          </div>

          <Card className="border-border bg-card p-6">
            <h3 className="mb-3 text-base font-semibold text-foreground">Distortions Identified</h3>
            <div className="flex flex-wrap gap-2">
              {session.distortions.map((d) => (
                <Badge key={d} variant="outline" className="border-warning/30 bg-warning/5 text-warning-foreground">{d}</Badge>
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
                <span className="text-sm text-muted-foreground">{transcript.length} messages -- {session.duration} duration</span>
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
            <button
              className="flex w-full items-center justify-center gap-2 rounded-xl border border-dashed border-border py-3 text-sm text-muted-foreground transition-colors hover:border-primary hover:text-primary"
              onClick={() => setTranscriptExpanded(true)}
            >
              <ChevronDown className="h-4 w-4" />
              Show {transcript.length - 4} more messages
            </button>
          )}
        </div>
      )}

      {/* AI Insights Tab */}
      {activeTab === "insights" && (
        <div className="space-y-4">
          <Card className="border-primary/20 bg-primary/5 p-4">
            <div className="flex items-start gap-3">
              <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-sm font-medium text-foreground">AI-Generated Insights</p>
                <p className="text-xs text-muted-foreground">Based on your conversation and responses during this session.</p>
              </div>
            </div>
          </Card>

          {session.insights.map((insight, i) => (
            <Card key={i} className="border-l-4 border-l-primary border-border bg-card p-5">
              <div className="flex items-start gap-3">
                <Lightbulb className="mt-0.5 h-5 w-5 text-primary" />
                <p className="text-sm leading-relaxed text-foreground/80">{insight}</p>
              </div>
            </Card>
          ))}

          <Card className="border-border bg-card p-5">
            <h4 className="mb-3 text-sm font-semibold text-foreground">Distortions Found</h4>
            <div className="space-y-2">
              {session.distortions.map((d, i) => (
                <div key={i} className="flex items-center gap-3 rounded-lg bg-warning/5 p-3">
                  <AlertTriangle className="h-4 w-4 text-warning" />
                  <span className="text-sm text-foreground">{d}</span>
                </div>
              ))}
            </div>
          </Card>

          <Card className="border-border bg-card p-5">
            <h4 className="mb-3 text-sm font-semibold text-foreground">Recovery Progress</h4>
            <div className="flex items-center gap-3">
              <div className="text-center">
                <p className="text-2xl font-bold text-foreground">{session.moodBefore}</p>
                <p className="text-[10px] text-muted-foreground">Before</p>
              </div>
              <div className="h-px flex-1 bg-gradient-to-r from-destructive/30 to-success/30" />
              <div className="text-center">
                <p className="text-2xl font-bold text-success">{session.moodAfter}</p>
                <p className="text-[10px] text-muted-foreground">After</p>
              </div>
            </div>
            <p className="mt-3 text-center text-sm text-success font-medium">Belief in distorted thought reduced by {Math.abs(session.beliefChange)}%</p>
          </Card>
        </div>
      )}
    </div>
  )
}
