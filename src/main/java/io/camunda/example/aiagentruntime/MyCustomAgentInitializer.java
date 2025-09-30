package io.camunda.example.aiagentruntime;

import io.camunda.connector.agenticai.adhoctoolsschema.schema.AdHocToolsSchemaResolver;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializationResult;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializer;
import io.camunda.connector.agenticai.aiagent.agent.AgentInitializerImpl;
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
      AdHocToolsSchemaResolver toolsSchemaResolver,
      GatewayToolHandlerRegistry gatewayToolHandlers) {
    this.delegate = new AgentInitializerImpl(toolsSchemaResolver, gatewayToolHandlers);
  }

  @Override
  public AgentInitializationResult initializeAgent(AgentExecutionContext executionContext) {
    LOGGER.info(">>> Initializing agent");

    final var result = delegate.initializeAgent(executionContext);

    LOGGER.info("<<< Agent initialized. Result: {}", result);

    return result;
  }
}
