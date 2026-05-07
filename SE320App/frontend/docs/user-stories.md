# MindBridge -- User Stories

---

## 1. Onboarding & Assessment

### US-1.1: Welcome Screen

| Field | Detail |
|-------|--------|
| **User Story** | As a **new user**, I want to see a welcome screen explaining the app's purpose, so that I understand how MindBridge supports burnout recovery before I begin. |
| **Priority** | Must |
| **Story Points** | 2 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** a first-time visitor opens the app, **When** the welcome screen loads, **Then** a headline, description, and a "Get Started" call-to-action button are displayed. |
| AC2 | **Given** the welcome screen is displayed, **When** the user reads the content, **Then** the screen clearly communicates the app focuses on AI-guided CBT for workplace burnout. |
| AC3 | **Given** the user taps "Get Started", **When** the transition occurs, **Then** they are navigated to the next onboarding step without data loss. |

---

### US-1.2: Join Path Selection

| Field | Detail |
|-------|--------|
| **User Story** | As a **new user**, I want to choose whether I'm joining on my own or was referred by a therapist, so that I receive a personalized onboarding experience. |
| **Priority** | Must |
| **Story Points** | 2 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the path selection step, **When** the screen renders, **Then** two distinct options ("On my own" and "Therapist referred") are displayed. |
| AC2 | **Given** the user selects a path, **When** they tap on an option, **Then** the selected option is visually highlighted and the "Continue" button becomes active. |
| AC3 | **Given** the user has selected a path and taps "Continue", **When** navigation occurs, **Then** they proceed to the consent screen with their selection persisted. |

---

### US-1.3: Privacy Consent

| Field | Detail |
|-------|--------|
| **User Story** | As a **new user**, I want to review privacy, HIPAA compliance, data ownership, and AI processing policies, so that I can give informed consent before sharing personal information. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the consent screen, **When** the screen renders, **Then** cards for HIPAA compliance, data ownership, AI processing, and crisis protocols are displayed with clear descriptions. |
| AC2 | **Given** the consent items are displayed, **When** the user toggles each consent checkbox, **Then** each item's accepted state is tracked independently. |
| AC3 | **Given** fewer than all required consent items are accepted, **When** the user views the "Continue" button, **Then** the button remains disabled. |
| AC4 | **Given** all required consent items are accepted, **When** the user taps "Continue", **Then** they proceed to the assessment step. |

---

### US-1.4: Burnout Assessment Questionnaire

| Field | Detail |
|-------|--------|
| **User Story** | As a **new user**, I want to complete a burnout assessment questionnaire covering exhaustion, efficacy, cynicism, cognitive function, and detachment, so that I receive a baseline severity score. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the assessment screen, **When** the screen renders, **Then** the first question is displayed with a 1-5 rating scale and a progress indicator. |
| AC2 | **Given** a question is displayed, **When** the user selects a rating, **Then** the selected rating is highlighted and automatically advances to the next question. |
| AC3 | **Given** the user is on a question after the first, **When** they tap the back button, **Then** they return to the previous question with their prior answer preserved. |
| AC4 | **Given** all five questions are answered, **When** the final rating is submitted, **Then** a composite burnout score is calculated from the individual dimension responses. |
| AC5 | **Given** the assessment is complete, **When** the score is computed, **Then** the user is navigated to the personalization step. |

---

### US-1.5: Profile Personalization

| Field | Detail |
|-------|--------|
| **User Story** | As a **new user**, I want to enter my name, select recovery goals, and pick a preferred session time, so that I have a tailored recovery program. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the personalization screen, **When** it renders, **Then** a name input field, a list of selectable recovery goals, and a session time picker are displayed. |
| AC2 | **Given** the goal selection list is displayed, **When** the user taps on one or more goals (e.g., reduce stress, better sleep, manage anxiety, improve focus), **Then** each selected goal is visually toggled and the selection count is tracked. |
| AC3 | **Given** a name has been entered and at least one goal selected, **When** the user taps "Continue", **Then** their personalization data is saved and they proceed to the results screen. |
| AC4 | **Given** the name field is empty, **When** the user tries to continue, **Then** the "Continue" button remains disabled. |

---

### US-1.6: Assessment Results & Plan Summary

| Field | Detail |
|-------|--------|
| **User Story** | As a **new user**, I want to see my assessment results with severity level and a summary of my personalized plan, so that I understand what my recovery journey looks like. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user arrives at the results screen, **When** it renders, **Then** their burnout severity level (Mild / Moderate / Significant) is prominently displayed with a corresponding visual indicator. |
| AC2 | **Given** the results are displayed, **When** the user scrolls, **Then** they see a personalized plan summary including their selected goals, recommended session frequency, and key focus areas. |
| AC3 | **Given** the user reviews their results, **When** they tap "Start My Journey", **Then** onboarding is marked as complete and they are navigated to the main dashboard. |

---

## 2. Interactive CBT Session

### US-2.1: Session Library Browsing

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to browse a session library organized by module (Foundation, Core Skills, Application, Advanced, Open Sessions), so that I can see my progress and which sessions are available. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the session library, **When** the screen renders, **Then** all sessions are listed grouped by module with each session showing its title, duration, and status (completed / current / locked / available). |
| AC2 | **Given** a session has status "completed", **When** it renders, **Then** a green check icon is displayed. **Given** a session has status "locked", **Then** a lock icon and reduced opacity are displayed. |
| AC3 | **Given** a session has status "locked", **When** the user taps on it, **Then** nothing happens (the button is disabled). |
| AC4 | **Given** a session has status "completed", "current", or "available", **When** the user taps on it, **Then** they are navigated to the session intro screen. |
| AC5 | **Given** the progress bar is visible, **When** sessions are completed, **Then** it reflects the ratio of completed sessions to total sessions (e.g., "4 of 8 completed"). |

---

### US-2.2: Session Introduction & Details

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to view session details including objectives, duration, and modality information before starting, so that I can prepare for the session. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user taps a session from the library, **When** the intro screen renders, **Then** the session title, estimated duration, list of objectives, and description of interaction modalities (text, voice, video) are displayed. |
| AC2 | **Given** the intro screen is displayed, **When** the user taps "Begin Session", **Then** they are navigated to the active session (AI chat) screen. |
| AC3 | **Given** the intro screen is displayed, **When** the user taps the back arrow, **Then** they return to the session library without losing any state. |

---

### US-2.3: Real-Time AI Chat Conversation

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to have a real-time AI chat conversation with "Dr. MindBridge" that responds to my inputs with therapeutic guidance, so that I can work through my thoughts interactively. |
| **Priority** | Must |
| **Story Points** | 8 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user starts a session, **When** the chat screen loads, **Then** an initial greeting from "Dr. MindBridge" with contextual prompts is displayed, along with a text input field. |
| AC2 | **Given** the user types a message and sends it, **When** the message is submitted, **Then** the user's message appears in the chat and a typing indicator shows while the AI generates a response. |
| AC3 | **Given** the AI has generated a response, **When** it is delivered, **Then** the response appears as a new chat bubble with the therapist avatar, and the chat scrolls to the latest message. |
| AC4 | **Given** the session is active, **When** the user taps "End Session", **Then** a confirmation prompt is shown before navigating to the session summary. |

---

### US-2.4: Multi-Modal Expression

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to express myself through multiple modalities (text, voice recording, or video) during a session, so that I can communicate in the way that feels most natural. |
| **Priority** | Should |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is in the active chat screen, **When** they view the input area, **Then** buttons for text, voice, and video modalities are available. |
| AC2 | **Given** the user selects the voice modality, **When** they tap the microphone button, **Then** a recording indicator is displayed and audio capture begins. |
| AC3 | **Given** the user selects the video modality, **When** they tap the video button, **Then** a camera preview is shown with a record control. |
| AC4 | **Given** a voice or video recording is completed, **When** the user confirms submission, **Then** the recording is sent as a chat message and the AI responds accordingly. |

---

### US-2.5: Interactive Workplace Scenarios

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to engage with interactive scenarios presenting realistic workplace situations and choose between helpful and unhelpful thought responses, so that I can practice identifying cognitive distortions in context. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the session reaches the scenario step, **When** the screen renders, **Then** a workplace scenario description is displayed with a progress indicator. |
| AC2 | **Given** a scenario is displayed, **When** the user reads it, **Then** two response options (one helpful, one unhelpful) are presented as tappable cards. |
| AC3 | **Given** the user selects a response, **When** the selection is confirmed, **Then** immediate feedback is shown indicating whether the choice was the helpful or unhelpful thought with an explanation. |
| AC4 | **Given** all scenarios in the set are completed, **When** the final feedback is acknowledged, **Then** a score summary (e.g., "3 of 4 correct") is displayed and the user proceeds to the next session step. |

---

### US-2.6: Structured Thought Challenge

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to complete a structured thought challenge exercise (identify thought, examine evidence, generate alternative, rate belief), so that I can learn cognitive restructuring step by step. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the session reaches the thought challenge step, **When** the screen renders, **Then** the user is prompted to type an automatic negative thought from a recent workplace situation. |
| AC2 | **Given** the user has entered their thought, **When** they proceed, **Then** they are asked to list evidence for and against the thought in separate text areas. |
| AC3 | **Given** evidence has been entered, **When** the user proceeds, **Then** they are prompted to write a balanced alternative thought based on the evidence. |
| AC4 | **Given** a balanced thought is entered, **When** the user proceeds, **Then** they rate their belief in the new thought on a percentage slider and the result is saved. |
| AC5 | **Given** all steps are complete, **When** the challenge is submitted, **Then** the user sees AI feedback on their reframe quality and proceeds to the next session step. |

---

### US-2.7: In-Session Progress Tracker

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to see my in-session progress tracker showing completed and remaining steps, so that I can understand how far along I am in the current session. |
| **Priority** | Should |
| **Story Points** | 2 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is in an active session, **When** any session step screen renders, **Then** a progress indicator (e.g., "Step 2 of 5" or progress bar) is visible at the top of the screen. |
| AC2 | **Given** the user completes a step and advances, **When** the next step loads, **Then** the progress indicator updates to reflect the new position. |
| AC3 | **Given** the progress tracker is displayed, **When** the user views it, **Then** completed steps are visually differentiated from upcoming steps (e.g., filled vs. outlined dots). |

---

### US-2.8: Session Completion Summary

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to receive a session completion summary with AI-generated insights, a full transcript, identified distortions, emotional arc, techniques applied, and homework recommendations, so that I can retain what I learned. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user completes or ends a session, **When** the summary screen renders, **Then** a congratulations message, session duration, and overall completion status are displayed. |
| AC2 | **Given** the summary screen is displayed, **When** the user views the insights tab, **Then** AI-generated key insights, identified cognitive distortions, the emotional arc (start vs. end mood), and techniques practiced are shown. |
| AC3 | **Given** the summary screen is displayed, **When** the user views the transcript tab, **Then** the full chat transcript of the session is available for review. |
| AC4 | **Given** the summary screen is displayed, **When** the user scrolls to homework, **Then** personalized practice recommendations for the coming days are listed. |
| AC5 | **Given** the user has reviewed the summary, **When** they tap "Back to Sessions", **Then** they return to the session library with the completed session now marked as "completed". |

---

### US-2.9: Session History Log

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to access a session history log with dates, durations, and key metrics, so that I can review my past sessions. |
| **Priority** | Should |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user taps "History" in the session library, **When** the history screen renders, **Then** a chronological list of completed sessions is displayed with date, title, duration, and mood change for each. |
| AC2 | **Given** the history list is displayed, **When** the user taps on a past session entry, **Then** the full session summary (same as the completion summary) is opened for review. |
| AC3 | **Given** no sessions have been completed, **When** the history screen renders, **Then** an empty state message with guidance to start a session is displayed. |

---

### US-2.10: General Discussion Open Session

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to access a General Discussion open session that is not gated by progression, so that I can have a free-form therapeutic conversation at any time. |
| **Priority** | Should |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user views the session library, **When** the list renders, **Then** a "General Discussion" session is listed under the "Open Sessions" module with an "Open" badge and a distinct icon. |
| AC2 | **Given** other sessions are locked, **When** the user taps "General Discussion", **Then** it is always accessible regardless of session progression. |
| AC3 | **Given** the user starts a General Discussion session, **When** the chat loads, **Then** Dr. MindBridge greets the user with an open-ended prompt and the full AI chat functionality is available. |

---

### US-2.11: Early Session Exit

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to end a session early at any point, so that I can leave if I feel overwhelmed or need to stop. |
| **Priority** | Must |
| **Story Points** | 2 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is in an active session at any step, **When** they tap "End Session", **Then** a confirmation dialog is displayed asking if they are sure. |
| AC2 | **Given** the confirmation dialog is shown, **When** the user confirms exit, **Then** they are navigated to the session summary screen with partial progress saved. |
| AC3 | **Given** the confirmation dialog is shown, **When** the user cancels, **Then** they remain in the current session step without any data loss. |

---

## 3. Thought Record Diary

### US-3.1: Diary Entry List

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to view a diary home screen listing all my entries with date, mood shift, emotions, and distortion tags, so that I can quickly review my thought records. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the diary, **When** the home screen renders, **Then** each entry card displays date, mood before/after, emotion labels, and cognitive distortion tags. |
| AC2 | **Given** entries exist, **When** the user taps on an entry, **Then** they are navigated to the entry detail screen. |
| AC3 | **Given** a "New Entry" button is displayed, **When** the user taps it, **Then** they are navigated to the mood check-in step of the new entry flow. |
| AC4 | **Given** no entries exist, **When** the diary home screen renders, **Then** an empty state with encouragement to create the first entry is displayed. |

---

### US-3.2: Mood Check-In

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to rate my current mood on a 1--10 scale before starting a new entry, so that I can capture my baseline emotional state. |
| **Priority** | Must |
| **Story Points** | 2 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user starts a new diary entry, **When** the mood check-in screen renders, **Then** a slider or selectable scale from 1 to 10 with a corresponding emoji and label (e.g., "Struggling" to "Great") is displayed. |
| AC2 | **Given** the user adjusts the mood rating, **When** the value changes, **Then** the emoji and label update in real time to match the selected value. |
| AC3 | **Given** a mood value is selected, **When** the user taps "Continue", **Then** the mood rating is saved and they proceed to the situation description step. |

---

### US-3.3: Situation Description

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to describe the triggering situation, when it happened, and where I was, so that I can record contextual details of my thought record. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the situation step, **When** the screen renders, **Then** a free-text area for describing the situation, a time selector, and a location input are displayed. |
| AC2 | **Given** the user fills in the situation description, **When** the text field has content, **Then** the "Continue" button becomes enabled. |
| AC3 | **Given** the description is entered, **When** the user taps "Continue", **Then** the situation data is saved and they proceed to the automatic thoughts step. |

---

### US-3.4: Automatic Thoughts & Emotions

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to write down my automatic thoughts and select emotions from a predefined list with intensity ratings, so that I can accurately capture my cognitive and emotional state. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the thoughts step, **When** the screen renders, **Then** a text area for the automatic thought and a grid of selectable emotions (e.g., Anxious, Frustrated, Sad, Overwhelmed) are displayed. |
| AC2 | **Given** the emotion list is displayed, **When** the user taps an emotion, **Then** it is toggled on/off with a visual highlight indicating selection, and multiple emotions can be selected. |
| AC3 | **Given** at least one emotion is selected, **When** the user views the intensity control, **Then** a slider for rating the emotional intensity (e.g., 1--10) is available. |
| AC4 | **Given** a thought is entered and at least one emotion selected, **When** the user taps "Continue", **Then** the data is saved and they proceed to the distortion identification step. |

---

### US-3.5: Cognitive Distortion Identification

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to identify cognitive distortions from a guided list with AI suggestions highlighting likely distortions, so that I can learn to recognize my thinking traps. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the distortion step, **When** the screen renders, **Then** a list of common cognitive distortions (e.g., All-or-Nothing, Catastrophizing, Mind Reading, Should Statements) is displayed with brief descriptions. |
| AC2 | **Given** the distortion list is displayed, **When** AI suggestions are available, **Then** likely distortions are visually highlighted with an "AI Suggested" badge based on the user's entered thought. |
| AC3 | **Given** the user taps on a distortion, **When** the selection toggles, **Then** it is visually marked as selected and multiple distortions can be chosen. |
| AC4 | **Given** at least one distortion is selected, **When** the user taps "Continue", **Then** the selections are saved and they proceed to the reframing step. |

---

### US-3.6: Thought Reframing

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to reframe my original thought into a balanced alternative using AI prompts and rate my belief in the new thought, so that I can practice cognitive restructuring. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the reframing step, **When** the screen renders, **Then** their original automatic thought is displayed alongside AI-generated guiding prompts to help construct a balanced alternative. |
| AC2 | **Given** the AI prompts are displayed, **When** the user types a balanced alternative thought, **Then** the text area accepts their input. |
| AC3 | **Given** a balanced thought is entered, **When** the user views the belief rating, **Then** a percentage slider (0--100%) for rating belief in the new thought is displayed. |
| AC4 | **Given** the balanced thought and belief rating are set, **When** the user taps "Save Entry", **Then** the complete diary entry is saved and they are navigated to the confirmation screen. |

---

### US-3.7: Entry Saved Confirmation

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to see a saved confirmation with my before/after mood shift and immediate AI insights, so that I can get instant feedback on my progress. |
| **Priority** | Should |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** a diary entry has been saved, **When** the confirmation screen renders, **Then** a success message with the before and after mood values and the change direction (improved / declined / unchanged) is displayed. |
| AC2 | **Given** the confirmation is displayed, **When** the user views AI insights, **Then** immediate observations (e.g., most common distortion, primary trigger pattern) are shown. |
| AC3 | **Given** the confirmation screen is displayed, **When** the user taps "Done" or "View Entry", **Then** they are navigated to the diary home or the entry detail screen respectively. |

---

### US-3.8: Pattern Insights

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to view pattern insights showing mood trends, top emotions, top distortions, and AI-generated analysis across all my entries, so that I can understand recurring patterns in my thinking. |
| **Priority** | Should |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the insights tab/screen, **When** it renders, **Then** aggregated charts for mood trends over time and ranked lists of top emotions and top cognitive distortions are displayed. |
| AC2 | **Given** insights are displayed, **When** the user scrolls to the AI analysis section, **Then** a narrative summary of identified patterns and correlations (e.g., "You tend to catastrophize most on Mondays") is shown. |
| AC3 | **Given** fewer than two entries exist, **When** the insights screen renders, **Then** a message indicating more entries are needed before patterns can be identified is displayed. |

---

### US-3.9: Diary Entry Detail

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to open a diary entry detail screen with overview, full AI-guided transcript, and AI analysis, so that I can revisit and reflect on past entries in depth. |
| **Priority** | Should |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user taps on a diary entry from the home screen, **When** the detail screen renders, **Then** an overview tab displays the situation, thought, emotions, distortions, reframe, and mood shift. |
| AC2 | **Given** the detail screen is displayed, **When** the user switches to the "AI Analysis" tab, **Then** AI-generated summary, key insights, and recommendations are displayed. |
| AC3 | **Given** the detail screen is displayed, **When** the user taps the back button, **Then** they return to the diary home screen. |

---

## 4. Progress Dashboard

### US-4.1: Key Stats Overview

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to see key stats at a glance (day streak, sessions completed, diary entries written), so that I can quickly gauge my overall engagement. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the progress dashboard, **When** the weekly overview screen renders, **Then** stat cards for day streak, sessions completed, and diary entries are prominently displayed. |
| AC2 | **Given** the stats are displayed, **When** the user views each card, **Then** the current value and a weekly change indicator (e.g., "+2 this week") are shown. |
| AC3 | **Given** the user has zero activity, **When** the stats render, **Then** values display as "0" with appropriate encouragement messaging. |

---

### US-4.2: Weekly Mood Chart

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to view a weekly mood bar chart with average and week-over-week change, so that I can track how my mood is trending. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the weekly overview, **When** the mood chart section renders, **Then** a bar chart showing mood values for each day of the current week is displayed. |
| AC2 | **Given** the mood chart is displayed, **When** the user views the summary, **Then** the weekly average mood and percentage change compared to the previous week are shown. |
| AC3 | **Given** mood data exists for the week, **When** individual bars render, **Then** each bar is proportionally sized and uses a color gradient indicating mood quality. |

---

### US-4.3: Burnout Recovery Score

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to see my burnout recovery percentage with sub-dimension breakdowns (Exhaustion, Cynicism, Efficacy) and weekly change indicators, so that I can monitor my recovery across different burnout factors. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the weekly overview, **When** the burnout section renders, **Then** an overall recovery percentage with a circular or radial progress indicator is displayed. |
| AC2 | **Given** the overall score is displayed, **When** the user views sub-dimensions, **Then** individual progress bars for Exhaustion, Cynicism, and Efficacy are shown with percentage values. |
| AC3 | **Given** sub-dimension bars are displayed, **When** the user views change indicators, **Then** each dimension shows a weekly change value (e.g., "+5%") with an up or down arrow. |

---

### US-4.4: Weekly Goal Tracking

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to track weekly goals (sessions, diary entries, breathing exercises) with progress bars, so that I can stay motivated and accountable. |
| **Priority** | Should |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user is on the weekly overview, **When** the goals section renders, **Then** each goal (e.g., "3 sessions", "5 diary entries", "7 breathing exercises") is displayed with a progress bar and fraction label (e.g., "2/3"). |
| AC2 | **Given** a goal is fully completed, **When** it renders, **Then** the progress bar is filled to 100% and a completion indicator (e.g., checkmark) is displayed. |
| AC3 | **Given** no progress has been made on a goal, **When** it renders, **Then** the progress bar is empty with "0/N" shown. |

---

### US-4.5: Monthly Trends & Heatmap

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to view monthly trend visualizations for mood, burnout score, and activity (including a heatmap) with AI-generated insights, so that I can understand long-term patterns and correlations. |
| **Priority** | Could |
| **Story Points** | 8 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the monthly view tab, **When** the screen renders, **Then** line or area charts for mood trend and burnout score over the past month are displayed. |
| AC2 | **Given** the monthly view is displayed, **When** the user scrolls to the activity heatmap, **Then** a calendar-style grid shows daily activity intensity using color coding. |
| AC3 | **Given** the monthly view is displayed, **When** the user views the AI insights section, **Then** narrative observations about correlations (e.g., "Your mood improves on days you complete sessions") are displayed. |
| AC4 | **Given** less than one week of data exists, **When** the monthly view renders, **Then** a message indicating insufficient data with an estimated date for when insights will be available is shown. |

---

### US-4.6: Achievements

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to earn and view achievements (e.g., 7-Day Streak, Session Master, Thoughtful Writer) with unlocked/locked status, so that I can feel motivated by gamified milestones. |
| **Priority** | Could |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the achievements section, **When** the screen renders, **Then** a grid of achievement badges is displayed with each badge showing its name, icon, and locked/unlocked state. |
| AC2 | **Given** an achievement is unlocked, **When** it renders, **Then** the badge is fully colored with a completion date shown below it. |
| AC3 | **Given** an achievement is locked, **When** it renders, **Then** the badge is displayed in a muted/greyed-out style with a description of how to unlock it. |

---

### US-4.7: AI Encouragement

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to receive an AI-powered encouragement card telling me what achievements are close to being unlocked, so that I can stay engaged with the program. |
| **Priority** | Could |
| **Story Points** | 2 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user views the achievements section, **When** an AI encouragement card renders, **Then** it displays a motivational message referencing the nearest achievement to being unlocked. |
| AC2 | **Given** all achievements are unlocked, **When** the encouragement card renders, **Then** a congratulatory message is displayed instead of a "near unlock" prompt. |
| AC3 | **Given** the encouragement card is displayed, **When** the user reads it, **Then** it includes a specific action suggestion (e.g., "Complete 1 more session to unlock Session Master!"). |

---

## 5. Crisis Support

### US-5.1: Crisis Detection & Triage

| Field | Detail |
|-------|--------|
| **User Story** | As a **user in distress**, I want to access a crisis support hub that asks how I need help and routes me to the appropriate resource, so that I can get the right support quickly. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to Crisis Support, **When** the detection screen renders, **Then** a prominent crisis awareness card ("Are you in crisis right now?") with a 911 reference is displayed. |
| AC2 | **Given** the crisis screen is displayed, **When** the user views the options, **Then** three distinct pathways are presented: "I need coping strategies", "Talk to someone now", and "My safety plan". |
| AC3 | **Given** the user taps a pathway, **When** the navigation occurs, **Then** they are routed to the corresponding screen (coping strategies, emergency resources, or safety plan). |
| AC4 | **Given** the crisis screen is displayed, **When** the user views the bottom action, **Then** a prominent "Call 988 Suicide & Crisis Lifeline" button is always visible. |
| AC5 | **Given** the crisis screen is displayed, **When** the user reads the support card, **Then** a reassuring message ("You are not alone") is shown with information about 24/7 help availability. |

---

### US-5.2: Guided Coping Strategies

| Field | Detail |
|-------|--------|
| **User Story** | As a **user in distress**, I want to access guided coping exercises (breathing, grounding, muscle relaxation), so that I can use immediate self-regulation techniques to calm down. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user selects "I need coping strategies", **When** the coping screen renders, **Then** cards for 4-7-8 Breathing, 5-4-3-2-1 Grounding, and Progressive Muscle Relaxation are displayed. |
| AC2 | **Given** the user taps "Try This" on the breathing exercise, **When** the exercise starts, **Then** an animated breathing circle cycles through "Breathe In", "Hold", "Breathe Out", "Hold" phases with visual scaling feedback. |
| AC3 | **Given** the breathing exercise completes all cycles, **When** it finishes, **Then** a "Complete!" state is shown with a "Well done! Feeling calmer?" encouragement message. |
| AC4 | **Given** the user taps "Try This" on the grounding exercise, **When** the exercise expands, **Then** all five sensory prompts (5 see, 4 touch, 3 hear, 2 smell, 1 taste) are displayed with corresponding icons. |
| AC5 | **Given** the user is on the coping screen, **When** they view the bottom action, **Then** a "Need more help? Talk to someone" button linking to emergency resources is always visible. |

---

### US-5.3: Emergency Resources & Contacts

| Field | Detail |
|-------|--------|
| **User Story** | As a **user in distress**, I want to see a directory of emergency hotlines, my therapist's contact, and my trusted personal contacts, so that I can reach out for human support immediately. |
| **Priority** | Must |
| **Story Points** | 3 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to emergency resources, **When** the screen renders, **Then** crisis hotlines (988 Lifeline, Crisis Text Line, SAMHSA Helpline) are listed with name, number, and one-tap "Call" or "Text" action buttons. |
| AC2 | **Given** the resources screen is displayed, **When** the user views the therapist section, **Then** their assigned therapist's name, last session date, and a "Call" button are shown. |
| AC3 | **Given** the resources screen is displayed, **When** the user views the trusted contacts section, **Then** each personal contact is listed with name, phone number, avatar initial, and a tap-to-call button. |
| AC4 | **Given** the resources screen is displayed, **When** the user taps "View Safety Plan", **Then** they are navigated to their personalized safety plan. |

---

### US-5.4: Personalized Safety Plan

| Field | Detail |
|-------|--------|
| **User Story** | As a **user**, I want to review my personalized safety plan with sequential steps (warning signs, coping strategies, social contacts, people to ask for help, professional resources, and environment safety), so that I can follow a structured protocol during a crisis. |
| **Priority** | Must |
| **Story Points** | 5 |

**Acceptance Criteria**

| # | Criteria |
|---|---------|
| AC1 | **Given** the user navigates to the safety plan, **When** the screen renders, **Then** six numbered steps are displayed in sequential order, each with a title and a list of specific items. |
| AC2 | **Given** the safety plan is displayed, **When** the user views Step 1 (Warning Signs), **Then** their personalized warning signs (e.g., "Racing thoughts about work", "Feeling physically tense") are listed with check icons. |
| AC3 | **Given** the safety plan is displayed, **When** the user scrolls through all steps, **Then** Steps 2--6 (Coping Strategies, Social Contacts for Distraction, People to Ask for Help, Professional & Crisis Resources, Making the Environment Safe) are each shown with their respective items. |
| AC4 | **Given** the user has reviewed the safety plan, **When** they view the bottom of the screen, **Then** a personal "reason for living" statement is displayed above a "Back to Dashboard" button. |
| AC5 | **Given** the user taps "Back to Dashboard", **When** navigation occurs, **Then** they are returned to the main dashboard. |

---

## Summary

| Module | Stories | Must | Should | Could | Total Story Points |
|--------|---------|------|--------|-------|--------------------|
| 1. Onboarding & Assessment | 6 | 6 | 0 | 0 | 18 |
| 2. Interactive CBT Session | 11 | 7 | 3 | 0 | 46 |
| 3. Thought Record Diary | 9 | 6 | 3 | 0 | 32 |
| 4. Progress Dashboard | 7 | 3 | 1 | 3 | 27 |
| 5. Crisis Support | 4 | 4 | 0 | 0 | 16 |
| **Total** | **37** | **26** | **7** | **3** | **139** |
