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
} from "lucide-react"

export function DashboardScreen() {
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
    <div className="flex h-full flex-col bg-background">
      {/* Header */}
      <div className="bg-primary px-6 pb-6 pt-4">
        <div className="mb-4 flex items-center justify-between">
          <div>
            <p className="text-sm text-primary-foreground/70">Good morning,</p>
            <h1 className="text-xl font-bold text-primary-foreground">
              {userName || "Friend"}
            </h1>
          </div>
          <button
            className="flex h-10 w-10 items-center justify-center rounded-full bg-primary-foreground/10"
            onClick={() => setScreen("crisis-detection")}
            aria-label="Emergency support"
          >
            <AlertTriangle className="h-5 w-5 text-primary-foreground" />
          </button>
        </div>
        <div className="flex gap-3">
          <div className="flex-1 rounded-xl bg-primary-foreground/10 p-3">
            <div className="flex items-center gap-1.5 text-primary-foreground/70">
              <Flame className="h-3.5 w-3.5" />
              <span className="text-xs">Streak</span>
            </div>
            <p className="text-lg font-bold text-primary-foreground">{streakDays} days</p>
          </div>
          <div className="flex-1 rounded-xl bg-primary-foreground/10 p-3">
            <div className="flex items-center gap-1.5 text-primary-foreground/70">
              <Brain className="h-3.5 w-3.5" />
              <span className="text-xs">Sessions</span>
            </div>
            <p className="text-lg font-bold text-primary-foreground">{sessionsCompleted}</p>
          </div>
          <div className="flex-1 rounded-xl bg-primary-foreground/10 p-3">
            <div className="flex items-center gap-1.5 text-primary-foreground/70">
              <Heart className="h-3.5 w-3.5" />
              <span className="text-xs">Mood</span>
            </div>
            <p className="text-lg font-bold text-primary-foreground">{avgMood}/10</p>
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 space-y-4 px-6 pt-6">
        {/* Burnout Score */}
        <Card className="border-border bg-card p-4">
          <div className="mb-2 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground">Burnout Recovery</h3>
            <Badge variant="secondary" className="text-xs">
              Week 3
            </Badge>
          </div>
          <div className="mb-2 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-primary">
              {100 - burnoutScore}%
            </span>
            <span className="text-xs text-muted-foreground">recovered</span>
          </div>
          <Progress value={100 - burnoutScore} className="h-2" />
        </Card>

        {/* Quick Actions */}
        <div className="grid grid-cols-2 gap-3">
          <button
            className="flex flex-col items-start rounded-2xl border border-border bg-card p-4 text-left transition-colors hover:border-primary hover:bg-primary/5"
            onClick={() => setScreen("session-library")}
          >
            <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10">
              <Brain className="h-5 w-5 text-primary" />
            </div>
            <p className="text-sm font-semibold text-foreground">CBT Session</p>
            <p className="text-xs text-muted-foreground">Start therapy</p>
          </button>
          <button
            className="flex flex-col items-start rounded-2xl border border-border bg-card p-4 text-left transition-colors hover:border-accent hover:bg-accent/5"
            onClick={() => setScreen("diary-home")}
          >
            <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-accent/20">
              <BookOpen className="h-5 w-5 text-accent" />
            </div>
            <p className="text-sm font-semibold text-foreground">Thought Diary</p>
            <p className="text-xs text-muted-foreground">Record thoughts</p>
          </button>
          <button
            className="flex flex-col items-start rounded-2xl border border-border bg-card p-4 text-left transition-colors hover:border-primary hover:bg-primary/5"
            onClick={() => setScreen("progress-weekly")}
          >
            <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-chart-2/20">
              <TrendingUp className="h-5 w-5 text-chart-2" />
            </div>
            <p className="text-sm font-semibold text-foreground">Progress</p>
            <p className="text-xs text-muted-foreground">View insights</p>
          </button>
          <button
            className="flex flex-col items-start rounded-2xl border border-border bg-card p-4 text-left transition-colors hover:border-destructive hover:bg-destructive/5"
            onClick={() => setScreen("crisis-detection")}
          >
            <div className="mb-3 flex h-10 w-10 items-center justify-center rounded-xl bg-destructive/10">
              <AlertTriangle className="h-5 w-5 text-destructive" />
            </div>
            <p className="text-sm font-semibold text-foreground">Crisis Help</p>
            <p className="text-xs text-muted-foreground">Get support now</p>
          </button>
        </div>

        {/* Today's Schedule */}
        <Card className="border-border bg-card p-4">
          <div className="mb-3 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground">Today</h3>
            <Calendar className="h-4 w-4 text-muted-foreground" />
          </div>
          <div className="space-y-3">
            <button
              className="flex w-full items-center gap-3 text-left"
              onClick={() => setScreen("session-library")}
            >
              <div className="h-10 w-0.5 rounded-full bg-primary" />
              <div className="flex-1">
                <p className="text-sm font-medium text-foreground">
                  CBT Session: Managing Workload
                </p>
                <p className="text-xs text-muted-foreground">10:00 AM - 25 min</p>
              </div>
              <ArrowRight className="h-4 w-4 text-muted-foreground" />
            </button>
            <button
              className="flex w-full items-center gap-3 text-left"
              onClick={() => setScreen("diary-home")}
            >
              <div className="h-10 w-0.5 rounded-full bg-accent" />
              <div className="flex-1">
                <p className="text-sm font-medium text-foreground">Evening Check-in</p>
                <p className="text-xs text-muted-foreground">8:00 PM - 5 min</p>
              </div>
              <ArrowRight className="h-4 w-4 text-muted-foreground" />
            </button>
          </div>
        </Card>

        {/* Goal Check */}
        <Card className="border-border bg-card p-4">
          <div className="mb-3 flex items-center gap-2">
            <Target className="h-4 w-4 text-primary" />
            <h3 className="text-sm font-semibold text-foreground">Weekly Goal</h3>
          </div>
          <p className="mb-2 text-xs text-muted-foreground">
            Complete 3 CBT sessions this week
          </p>
          <div className="flex items-center gap-2">
            <Progress value={66} className="h-2 flex-1" />
            <span className="text-xs font-medium text-foreground">2/3</span>
          </div>
        </Card>
      </div>

      {/* Bottom Nav */}
      <BottomNav />
    </div>
  )
}

export function BottomNav() {
  const { setScreen, screen } = useApp()
  const items = [
    { icon: Brain, label: "Sessions", target: "session-library" as const },
    { icon: BookOpen, label: "Diary", target: "diary-home" as const },
    { icon: Target, label: "Home", target: "dashboard" as const },
    { icon: TrendingUp, label: "Progress", target: "progress-weekly" as const },
    { icon: AlertTriangle, label: "Crisis", target: "crisis-detection" as const },
  ]

  return (
    <nav className="flex items-center justify-around border-t border-border bg-card px-2 pb-2 pt-2">
      {items.map(({ icon: Icon, label, target }) => {
        const isActive =
          screen === target ||
          (target === "session-library" && screen.startsWith("session")) ||
          (target === "diary-home" && screen.startsWith("diary")) ||
          (target === "progress-weekly" && screen.startsWith("progress")) ||
          (target === "crisis-detection" && screen.startsWith("crisis"))
        return (
          <button
            key={label}
            className={`flex flex-col items-center gap-0.5 rounded-lg px-3 py-1.5 text-xs transition-colors ${
              isActive
                ? "text-primary"
                : "text-muted-foreground hover:text-foreground"
            }`}
            onClick={() => setScreen(target)}
          >
            <Icon className="h-5 w-5" />
            <span className="text-[10px]">{label}</span>
          </button>
        )
      })}
    </nav>
  )
}
