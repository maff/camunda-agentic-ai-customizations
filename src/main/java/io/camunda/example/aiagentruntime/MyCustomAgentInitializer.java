package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.aiagent.agent.AgentInitializationResult;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializerImpl;
import io.camunda.connector.agenticai.aiagent.agent.AgentToolsResolver;
import io.camunda.connector.agenticai.aiagent.agent.ToolCallResultCompletedAtResolver;
import io.camunda.connector.agenticai.aiagent.agentinstance.AgentInstanceClient;
import io.camunda.connector.agenticai.aiagent.model.AgentExecutionContext;
import io.camunda.connector.agenticai.aiagent.tool.GatewayToolHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyCustomAgentInitializer implements AgentInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomAgentInitializer.class);

  private final AgentInitializer delegate;

  public MyCustomAgentInitializer(
      AgentToolsResolver agentToolsResolver,
      GatewayToolHandlerRegistry gatewayToolHandlers,
      AgentInstanceClient agentInstanceClient) {
    this.delegate =
        new AgentInitializerImpl(
            agentToolsResolver,
            gatewayToolHandlers,
            agentInstanceClient,
            new ToolCallResultCompletedAtResolver());
  }

  @Override
  public AgentInitializationResult initializeAgent(AgentExecutionContext executionContext) {
    LOGGER.info(">>> Initializing agent");

    final var result = delegate.initializeAgent(executionContext);

    LOGGER.info("<<< Agent initialized. Result: {}", result);

    return result;
  }
}
