"use client"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? ""

type RequestOptions = RequestInit & {
  token?: string | null
}

export type BackendUser = {
  id: string
  userType: "PATIENT" | "DOCTOR" | "ADMIN"
  firstName: string
  lastName: string
  email: string
  phoneNumber?: string | null
  online?: boolean
}

export type AuthPayload = {
  accessToken: string
  refreshToken: string
  user: BackendUser
}

export type BackendSession = {
  id?: number
  sessionId?: number
  title?: string
  name?: string
  durationMinutes?: number
  duration?: string
  module?: string
  status?: string
  description?: string
}

export type BackendDiaryEntry = {
  id: string
  createdAt?: string
  date?: string
  situation: string
  automaticThought?: string
  thoughts?: string
  alternativeThought?: string
  reframe?: string
  moodBefore?: number
  moodAfter?: number
}

export type BackendDashboard = {
  sessionsCompleted?: number
  streakDays?: number
  burnoutScore?: number
  weeklyMood?: number[]
}

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set("Accept", "application/json")

  if (options.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json")
  }

  if (options.token) {
    headers.set("Authorization", `Bearer ${options.token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  })

  if (!response.ok) {
    const message = await response.text()
    throw new Error(toFriendlyError(response.status, message))
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

function toFriendlyError(status: number, body: string) {
  if (status === 400) return "Please check the form and try again."
  if (status === 401) return "Your session has expired. Please log in again."
  if (status === 403) return "You do not have permission to perform this action."
  if (status === 404) return "The requested item could not be found."
  if (status === 422) return "Some required information is missing or invalid."
  return body || "Something went wrong. Please try again."
}

export async function login(email: string, password: string) {
  return request<AuthPayload>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  })
}

export function registerUser(input: {
  firstName: string
  lastName: string
  email: string
  password: string
  phoneNumber?: string
}) {
  return request<AuthPayload>("/auth/register", {
    method: "POST",
    body: JSON.stringify({
      userType: "PATIENT",
      ...input,
      phoneNumber: input.phoneNumber ?? "",
    }),
  })
}

export function refreshSession(refreshToken: string) {
  return request<AuthPayload>("/auth/refresh", {
    method: "POST",
    headers: { "Content-Type": "text/plain" },
    body: refreshToken,
  })
}

export function logout(accessToken: string) {
  return request<void>("/auth/logout", {
    method: "POST",
    token: accessToken,
  })
}

export function getDashboard(userId: string, token: string) {
  return request<BackendDashboard>(`/progress?userId=${encodeURIComponent(userId)}`, { token })
}

export async function getDiaryEntries(userId: string, token: string) {
  const page = await request<{ content?: BackendDiaryEntry[] }>(
    `/diary/entries?userId=${encodeURIComponent(userId)}&size=50`,
    { token }
  )
  return page.content ?? []
}

export function createDiaryEntry(
  userId: string,
  token: string,
  entry: {
    situation: string
    automaticThought: string
    alternativeThought: string
    moodBefore: number
    moodAfter: number
  }
) {
  return request<BackendDiaryEntry>(`/diary/entries?userId=${encodeURIComponent(userId)}`, {
    method: "POST",
    token,
    body: JSON.stringify(entry),
  })
}

export function getSessions(token: string) {
  return request<BackendSession[]>("/sessions", { token })
}

export function startSession(userId: string, sessionId: number, token: string, moodBefore = 5) {
  return request<{ sessionId?: number; userSessionId?: string }>(`/sessions/${sessionId}/start`, {
    method: "POST",
    token,
    body: JSON.stringify({ userId, moodBefore }),
  })
}

export function sendSessionMessage(userId: string, sessionId: number, token: string, message: string) {
  return request<{ assistantMessage?: { content?: string }; response?: string; message?: string }>(
    `/sessions/${sessionId}/chat`,
    {
      method: "POST",
      token,
      body: JSON.stringify({ userId, message, modality: "TEXT" }),
    }
  )
}
