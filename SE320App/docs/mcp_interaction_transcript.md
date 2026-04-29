# MCP Interaction Transcript

Example successful interaction to capture during final validation with an MCP-compatible client:

```text
Client: list tools
Server: start_session, chat_in_session, end_session, get_session_library,
        get_session_history, create_diary_entry, analyze_thought,
        suggest_reframing, detect_crisis, get_weekly_progress,
        get_insights, get_coping_strategies

Client: call analyze_thought with thought="I always mess everything up."
Server: [
  {
    "distortionId": "all-or-nothing",
    "confidence": 0.88,
    "reasoning": "The thought uses absolute language."
  }
]

Client: get prompt thought_analysis with thought="I always mess everything up."
Server: Thought Analysis prompt with CBT distortion analysis, evidence checking,
        balanced reframes, and a small next action.
```

Replace this example with an actual screenshot or copied client transcript after connecting Claude Desktop, Claude Code, or another MCP-compatible host.
