"use client"

import { AppProvider, useApp, type ViewMode } from "@/lib/app-context"
import { AppScreen } from "@/components/app-screen"
import { Monitor, Smartphone } from "lucide-react"

function ViewModeToggle() {
  const { viewMode, setViewMode } = useApp()
  return (
    <div className="fixed bottom-6 right-6 z-50 flex items-center gap-1 rounded-full border border-border bg-card p-1 shadow-lg">
      {([
        { mode: "web" as ViewMode, icon: Monitor, label: "Web" },
        { mode: "mobile" as ViewMode, icon: Smartphone, label: "Mobile" },
      ]).map(({ mode, icon: Icon, label }) => (
        <button
          key={mode}
          className={`flex items-center gap-1.5 rounded-full px-4 py-2 text-xs font-medium transition-all ${
            viewMode === mode
              ? "bg-primary text-primary-foreground shadow-sm"
              : "text-muted-foreground hover:text-foreground"
          }`}
          onClick={() => setViewMode(mode)}
          aria-label={`Switch to ${label} view`}
        >
          <Icon className="h-3.5 w-3.5" />
          <span>{label}</span>
        </button>
      ))}
    </div>
  )
}

export default function Home() {
  return (
    <AppProvider>
      <AppScreen />
      <ViewModeToggle />
    </AppProvider>
  )
}
