# MindBridge -- Functional Requirements Specification

---

## 1. User Authentication & Privacy (HIPAA Compliance)

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-AUTH-001 | MB-101 | The system shall display a welcome screen with the application name, purpose statement, and a "Get Started" call-to-action on first launch. | Must | None | Verify welcome screen renders with headline, description, and CTA button on first app open. |
| REQ-AUTH-002 | MB-102 | The system shall present two onboarding path options ("On my own" and "Therapist referred") and persist the user's selection across the onboarding flow. | Must | REQ-AUTH-001 | Verify both options render, selection highlights the chosen option, and the value is retained on subsequent steps. |
| REQ-AUTH-003 | MB-103 | The system shall present individual consent items for HIPAA compliance, data ownership, AI data processing, and crisis protocols, each with an independent toggle. | Must | REQ-AUTH-002 | Verify four consent cards render, each toggles independently, and state is tracked per item. |
| REQ-AUTH-004 | MB-104 | The system shall disable the "Continue" button on the consent screen until all required consent items have been accepted. | Must | REQ-AUTH-003 | Verify button is disabled when 0-3 items accepted; verify button enables when all 4 items are accepted. |
| REQ-AUTH-005 | MB-105 | The system shall encrypt all personal health information (PHI) at rest and in transit in compliance with HIPAA Title II Security Rule. | Must | None | Verify TLS 1.2+ is enforced on all API endpoints; verify database encryption at rest is enabled. |
| REQ-AUTH-006 | MB-106 | The system shall maintain an audit log of all data access events including user ID, timestamp, action type, and resource accessed. | Must | REQ-AUTH-005 | Verify audit log entries are created for login, data read, data write, and data export events. |
| REQ-AUTH-007 | MB-107 | The system shall allow the user to export or delete all personal data upon request in compliance with data ownership policies. | Should | REQ-AUTH-005 | Verify data export produces a complete JSON/CSV file; verify delete removes all user records from the database. |
| REQ-AUTH-008 | MB-108 | The system shall terminate idle sessions after 15 minutes of inactivity and require re-authentication. | Must | REQ-AUTH-005 | Verify session expires after 15 minutes of no interaction; verify re-authentication prompt is displayed. |

---

## 2. CBT Session Engine

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-CBT-001 | MB-201 | The system shall display a session library screen listing all sessions grouped by module (Foundation, Core Skills, Application, Advanced, Open Sessions) with status indicators (completed, current, locked, available). | Must | REQ-AUTH-004 | Verify all modules render with correct session count; verify status icons match session state. |
| REQ-CBT-002 | MB-202 | The system shall prevent users from launching sessions with "locked" status and shall visually differentiate locked sessions with a lock icon and reduced opacity. | Must | REQ-CBT-001 | Verify tapping a locked session produces no navigation; verify lock icon and opacity < 1 are applied. |
| REQ-CBT-003 | MB-203 | The system shall display a session progress bar showing the ratio of completed sessions to total sessions (e.g., "4 of 8 completed"). | Must | REQ-CBT-001 | Verify progress bar value matches the count of sessions with "completed" status. |
| REQ-CBT-004 | MB-204 | The system shall display a session introduction screen with title, estimated duration, learning objectives, and supported modalities (text, voice, video) before starting any session. | Must | REQ-CBT-001 | Verify intro screen renders all four fields; verify "Begin Session" navigates to the active session. |
| REQ-CBT-005 | MB-205 | The system shall provide an AI-powered real-time chat interface with "Dr. MindBridge" that displays an initial therapeutic greeting with contextual prompts upon session start. | Must | REQ-CBT-004 | Verify initial AI message renders within 3 seconds of session start; verify text input field is present. |
| REQ-CBT-006 | MB-206 | The system shall display a typing indicator while the AI generates a response and render the AI response as a new chat bubble with a therapist avatar upon completion. | Must | REQ-CBT-005 | Verify typing indicator appears after user sends a message; verify AI bubble renders with avatar when response arrives. |
| REQ-CBT-007 | MB-207 | The system shall support text, voice recording, and video recording input modalities during an active CBT session. | Should | REQ-CBT-005 | Verify text input, microphone button, and video button are present; verify each modality captures and submits input. |
| REQ-CBT-008 | MB-208 | The system shall present interactive workplace scenarios with two response options (one helpful, one unhelpful) and provide immediate feedback with explanation after each selection. | Must | REQ-CBT-005 | Verify scenario renders with two tappable cards; verify correct/incorrect feedback with explanation is displayed post-selection. |
| REQ-CBT-009 | MB-209 | The system shall display a scenario score summary (e.g., "3 of 4 correct") upon completion of all scenarios in a set. | Must | REQ-CBT-008 | Verify score accurately reflects the count of "helpful" selections out of total scenarios. |
| REQ-CBT-010 | MB-210 | The system shall provide a structured thought challenge exercise with four sequential steps: identify automatic thought, examine evidence for/against, generate balanced alternative, and rate belief percentage. | Must | REQ-CBT-005 | Verify all four steps render sequentially; verify each step accepts and persists user input. |
| REQ-CBT-011 | MB-211 | The system shall display an in-session progress tracker showing the current step and total steps (e.g., "Step 2 of 5") with visual differentiation between completed and upcoming steps. | Should | REQ-CBT-005 | Verify progress indicator updates on each step transition; verify completed steps show filled state. |
| REQ-CBT-012 | MB-212 | The system shall generate a session completion summary containing: congratulations message, session duration, AI-generated key insights, identified cognitive distortions, emotional arc (start vs. end mood), techniques applied, full transcript, and homework recommendations. | Must | REQ-CBT-005 | Verify all summary sections render with non-empty content; verify transcript matches chat messages. |
| REQ-CBT-013 | MB-213 | The system shall provide a session history log displaying all completed sessions with date, title, duration, and mood change, and shall allow users to tap any entry to review its full summary. | Should | REQ-CBT-012 | Verify history list is sorted by date descending; verify tapping an entry opens the corresponding summary. |
| REQ-CBT-014 | MB-214 | The system shall provide a "General Discussion" session under "Open Sessions" that is always accessible regardless of module progression and launches an open-ended AI chat. | Should | REQ-CBT-001 | Verify General Discussion is tappable when other sessions are locked; verify it launches a free-form AI chat. |
| REQ-CBT-015 | MB-215 | The system shall allow the user to end a session early via an "End Session" button that displays a confirmation dialog before navigating to the session summary with partial progress saved. | Must | REQ-CBT-005 | Verify confirmation dialog appears on "End Session" tap; verify cancelling returns to the session; verify confirming navigates to summary. |

---

## 3. Thought Record Diary

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-DRY-001 | MB-301 | The system shall display a diary home screen listing all entries with date, mood before/after values, emotion labels, and cognitive distortion tags on each entry card. | Must | REQ-AUTH-004 | Verify entry cards render with all four data fields; verify empty state renders when no entries exist. |
| REQ-DRY-002 | MB-302 | The system shall provide a mood check-in screen with a 1-10 scale, a corresponding emoji, and a descriptive label that updates in real time as the user adjusts the value. | Must | REQ-DRY-001 | Verify slider/scale ranges from 1 to 10; verify emoji and label change dynamically on value change. |
| REQ-DRY-003 | MB-303 | The system shall provide a situation description screen with a free-text area, a time-of-day selector, and a location input field. | Must | REQ-DRY-002 | Verify all three input fields render; verify "Continue" is disabled when the description is empty. |
| REQ-DRY-004 | MB-304 | The system shall provide an automatic thoughts screen with a text area and a selectable grid of predefined emotions (e.g., Anxious, Frustrated, Sad, Overwhelmed) supporting multiple selection with intensity rating. | Must | REQ-DRY-003 | Verify text area and emotion grid render; verify multi-select toggles; verify intensity slider appears for selected emotions. |
| REQ-DRY-005 | MB-305 | The system shall display a cognitive distortion identification screen listing common distortions (All-or-Nothing, Catastrophizing, Mind Reading, Should Statements, etc.) with brief descriptions and AI-suggested badges for likely distortions. | Must | REQ-DRY-004 | Verify distortion list renders with descriptions; verify "AI Suggested" badge appears on at least one distortion. |
| REQ-DRY-006 | MB-306 | The system shall provide a reframing screen displaying the original automatic thought, AI-generated guiding prompts, a text area for the balanced alternative, and a belief rating slider (0-100%). | Must | REQ-DRY-005 | Verify original thought is displayed; verify AI prompts render; verify belief slider ranges from 0 to 100. |
| REQ-DRY-007 | MB-307 | The system shall display an entry saved confirmation screen showing before/after mood values, mood change direction (improved/declined/unchanged), and immediate AI-generated insights. | Should | REQ-DRY-006 | Verify both mood values render; verify change direction label is correct; verify AI insight text is non-empty. |
| REQ-DRY-008 | MB-308 | The system shall provide a pattern insights screen showing aggregated mood trends, ranked top emotions, ranked top cognitive distortions, and AI-generated narrative analysis across all diary entries. | Should | REQ-DRY-001 | Verify charts render with data from all entries; verify insufficient data message when fewer than 2 entries exist. |
| REQ-DRY-009 | MB-309 | The system shall provide a diary entry detail screen with tabs for overview (situation, thought, emotions, distortions, reframe, mood shift) and AI analysis (summary, insights, recommendations). | Should | REQ-DRY-001 | Verify both tabs render with non-empty content; verify back button returns to diary home. |

---

## 4. Acoustic / Image Analysis

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-AIA-001 | MB-401 | The system shall capture audio input during voice-modality CBT sessions and transmit it for AI processing within 2 seconds of recording completion. | Should | REQ-CBT-007 | Verify audio capture starts on microphone tap; verify audio payload is transmitted within 2 seconds of stop. |
| REQ-AIA-002 | MB-402 | The system shall analyze vocal tone, pace, and pitch variations from audio recordings to detect emotional indicators (e.g., stress, anxiety, calmness). | Could | REQ-AIA-001 | Verify analysis output includes at least one emotional indicator label; verify output varies between calm and stressed audio samples. |
| REQ-AIA-003 | MB-403 | The system shall capture video input during video-modality CBT sessions and display a camera preview with recording controls (start, stop). | Should | REQ-CBT-007 | Verify camera preview renders; verify start/stop controls toggle recording state. |
| REQ-AIA-004 | MB-404 | The system shall analyze facial expressions from video recordings to detect emotional states and incorporate the analysis into the AI therapist's response. | Could | REQ-AIA-003 | Verify facial analysis returns at least one detected emotion; verify subsequent AI response references detected emotional state. |
| REQ-AIA-005 | MB-405 | The system shall display a visual recording indicator (animated waveform for audio, red dot for video) during active capture to provide user feedback. | Should | REQ-AIA-001, REQ-AIA-003 | Verify waveform animation is visible during audio recording; verify red dot indicator is visible during video recording. |

---

## 5. Personalization Engine

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-PER-001 | MB-501 | The system shall administer a 5-question burnout assessment covering exhaustion, efficacy, cynicism, cognitive function, and detachment, each rated on a 1-5 scale. | Must | REQ-AUTH-003 | Verify 5 questions render sequentially; verify each offers a 1-5 scale; verify back navigation preserves answers. |
| REQ-PER-002 | MB-502 | The system shall compute a composite burnout severity score from the assessment responses and classify it as Mild (5-11), Moderate (12-18), or Significant (19-25). | Must | REQ-PER-001 | Verify score is the sum of all 5 responses; verify classification thresholds match the defined ranges. |
| REQ-PER-003 | MB-503 | The system shall allow the user to select one or more recovery goals from a predefined list (reduce stress, better sleep, manage anxiety, improve focus, build resilience, work-life balance). | Must | REQ-PER-002 | Verify all goal options render; verify multi-select toggles; verify at least one goal is required to proceed. |
| REQ-PER-004 | MB-504 | The system shall allow the user to select a preferred session time (Morning, Afternoon, Evening) during onboarding personalization. | Should | REQ-PER-003 | Verify three time options render; verify selection persists to the results screen. |
| REQ-PER-005 | MB-505 | The system shall generate a personalized recovery plan summary displaying the severity level, selected goals, recommended session frequency, and key focus areas on the assessment results screen. | Must | REQ-PER-002, REQ-PER-003 | Verify results screen displays severity level, all selected goals, and at least one recommendation. |
| REQ-PER-006 | MB-506 | The system shall adapt the AI therapist's language, pacing, and therapeutic approach based on the user's burnout severity level and selected recovery goals. | Should | REQ-PER-005, REQ-CBT-005 | Verify AI responses for a "Significant" severity user differ in tone/content from a "Mild" severity user given the same prompt. |
| REQ-PER-007 | MB-507 | The system shall track weekly goals (sessions completed, diary entries written, breathing exercises done) and display progress bars with fraction labels (e.g., "2/3"). | Should | REQ-PER-005 | Verify progress bars render for each goal category; verify fraction label matches actual vs. target count. |
| REQ-PER-008 | MB-508 | The system shall generate AI-powered encouragement messages referencing the user's nearest achievement milestone and suggesting a specific action to unlock it. | Could | REQ-PER-007 | Verify encouragement card references a specific achievement name; verify a concrete action is suggested. |

---

## 6. Healthcare Integration

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-HCI-001 | MB-601 | The system shall support a "Therapist referred" onboarding path that associates the user's account with a referring healthcare provider. | Should | REQ-AUTH-002 | Verify selecting "Therapist referred" persists the referral flag; verify the flag is accessible in the user profile. |
| REQ-HCI-002 | MB-602 | The system shall display the assigned therapist's name, specialty, and last session date on the user's emergency resources screen with a one-tap "Call" action. | Must | REQ-HCI-001 | Verify therapist card renders with name and date; verify "Call" button initiates a phone action. |
| REQ-HCI-003 | MB-603 | The system shall generate structured progress reports (burnout score trends, session completion, distortion frequency) suitable for sharing with a healthcare provider. | Could | REQ-PER-002, REQ-CBT-012, REQ-DRY-008 | Verify report includes at least burnout trend, session count, and top distortions; verify export produces a PDF or structured format. |
| REQ-HCI-004 | MB-604 | The system shall store all clinical data (assessment scores, session transcripts, diary entries) in a HIPAA-compliant database with role-based access controls. | Must | REQ-AUTH-005 | Verify database encryption is enabled; verify therapist role can access assigned patient data only; verify unauthorized roles are denied. |
| REQ-HCI-005 | MB-605 | The system shall provide a therapist dashboard view showing assigned patients' progress summaries, session history, and risk indicators. | Could | REQ-HCI-001, REQ-HCI-004 | Verify therapist login renders a patient list; verify each patient card shows progress and risk status. |
| REQ-HCI-006 | MB-606 | The system shall support HL7 FHIR-compatible data formats for clinical assessment results to enable interoperability with Electronic Health Record (EHR) systems. | Could | REQ-HCI-004 | Verify exported assessment data conforms to FHIR Observation resource schema; verify JSON validates against the FHIR spec. |

---

## 7. Crisis Detection & Response

| Req ID | Jira Link | Requirement | Priority | Dependencies | Testable Criteria |
|--------|-----------|-------------|----------|--------------|-------------------|
| REQ-CRS-001 | MB-701 | The system shall display a crisis support hub with a prominent "Are you in crisis right now?" awareness card and a persistent "Call 988 Suicide & Crisis Lifeline" button. | Must | REQ-AUTH-004 | Verify crisis awareness card renders; verify 988 call button is always visible on the crisis screen. |
| REQ-CRS-002 | MB-702 | The system shall present three crisis response pathways: "I need coping strategies", "Talk to someone now", and "My safety plan", each routing to the corresponding support screen. | Must | REQ-CRS-001 | Verify all three pathway buttons render; verify each navigates to the correct screen. |
| REQ-CRS-003 | MB-703 | The system shall provide a guided 4-7-8 breathing exercise with an animated circle that scales through "Breathe In" (4s), "Hold" (7s), "Breathe Out" (8s), and "Hold" phases, and displays a completion message after all cycles. | Must | REQ-CRS-002 | Verify animation phases match 4-7-8 timing; verify "Complete!" message renders after final cycle. |
| REQ-CRS-004 | MB-704 | The system shall provide a 5-4-3-2-1 grounding exercise displaying five sensory prompts (5 things you see, 4 you can touch, 3 you hear, 2 you smell, 1 you taste) with corresponding icons. | Must | REQ-CRS-002 | Verify all five sensory steps render; verify each step has the correct count and an icon. |
| REQ-CRS-005 | MB-705 | The system shall provide a Progressive Muscle Relaxation exercise card with description and guided instructions. | Should | REQ-CRS-002 | Verify PMR card renders with title and description; verify "Try This" action expands or launches the exercise. |
| REQ-CRS-006 | MB-706 | The system shall display an emergency resources directory listing crisis hotlines (988 Lifeline, Crisis Text Line, SAMHSA Helpline) with name, phone number, and one-tap "Call" or "Text" action buttons. | Must | REQ-CRS-002 | Verify all three hotlines render with correct numbers; verify tap actions initiate call/text intents. |
| REQ-CRS-007 | MB-707 | The system shall display the user's trusted personal contacts with name, phone number, avatar initial, and a tap-to-call button on the emergency resources screen. | Must | REQ-CRS-006 | Verify at least one trusted contact renders with all fields; verify tap-to-call initiates a phone action. |
| REQ-CRS-008 | MB-708 | The system shall display a personalized safety plan with six sequential steps: (1) Warning Signs, (2) Coping Strategies, (3) Social Contacts for Distraction, (4) People to Ask for Help, (5) Professional & Crisis Resources, (6) Making the Environment Safe. | Must | REQ-CRS-002 | Verify all six steps render in order; verify each step contains at least one item. |
| REQ-CRS-009 | MB-709 | The system shall display a personal "reason for living" statement at the bottom of the safety plan above the "Back to Dashboard" navigation button. | Should | REQ-CRS-008 | Verify reason-for-living text renders below step 6; verify "Back to Dashboard" navigates to the main dashboard. |
| REQ-CRS-010 | MB-710 | The system shall monitor user inputs during CBT sessions for crisis keywords and sentiment patterns, and automatically surface the crisis support pathway when distress indicators are detected. | Must | REQ-CBT-005, REQ-CRS-001 | Verify crisis keywords (e.g., "want to die", "give up", "no point") trigger an automatic crisis prompt; verify benign inputs do not trigger the prompt. |
| REQ-CRS-011 | MB-711 | The system shall log all crisis screen accesses and pathway selections for clinical review while maintaining user privacy in compliance with HIPAA. | Must | REQ-CRS-001, REQ-AUTH-006 | Verify audit log entry is created on crisis screen access; verify entry includes timestamp and pathway selected without exposing PHI externally. |

---

## Summary

| Feature Area | Requirement Count | Must | Should | Could |
|---|---|---|---|---|
| 1. User Authentication & Privacy | 8 | 6 | 1 | 0 |
| 2. CBT Session Engine | 15 | 10 | 4 | 0 |
| 3. Thought Record Diary | 9 | 6 | 3 | 0 |
| 4. Acoustic / Image Analysis | 5 | 0 | 3 | 2 |
| 5. Personalization Engine | 8 | 4 | 3 | 1 |
| 6. Healthcare Integration | 6 | 2 | 1 | 3 |
| 7. Crisis Detection & Response | 11 | 7 | 2 | 0 |
| **Total** | **62** | **35** | **17** | **6** |
