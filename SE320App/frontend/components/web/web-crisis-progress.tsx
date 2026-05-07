"use client"

import React, { useState } from "react"
import { useApp } from "@/lib/app-context"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  ArrowLeft,
  ArrowRight,
  AlertTriangle,
  Phone,
  MessageCircle,
  Heart,
  Shield,
  Users,
  ChevronRight,
  CheckCircle2,
  Wind,
  Eye,
  Ear,
  Hand,
  Droplets,
  TrendingUp,
  TrendingDown,
  Flame,
  Brain,
  BookOpen,
  Target,
  Award,
  Star,
  Calendar,
  Sparkles,
  Trophy,
  Zap,
} from "lucide-react"

/* ──────────── Crisis Screens ──────────── */

export function WebCrisisDetectionScreen() {
  const { setScreen } = useApp()
  return (
    <div className="p-6 lg:p-8">
      <div className="mx-auto max-w-3xl">
        <Card className="mb-6 border-destructive/20 bg-destructive/5 p-6">
          <div className="flex items-start gap-4">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-destructive/10">
              <AlertTriangle className="h-6 w-6 text-destructive" />
            </div>
            <div>
              <p className="text-lg font-semibold text-foreground">Are you in crisis right now?</p>
              <p className="text-sm text-muted-foreground">If you are in immediate danger, please call emergency services (911).</p>
            </div>
          </div>
        </Card>

        <h2 className="mb-4 text-xl font-bold text-foreground">How can we help?</h2>

        <div className="grid gap-4 lg:grid-cols-3">
          <button
            className="flex flex-col items-start rounded-2xl border border-destructive/20 bg-card p-6 text-left transition-all hover:bg-destructive/5 hover:shadow-md"
            onClick={() => setScreen("crisis-coping")}
          >
            <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-xl bg-destructive/10">
              <Wind className="h-7 w-7 text-destructive" />
            </div>
            <p className="text-base font-semibold text-foreground">Coping Strategies</p>
            <p className="text-sm text-muted-foreground">Guided breathing and grounding exercises</p>
          </button>

          <button
            className="flex flex-col items-start rounded-2xl border border-border bg-card p-6 text-left transition-all hover:bg-primary/5 hover:shadow-md"
            onClick={() => setScreen("crisis-resources")}
          >
            <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-xl bg-primary/10">
              <Phone className="h-7 w-7 text-primary" />
            </div>
            <p className="text-base font-semibold text-foreground">Talk to Someone</p>
            <p className="text-sm text-muted-foreground">Crisis hotlines and therapist contact</p>
          </button>

          <button
            className="flex flex-col items-start rounded-2xl border border-border bg-card p-6 text-left transition-all hover:bg-accent/5 hover:shadow-md"
            onClick={() => setScreen("crisis-safety-plan")}
          >
            <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-xl bg-accent/20">
              <Shield className="h-7 w-7 text-accent" />
            </div>
            <p className="text-base font-semibold text-foreground">Safety Plan</p>
            <p className="text-sm text-muted-foreground">Review your personalized safety plan</p>
          </button>
        </div>

        <Card className="mt-6 border-primary/20 bg-primary/5 p-5">
          <div className="flex items-start gap-3">
            <Heart className="mt-0.5 h-5 w-5 text-primary" />
            <div>
              <p className="text-sm font-medium text-foreground">You are not alone</p>
              <p className="text-xs leading-relaxed text-muted-foreground">It takes courage to reach out. Whatever you are going through, help is available 24/7.</p>
            </div>
          </div>
        </Card>

        <div className="mt-6">
          <Button variant="destructive" className="h-14 w-full rounded-2xl text-base font-semibold lg:w-auto lg:px-12" onClick={() => setScreen("crisis-resources")}>
            <Phone className="mr-2 h-5 w-5" />
            Call 988 Suicide & Crisis Lifeline
          </Button>
        </div>
      </div>
    </div>
  )
}

export function WebCrisisCopingScreen() {
  const { setScreen } = useApp()
  const [activeExercise, setActiveExercise] = useState<string | null>(null)
  const [breathPhase, setBreathPhase] = useState("Ready")
  const [breathActive, setBreathActive] = useState(false)

  const startBreathing = () => {
    setBreathActive(true)
    const phases = ["Breathe In...", "Hold...", "Breathe Out...", "Hold..."]
    let i = 0
    const interval = setInterval(() => {
      setBreathPhase(phases[i % 4])
      i++
      if (i >= 16) { clearInterval(interval); setBreathActive(false); setBreathPhase("Complete!") }
    }, 2000)
  }

  return (
    <div className="p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("crisis-detection")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Crisis Support
      </button>
      <h1 className="mb-6 text-2xl font-bold text-foreground">Coping Strategies</h1>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card className="border-border bg-card p-6">
          <h3 className="mb-2 text-base font-semibold text-foreground">4-7-8 Breathing</h3>
          <p className="mb-4 text-sm text-muted-foreground">A calming breathing technique to activate your relaxation response.</p>
          {activeExercise === "breathing" ? (
            <div className="flex flex-col items-center gap-4">
              <div className={`flex h-32 w-32 items-center justify-center rounded-full border-4 transition-all duration-1000 ${breathActive ? "border-primary bg-primary/10 scale-110" : "border-border bg-card"}`}>
                <span className="text-center text-sm font-medium text-foreground">{breathPhase}</span>
              </div>
              {!breathActive && breathPhase === "Ready" && <Button onClick={startBreathing} className="rounded-xl">Start</Button>}
              {breathPhase === "Complete!" && <p className="text-sm text-success">Well done! Feeling calmer?</p>}
            </div>
          ) : (
            <Button variant="outline" className="w-full rounded-xl" onClick={() => setActiveExercise("breathing")}>
              <Wind className="mr-2 h-4 w-4" />
              Try This
            </Button>
          )}
        </Card>

        <Card className="border-border bg-card p-6">
          <h3 className="mb-2 text-base font-semibold text-foreground">5-4-3-2-1 Grounding</h3>
          <p className="mb-4 text-sm text-muted-foreground">Use your senses to ground yourself in the present moment.</p>
          {activeExercise === "grounding" ? (
            <div className="space-y-3">
              {[
                { icon: Eye, count: 5, sense: "things you can see" },
                { icon: Hand, count: 4, sense: "things you can touch" },
                { icon: Ear, count: 3, sense: "things you can hear" },
                { icon: Droplets, count: 2, sense: "things you can smell" },
                { icon: Heart, count: 1, sense: "thing you can taste" },
              ].map(({ icon: Icon, count, sense }) => (
                <div key={sense} className="flex items-center gap-3 rounded-lg bg-secondary p-3">
                  <Icon className="h-4 w-4 text-primary" />
                  <span className="text-sm text-foreground">Name <strong>{count}</strong> {sense}</span>
                </div>
              ))}
            </div>
          ) : (
            <Button variant="outline" className="w-full rounded-xl" onClick={() => setActiveExercise("grounding")}>
              <Eye className="mr-2 h-4 w-4" />
              Try This
            </Button>
          )}
        </Card>
      </div>

      <div className="mt-6">
        <Button variant="outline" className="h-12 rounded-2xl lg:px-8" onClick={() => setScreen("crisis-resources")}>
          Need more help? Talk to someone
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}

export function WebCrisisResourcesScreen() {
  const { setScreen } = useApp()
  return (
    <div className="p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("crisis-detection")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Crisis Support
      </button>
      <h1 className="mb-6 text-2xl font-bold text-foreground">Emergency Resources</h1>

      <div className="mx-auto max-w-3xl space-y-4">
        <Card className="border-destructive/20 bg-destructive/5 p-5">
          <div className="flex items-center gap-4">
            <Phone className="h-6 w-6 text-destructive" />
            <div className="flex-1">
              <p className="text-base font-bold text-foreground">988 Suicide & Crisis Lifeline</p>
              <p className="text-sm text-muted-foreground">Call or text 988 - Available 24/7</p>
            </div>
            <Button size="sm" variant="destructive" className="rounded-lg">Call</Button>
          </div>
        </Card>
        {[
          { icon: MessageCircle, name: "Crisis Text Line", desc: "Text HOME to 741741", color: "text-primary" },
          { icon: Phone, name: "SAMHSA Helpline", desc: "1-800-662-4357 - Free & confidential", color: "text-primary" },
          { icon: Users, name: "Your Therapist", desc: "Dr. Sarah Chen - Last session 3 days ago", color: "text-accent" },
        ].map(({ icon: Icon, name, desc, color }) => (
          <Card key={name} className="border-border bg-card p-5">
            <div className="flex items-center gap-4">
              <Icon className={`h-6 w-6 ${color}`} />
              <div className="flex-1">
                <p className="text-base font-bold text-foreground">{name}</p>
                <p className="text-sm text-muted-foreground">{desc}</p>
              </div>
              <Button size="sm" variant="outline" className="rounded-lg">Contact</Button>
            </div>
          </Card>
        ))}

        <h3 className="mt-4 text-base font-semibold text-foreground">Trusted Contacts</h3>
        <div className="grid gap-3 lg:grid-cols-3">
          {[
            { name: "Alex (Partner)", phone: "(555) 123-4567" },
            { name: "Mom", phone: "(555) 987-6543" },
            { name: "Jamie (Best Friend)", phone: "(555) 246-8135" },
          ].map(({ name, phone }) => (
            <div key={name} className="flex items-center gap-3 rounded-xl border border-border bg-card p-4">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-secondary text-sm font-bold text-secondary-foreground">{name[0]}</div>
              <div className="flex-1">
                <p className="text-sm font-medium text-foreground">{name}</p>
                <p className="text-xs text-muted-foreground">{phone}</p>
              </div>
              <Button size="sm" variant="ghost"><Phone className="h-4 w-4" /></Button>
            </div>
          ))}
        </div>
      </div>

      <div className="mt-6">
        <Button className="h-12 rounded-2xl lg:px-8" onClick={() => setScreen("crisis-safety-plan")}>
          View Safety Plan
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}

export function WebCrisisSafetyPlanScreen() {
  const { setScreen } = useApp()
  const steps = [
    { num: 1, title: "Warning Signs", items: ["Racing thoughts about work", "Feeling physically tense", "Withdrawing from others"] },
    { num: 2, title: "Coping Strategies", items: ["4-7-8 breathing exercise", "Go for a 10 minute walk", "Listen to calming music"] },
    { num: 3, title: "Social Contacts", items: ["Call Jamie", "Visit the park", "Join online support group"] },
    { num: 4, title: "People to Help", items: ["Dr. Sarah Chen (Therapist)", "Alex (Partner)", "Mom"] },
    { num: 5, title: "Professional Resources", items: ["988 Suicide & Crisis Lifeline", "Crisis Text Line: 741741", "Local ER: City General Hospital"] },
    { num: 6, title: "Environment Safety", items: ["Remove or secure items of concern", "Stay with a trusted person", "Go to a safe location"] },
  ]

  return (
    <div className="p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("crisis-detection")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Crisis Support
      </button>
      <h1 className="mb-2 text-2xl font-bold text-foreground">My Safety Plan</h1>
      <p className="mb-6 text-muted-foreground">Follow these steps in order when you feel overwhelmed.</p>
      <div className="grid gap-4 lg:grid-cols-2 xl:grid-cols-3">
        {steps.map(({ num, title, items }) => (
          <Card key={num} className="border-border bg-card p-5">
            <div className="mb-3 flex items-center gap-3">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-sm font-bold text-primary-foreground">{num}</div>
              <h3 className="text-sm font-semibold text-foreground">{title}</h3>
            </div>
            <div className="space-y-2">
              {items.map((item) => (
                <div key={item} className="flex items-center gap-2">
                  <CheckCircle2 className="h-3.5 w-3.5 text-muted-foreground" />
                  <span className="text-sm text-foreground">{item}</span>
                </div>
              ))}
            </div>
          </Card>
        ))}
      </div>
      <p className="mt-6 text-center text-sm text-muted-foreground">
        {"My reason for living: My family and the life I'm building."}
      </p>
      <div className="mt-4 text-center">
        <Button variant="outline" className="h-12 rounded-2xl lg:px-8" onClick={() => setScreen("dashboard")}>Back to Dashboard</Button>
      </div>
    </div>
  )
}

/* ──────────── Progress Screens ──────────── */

export function WebProgressWeeklyScreen() {
  const { setScreen, sessionsCompleted, streakDays, weeklyMood, burnoutScore } = useApp()
  const avgMood = Math.round((weeklyMood.reduce((a, b) => a + b, 0) / weeklyMood.length) * 10) / 10
  const prevAvgMood = 4.2
  const moodChange = Math.round((avgMood - prevAvgMood) * 10) / 10

  return (
    <div className="p-6 lg:p-8">
      <div className="mb-6 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Your Progress</h1>
          <p className="text-sm text-muted-foreground">Week 3 of your recovery program</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" className="rounded-xl" onClick={() => setScreen("progress-trends")}>Trends</Button>
          <Button className="rounded-xl" onClick={() => setScreen("progress-achievements")}>Achievements</Button>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-6 lg:col-span-2">
          {/* Key Stats */}
          <div className="grid grid-cols-3 gap-4">
            <Card className="border-border bg-card p-5 text-center">
              <Flame className="mx-auto mb-2 h-6 w-6 text-chart-1" />
              <p className="text-2xl font-bold text-foreground">{streakDays}</p>
              <p className="text-xs text-muted-foreground">Day Streak</p>
            </Card>
            <Card className="border-border bg-card p-5 text-center">
              <Brain className="mx-auto mb-2 h-6 w-6 text-primary" />
              <p className="text-2xl font-bold text-foreground">{sessionsCompleted}</p>
              <p className="text-xs text-muted-foreground">Sessions</p>
            </Card>
            <Card className="border-border bg-card p-5 text-center">
              <BookOpen className="mx-auto mb-2 h-6 w-6 text-accent" />
              <p className="text-2xl font-bold text-foreground">12</p>
              <p className="text-xs text-muted-foreground">Diary Entries</p>
            </Card>
          </div>

          {/* Weekly Mood Chart */}
          <Card className="border-border bg-card p-6">
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-base font-semibold text-foreground">Weekly Mood</h3>
              <div className="flex items-center gap-1">
                {moodChange > 0 ? <TrendingUp className="h-4 w-4 text-success" /> : <TrendingDown className="h-4 w-4 text-destructive" />}
                <span className={`text-sm font-medium ${moodChange > 0 ? "text-success" : "text-destructive"}`}>
                  {moodChange > 0 ? "+" : ""}{moodChange}
                </span>
              </div>
            </div>
            <div className="flex items-end gap-3">
              {weeklyMood.map((val, i) => (
                <div key={i} className="flex flex-1 flex-col items-center gap-1.5">
                  <span className="text-xs font-medium text-foreground">{val}</span>
                  <div className="w-full overflow-hidden rounded-t-lg bg-muted" style={{ height: "100px" }}>
                    <div className="w-full rounded-t-lg bg-primary transition-all" style={{ height: `${(val / 10) * 100}px`, marginTop: `${100 - (val / 10) * 100}px` }} />
                  </div>
                  <span className="text-xs text-muted-foreground">{["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"][i]}</span>
                </div>
              ))}
            </div>
            <p className="mt-4 text-center text-sm text-muted-foreground">Average: <span className="font-medium text-foreground">{avgMood}/10</span></p>
          </Card>
        </div>

        {/* Right column */}
        <div className="space-y-6">
          <Card className="border-border bg-card p-6">
            <h3 className="mb-3 text-base font-semibold text-foreground">Burnout Recovery</h3>
            <div className="mb-3 flex items-baseline gap-2">
              <span className="text-3xl font-bold text-primary">{100 - burnoutScore}%</span>
              <span className="text-xs text-success">+12% this week</span>
            </div>
            <Progress value={100 - burnoutScore} className="mb-4 h-3" />
            <div className="space-y-2">
              {[
                { label: "Exhaustion", value: 65 },
                { label: "Cynicism", value: 40 },
                { label: "Efficacy", value: 55 },
              ].map(({ label, value }) => (
                <div key={label} className="flex items-center justify-between rounded-lg bg-secondary p-2">
                  <span className="text-xs text-muted-foreground">{label}</span>
                  <span className="text-sm font-bold text-foreground">{value}%</span>
                </div>
              ))}
            </div>
          </Card>

          <Card className="border-border bg-card p-6">
            <h3 className="mb-3 text-base font-semibold text-foreground">Weekly Goals</h3>
            <div className="space-y-3">
              {[
                { label: "Complete 3 CBT sessions", progress: 66, current: "2/3" },
                { label: "Write 5 diary entries", progress: 60, current: "3/5" },
                { label: "Practice breathing daily", progress: 86, current: "6/7" },
              ].map(({ label, progress, current }) => (
                <div key={label}>
                  <div className="mb-1 flex items-center justify-between">
                    <span className="text-xs text-foreground">{label}</span>
                    <span className="text-xs font-medium text-primary">{current}</span>
                  </div>
                  <Progress value={progress} className="h-1.5" />
                </div>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}

export function WebProgressTrendsScreen() {
  const { setScreen } = useApp()
  const monthlyData = [
    { week: "W1", mood: 3.5, burnout: 85, sessions: 2 },
    { week: "W2", mood: 4.2, burnout: 78, sessions: 3 },
    { week: "W3", mood: 5.1, burnout: 72, sessions: 4 },
    { week: "W4", mood: 5.8, burnout: 65, sessions: 3 },
  ]

  return (
    <div className="p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("progress-weekly")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Progress
      </button>
      <h1 className="mb-6 text-2xl font-bold text-foreground">Trends</h1>

      <Tabs defaultValue="mood" className="w-full">
        <TabsList className="mb-6 w-full lg:w-auto">
          <TabsTrigger value="mood" className="flex-1 lg:flex-none lg:px-6">Mood</TabsTrigger>
          <TabsTrigger value="burnout" className="flex-1 lg:flex-none lg:px-6">Burnout</TabsTrigger>
          <TabsTrigger value="activity" className="flex-1 lg:flex-none lg:px-6">Activity</TabsTrigger>
        </TabsList>

        <TabsContent value="mood" className="grid gap-6 lg:grid-cols-2">
          <Card className="border-border bg-card p-6">
            <h3 className="mb-4 text-base font-semibold text-foreground">Monthly Mood Trend</h3>
            <div className="relative h-48">
              <div className="absolute inset-0 flex items-end gap-6">
                {monthlyData.map((d, i) => (
                  <div key={i} className="flex flex-1 flex-col items-center gap-1.5">
                    <span className="text-sm font-medium text-foreground">{d.mood}</span>
                    <div className="w-full overflow-hidden rounded-t-lg bg-muted" style={{ height: "160px" }}>
                      <div className="w-full rounded-t-lg bg-primary" style={{ height: `${(d.mood / 10) * 160}px`, marginTop: `${160 - (d.mood / 10) * 160}px` }} />
                    </div>
                    <span className="text-xs text-muted-foreground">{d.week}</span>
                  </div>
                ))}
              </div>
            </div>
            <div className="mt-4 flex items-center justify-center gap-2">
              <TrendingUp className="h-4 w-4 text-success" />
              <span className="text-sm font-medium text-success">+66% improvement over 4 weeks</span>
            </div>
          </Card>
          <Card className="border-primary/20 bg-primary/5 p-6">
            <div className="flex items-start gap-3">
              <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-base font-medium text-foreground">AI Insight</p>
                <p className="text-sm leading-relaxed text-muted-foreground">Your mood shows a consistent upward trend. Sessions on weekday mornings correlate with the highest mood improvement. Consider maintaining your current routine.</p>
              </div>
            </div>
          </Card>
        </TabsContent>

        <TabsContent value="burnout" className="space-y-6">
          <Card className="border-border bg-card p-6">
            <h3 className="mb-4 text-base font-semibold text-foreground">Burnout Score Over Time</h3>
            <div className="relative h-48">
              <div className="absolute inset-0 flex items-end gap-6">
                {monthlyData.map((d, i) => (
                  <div key={i} className="flex flex-1 flex-col items-center gap-1.5">
                    <span className="text-sm font-medium text-foreground">{100 - d.burnout}%</span>
                    <div className="w-full overflow-hidden rounded-t-lg bg-muted" style={{ height: "160px" }}>
                      <div className="w-full rounded-t-lg bg-success" style={{ height: `${((100 - d.burnout) / 100) * 160}px`, marginTop: `${160 - ((100 - d.burnout) / 100) * 160}px` }} />
                    </div>
                    <span className="text-xs text-muted-foreground">{d.week}</span>
                  </div>
                ))}
              </div>
            </div>
            <p className="mt-4 text-center text-sm text-muted-foreground">Recovery: <span className="font-medium text-success">15% to 35% in 4 weeks</span></p>
          </Card>
        </TabsContent>

        <TabsContent value="activity" className="grid gap-6 lg:grid-cols-2">
          <Card className="border-border bg-card p-6">
            <h3 className="mb-4 text-base font-semibold text-foreground">Activity Heatmap</h3>
            <div className="grid grid-cols-7 gap-1.5">
              {Array.from({ length: 28 }).map((_, i) => {
                const intensity = Math.random()
                return (
                  <div key={i} className="aspect-square rounded-md" style={{ backgroundColor: `oklch(${0.55 + (1 - intensity) * 0.4} ${intensity * 0.12} 190 / ${0.2 + intensity * 0.8})` }} />
                )
              })}
            </div>
            <div className="mt-2 flex items-center justify-between">
              <span className="text-xs text-muted-foreground">Less active</span>
              <span className="text-xs text-muted-foreground">More active</span>
            </div>
          </Card>
          <Card className="border-border bg-card p-6">
            <h3 className="mb-4 text-base font-semibold text-foreground">This Month</h3>
            <div className="space-y-3">
              {[
                { label: "CBT Sessions", value: "12 completed" },
                { label: "Diary Entries", value: "18 written" },
                { label: "Breathing Exercises", value: "24 completed" },
                { label: "Total Active Days", value: "22/28" },
              ].map(({ label, value }) => (
                <div key={label} className="flex items-center justify-between">
                  <span className="text-sm text-foreground">{label}</span>
                  <span className="text-sm font-medium text-foreground">{value}</span>
                </div>
              ))}
            </div>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}

export function WebProgressAchievementsScreen() {
  const { setScreen, streakDays, sessionsCompleted } = useApp()
  const achievements = [
    { icon: Flame, title: "7-Day Streak", desc: "Used the app 7 days in a row", unlocked: streakDays >= 7, color: "text-chart-1" },
    { icon: Brain, title: "Session Master", desc: "Complete 5 CBT sessions", unlocked: sessionsCompleted >= 5, color: "text-primary" },
    { icon: BookOpen, title: "Thoughtful Writer", desc: "Write 10 diary entries", unlocked: true, color: "text-accent" },
    { icon: Target, title: "Goal Setter", desc: "Set your weekly goals", unlocked: true, color: "text-chart-2" },
    { icon: Heart, title: "Self-Compassion", desc: "Complete a reframing exercise", unlocked: true, color: "text-destructive" },
    { icon: Zap, title: "Quick Learner", desc: "Complete first session under 20 min", unlocked: true, color: "text-warning" },
    { icon: Trophy, title: "Halfway Hero", desc: "Complete 50% of the program", unlocked: true, color: "text-primary" },
    { icon: Star, title: "Consistent", desc: "Maintain a 14-day streak", unlocked: false, color: "text-chart-4" },
    { icon: Award, title: "CBT Graduate", desc: "Complete all 8 sessions", unlocked: false, color: "text-chart-5" },
  ]
  const unlockedCount = achievements.filter((a) => a.unlocked).length

  return (
    <div className="p-6 lg:p-8">
      <button className="mb-6 flex items-center gap-2 text-sm text-muted-foreground hover:text-foreground" onClick={() => setScreen("progress-weekly")}>
        <ArrowLeft className="h-4 w-4" />
        Back to Progress
      </button>
      <h1 className="mb-6 text-2xl font-bold text-foreground">Achievements</h1>

      <div className="mx-auto max-w-3xl">
        <Card className="mb-8 border-primary/20 bg-primary/5 p-6 text-center">
          <Trophy className="mx-auto mb-2 h-12 w-12 text-primary" />
          <p className="text-3xl font-bold text-foreground">{unlockedCount}/{achievements.length}</p>
          <p className="text-muted-foreground">Achievements Unlocked</p>
          <Progress value={(unlockedCount / achievements.length) * 100} className="mx-auto mt-3 h-2 w-48" />
        </Card>

        <h3 className="mb-4 text-xs font-semibold uppercase tracking-wider text-muted-foreground">Unlocked</h3>
        <div className="mb-8 grid grid-cols-3 gap-4 lg:grid-cols-4 xl:grid-cols-5">
          {achievements.filter((a) => a.unlocked).map(({ icon: Icon, title, color }) => (
            <div key={title} className="flex flex-col items-center gap-2 rounded-xl border border-border bg-card p-4 text-center transition-all hover:shadow-md">
              <div className="flex h-14 w-14 items-center justify-center rounded-full bg-primary/10">
                <Icon className={`h-7 w-7 ${color}`} />
              </div>
              <span className="text-xs font-medium text-foreground">{title}</span>
            </div>
          ))}
        </div>

        <h3 className="mb-4 text-xs font-semibold uppercase tracking-wider text-muted-foreground">Upcoming</h3>
        <div className="grid grid-cols-3 gap-4 lg:grid-cols-4 xl:grid-cols-5">
          {achievements.filter((a) => !a.unlocked).map(({ icon: Icon, title }) => (
            <div key={title} className="flex flex-col items-center gap-2 rounded-xl border border-border bg-muted/50 p-4 text-center opacity-50">
              <div className="flex h-14 w-14 items-center justify-center rounded-full bg-muted">
                <Icon className="h-7 w-7 text-muted-foreground" />
              </div>
              <span className="text-xs font-medium text-muted-foreground">{title}</span>
            </div>
          ))}
        </div>

        <Card className="mt-8 border-success/20 bg-success/5 p-5">
          <div className="flex items-start gap-3">
            <Sparkles className="mt-0.5 h-5 w-5 text-success" />
            <div>
              <p className="text-sm font-medium text-foreground">Keep Going!</p>
              <p className="text-xs leading-relaxed text-muted-foreground">
                {"You're making incredible progress. Just 1 more session to unlock \"Session Master\" and 7 days from the \"Consistent\" streak badge!"}
              </p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}
