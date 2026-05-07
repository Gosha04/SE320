"use client"

import React, { useState } from "react"
import { useApp } from "@/lib/app-context"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Progress } from "@/components/ui/progress"
import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  Heart,
  Shield,
  Sparkles,
  ArrowRight,
  User,
  Stethoscope,
  Lock,
  CheckCircle2,
  ChevronRight,
  Brain,
} from "lucide-react"

/* ──────────── Screen 1: Welcome ──────────── */
export function WelcomeScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background">
      <div className="flex flex-1 flex-col items-center justify-center px-8">
        <div className="mb-8 flex h-24 w-24 items-center justify-center rounded-3xl bg-primary/10">
          <Brain className="h-12 w-12 text-primary" />
        </div>
        <h1 className="mb-3 text-center text-3xl font-bold tracking-tight text-foreground text-balance">
          MindBridge
        </h1>
        <p className="mb-2 text-center text-lg font-medium text-foreground/80">
          Your Path to Recovery
        </p>
        <p className="mb-12 text-center text-sm leading-relaxed text-muted-foreground">
          Evidence-based CBT therapy for burnout recovery, powered by AI and guided by compassion.
        </p>
        <div className="flex w-full flex-col gap-3">
          <Button
            className="h-14 w-full rounded-2xl text-base font-semibold"
            onClick={() => setScreen("user-type")}
          >
            Get Started
            <ArrowRight className="ml-2 h-5 w-5" />
          </Button>
          <Button
            variant="ghost"
            className="text-sm text-muted-foreground"
            onClick={() => setScreen("dashboard")}
          >
            I already have an account
          </Button>
        </div>
      </div>
      <div className="flex items-center justify-center gap-6 px-8 pb-8 pt-4">
        <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
          <Shield className="h-3.5 w-3.5" />
          <span>HIPAA Compliant</span>
        </div>
        <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
          <Lock className="h-3.5 w-3.5" />
          <span>End-to-End Encrypted</span>
        </div>
      </div>
    </div>
  )
}

/* ──────────── Screen 2: User Type ──────────── */
export function UserTypeScreen() {
  const { setScreen, setUserType } = useApp()
  return (
    <div className="flex h-full flex-col bg-background px-6 pt-6">
      <button
        className="mb-6 self-start text-sm text-muted-foreground"
        onClick={() => setScreen("welcome")}
      >
        {"< Back"}
      </button>
      <h2 className="mb-2 text-2xl font-bold text-foreground">How are you joining?</h2>
      <p className="mb-8 text-sm text-muted-foreground">
        This helps us personalize your experience.
      </p>
      <div className="flex flex-col gap-4">
        <button
          className="flex items-center gap-4 rounded-2xl border border-border bg-card p-5 text-left transition-colors hover:border-primary hover:bg-primary/5"
          onClick={() => {
            setUserType("individual")
            setScreen("privacy")
          }}
        >
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
            <User className="h-6 w-6 text-primary" />
          </div>
          <div className="flex-1">
            <p className="font-semibold text-foreground">On My Own</p>
            <p className="text-sm text-muted-foreground">
              Self-guided burnout recovery program
            </p>
          </div>
          <ChevronRight className="h-5 w-5 text-muted-foreground" />
        </button>
        <button
          className="flex items-center gap-4 rounded-2xl border border-border bg-card p-5 text-left transition-colors hover:border-primary hover:bg-primary/5"
          onClick={() => {
            setUserType("therapist-referred")
            setScreen("privacy")
          }}
        >
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-accent/20">
            <Stethoscope className="h-6 w-6 text-accent" />
          </div>
          <div className="flex-1">
            <p className="font-semibold text-foreground">Therapist Referred</p>
            <p className="text-sm text-muted-foreground">
              My therapist recommended this app
            </p>
          </div>
          <ChevronRight className="h-5 w-5 text-muted-foreground" />
        </button>
      </div>
      <div className="mt-auto flex justify-center gap-1.5 pb-8 pt-6">
        <div className="h-1.5 w-6 rounded-full bg-primary" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
      </div>
    </div>
  )
}

/* ──────────── Screen 3: Privacy Policy ──────────── */
export function PrivacyScreen() {
  const { setScreen } = useApp()
  const [accepted, setAccepted] = useState(false)
  return (
    <div className="flex h-full flex-col bg-background px-6 pt-6">
      <button
        className="mb-6 self-start text-sm text-muted-foreground"
        onClick={() => setScreen("user-type")}
      >
        {"< Back"}
      </button>
      <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/10">
        <Shield className="h-7 w-7 text-primary" />
      </div>
      <h2 className="mb-2 text-2xl font-bold text-foreground">Your Privacy Matters</h2>
      <p className="mb-6 text-sm text-muted-foreground">
        We take your privacy seriously. Please review our policies.
      </p>
      <div className="flex-1 space-y-4 overflow-y-auto">
        <Card className="border-border bg-card p-4">
          <h3 className="mb-1 text-sm font-semibold text-foreground">HIPAA Compliance</h3>
          <p className="text-xs leading-relaxed text-muted-foreground">
            All health information is protected under HIPAA regulations. Your data is encrypted at rest and in transit.
          </p>
        </Card>
        <Card className="border-border bg-card p-4">
          <h3 className="mb-1 text-sm font-semibold text-foreground">Data Ownership</h3>
          <p className="text-xs leading-relaxed text-muted-foreground">
            You own your data. You can export or delete your information at any time from the settings.
          </p>
        </Card>
        <Card className="border-border bg-card p-4">
          <h3 className="mb-1 text-sm font-semibold text-foreground">AI Processing</h3>
          <p className="text-xs leading-relaxed text-muted-foreground">
            AI interactions are processed securely. No conversation data is used for model training.
          </p>
        </Card>
        <Card className="border-border bg-card p-4">
          <h3 className="mb-1 text-sm font-semibold text-foreground">Crisis Protocol</h3>
          <p className="text-xs leading-relaxed text-muted-foreground">
            If immediate risk is detected, we may share necessary information with emergency services to ensure your safety.
          </p>
        </Card>
      </div>
      <div className="pb-6 pt-4">
        <label className="mb-4 flex cursor-pointer items-start gap-3">
          <input
            type="checkbox"
            checked={accepted}
            onChange={(e) => setAccepted(e.target.checked)}
            className="mt-0.5 h-5 w-5 rounded border-border text-primary accent-primary"
          />
          <span className="text-xs leading-relaxed text-muted-foreground">
            I have read and agree to the Privacy Policy, Terms of Service, and HIPAA Notice of Privacy Practices.
          </span>
        </label>
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          disabled={!accepted}
          onClick={() => setScreen("assessment")}
        >
          Continue
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 4: Assessment ──────────── */
export function AssessmentScreen() {
  const { setScreen, setAssessmentScore } = useApp()
  const [currentQ, setCurrentQ] = useState(0)
  const [answers, setAnswers] = useState<number[]>([])

  const questions = [
    { q: "How often do you feel emotionally drained from work?", cat: "Exhaustion" },
    { q: "Do you feel a lack of accomplishment in your work?", cat: "Efficacy" },
    { q: "Have you become more cynical about your work?", cat: "Cynicism" },
    { q: "How often do you have difficulty concentrating?", cat: "Cognitive" },
    { q: "Do you feel disconnected from colleagues?", cat: "Detachment" },
  ]

  const options = ["Never", "Rarely", "Sometimes", "Often", "Always"]

  const handleAnswer = (score: number) => {
    const newAnswers = [...answers, score]
    setAnswers(newAnswers)
    if (currentQ < questions.length - 1) {
      setCurrentQ(currentQ + 1)
    } else {
      const total = Math.round(
        (newAnswers.reduce((a, b) => a + b, 0) / (questions.length * 4)) * 100
      )
      setAssessmentScore(total)
      setScreen("personalization")
    }
  }

  return (
    <div className="flex h-full flex-col bg-background px-6 pt-6">
      <button
        className="mb-6 self-start text-sm text-muted-foreground"
        onClick={() => setScreen("privacy")}
      >
        {"< Back"}
      </button>
      <div className="mb-6">
        <div className="mb-2 flex items-center justify-between">
          <Badge variant="secondary" className="text-xs">
            {questions[currentQ].cat}
          </Badge>
          <span className="text-xs text-muted-foreground">
            {currentQ + 1} of {questions.length}
          </span>
        </div>
        <Progress value={((currentQ + 1) / questions.length) * 100} className="h-2" />
      </div>
      <h2 className="mb-2 text-xl font-bold text-foreground">Burnout Assessment</h2>
      <p className="mb-8 text-base leading-relaxed text-foreground/80">
        {questions[currentQ].q}
      </p>
      <div className="flex flex-col gap-3">
        {options.map((opt, i) => (
          <button
            key={opt}
            className="flex items-center justify-between rounded-xl border border-border bg-card px-5 py-4 text-left transition-colors hover:border-primary hover:bg-primary/5"
            onClick={() => handleAnswer(i)}
          >
            <span className="text-sm font-medium text-foreground">{opt}</span>
            <span className="text-xs text-muted-foreground">{i}/4</span>
          </button>
        ))}
      </div>
      <div className="mt-auto flex justify-center gap-1.5 pb-8 pt-6">
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
        <div className="h-1.5 w-6 rounded-full bg-primary" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
        <div className="h-1.5 w-1.5 rounded-full bg-border" />
      </div>
    </div>
  )
}

/* ──────────── Screen 5: Personalization ──────────── */
export function PersonalizationScreen() {
  const { setScreen, setUserName, userName } = useApp()
  const [name, setName] = useState(userName)
  const [goals, setGoals] = useState<string[]>([])

  const goalOptions = [
    "Reduce stress",
    "Better sleep",
    "Work-life balance",
    "Manage anxiety",
    "Build resilience",
    "Improve focus",
  ]

  const toggleGoal = (goal: string) => {
    setGoals((prev) =>
      prev.includes(goal) ? prev.filter((g) => g !== goal) : [...prev, goal]
    )
  }

  return (
    <div className="flex h-full flex-col bg-background px-6 pt-6">
      <button
        className="mb-6 self-start text-sm text-muted-foreground"
        onClick={() => setScreen("assessment")}
      >
        {"< Back"}
      </button>
      <h2 className="mb-2 text-2xl font-bold text-foreground">Personalize Your Journey</h2>
      <p className="mb-8 text-sm text-muted-foreground">
        Help us tailor your recovery program.
      </p>
      <div className="mb-6">
        <label className="mb-2 block text-sm font-medium text-foreground">
          What should we call you?
        </label>
        <Input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Your first name"
          className="h-12 rounded-xl"
        />
      </div>
      <div className="mb-6">
        <label className="mb-3 block text-sm font-medium text-foreground">
          What are your main goals?
        </label>
        <div className="flex flex-wrap gap-2">
          {goalOptions.map((goal) => (
            <button
              key={goal}
              className={`rounded-full border px-4 py-2 text-sm font-medium transition-colors ${
                goals.includes(goal)
                  ? "border-primary bg-primary/10 text-primary"
                  : "border-border bg-card text-muted-foreground hover:border-primary/50"
              }`}
              onClick={() => toggleGoal(goal)}
            >
              {goal}
            </button>
          ))}
        </div>
      </div>
      <div className="mb-6">
        <label className="mb-3 block text-sm font-medium text-foreground">
          Preferred session time
        </label>
        <div className="flex gap-3">
          {["Morning", "Afternoon", "Evening"].map((time) => (
            <button
              key={time}
              className="flex-1 rounded-xl border border-border bg-card px-3 py-3 text-center text-sm font-medium text-foreground transition-colors hover:border-primary hover:bg-primary/5"
            >
              {time}
            </button>
          ))}
        </div>
      </div>
      <div className="mt-auto pb-6 pt-4">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          disabled={!name.trim()}
          onClick={() => {
            setUserName(name)
            setScreen("onboarding-complete")
          }}
        >
          Continue
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 6: Onboarding Complete ──────────── */
export function OnboardingCompleteScreen() {
  const { setScreen, userName, assessmentScore } = useApp()

  const severity =
    assessmentScore < 30 ? "Mild" : assessmentScore < 60 ? "Moderate" : "Significant"
  const severityColor =
    assessmentScore < 30
      ? "text-success"
      : assessmentScore < 60
      ? "text-warning"
      : "text-destructive"

  return (
    <div className="flex h-full flex-col items-center bg-background px-6 pt-12">
      <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-success/10">
        <CheckCircle2 className="h-10 w-10 text-success" />
      </div>
      <h2 className="mb-2 text-2xl font-bold text-foreground">
        {"Welcome, "}{userName || "Friend"}{"!"}
      </h2>
      <p className="mb-8 text-center text-sm text-muted-foreground">
        Your personalized recovery program is ready.
      </p>
      <Card className="mb-6 w-full border-border bg-card p-5">
        <h3 className="mb-3 text-sm font-semibold text-muted-foreground">
          ASSESSMENT RESULTS
        </h3>
        <div className="mb-3 flex items-baseline gap-2">
          <span className={`text-3xl font-bold ${severityColor}`}>
            {severity}
          </span>
          <span className="text-sm text-muted-foreground">burnout level</span>
        </div>
        <Progress value={assessmentScore} className="mb-3 h-3" />
        <p className="text-xs leading-relaxed text-muted-foreground">
          Based on your responses, we have created a tailored 8-week CBT program focused on your specific needs.
        </p>
      </Card>
      <Card className="mb-6 w-full border-border bg-card p-5">
        <h3 className="mb-3 text-sm font-semibold text-muted-foreground">YOUR PLAN</h3>
        <div className="space-y-3">
          {[
            { icon: Brain, label: "Weekly CBT sessions", desc: "3 interactive sessions" },
            { icon: Heart, label: "Daily thought diary", desc: "Track patterns" },
            { icon: Sparkles, label: "AI-powered insights", desc: "Personalized guidance" },
          ].map(({ icon: Icon, label, desc }) => (
            <div key={label} className="flex items-center gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary/10">
                <Icon className="h-4 w-4 text-primary" />
              </div>
              <div>
                <p className="text-sm font-medium text-foreground">{label}</p>
                <p className="text-xs text-muted-foreground">{desc}</p>
              </div>
            </div>
          ))}
        </div>
      </Card>
      <div className="mt-auto w-full pb-6">
        <Button
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("dashboard")}
        >
          Go to Dashboard
          <ArrowRight className="ml-2 h-5 w-5" />
        </Button>
      </div>
    </div>
  )
}
