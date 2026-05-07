"use client"

import React, { createContext, useContext, useState, useCallback, useEffect } from "react"
import {
  createDiaryEntry,
  getDashboard,
  getDiaryEntries,
  getSessions,
  login,
  logout,
  refreshSession,
  registerUser,
  sendSessionMessage,
  startSession,
  type BackendSession,
} from "@/lib/api"

export type Screen =
  | "welcome"
  | "login"
  | "register"
  | "user-type"
  | "privacy"
  | "assessment"
  | "personalization"
  | "onboarding-complete"
  | "dashboard"
  | "session-library"
  | "session-intro"
  | "session-scenario"
  | "session-thought-challenge"
  | "session-progress"
  | "session-completion"
  | "session-rating"
  | "session-chat"
  | "session-multimodal"
  | "session-history"
  | "session-detail"
  | "diary-home"
  | "diary-detail"
  | "diary-new"
  | "diary-situation"
  | "diary-thoughts"
  | "diary-distortions"
  | "diary-reframe"
  | "diary-saved"
  | "diary-insights"
  | "crisis-detection"
  | "crisis-coping"
  | "crisis-resources"
  | "crisis-safety-plan"
  | "progress-weekly"
  | "progress-trends"
  | "progress-achievements"

export type ViewMode = "mobile" | "web"

interface AppState {
  screen: Screen
  viewMode: ViewMode
  accessToken: string | null
  userId: string | null
  backendStatus: "idle" | "connecting" | "connected" | "offline"
  authError: string | null
  userName: string
  userType: "individual" | "therapist-referred" | null
  assessmentScore: number
  sessionProgress: number
  diaryEntries: DiaryEntry[]
  sessions: BackendSession[]
  activeSessionId: number | null
  weeklyMood: number[]
  burnoutScore: number
  sessionsCompleted: number
  streakDays: number
  selectedSessionId: number | null
  selectedDiaryId: string | null
}

export interface DiaryEntry {
  id: string
  date: string
  situation: string
  thoughts: string
  emotions: string[]
  distortions: string[]
  reframe: string
  moodBefore: number
  moodAfter: number
}

interface AppContextType extends AppState {
  setScreen: (screen: Screen) => void
  setViewMode: (mode: ViewMode) => void
  setUserName: (name: string) => void
  setUserType: (type: "individual" | "therapist-referred") => void
  setAssessmentScore: (score: number) => void
  setSessionProgress: (progress: number) => void
  connectBackend: () => Promise<{ userId: string; accessToken: string } | null>
  loginUser: (email: string, password: string) => Promise<void>
  registerAccount: (input: { firstName: string; lastName: string; email: string; password: string; phoneNumber?: string }) => Promise<void>
  logoutUser: () => Promise<void>
  addDiaryEntry: (entry: DiaryEntry) => Promise<void>
  beginSession: (sessionId: number, moodBefore?: number) => Promise<void>
  sendChatMessage: (message: string) => Promise<string>
  setBurnoutScore: (score: number) => void
  incrementSessions: () => void
  selectSession: (id: number) => void
  selectDiaryEntry: (id: string) => void
}

const AppContext = createContext<AppContextType | null>(null)

export function AppProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AppState>({
    screen: "welcome",
    viewMode: "web",
    accessToken: null,
    userId: null,
    backendStatus: "idle",
    authError: null,
    userName: "",
    userType: null,
    assessmentScore: 0,
    sessionProgress: 0,
    sessions: [],
    activeSessionId: null,
    diaryEntries: [
      {
        id: "1",
        date: "2026-02-18",
        situation: "Overwhelmed by work deadlines",
        thoughts: "I can never keep up. Everyone else manages fine.",
        emotions: ["Anxious", "Frustrated"],
        distortions: ["All-or-Nothing", "Comparison"],
        reframe: "I have successfully managed heavy workloads before. I can prioritize and ask for help.",
        moodBefore: 3,
        moodAfter: 6,
      },
      {
        id: "2",
        date: "2026-02-16",
        situation: "Skipped exercise for the third day",
        thoughts: "I have no discipline. What is the point?",
        emotions: ["Guilty", "Sad"],
        distortions: ["Labeling", "Catastrophizing"],
        reframe: "Missing a few days does not define me. I can start again today with a short walk.",
        moodBefore: 2,
        moodAfter: 5,
      },
    ],
    weeklyMood: [4, 5, 3, 6, 5, 7, 6],
    burnoutScore: 72,
    sessionsCompleted: 4,
    streakDays: 7,
    selectedSessionId: null,
    selectedDiaryId: null,
  })

  const setScreen = useCallback((screen: Screen) => {
    setState((prev) => ({ ...prev, screen }))
  }, [])

  const setViewMode = useCallback((viewMode: ViewMode) => {
    setState((prev) => ({ ...prev, viewMode }))
  }, [])

  const setUserName = useCallback((userName: string) => {
    setState((prev) => ({ ...prev, userName }))
  }, [])

  const setUserType = useCallback((userType: "individual" | "therapist-referred") => {
    setState((prev) => ({ ...prev, userType }))
  }, [])

  const setAssessmentScore = useCallback((assessmentScore: number) => {
    setState((prev) => ({ ...prev, assessmentScore }))
  }, [])

  const setSessionProgress = useCallback((sessionProgress: number) => {
    setState((prev) => ({ ...prev, sessionProgress }))
  }, [])

  const persistAuth = useCallback((auth: { accessToken: string; refreshToken: string; user: { id: string; firstName: string } }) => {
    localStorage.setItem("mindbridge.accessToken", auth.accessToken)
    localStorage.setItem("mindbridge.refreshToken", auth.refreshToken)
    localStorage.setItem("mindbridge.userId", auth.user.id)
    setState((prev) => ({
      ...prev,
      accessToken: auth.accessToken,
      userId: auth.user.id,
      userName: prev.userName || auth.user.firstName,
      backendStatus: "connected",
      authError: null,
    }))
  }, [])

  const loadBackendData = useCallback(async (userId: string, accessToken: string) => {
    const [dashboard, entries, sessions] = await Promise.allSettled([
      getDashboard(userId, accessToken),
      getDiaryEntries(userId, accessToken),
      getSessions(accessToken),
    ])

    setState((prev) => ({
      ...prev,
      backendStatus: "connected",
      weeklyMood:
        dashboard.status === "fulfilled" && Array.isArray(dashboard.value.weeklyMood)
          ? dashboard.value.weeklyMood
          : prev.weeklyMood,
      burnoutScore:
        dashboard.status === "fulfilled" && typeof dashboard.value.burnoutScore === "number"
          ? dashboard.value.burnoutScore
          : prev.burnoutScore,
      sessionsCompleted:
        dashboard.status === "fulfilled" && typeof dashboard.value.sessionsCompleted === "number"
          ? dashboard.value.sessionsCompleted
          : prev.sessionsCompleted,
      streakDays:
        dashboard.status === "fulfilled" && typeof dashboard.value.streakDays === "number"
          ? dashboard.value.streakDays
          : prev.streakDays,
      diaryEntries:
        entries.status === "fulfilled" && entries.value.length > 0
          ? entries.value.map((entry) => ({
              id: entry.id,
              date: (entry.createdAt ?? entry.date ?? new Date().toISOString()).slice(0, 10),
              situation: entry.situation,
              thoughts: entry.automaticThought ?? entry.thoughts ?? "",
              emotions: [],
              distortions: [],
              reframe: entry.alternativeThought ?? entry.reframe ?? "",
              moodBefore: entry.moodBefore ?? 5,
              moodAfter: entry.moodAfter ?? entry.moodBefore ?? 5,
            }))
          : prev.diaryEntries,
      sessions: sessions.status === "fulfilled" ? sessions.value : prev.sessions,
    }))
  }, [])

  const connectBackend = useCallback(async () => {
    setState((prev) => ({ ...prev, backendStatus: "connecting" }))
    try {
      const accessToken = state.accessToken ?? localStorage.getItem("mindbridge.accessToken")
      const userId = state.userId ?? localStorage.getItem("mindbridge.userId")
      if (!accessToken || !userId) {
        setState((prev) => ({ ...prev, backendStatus: "offline" }))
        return null
      }
      await loadBackendData(userId, accessToken)
      setState((prev) => ({ ...prev, accessToken, userId, backendStatus: "connected" }))
      return { userId, accessToken }
    } catch (error) {
      console.warn("MindBridge backend is unavailable; using local prototype data.", error)
      setState((prev) => ({ ...prev, backendStatus: "offline" }))
      return null
    }
  }, [loadBackendData, state.accessToken, state.userId])

  const loginUser = useCallback(async (email: string, password: string) => {
    setState((prev) => ({ ...prev, backendStatus: "connecting", authError: null }))
    try {
      const auth = await login(email, password)
      persistAuth(auth)
      await loadBackendData(auth.user.id, auth.accessToken)
      setState((prev) => ({ ...prev, screen: "dashboard" }))
    } catch (error) {
      setState((prev) => ({ ...prev, backendStatus: "offline", authError: error instanceof Error ? error.message : "Login failed." }))
      throw error
    }
  }, [loadBackendData, persistAuth])

  const registerAccount = useCallback(async (input: { firstName: string; lastName: string; email: string; password: string; phoneNumber?: string }) => {
    setState((prev) => ({ ...prev, backendStatus: "connecting", authError: null }))
    try {
      const auth = await registerUser(input)
      persistAuth(auth)
      await loadBackendData(auth.user.id, auth.accessToken)
      setState((prev) => ({ ...prev, screen: "dashboard" }))
    } catch (error) {
      setState((prev) => ({ ...prev, backendStatus: "offline", authError: error instanceof Error ? error.message : "Registration failed." }))
      throw error
    }
  }, [loadBackendData, persistAuth])

  const logoutUser = useCallback(async () => {
    const token = state.accessToken ?? localStorage.getItem("mindbridge.accessToken")
    if (token) {
      try {
        await logout(token)
      } catch (error) {
        console.warn("Backend logout failed; clearing local session.", error)
      }
    }
    localStorage.removeItem("mindbridge.accessToken")
    localStorage.removeItem("mindbridge.refreshToken")
    localStorage.removeItem("mindbridge.userId")
    setState((prev) => ({
      ...prev,
      accessToken: null,
      userId: null,
      activeSessionId: null,
      backendStatus: "idle",
      screen: "login",
    }))
  }, [state.accessToken])

  useEffect(() => {
    const refreshToken = localStorage.getItem("mindbridge.refreshToken")
    if (!refreshToken) return

    refreshSession(refreshToken)
      .then(async (auth) => {
        persistAuth(auth)
        await loadBackendData(auth.user.id, auth.accessToken)
      })
      .catch(() => {
        localStorage.removeItem("mindbridge.accessToken")
        localStorage.removeItem("mindbridge.refreshToken")
        localStorage.removeItem("mindbridge.userId")
      })
  }, [loadBackendData, persistAuth])

  const addDiaryEntry = useCallback(async (entry: DiaryEntry) => {
    if (state.userId && state.accessToken) {
      try {
        const saved = await createDiaryEntry(state.userId, state.accessToken, {
          situation: entry.situation,
          automaticThought: entry.thoughts,
          alternativeThought: entry.reframe,
          moodBefore: entry.moodBefore,
          moodAfter: entry.moodAfter,
        })
        entry = {
          ...entry,
          id: saved.id ?? entry.id,
          date: (saved.createdAt ?? saved.date ?? entry.date).slice(0, 10),
        }
      } catch (error) {
        console.warn("Diary entry could not be saved to the backend; keeping it locally.", error)
      }
    }

    setState((prev) => ({
      ...prev,
      diaryEntries: [entry, ...prev.diaryEntries],
    }))
  }, [state.accessToken, state.userId])

  const beginSession = useCallback(async (sessionId: number, moodBefore = 5) => {
    const connection = !state.userId || !state.accessToken ? await connectBackend() : null
    const userId = state.userId ?? connection?.userId
    const accessToken = state.accessToken ?? connection?.accessToken
    if (!userId || !accessToken) {
      setState((prev) => ({ ...prev, activeSessionId: sessionId }))
      return
    }

    const started = await startSession(userId, sessionId, accessToken, moodBefore)
    setState((prev) => ({
      ...prev,
      activeSessionId: started.sessionId ?? sessionId,
    }))
  }, [connectBackend, state.accessToken, state.userId])

  const sendChatMessage = useCallback(async (message: string) => {
    if (!state.userId || !state.accessToken || !state.activeSessionId) {
      return "I am connected locally right now. Start a backend session first, then I can send this through your Spring API."
    }

    const response = await sendSessionMessage(
      state.userId,
      state.activeSessionId,
      state.accessToken,
      message
    )

    return response.assistantMessage?.content ?? response.response ?? response.message ?? "I received your message."
  }, [state.accessToken, state.activeSessionId, state.userId])

  const setBurnoutScore = useCallback((burnoutScore: number) => {
    setState((prev) => ({ ...prev, burnoutScore }))
  }, [])

  const incrementSessions = useCallback(() => {
    setState((prev) => ({
      ...prev,
      sessionsCompleted: prev.sessionsCompleted + 1,
    }))
  }, [])

  const selectSession = useCallback((id: number) => {
    setState((prev) => ({ ...prev, selectedSessionId: id, screen: "session-detail" as Screen }))
  }, [])

  const selectDiaryEntry = useCallback((id: string) => {
    setState((prev) => ({ ...prev, selectedDiaryId: id, screen: "diary-detail" as Screen }))
  }, [])

  return (
    <AppContext.Provider
      value={{
        ...state,
        setScreen,
        setViewMode,
        setUserName,
        setUserType,
        setAssessmentScore,
        setSessionProgress,
        connectBackend,
        loginUser,
        registerAccount,
        logoutUser,
        addDiaryEntry,
        beginSession,
        sendChatMessage,
        setBurnoutScore,
        incrementSessions,
        selectSession,
        selectDiaryEntry,
      }}
    >
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  const context = useContext(AppContext)
  if (!context) throw new Error("useApp must be used within AppProvider")
  return context
}
