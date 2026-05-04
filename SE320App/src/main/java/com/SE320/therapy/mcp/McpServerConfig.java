package com.SE320.therapy.mcp;

import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {
    public static final String SERVER_NAME = "digital-therapy-assistant";
    public static final String STDIO_FLAG = "--spring.ai.mcp.server.stdio=true";
    public static final String DISABLE_CLI_FLAG = "--app.cli.enabled=false";
}
