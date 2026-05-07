"use client"

import React, { useState } from "react"
import { useApp, type Screen } from "@/lib/app-context"
import {
  Brain,
  BookOpen,
  Target,
  TrendingUp,
  AlertTriangle,
  LogOut,
  Sparkles,
  ChevronRight,
  Shield,
  Flame,
  Heart,
  Settings,
  Bell,
  Search,
  Menu,
  X,
} from "lucide-react"
import { Badge } from "@/components/ui/badge"

interface NavItem {
  icon: React.ElementType
  label: string
  target: Screen
  group: string
  badge?: string
}

const navItems: NavItem[] = [
  { icon: Target, label: "Dashboard", target: "dashboard", group: "Main" },
  { icon: Brain, label: "CBT Sessions", target: "session-library", group: "Therapy" },
  { icon: BookOpen, label: "Thought Diary", target: "diary-home", group: "Therapy" },
  { icon: TrendingUp, label: "Progress", target: "progress-weekly", group: "Tracking" },
  { icon: AlertTriangle, label: "Crisis Support", target: "crisis-detection", group: "Support" },
]

function isScreenInGroup(screen: string, target: string): boolean {
  if (target === "dashboard" && screen === "dashboard") return true
  if (target === "session-library" && screen.startsWith("session")) return true
  if (target === "diary-home" && screen.startsWith("diary")) return true
  if (target === "progress-weekly" && screen.startsWith("progress")) return true
  if (target === "crisis-detection" && screen.startsWith("crisis")) return true
  return false
}

export function WebLayout({ children }: { children: React.ReactNode }) {
  const { screen, setScreen, userName, streakDays, logoutUser } = useApp()
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)

  const isOnboarding = [
    "welcome",
    "login",
    "register",
    "user-type",
    "privacy",
    "assessment",
    "personalization",
    "onboarding-complete",
  ].includes(screen)

  if (isOnboarding) {
    return (
      <div className="flex min-h-screen bg-background">
        {/* Left brand panel */}
        <div className="hidden w-[480px] flex-col justify-between bg-primary p-12 lg:flex">
          <div>
            <div className="mb-8 flex items-center gap-3">
              <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary-foreground/10">
                <Brain className="h-7 w-7 text-primary-foreground" />
              </div>
              <span className="text-2xl font-bold text-primary-foreground">MindBridge</span>
            </div>
            <h1 className="mb-4 text-4xl font-bold leading-tight text-primary-foreground text-balance">
              Your path to burnout recovery starts here
            </h1>
            <p className="text-lg leading-relaxed text-primary-foreground/70">
              Evidence-based CBT therapy guided by AI and compassion. Join thousands of professionals who have reclaimed their wellbeing.
            </p>
          </div>
          <div className="space-y-6">
            <div className="flex items-center gap-4 rounded-2xl bg-primary-foreground/10 p-4">
              <Sparkles className="h-6 w-6 text-primary-foreground/70" />
              <div>
                <p className="text-sm font-semibold text-primary-foreground">AI-Powered Therapy</p>
                <p className="text-xs text-primary-foreground/60">Personalized sessions with Dr. MindBridge</p>
              </div>
            </div>
            <div className="flex items-center gap-4 rounded-2xl bg-primary-foreground/10 p-4">
              <Shield className="h-6 w-6 text-primary-foreground/70" />
              <div>
                <p className="text-sm font-semibold text-primary-foreground">HIPAA Compliant</p>
                <p className="text-xs text-primary-foreground/60">End-to-end encrypted and privacy-first</p>
              </div>
            </div>
            <div className="flex items-center gap-4 rounded-2xl bg-primary-foreground/10 p-4">
              <Heart className="h-6 w-6 text-primary-foreground/70" />
              <div>
                <p className="text-sm font-semibold text-primary-foreground">Proven Results</p>
                <p className="text-xs text-primary-foreground/60">78% of users report reduced burnout in 4 weeks</p>
              </div>
            </div>
          </div>
        </div>

        {/* Right content panel */}
        <div className="flex flex-1 flex-col">
          <div className="flex items-center justify-between border-b border-border px-8 py-4 lg:hidden">
            <div className="flex items-center gap-2">
              <Brain className="h-6 w-6 text-primary" />
              <span className="text-lg font-bold text-foreground">MindBridge</span>
            </div>
          </div>
          <div className="flex flex-1 items-center justify-center p-6">
            <div className="w-full max-w-lg">
              {children}
            </div>
          </div>
        </div>
      </div>
    )
  }

  // Main app layout with sidebar
  return (
    <div className="flex h-screen bg-background">
      {/* Sidebar */}
      <aside className="hidden w-64 flex-col border-r border-border bg-card lg:flex">
        {/* Logo */}
        <div className="flex items-center gap-3 border-b border-border px-6 py-5">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary/10">
            <Brain className="h-5 w-5 text-primary" />
          </div>
          <span className="text-lg font-bold text-foreground">MindBridge</span>
        </div>

        {/* User info */}
        <div className="border-b border-border px-6 py-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary text-sm font-bold text-primary-foreground">
              {userName ? userName[0].toUpperCase() : "U"}
            </div>
            <div className="flex-1">
              <p className="text-sm font-semibold text-foreground">{userName || "Friend"}</p>
              <div className="flex items-center gap-2">
                <Flame className="h-3 w-3 text-chart-1" />
                <span className="text-xs text-muted-foreground">{streakDays} day streak</span>
              </div>
            </div>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto px-3 py-4">
          {["Main", "Therapy", "Tracking", "Support"].map((group) => {
            const items = navItems.filter((item) => item.group === group)
            if (items.length === 0) return null
            return (
              <div key={group} className="mb-4">
                <p className="mb-2 px-3 text-[10px] font-semibold uppercase tracking-widest text-muted-foreground">
                  {group}
                </p>
                <div className="space-y-0.5">
                  {items.map(({ icon: Icon, label, target, badge }) => {
                    const active = isScreenInGroup(screen, target)
                    return (
                      <button
                        key={label}
                        className={`flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition-colors ${
                          active
                            ? "bg-primary/10 font-semibold text-primary"
                            : "text-muted-foreground hover:bg-secondary hover:text-foreground"
                        }`}
                        onClick={() => setScreen(target)}
                      >
                        <Icon className="h-4.5 w-4.5" />
                        <span className="flex-1">{label}</span>
                        {badge && (
                          <Badge variant="secondary" className="text-[10px]">
                            {badge}
                          </Badge>
                        )}
                        {active && <ChevronRight className="h-3.5 w-3.5 opacity-50" />}
                      </button>
                    )
                  })}
                </div>
              </div>
            )
          })}
        </nav>

        {/* Sidebar footer */}
        <div className="border-t border-border px-3 py-3">
          <button
            className="flex w-full items-center gap-3 rounded-xl px-3 py-2 text-left text-sm text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
            onClick={() => logoutUser()}
          >
            <LogOut className="h-4 w-4" />
            <span>Sign Out</span>
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Top bar */}
        <header className="relative z-20 border-b border-border bg-card">
          <div className="flex items-center justify-between px-6 py-3 lg:px-8">
            {/* Mobile logo */}
            <div className="flex items-center gap-2 lg:hidden">
              <Brain className="h-5 w-5 text-primary" />
              <span className="text-sm font-bold text-foreground">MindBridge</span>
            </div>

            {/* Breadcrumb (desktop) */}
            <div className="hidden items-center gap-2 lg:flex">
              <span className="text-sm text-muted-foreground">
                {getScreenBreadcrumb(screen)}
              </span>
            </div>

            {/* Mobile nav items (tablet-ish, between sm and lg) */}
            <nav className="hidden items-center gap-1 sm:flex lg:hidden" aria-label="Main navigation">
              {navItems.map(({ icon: Icon, label, target }) => {
                const active = isScreenInGroup(screen, target)
                return (
                  <button
                    key={label}
                    className={`flex items-center gap-1.5 rounded-lg px-2.5 py-1.5 text-xs font-medium transition-colors ${
                      active
                        ? "bg-primary/10 text-primary"
                        : "text-muted-foreground hover:bg-secondary hover:text-foreground"
                    }`}
                    onClick={() => setScreen(target)}
                  >
                    <Icon className="h-3.5 w-3.5" />
                    <span className="hidden md:inline">{label}</span>
                  </button>
                )
              })}
            </nav>

            {/* Actions */}
            <div className="flex items-center gap-2">
              <button
                className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
                aria-label="Search"
              >
                <Search className="h-4 w-4" />
              </button>
              <button
                className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
                aria-label="Notifications"
              >
                <Bell className="h-4 w-4" />
              </button>
              <button
                className="hidden sm:flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
                aria-label="Settings"
              >
                <Settings className="h-4 w-4" />
              </button>
              {/* Hamburger for small mobile */}
              <button
                className="flex h-9 w-9 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground sm:hidden"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                aria-label="Toggle navigation menu"
              >
                {mobileMenuOpen ? <X className="h-4.5 w-4.5" /> : <Menu className="h-4.5 w-4.5" />}
              </button>
            </div>
          </div>

          {/* Mobile dropdown menu (small screens only) */}
          {mobileMenuOpen && (
            <div className="absolute left-0 right-0 top-full border-b border-border bg-card shadow-lg sm:hidden">
              <nav className="space-y-0.5 p-2" aria-label="Mobile navigation">
                {navItems.map(({ icon: Icon, label, target }) => {
                  const active = isScreenInGroup(screen, target)
                  return (
                    <button
                      key={label}
                      className={`flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-left text-sm transition-colors ${
                        active
                          ? "bg-primary/10 font-semibold text-primary"
                          : "text-muted-foreground hover:bg-secondary hover:text-foreground"
                      }`}
                      onClick={() => {
                        setScreen(target)
                        setMobileMenuOpen(false)
                      }}
                    >
                      <Icon className="h-4 w-4" />
                      <span>{label}</span>
                      {active && <ChevronRight className="ml-auto h-3.5 w-3.5 opacity-50" />}
                    </button>
                  )
                })}
              </nav>
            </div>
          )}
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  )
}

function getScreenBreadcrumb(screen: string): string {
  const map: Record<string, string> = {
    dashboard: "Dashboard",
    "session-library": "CBT Sessions",
    "session-intro": "CBT Sessions / Session Details",
    "session-chat": "CBT Sessions / AI Chat",
    "session-multimodal": "CBT Sessions / Express Yourself",
    "session-scenario": "CBT Sessions / Scenario",
    "session-thought-challenge": "CBT Sessions / Thought Challenge",
    "session-progress": "CBT Sessions / Progress",
  "session-completion": "CBT Sessions / Complete",
  "session-rating": "CBT Sessions / Rating",
  "session-history": "CBT Sessions / Completed Sessions",
  "session-detail": "CBT Sessions / Session Detail",
  "diary-home": "Thought Diary",
  "diary-detail": "Thought Diary / Entry Detail",
    "diary-new": "Thought Diary / New Entry",
    "diary-situation": "Thought Diary / Situation",
    "diary-thoughts": "Thought Diary / Thoughts",
    "diary-distortions": "Thought Diary / Distortions",
    "diary-reframe": "Thought Diary / Reframe",
    "diary-saved": "Thought Diary / Saved",
    "diary-insights": "Thought Diary / Insights",
    "crisis-detection": "Crisis Support",
    "crisis-coping": "Crisis Support / Coping",
    "crisis-resources": "Crisis Support / Resources",
    "crisis-safety-plan": "Crisis Support / Safety Plan",
    "progress-weekly": "Progress",
    "progress-trends": "Progress / Trends",
    "progress-achievements": "Progress / Achievements",
  }
  return map[screen] || "Dashboard"
}
