"use client"

import React, { useState } from "react"
import { useApp } from "@/lib/app-context"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
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
} from "lucide-react"

/* ──────────── Screen 1: Crisis Detection ──────────── */
export function CrisisDetectionScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background">
      <div className="bg-destructive/5 px-6 pb-6 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("dashboard")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">Crisis Support</h1>
        </div>

        <Card className="border-destructive/20 bg-card p-4">
          <div className="flex items-start gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-destructive/10">
              <AlertTriangle className="h-5 w-5 text-destructive" />
            </div>
            <div>
              <p className="text-sm font-semibold text-foreground">
                Are you in crisis right now?
              </p>
              <p className="text-xs text-muted-foreground">
                If you are in immediate danger, please call emergency services (911).
              </p>
            </div>
          </div>
        </Card>
      </div>

      <div className="flex-1 space-y-3 px-6 pt-4">
        <p className="text-sm font-medium text-foreground">How can we help?</p>

        <button
          className="flex w-full items-center gap-4 rounded-xl border border-destructive/20 bg-card p-4 text-left transition-colors hover:bg-destructive/5"
          onClick={() => setScreen("crisis-coping")}
        >
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-destructive/10">
            <Wind className="h-6 w-6 text-destructive" />
          </div>
          <div className="flex-1">
            <p className="text-sm font-semibold text-foreground">
              I need coping strategies
            </p>
            <p className="text-xs text-muted-foreground">
              Guided breathing and grounding exercises
            </p>
          </div>
          <ChevronRight className="h-5 w-5 text-muted-foreground" />
        </button>

        <button
          className="flex w-full items-center gap-4 rounded-xl border border-border bg-card p-4 text-left transition-colors hover:bg-primary/5"
          onClick={() => setScreen("crisis-resources")}
        >
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10">
            <Phone className="h-6 w-6 text-primary" />
          </div>
          <div className="flex-1">
            <p className="text-sm font-semibold text-foreground">
              Talk to someone now
            </p>
            <p className="text-xs text-muted-foreground">
              Crisis hotlines and therapist contact
            </p>
          </div>
          <ChevronRight className="h-5 w-5 text-muted-foreground" />
        </button>

        <button
          className="flex w-full items-center gap-4 rounded-xl border border-border bg-card p-4 text-left transition-colors hover:bg-primary/5"
          onClick={() => setScreen("crisis-safety-plan")}
        >
          <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-accent/20">
            <Shield className="h-6 w-6 text-accent" />
          </div>
          <div className="flex-1">
            <p className="text-sm font-semibold text-foreground">
              My safety plan
            </p>
            <p className="text-xs text-muted-foreground">
              Review your personalized safety plan
            </p>
          </div>
          <ChevronRight className="h-5 w-5 text-muted-foreground" />
        </button>

        <Card className="border-primary/20 bg-primary/5 p-4">
          <div className="flex items-start gap-3">
            <Heart className="mt-0.5 h-5 w-5 text-primary" />
            <div>
              <p className="text-sm font-medium text-foreground">You are not alone</p>
              <p className="text-xs leading-relaxed text-muted-foreground">
                It takes courage to reach out. Whatever you are going through, help is available 24/7.
              </p>
            </div>
          </div>
        </Card>
      </div>

      <div className="px-6 pb-6 pt-4">
        <Button
          variant="destructive"
          className="h-14 w-full rounded-2xl text-base font-semibold"
          onClick={() => setScreen("crisis-resources")}
        >
          <Phone className="mr-2 h-5 w-5" />
          Call 988 Suicide & Crisis Lifeline
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 2: Coping Strategies ──────────── */
export function CrisisCopingScreen() {
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
      if (i >= 16) {
        clearInterval(interval)
        setBreathActive(false)
        setBreathPhase("Complete!")
      }
    }, 2000)
  }

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("crisis-detection")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">Coping Strategies</h1>
        </div>
      </div>

      <div className="flex-1 space-y-4 overflow-y-auto px-6">
        {/* Breathing Exercise */}
        <Card className="border-border bg-card p-4">
          <h3 className="mb-2 text-sm font-semibold text-foreground">
            4-7-8 Breathing
          </h3>
          <p className="mb-4 text-xs text-muted-foreground">
            A calming breathing technique to activate your relaxation response.
          </p>
          {activeExercise === "breathing" ? (
            <div className="flex flex-col items-center gap-4">
              <div
                className={`flex h-28 w-28 items-center justify-center rounded-full border-4 transition-all duration-1000 ${
                  breathActive
                    ? "border-primary bg-primary/10 scale-110"
                    : "border-border bg-card"
                }`}
              >
                <span className="text-center text-sm font-medium text-foreground">
                  {breathPhase}
                </span>
              </div>
              {!breathActive && breathPhase === "Ready" && (
                <Button onClick={startBreathing} className="rounded-xl">
                  Start
                </Button>
              )}
              {breathPhase === "Complete!" && (
                <p className="text-sm text-success">Well done! Feeling calmer?</p>
              )}
            </div>
          ) : (
            <Button
              variant="outline"
              className="w-full rounded-xl"
              onClick={() => setActiveExercise("breathing")}
            >
              <Wind className="mr-2 h-4 w-4" />
              Try This
            </Button>
          )}
        </Card>

        {/* 5-4-3-2-1 Grounding */}
        <Card className="border-border bg-card p-4">
          <h3 className="mb-2 text-sm font-semibold text-foreground">
            5-4-3-2-1 Grounding
          </h3>
          <p className="mb-4 text-xs text-muted-foreground">
            Use your senses to ground yourself in the present moment.
          </p>
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
                  <span className="text-sm text-foreground">
                    Name <strong>{count}</strong> {sense}
                  </span>
                </div>
              ))}
            </div>
          ) : (
            <Button
              variant="outline"
              className="w-full rounded-xl"
              onClick={() => setActiveExercise("grounding")}
            >
              <Eye className="mr-2 h-4 w-4" />
              Try This
            </Button>
          )}
        </Card>

        {/* Progressive Muscle Relaxation */}
        <Card className="border-border bg-card p-4">
          <h3 className="mb-2 text-sm font-semibold text-foreground">
            Progressive Muscle Relaxation
          </h3>
          <p className="mb-4 text-xs text-muted-foreground">
            Tense and release muscle groups to reduce physical tension.
          </p>
          <Button variant="outline" className="w-full rounded-xl">
            <Hand className="mr-2 h-4 w-4" />
            Start Guided Exercise
          </Button>
        </Card>
      </div>

      <div className="px-6 pb-6 pt-4">
        <Button
          variant="outline"
          className="h-12 w-full rounded-2xl"
          onClick={() => setScreen("crisis-resources")}
        >
          Need more help? Talk to someone
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 3: Emergency Resources ──────────── */
export function CrisisResourcesScreen() {
  const { setScreen } = useApp()
  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("crisis-detection")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">Emergency Resources</h1>
        </div>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto px-6">
        <Card className="border-destructive/20 bg-destructive/5 p-4">
          <div className="flex items-center gap-3">
            <Phone className="h-6 w-6 text-destructive" />
            <div className="flex-1">
              <p className="text-sm font-bold text-foreground">988 Suicide & Crisis Lifeline</p>
              <p className="text-xs text-muted-foreground">Call or text 988 - Available 24/7</p>
            </div>
            <Button size="sm" variant="destructive" className="rounded-lg">
              Call
            </Button>
          </div>
        </Card>

        <Card className="border-border bg-card p-4">
          <div className="flex items-center gap-3">
            <MessageCircle className="h-6 w-6 text-primary" />
            <div className="flex-1">
              <p className="text-sm font-bold text-foreground">Crisis Text Line</p>
              <p className="text-xs text-muted-foreground">Text HOME to 741741</p>
            </div>
            <Button size="sm" variant="outline" className="rounded-lg">
              Text
            </Button>
          </div>
        </Card>

        <Card className="border-border bg-card p-4">
          <div className="flex items-center gap-3">
            <Phone className="h-6 w-6 text-primary" />
            <div className="flex-1">
              <p className="text-sm font-bold text-foreground">SAMHSA Helpline</p>
              <p className="text-xs text-muted-foreground">1-800-662-4357 - Free & confidential</p>
            </div>
            <Button size="sm" variant="outline" className="rounded-lg">
              Call
            </Button>
          </div>
        </Card>

        <Card className="border-border bg-card p-4">
          <div className="flex items-center gap-3">
            <Users className="h-6 w-6 text-accent" />
            <div className="flex-1">
              <p className="text-sm font-bold text-foreground">Your Therapist</p>
              <p className="text-xs text-muted-foreground">Dr. Sarah Chen - Last session 3 days ago</p>
            </div>
            <Button size="sm" variant="outline" className="rounded-lg">
              Call
            </Button>
          </div>
        </Card>

        <div className="pt-2">
          <h3 className="mb-3 text-sm font-semibold text-foreground">Trusted Contacts</h3>
          <div className="space-y-2">
            {[
              { name: "Alex (Partner)", phone: "(555) 123-4567" },
              { name: "Mom", phone: "(555) 987-6543" },
              { name: "Jamie (Best Friend)", phone: "(555) 246-8135" },
            ].map(({ name, phone }) => (
              <div key={name} className="flex items-center gap-3 rounded-xl border border-border bg-card p-3">
                <div className="flex h-9 w-9 items-center justify-center rounded-full bg-secondary text-sm font-bold text-secondary-foreground">
                  {name[0]}
                </div>
                <div className="flex-1">
                  <p className="text-sm font-medium text-foreground">{name}</p>
                  <p className="text-xs text-muted-foreground">{phone}</p>
                </div>
                <Button size="sm" variant="ghost" className="rounded-lg">
                  <Phone className="h-4 w-4" />
                </Button>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="px-6 pb-6 pt-4">
        <Button
          className="h-12 w-full rounded-2xl"
          onClick={() => setScreen("crisis-safety-plan")}
        >
          View Safety Plan
          <ArrowRight className="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  )
}

/* ──────────── Screen 4: Safety Plan ──────────── */
export function CrisisSafetyPlanScreen() {
  const { setScreen } = useApp()

  const steps = [
    {
      num: 1,
      title: "Warning Signs",
      items: ["Racing thoughts about work", "Feeling physically tense", "Withdrawing from others"],
    },
    {
      num: 2,
      title: "Coping Strategies",
      items: ["4-7-8 breathing exercise", "Go for a 10 minute walk", "Listen to calming music"],
    },
    {
      num: 3,
      title: "Social Contacts for Distraction",
      items: ["Call Jamie", "Visit the park", "Join online support group"],
    },
    {
      num: 4,
      title: "People to Ask for Help",
      items: ["Dr. Sarah Chen (Therapist)", "Alex (Partner)", "Mom"],
    },
    {
      num: 5,
      title: "Professional & Crisis Resources",
      items: ["988 Suicide & Crisis Lifeline", "Crisis Text Line: 741741", "Local ER: City General Hospital"],
    },
    {
      num: 6,
      title: "Making the Environment Safe",
      items: ["Remove or secure items of concern", "Stay with a trusted person", "Go to a safe location"],
    },
  ]

  return (
    <div className="flex h-full flex-col bg-background">
      <div className="px-6 pt-4">
        <div className="mb-4 flex items-center gap-3">
          <button onClick={() => setScreen("crisis-detection")}>
            <ArrowLeft className="h-5 w-5 text-foreground" />
          </button>
          <h1 className="text-xl font-bold text-foreground">My Safety Plan</h1>
        </div>
        <p className="mb-4 text-sm text-muted-foreground">
          Follow these steps in order when you feel overwhelmed.
        </p>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto px-6">
        {steps.map(({ num, title, items }) => (
          <Card key={num} className="border-border bg-card p-4">
            <div className="mb-2 flex items-center gap-3">
              <div className="flex h-7 w-7 items-center justify-center rounded-full bg-primary text-xs font-bold text-primary-foreground">
                {num}
              </div>
              <h3 className="text-sm font-semibold text-foreground">{title}</h3>
            </div>
            <div className="ml-10 space-y-1.5">
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

      <div className="px-6 pb-6 pt-4">
        <p className="mb-3 text-center text-xs text-muted-foreground">
          {"My reason for living: My family and the life I'm building."}
        </p>
        <Button
          variant="outline"
          className="h-12 w-full rounded-2xl"
          onClick={() => setScreen("dashboard")}
        >
          Back to Dashboard
        </Button>
      </div>
    </div>
  )
}
