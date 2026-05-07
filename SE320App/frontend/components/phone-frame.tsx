"use client"

import React from "react"
import { Signal, Wifi, Battery } from "lucide-react"

export function PhoneFrame({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/50 p-4">
      <div className="relative mx-auto w-full max-w-[390px]">
        <div className="overflow-hidden rounded-[2.5rem] border-[8px] border-foreground/90 bg-background shadow-2xl">
          {/* Status Bar */}
          <div className="flex items-center justify-between bg-background px-6 pb-1 pt-3">
            <span className="text-xs font-semibold text-foreground">9:41</span>
            <div className="absolute left-1/2 top-[8px] h-[28px] w-[120px] -translate-x-1/2 rounded-b-2xl bg-foreground/90" />
            <div className="flex items-center gap-1">
              <Signal className="h-3.5 w-3.5 text-foreground" />
              <Wifi className="h-3.5 w-3.5 text-foreground" />
              <Battery className="h-3.5 w-3.5 text-foreground" />
            </div>
          </div>
          {/* Content */}
          <div className="h-[740px] overflow-y-auto">
            {children}
          </div>
          {/* Home Indicator */}
          <div className="flex justify-center bg-background pb-2 pt-1">
            <div className="h-1 w-32 rounded-full bg-foreground/20" />
          </div>
        </div>
      </div>
    </div>
  )
}
