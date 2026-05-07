"use client"

import React, { useState } from "react"
import { useApp } from "@/lib/app-context"
import { BottomNav } from "@/components/flows/dashboard"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
  ArrowLeft,
  ArrowRight,
  TrendingUp,
  TrendingDown,
  Flame,
  Brain,
  BookOpen,
  Target,
  Award,
  Star,
  Calendar,
  CheckCircle2,
  Sparkles,
  Trophy,
  Zap,
  Heart,
} from "lucide-react"

/* ──────────── Screen 1: Weekly Overview ──────────── */
export function ProgressWeeklyScreen() {
  const { setScreen, sessionsCompleted, streakDays, weeklyMood, burnoutScore } = useApp()

  const avgMood = Math.round((weeklyMood.reduce((a, b) => a + b, 0) / weeklyMood.length) * 10) / 10
  const prevAvgMood = 4.2
  const moodChange = Math.round((avgMood - prevAvgMood) * 10) / 10

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pb-4 pt-4">
        <div className="mb-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button onClick={() => setScreen("dashboard")}>
              <ArrowLeft className="h-5 w-5 text-foreground" />
            </button>
            <h1 className="text-xl font-bold text-foreground">Your Progress</h1>
          </div>
          <Badge variant="secondary" className="text-xs">
            <Calendar className="mr-1 h-3 w-3" />
            Week 3
          </Badge>
        </div>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto px-6">
        {/* Key Stats */}
        <div className="grid grid-cols-3 gap-3">
          <Card className="border-border bg-card p-3 text-center">
            <Flame className="mx-auto mb-1 h-5 w-5 text-chart-1" />
            <p className="text-lg font-bold text-foreground">{streakDays}</p>
            <p className="text-[10px] text-muted-foreground">Day Streak</p>
          </Card>
          <Card className="border-border bg-card p-3 text-center">
            <Brain className="mx-auto mb-1 h-5 w-5 text-primary" />
            <p className="text-lg font-bold text-foreground">{sessionsCompleted}</p>
            <p className="text-[10px] text-muted-foreground">Sessions</p>
          </Card>
          <Card className="border-border bg-card p-3 text-center">
            <BookOpen className="mx-auto mb-1 h-5 w-5 text-accent" />
            <p className="text-lg font-bold text-foreground">12</p>
            <p className="text-[10px] text-muted-foreground">Diary Entries</p>
          </Card>
        </div>

        {/* Weekly Mood Chart */}
        <Card className="border-border bg-card p-4">
          <div className="mb-3 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground">Weekly Mood</h3>
            <div className="flex items-center gap-1">
              {moodChange > 0 ? (
                <TrendingUp className="h-4 w-4 text-success" />
              ) : (
                <TrendingDown className="h-4 w-4 text-destructive" />
              )}
              <span className={`text-xs font-medium ${moodChange > 0 ? "text-success" : "text-destructive"}`}>
                {moodChange > 0 ? "+" : ""}{moodChange}
              </span>
            </div>
          </div>
          <div className="flex items-end gap-2">
            {weeklyMood.map((val, i) => (
              <div key={i} className="flex flex-1 flex-col items-center gap-1">
                <span className="text-[10px] font-medium text-foreground">{val}</span>
                <div className="w-full overflow-hidden rounded-t-md bg-muted" style={{ height: "80px" }}>
                  <div
                    className="w-full rounded-t-md bg-primary transition-all"
                    style={{ height: `${(val / 10) * 80}px`, marginTop: `${80 - (val / 10) * 80}px` }}
                  />
                </div>
                <span className="text-[10px] text-muted-foreground">
                  {["M", "T", "W", "T", "F", "S", "S"][i]}
                </span>
              </div>
            ))}
          </div>
          <p className="mt-3 text-center text-xs text-muted-foreground">
            Average: <span className="font-medium text-foreground">{avgMood}/10</span>
          </p>
        </Card>

        {/* Burnout Recovery */}
        <Card className="border-border bg-card p-4">
          <div className="mb-3 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-foreground">Burnout Recovery</h3>
            <span className="text-xs text-success">+12% this week</span>
          </div>
          <div className="mb-2 flex items-baseline gap-2">
            <span className="text-2xl font-bold text-primary">{100 - burnoutScore}%</span>
            <span className="text-xs text-muted-foreground">recovered</span>
          </div>
          <Progress value={100 - burnoutScore} className="mb-3 h-3" />
          <div className="flex gap-2">
            {[
              { label: "Exhaustion", value: 65, prev: 45 },
              { label: "Cynicism", value: 40, prev: 30 },
              { label: "Efficacy", value: 55, prev: 40 },
            ].map(({ label, value, prev }) => (
              <div key={label} className="flex-1 rounded-lg bg-secondary p-2 text-center">
                <p className="text-xs text-muted-foreground">{label}</p>
                <p className="text-sm font-bold text-foreground">{value}%</p>
                <div className="flex items-center justify-center gap-0.5">
                  <TrendingUp className="h-3 w-3 text-success" />
                  <span className="text-[10px] text-success">+{value - prev}%</span>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Weekly Goals */}
        <Card className="border-border bg-card p-4">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Weekly Goals</h3>
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

      <div className="flex gap-3 px-6 py-3">
        <Button
          variant="outline"
          className="flex-1 rounded-xl text-xs"
          onClick={() => setScreen("progress-trends")}
        >
          View Trends
        </Button>
        <Button
          className="flex-1 rounded-xl text-xs"
          onClick={() => setScreen("progress-achievements")}
        >
          Achievements
        </Button>
      </div>
      <BottomNav />
    </div>
  )
}

/* ──────────── Screen 2: Trend Visualizations ──────────── */
export function ProgressTrendsScreen() {
  const { setScreen } = useApp()

  const monthlyData = [
    { week: "W1", mood: 3.5, burnout: 85, sessions: 2 },
    { week: "W2", mood: 4.2, burnout: 78, sessions: 3 },
    { week: "W3", mood: 5.1, burnout: 72, sessions: 4 },
    { week: "W4", mood: 5.8, burnout: 65, sessions: 3 },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("progress-weekly")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">Trends</h1>
        </div>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto px-6">
        <Tabs defaultValue="mood" className="w-full">
          <TabsList className="w-full">
            <TabsTrigger value="mood" className="flex-1 text-xs">Mood</TabsTrigger>
            <TabsTrigger value="burnout" className="flex-1 text-xs">Burnout</TabsTrigger>
            <TabsTrigger value="activity" className="flex-1 text-xs">Activity</TabsTrigger>
          </TabsList>

          <TabsContent value="mood" className="space-y-4">
            <Card className="border-border bg-card p-4">
              <h3 className="mb-4 text-sm font-semibold text-foreground">Monthly Mood Trend</h3>
              <div className="relative h-40">
                <div className="absolute inset-0 flex items-end gap-4">
                  {monthlyData.map((d, i) => (
                    <div key={i} className="flex flex-1 flex-col items-center gap-1">
                      <span className="text-xs font-medium text-foreground">{d.mood}</span>
                      <div className="w-full overflow-hidden rounded-t-lg bg-muted" style={{ height: "120px" }}>
                        <div
                          className="w-full rounded-t-lg bg-primary"
                          style={{
                            height: `${(d.mood / 10) * 120}px`,
                            marginTop: `${120 - (d.mood / 10) * 120}px`,
                          }}
                        />
                      </div>
                      <span className="text-xs text-muted-foreground">{d.week}</span>
                    </div>
                  ))}
                </div>
              </div>
              <div className="mt-3 flex items-center justify-center gap-2">
                <TrendingUp className="h-4 w-4 text-success" />
                <span className="text-sm font-medium text-success">
                  +66% improvement over 4 weeks
                </span>
              </div>
            </Card>

            <Card className="border-primary/20 bg-primary/5 p-4">
              <div className="flex items-start gap-3">
                <Sparkles className="mt-0.5 h-5 w-5 text-primary" />
                <div>
                  <p className="text-sm font-medium text-foreground">AI Insight</p>
                  <p className="text-xs leading-relaxed text-muted-foreground">
                    Your mood shows a consistent upward trend. Sessions on weekday mornings correlate with the highest mood improvement. Consider maintaining your current routine.
                  </p>
                </div>
              </div>
            </Card>
          </TabsContent>

          <TabsContent value="burnout" className="space-y-4">
            <Card className="border-border bg-card p-4">
              <h3 className="mb-4 text-sm font-semibold text-foreground">Burnout Score Over Time</h3>
              <div className="relative h-40">
                <div className="absolute inset-0 flex items-end gap-4">
                  {monthlyData.map((d, i) => (
                    <div key={i} className="flex flex-1 flex-col items-center gap-1">
                      <span className="text-xs font-medium text-foreground">{100 - d.burnout}%</span>
                      <div className="w-full overflow-hidden rounded-t-lg bg-muted" style={{ height: "120px" }}>
                        <div
                          className="w-full rounded-t-lg bg-success"
                          style={{
                            height: `${((100 - d.burnout) / 100) * 120}px`,
                            marginTop: `${120 - ((100 - d.burnout) / 100) * 120}px`,
                          }}
                        />
                      </div>
                      <span className="text-xs text-muted-foreground">{d.week}</span>
                    </div>
                  ))}
                </div>
              </div>
              <p className="mt-3 text-center text-sm text-muted-foreground">
                Recovery: <span className="font-medium text-success">15% to 35% in 4 weeks</span>
              </p>
            </Card>
          </TabsContent>

          <TabsContent value="activity" className="space-y-4">
            <Card className="border-border bg-card p-4">
              <h3 className="mb-4 text-sm font-semibold text-foreground">Activity Heatmap</h3>
              <div className="grid grid-cols-7 gap-1">
                {Array.from({ length: 28 }).map((_, i) => {
                  const intensity = Math.random()
                  return (
                    <div
                      key={i}
                      className="aspect-square rounded-sm"
                      style={{
                        backgroundColor: `oklch(${0.55 + (1 - intensity) * 0.4} ${intensity * 0.12} 190 / ${0.2 + intensity * 0.8})`,
                      }}
                    />
                  )
                })}
              </div>
              <div className="mt-2 flex items-center justify-between">
                <span className="text-[10px] text-muted-foreground">Less active</span>
                <span className="text-[10px] text-muted-foreground">More active</span>
              </div>
            </Card>
            <Card className="border-border bg-card p-4">
              <h3 className="mb-3 text-sm font-semibold text-foreground">This Month</h3>
              <div className="space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-foreground">CBT Sessions</span>
                  <span className="text-sm font-medium text-foreground">12 completed</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-foreground">Diary Entries</span>
                  <span className="text-sm font-medium text-foreground">18 written</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-foreground">Breathing Exercises</span>
                  <span className="text-sm font-medium text-foreground">24 completed</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-foreground">Total Active Days</span>
                  <span className="text-sm font-medium text-foreground">22/28</span>
                </div>
              </div>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      <div className="px-6 py-3">
        <Button
          variant="outline"
          className="h-10 w-full rounded-xl text-sm"
          onClick={() => setScreen("progress-weekly")}
        >
          Back to Overview
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 3: Achievements ──────────── */
export function ProgressAchievementsScreen() {
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
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("progress-weekly")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">Achievements</h1>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto px-6">
        {/* Summary */}
        <Card className="mb-6 border-primary/20 bg-primary/5 p-5 text-center">
          <div className="mb-2 flex justify-center">
            <Trophy className="h-10 w-10 text-primary" />
          </div>
          <p className="text-2xl font-bold text-foreground">{unlockedCount}/{achievements.length}</p>
          <p className="text-sm text-muted-foreground">Achievements Unlocked</p>
          <Progress value={(unlockedCount / achievements.length) * 100} className="mt-3 h-2" />
        </Card>

        {/* Recently Unlocked */}
        <h3 className="mb-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
          Unlocked
        </h3>
        <div className="mb-6 grid grid-cols-3 gap-3">
          {achievements
            .filter((a) => a.unlocked)
            .map(({ icon: Icon, title, color }) => (
              <div
                key={title}
                className="flex flex-col items-center gap-2 rounded-xl border border-border bg-card p-3 text-center"
              >
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-primary/10">
                  <Icon className={`h-6 w-6 ${color}`} />
                </div>
                <span className="text-[10px] font-medium leading-tight text-foreground">
                  {title}
                </span>
              </div>
            ))}
        </div>

        {/* Locked */}
        <h3 className="mb-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
          Upcoming
        </h3>
        <div className="grid grid-cols-3 gap-3">
          {achievements
            .filter((a) => !a.unlocked)
            .map(({ icon: Icon, title }) => (
              <div
                key={title}
                className="flex flex-col items-center gap-2 rounded-xl border border-border bg-muted/50 p-3 text-center opacity-50"
              >
                <div className="flex h-12 w-12 items-center justify-center rounded-full bg-muted">
                  <Icon className="h-6 w-6 text-muted-foreground" />
                </div>
                <span className="text-[10px] font-medium leading-tight text-muted-foreground">
                  {title}
                </span>
              </div>
            ))}
        </div>

        {/* Celebration Card */}
        <Card className="mt-6 border-success/20 bg-success/5 p-4">
          <div className="flex items-start gap-3">
            <Sparkles className="mt-0.5 h-5 w-5 text-success" />
            <div>
              <p className="text-sm font-medium text-foreground">Keep Going!</p>
              <p className="text-xs leading-relaxed text-muted-foreground">
                {"You're making incredible progress. Just 1 more session to unlock \"Session Master\" and you're only 7 days from the \"Consistent\" streak badge!"}
              </p>
            </div>
          </div>
        </Card>
      </div>

      <div className="px-6 py-4">
        <Button
          variant="outline"
          className="h-10 w-full rounded-xl text-sm"
          onClick={() => setScreen("progress-weekly")}
        >
          Back to Progress
        </Button>
      </div>
    </div>
  )
}
