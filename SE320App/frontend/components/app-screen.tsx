"use client"

import React from "react"
import { useApp } from "@/lib/app-context"
import { PhoneFrame } from "@/components/phone-frame"
import { WebLayout } from "@/components/web-layout"

/* ── Mobile Flow Imports ── */
import {
  WelcomeScreen,
  UserTypeScreen,
  PrivacyScreen,
  AssessmentScreen,
  PersonalizationScreen,
  OnboardingCompleteScreen,
} from "@/components/flows/onboarding"
import { DashboardScreen } from "@/components/flows/dashboard"
import {
  SessionLibraryScreen,
  SessionIntroScreen,
  SessionChatScreen,
  SessionMultimodalScreen,
  SessionScenarioScreen,
  SessionThoughtChallengeScreen,
  SessionProgressScreen,
  SessionCompletionScreen,
  SessionRatingScreen,
  SessionHistoryScreen,
  SessionDetailScreen,
} from "@/components/flows/cbt-session"
import {
  DiaryHomeScreen,
  DiaryNewScreen,
  DiarySituationScreen,
  DiaryThoughtsScreen,
  DiaryDistortionsScreen,
  DiaryReframeScreen,
  DiarySavedScreen,
  DiaryInsightsScreen,
  DiaryDetailScreen,
} from "@/components/flows/thought-diary"
import {
  CrisisDetectionScreen,
  CrisisCopingScreen,
  CrisisResourcesScreen,
  CrisisSafetyPlanScreen,
} from "@/components/flows/crisis-support"
import {
  ProgressWeeklyScreen,
  ProgressTrendsScreen,
  ProgressAchievementsScreen,
} from "@/components/flows/progress-dashboard"

/* ── Web Flow Imports ── */
import {
  WebWelcomeScreen,
  WebLoginScreen,
  WebRegisterScreen,
  WebUserTypeScreen,
  WebPrivacyScreen,
  WebAssessmentScreen,
  WebPersonalizationScreen,
  WebOnboardingCompleteScreen,
} from "@/components/web/web-onboarding"
import { WebDashboardScreen } from "@/components/web/web-dashboard"
import {
  WebSessionLibraryScreen,
  WebSessionIntroScreen,
  WebSessionChatScreen,
  WebSessionMultimodalScreen,
  WebSessionScenarioScreen,
  WebSessionThoughtChallengeScreen,
  WebSessionProgressScreen,
  WebSessionCompletionScreen,
  WebSessionRatingScreen,
  WebSessionHistoryScreen,
  WebSessionDetailScreen,
} from "@/components/web/web-cbt-session"
import {
  WebDiaryHomeScreen,
  WebDiaryNewScreen,
  WebDiarySituationScreen,
  WebDiaryThoughtsScreen,
  WebDiaryDistortionsScreen,
  WebDiaryReframeScreen,
  WebDiarySavedScreen,
  WebDiaryInsightsScreen,
  WebDiaryDetailScreen,
} from "@/components/web/web-thought-diary"
import {
  WebCrisisDetectionScreen,
  WebCrisisCopingScreen,
  WebCrisisResourcesScreen,
  WebCrisisSafetyPlanScreen,
  WebProgressWeeklyScreen,
  WebProgressTrendsScreen,
  WebProgressAchievementsScreen,
} from "@/components/web/web-crisis-progress"

const mobileScreenMap: Record<string, React.ComponentType> = {
  welcome: WelcomeScreen,
  "user-type": UserTypeScreen,
  privacy: PrivacyScreen,
  assessment: AssessmentScreen,
  personalization: PersonalizationScreen,
  "onboarding-complete": OnboardingCompleteScreen,
  dashboard: DashboardScreen,
  "session-library": SessionLibraryScreen,
  "session-intro": SessionIntroScreen,
  "session-chat": SessionChatScreen,
  "session-multimodal": SessionMultimodalScreen,
  "session-scenario": SessionScenarioScreen,
  "session-thought-challenge": SessionThoughtChallengeScreen,
  "session-progress": SessionProgressScreen,
  "session-completion": SessionCompletionScreen,
  "session-rating": SessionRatingScreen,
  "session-history": SessionHistoryScreen,
  "session-detail": SessionDetailScreen,
  "diary-home": DiaryHomeScreen,
  "diary-detail": DiaryDetailScreen,
  "diary-new": DiaryNewScreen,
  "diary-situation": DiarySituationScreen,
  "diary-thoughts": DiaryThoughtsScreen,
  "diary-distortions": DiaryDistortionsScreen,
  "diary-reframe": DiaryReframeScreen,
  "diary-saved": DiarySavedScreen,
  "diary-insights": DiaryInsightsScreen,
  "crisis-detection": CrisisDetectionScreen,
  "crisis-coping": CrisisCopingScreen,
  "crisis-resources": CrisisResourcesScreen,
  "crisis-safety-plan": CrisisSafetyPlanScreen,
  "progress-weekly": ProgressWeeklyScreen,
  "progress-trends": ProgressTrendsScreen,
  "progress-achievements": ProgressAchievementsScreen,
}

const webScreenMap: Record<string, React.ComponentType> = {
  welcome: WebWelcomeScreen,
  login: WebLoginScreen,
  register: WebRegisterScreen,
  "user-type": WebUserTypeScreen,
  privacy: WebPrivacyScreen,
  assessment: WebAssessmentScreen,
  personalization: WebPersonalizationScreen,
  "onboarding-complete": WebOnboardingCompleteScreen,
  dashboard: WebDashboardScreen,
  "session-library": WebSessionLibraryScreen,
  "session-intro": WebSessionIntroScreen,
  "session-chat": WebSessionChatScreen,
  "session-multimodal": WebSessionMultimodalScreen,
  "session-scenario": WebSessionScenarioScreen,
  "session-thought-challenge": WebSessionThoughtChallengeScreen,
  "session-progress": WebSessionProgressScreen,
  "session-completion": WebSessionCompletionScreen,
  "session-rating": WebSessionRatingScreen,
  "session-history": WebSessionHistoryScreen,
  "session-detail": WebSessionDetailScreen,
  "diary-home": WebDiaryHomeScreen,
  "diary-detail": WebDiaryDetailScreen,
  "diary-new": WebDiaryNewScreen,
  "diary-situation": WebDiarySituationScreen,
  "diary-thoughts": WebDiaryThoughtsScreen,
  "diary-distortions": WebDiaryDistortionsScreen,
  "diary-reframe": WebDiaryReframeScreen,
  "diary-saved": WebDiarySavedScreen,
  "diary-insights": WebDiaryInsightsScreen,
  "crisis-detection": WebCrisisDetectionScreen,
  "crisis-coping": WebCrisisCopingScreen,
  "crisis-resources": WebCrisisResourcesScreen,
  "crisis-safety-plan": WebCrisisSafetyPlanScreen,
  "progress-weekly": WebProgressWeeklyScreen,
  "progress-trends": WebProgressTrendsScreen,
  "progress-achievements": WebProgressAchievementsScreen,
}

export function AppScreen() {
  const { screen, viewMode, accessToken } = useApp()
  const publicScreens = ["welcome", "login", "register", "user-type", "privacy", "assessment", "personalization", "onboarding-complete"]
  const effectiveScreen = !accessToken && !publicScreens.includes(screen) ? "login" : screen

  if (viewMode === "mobile") {
    const ScreenComponent = mobileScreenMap[effectiveScreen] || WelcomeScreen
    return (
      <PhoneFrame>
        <ScreenComponent />
      </PhoneFrame>
    )
  }

  // Web mode
  const ScreenComponent = webScreenMap[effectiveScreen] || WebWelcomeScreen
  return (
    <WebLayout>
      <ScreenComponent />
    </WebLayout>
  )
}
