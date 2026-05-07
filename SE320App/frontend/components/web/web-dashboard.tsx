"use client"

import React from "react"
import { useApp } from "@/lib/app-context"
import { Card } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"
import {
  Brain,
  BookOpen,
  TrendingUp,
  AlertTriangle,
  Flame,
  ArrowRight,
  Calendar,
  Target,
  Heart,
  Sparkles,
} from "lucide-react"

export function WebDashboardScreen() {
  const {
    setScreen,
    userName,
    sessionsCompleted,
    streakDays,
    burnoutScore,
    weeklyMood,
  } = useApp()

  const avgMood = Math.round(
    (weeklyMood.reduce((a, b) => a + b, 0) / weeklyMood.length) * 10
  ) / 10

  return (
    <div className="p-6 lg:p-8">
      {/* Welcome header */}
      <div className="mb-8 flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-sm text-muted-foreground">Good morning,</p>
          <h1 className="text-2xl font-bold text-foreground">{userName || "Friend"}</h1>
        </div>
        <div className="flex gap-3">
          <div className="flex items-center gap-2 rounded-xl border border-border bg-card px-4 py-2.5">
            <Flame className="h-4 w-4 text-chart-1" />
            <span className="text-sm font-semibold text-foreground">{streakDays} day streak</span>
          </div>
          <div className="flex items-center gap-2 rounded-xl border border-border bg-card px-4 py-2.5">
            <Brain className="h-4 w-4 text-primary" />
            <span className="text-sm font-semibold text-foreground">{sessionsCompleted} sessions</span>
          </div>
          <div className="flex items-center gap-2 rounded-xl border border-border bg-card px-4 py-2.5">
            <Heart className="h-4 w-4 text-accent" />
            <span className="text-sm font-semibold text-foreground">{avgMood}/10 mood</span>
          </div>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Left column */}
        <div className="space-y-6 lg:col-span-2">
          {/* Quick Actions */}
          <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
            <button
              className="flex flex-col items-start rounded-2xl border border-border bg-card p-5 text-left transition-all hover:border-primary hover:bg-primary/5 hover:shadow-md"
              onClick={() => setScreen("session-library")}
            >
              <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
                <Brain className="h-6 w-6 text-primary" />
              </div>
              <p className="text-sm font-semibold text-foreground">CBT Session</p>
              <p className="text-xs text-muted-foreground">Start therapy</p>
            </button>
            <button
              className="flex flex-col items-start rounded-2xl border border-border bg-card p-5 text-left transition-all hover:border-accent hover:bg-accent/5 hover:shadow-md"
              onClick={() => setScreen("diary-home")}
            >
              <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-accent/20">
                <BookOpen className="h-6 w-6 text-accent" />
              </div>
              <p className="text-sm font-semibold text-foreground">Thought Diary</p>
              <p className="text-xs text-muted-foreground">Record thoughts</p>
            </button>
            <button
              className="flex flex-col items-start rounded-2xl border border-border bg-card p-5 text-left transition-all hover:border-primary hover:bg-primary/5 hover:shadow-md"
              onClick={() => setScreen("progress-weekly")}
            >
              <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-chart-2/20">
                <TrendingUp className="h-6 w-6 text-chart-2" />
              </div>
              <p className="text-sm font-semibold text-foreground">Progress</p>
              <p className="text-xs text-muted-foreground">View insights</p>
            </button>
            <button
              className="flex flex-col items-start rounded-2xl border border-border bg-card p-5 text-left transition-all hover:border-destructive hover:bg-destructive/5 hover:shadow-md"
              onClick={() => setScreen("crisis-detection")}
            >
              <div className="mb-3 flex h-12 w-12 items-center justify-center rounded-xl bg-destructive/10">
                <AlertTriangle className="h-6 w-6 text-destructive" />
              </div>
              <p className="text-sm font-semibold text-foreground">Crisis Help</p>
              <p className="text-xs text-muted-foreground">Get support now</p>
            </button>
          </div>

          {/* Burnout Recovery */}
          <Card className="border-border bg-card p-6">
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-base font-semibold text-foreground">Burnout Recovery</h3>
              <Badge variant="secondary">Week 3</Badge>
            </div>
            <div className="mb-3 flex items-baseline gap-2">
              <span className="text-3xl font-bold text-primary">{100 - burnoutScore}%</span>
              <span className="text-sm text-muted-foreground">recovered</span>
            </div>
            <Progress value={100 - burnoutScore} className="mb-4 h-3" />
            <div className="grid grid-cols-3 gap-4">
              {[
                { label: "Exhaustion", value: 65, change: "+20%" },
                { label: "Cynicism", value: 40, change: "+10%" },
                { label: "Efficacy", value: 55, change: "+15%" },
              ].map(({ label, value, change }) => (
                <div key={label} className="rounded-xl bg-secondary p-3 text-center">
                  <p className="text-xs text-muted-foreground">{label}</p>
                  <p className="text-lg font-bold text-foreground">{value}%</p>
                  <p className="text-xs text-success">{change}</p>
                </div>
              ))}
            </div>
          </Card>

          {/* Weekly Mood */}
          <Card className="border-border bg-card p-6">
            <h3 className="mb-4 text-base font-semibold text-foreground">Weekly Mood</h3>
            <div className="flex items-end gap-3">
              {weeklyMood.map((val, i) => (
                <div key={i} className="flex flex-1 flex-col items-center gap-1.5">
                  <span className="text-xs font-medium text-foreground">{val}</span>
                  <div className="w-full overflow-hidden rounded-t-lg bg-muted" style={{ height: "100px" }}>
                    <div
                      className="w-full rounded-t-lg bg-primary transition-all"
                      style={{ height: `${(val / 10) * 100}px`, marginTop: `${100 - (val / 10) * 100}px` }}
                    />
                  </div>
                  <span className="text-xs text-muted-foreground">
                    {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"][i]}
                  </span>
                </div>
              ))}
            </div>
            <p className="mt-4 text-center text-sm text-muted-foreground">
              Average: <span className="font-medium text-foreground">{avgMood}/10</span>
            </p>
          </Card>
        </div>

        {/* Right column */}
        <div className="space-y-6">
          {/* Today's Schedule */}
          <Card className="border-border bg-card p-6">
            <div className="mb-4 flex items-center justify-between">
              <h3 className="text-base font-semibold text-foreground">Today</h3>
              <Calendar className="h-4 w-4 text-muted-foreground" />
            </div>
            <div className="space-y-4">
              <button
                className="flex w-full items-center gap-3 text-left"
                onClick={() => setScreen("session-library")}
              >
                <div className="h-12 w-0.5 rounded-full bg-primary" />
                <div className="flex-1">
                  <p className="text-sm font-medium text-foreground">CBT Session: Managing Workload</p>
                  <p className="text-xs text-muted-foreground">10:00 AM - 25 min</p>
                </div>
                <ArrowRight className="h-4 w-4 text-muted-foreground" />
              </button>
              <button
                className="flex w-full items-center gap-3 text-left"
                onClick={() => setScreen("diary-home")}
              >
                <div className="h-12 w-0.5 rounded-full bg-accent" />
                <div className="flex-1">
                  <p className="text-sm font-medium text-foreground">Evening Check-in</p>
                  <p className="text-xs text-muted-foreground">8:00 PM - 5 min</p>
                </div>
                <ArrowRight className="h-4 w-4 text-muted-foreground" />
              </button>
            </div>
          </Card>

          {/* Weekly Goal */}
          <Card className="border-border bg-card p-6">
            <div className="mb-4 flex items-center gap-2">
              <Target className="h-4 w-4 text-primary" />
              <h3 className="text-base font-semibold text-foreground">Weekly Goals</h3>
            </div>
            <div className="space-y-4">
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

          {/* AI Insight */}
          <Card className="border-primary/20 bg-primary/5 p-5">
            <div className="flex items-start gap-3">
              <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
              <div>
                <p className="text-sm font-medium text-foreground">AI Insight</p>
                <p className="text-xs leading-relaxed text-muted-foreground">
                  Your mood has improved 66% over 4 weeks. Morning sessions correlate with the highest mood improvement.
                </p>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
