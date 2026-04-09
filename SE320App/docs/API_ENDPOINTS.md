# Application Programming interface describing End Points for external System Access

| # | End Point | API Method | Request Parameters | Response Parameters | Response Code | Notes |
|---|---|---|---|---|---|---|
| 1 | `/auth/register` | `POST` | Body: `userType, firstName, lastName, email, password, phoneNumber` | `accessToken, refreshToken, user{id,userType,firstName,lastName,email,phoneNumber,online}` | `201, 400, 409` | Public endpoint |
| 2 | `/auth/login` | `POST` | Body: `email, password` | `accessToken, refreshToken, user{...}` | `200, 400, 401` | Public endpoint |
| 3 | `/auth/logout` | `POST` | Header: `Authorization` | No body | `204, 400, 401` | Requires auth token |
| 4 | `/auth/delete` | `DELETE` | Body: `userId, email, password` | `id, userType, firstName, lastName, email, phoneNumber, online` | `200, 400, 401, 404` | Requires auth token |
| 5 | `/auth/refresh` | `POST` | Body: raw `refreshToken` string | `accessToken, refreshToken, user{...}` | `200, 400, 401` | Public endpoint |
| 6 | `/auth/users` | `GET` | Query: `page,size,sort` | `Page<UserResponse>` | `200, 400` | Requires auth token |
| 7 | `/sessions` | `GET` | None | `List<SessionLibraryItemResponse>{sessionId,title,description,durationMinutes,orderIndex,modalities}` | `200, 401, 403` | Role: `PATIENT` or `DOCTOR` |
| 8 | `/sessions/page` | `GET` | Query: `page,size,sort` | `Page<SessionLibraryItemResponse>` | `200, 400` | Paginated library |
| 9 | `/sessions/{sessionId}` | `GET` | Path: `sessionId` | `SessionDetailResponse{sessionId,title,description,durationMinutes,orderIndex,moduleName,objectives,modalities}` | `200, 400, 401, 403, 404` | Session detail |
| 10 | `/sessions/{sessionId}/start` | `POST` | Path: `sessionId`; Body: `userId, moodBefore` | `SessionRunResponse{userSessionId,userId,sessionId,title,status,moodBefore,moodAfter,startedAt,endedAt}` | `201, 400, 401, 403, 404, 409` | Starts active run |
| 11 | `/sessions/{sessionId}/chat` | `POST` | Path: `sessionId`; Body: `userId, message, modality` | `SessionChatResponse{userSessionId,sessionId,userMessage,assistantMessage}` | `200, 400, 401, 403, 404` | In-session chat |
| 12 | `/sessions/{sessionId}/end` | `POST` | Path: `sessionId`; Body: `userId, moodAfter` | `SessionRunResponse{...}` | `200, 400, 401, 403, 404` | Ends active run |
| 13 | `/diary/entries` | `POST` | Query: `userId`; Body: `situation, automaticThought, alternativeThought, moodBefore, moodAfter` | `DiaryEntryResponse{id,message,createdAt}` | `201, 400, 404` | Creates diary entry |
| 14 | `/diary/entries` | `GET` | Query: `userId,page,size,sort` | `Page<DiaryEntrySummary>{id,situationPreview,moodBefore,moodAfter,createdAt}` | `200, 400` | List entries |
| 15 | `/diary/entries/{entryId}` | `GET` | Path: `entryId` | `DiaryEntryDetail{id,userId,situation,automaticThought,alternativeThought,moodBefore,moodAfter,createdAt}` | `200, 404` | Entry detail |
| 16 | `/diary/entries/{entryId}` | `DELETE` | Path: `entryId` | No body | `204, 404` | Soft delete |
| 17 | `/diary/insights` | `GET` | Query: `userId` | `DiaryInsights{totalEntries,averageMoodImprovement,bestMoodImprovement}` | `200, 400` | Aggregate metrics |
| 18 | `/diary/distortions/suggest` | `POST` | Body: `thought` | `List<DistortionSuggestion>{distortionId,confidence,reasoning}` | `200, 400` | AI suggestion endpoint |
| 19 | `/progress` | `GET` | Query: `userId` | `Dashboard{ownerUserId,monthlyTrends,weeklyProgress,burnoutRecovery,achievements}` | `200` | Requires auth token |
| 20 | `/progress/progress/monthly` | `GET` | Query: `userId` | `MonthlyTrends{period,averageMoodScore,sessionsCompleted,journalEntriesCreated,improvementRate}` | `200` | Path includes duplicated `/progress` |
| 21 | `/progress/progress/weekly` | `GET` | Query: `userId` | `WeeklyProgress{weekStart,completedGoals,totalGoals,currentStreak,progressPoints}` | `200` | Path includes duplicated `/progress` |
| 22 | `/progress/progress/burnout` | `GET` | Query: `userId` | `BurnoutRecovery{maslachBurnoutInventoryDimensions,recoveryStrategies,workLifeBalanceTechniques,boundarySetting}` | `200` | Path includes duplicated `/progress` |
| 23 | `/progress/progress/achievements` | `GET` | Query: `userId,page,size,sort` | `Page<AchievementResponse>{id,userId,title,description,unlocked,unlockedMonth}` | `200` | Path includes duplicated `/progress` |
| 24 | `/progress/progress/achievements` | `POST` | Query: `userId`; Body: `title,description,unlocked,unlockedMonth` | `AchievementResponse{...}` | `201` | Bonus endpoint; duplicated path segment |
| 25 | `/progress/progress/achievements/{achievementId}` | `PUT` | Query: `userId`; Path: `achievementId`; Body: `title,description,unlocked,unlockedMonth` | `AchievementResponse{...}` | `200` | Bonus endpoint; duplicated path segment |
| 26 | `/progress/progress/achievements/{achievementId}` | `DELETE` | Query: `userId`; Path: `achievementId` | No body | `204` | Bonus endpoint; duplicated path segment |
| 27 | `/crisis` | `GET` | Query: `userId` | `Crisis{ownerUserId,warningSignRecognition,deescalationTechniques,safetyPlanningSteps,emergencyResources}` | `200` | Requires auth token |
| 28 | `/crisis/crisis/coping-strategies` | `GET` | None | `List<String>` | `200` | Path includes duplicated `/crisis` |
| 29 | `/crisis/crisis/coping-strategies/page` | `GET` | Query: `page,size,sort` | `Page<String>` | `200` | Paginated coping strategies |
| 30 | `/crisis/crisis/safety-plan` | `GET` | Query: `userId` | `List<String>` | `200` | Path includes duplicated `/crisis` |
| 31 | `/crisis/crisis/safety-plan/page` | `GET` | Query: `userId,page,size,sort` | `Page<String>` | `200` | Paginated safety plan |
| 32 | `/crisis/crisis/detect` | `POST` | Body: `message, observedIndicators[]` | `CrisisDetectionResponse{crisisDetected,severityLevel,matchedIndicators,recommendedNextSteps}` | `200` | Path includes duplicated `/crisis` |
| 33 | `/crisis/crisis/safety-plan` | `PUT` | Query: `userId`; Body: `steps[]` | `List<String>` | `200` | Updates safety plan; duplicated path segment |
